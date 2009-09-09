/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import gnu.prolog.database.Module;
import gnu.prolog.database.Predicate;
import gnu.prolog.database.PrologTextLoader;
import gnu.prolog.database.PrologTextLoaderError;
import gnu.prolog.database.PrologTextLoaderState;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.term.Term;
import gnu.prolog.vm.PrologException;
import gnu.prolog.vm.TermConstants;

import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import net.ample.tracing.core.Augmentable;
import net.ample.tracing.core.PersistenceManager;
import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceLink;
import net.ample.tracing.core.TraceLinkType;
import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.core.TraceableArtefactType;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import trese.taf.Activator;
import trese.taf.atf.util.AtfQueue;
import trese.taf.atf.util.AtfQueueEntry;
import trese.taf.atf.util.AtfRepoCache;
import trese.taf.atf.util.CompositeReader;

/**
 * Import artefacts/links/types from a prolog fact database.
 * 
 * To delete artefacts use the predicate remove_traceable_artefact(UUID), to
 * delete links use the predicate remove_trace_link(UUID). When an artefact/link
 * does not exist it will result in an warning.
 * 
 * To add new or update new artefacts use the standard traceable_artefact/4
 * predicate. The UUID is a defining element in this process. If the type UUID
 * does not exist the type will be created with the given UUID, and a warning
 * will be created. The name of the artefact type will be auto generated (how?).
 * 
 * To add new tracelinks use the trace_link/5 predicate. This follows the same
 * rules as for the artefacts. Tracelinks cannot be updated.
 * 
 * Creation of new types can be done using the traceable_artefact_type/2 and
 * trace_link_type/2 predicates. Removing or updating of types is not possible.
 * 
 * @author Michiel Hendriks
 */
public class PrologFactImporter implements ILogListener
{
	// Note: this importer contains a lot of hacks to work around the terrible
	// implementation of the repository. This importer performs it's own caching
	// and transaction handling.

	public static final CompoundTermTag ATF_ELEMENT_PROPERTY_TAG = CompoundTermTag.get("atf_element_property", 3);
	public static final CompoundTermTag TRACE_LINK_TAG = CompoundTermTag.get("trace_link", 5);
	public static final CompoundTermTag TRACEABLE_ARTEFACT_TAG = CompoundTermTag.get("traceable_artefact", 4);
	public static final CompoundTermTag REMOVE_TRACE_LINK_TAG = CompoundTermTag.get("remove_trace_link", 1);
	public static final CompoundTermTag REMOVE_TRACEABLE_ARTEFACT_TAG = CompoundTermTag.get(
			"remove_traceable_artefact", 1);
	public static final CompoundTermTag TRACE_LINK_TYPE_TAG = CompoundTermTag.get("trace_link_type", 2);
	public static final CompoundTermTag TRACEABLE_ARTEFACT_TYPE_TAG = CompoundTermTag.get("traceable_artefact_type", 2);

	public static final CompoundTermTag[] TAGS = new CompoundTermTag[] { TRACEABLE_ARTEFACT_TYPE_TAG,
			TRACE_LINK_TYPE_TAG, REMOVE_TRACEABLE_ARTEFACT_TAG, REMOVE_TRACE_LINK_TAG, TRACEABLE_ARTEFACT_TAG,
			TRACE_LINK_TAG, ATF_ELEMENT_PROPERTY_TAG };

	/**
	 * The maximum size of a string which the ATF (persistent manager)
	 * repository supports. When an import string is larger than this it will
	 * truncate the string (and produce a warning) by removing leading
	 * characters.
	 */
	public static final int MAX_STRING_SIZE = 255;

	/**
	 * Helper interface for method delegates
	 * 
	 * @author Michiel Hendriks
	 */
	public interface FactImporter
	{
		void processFact(Term[] args) throws PrologException;
	}

	protected RepositoryManager repository;

	protected ILog logger;

	protected boolean hasErrors;

	// Caches because the ATF doesn't know about new/removed elements until the
	// commit is performed
	protected AtfRepoCache cache;

	// Queue because ATF does not handle query() during write operations on the
	// repository (which actually makes the whole transaction system completely
	// useless).
	protected AtfQueue queue;

	public PrologFactImporter(RepositoryManager repo, ILog log)
	{
		if (repo == null)
		{
			throw new NullPointerException("RepositoryManager cannot be null");
		}
		if (log == null)
		{
			throw new NullPointerException("ILog cannot be null");
		}
		repository = repo;
		logger = log;
	}

	public boolean importFacts(Reader data, IProgressMonitor monitor)
	{
		if (data == null)
		{
			throw new NullPointerException("Reader cannot be null");
		}
		monitor.beginTask("Importing prolog facts to ATF", 10);
		hasErrors = false;
		monitor.subTask("Parsing");
		PrologTextLoaderState state = new PrologTextLoaderState();
		new PrologTextLoader(state, new CompositeReader(predicateInit(), data));
		monitor.worked(1);
		List<PrologTextLoaderError> errors = state.getErrors();
		if (!errors.isEmpty())
		{
			for (PrologTextLoaderError error : errors)
			{
				Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, error.getMessage(), error);
				logger.log(status);
			}
			return false;
		}

		queue = new AtfQueue();
		cache = new AtfRepoCache(repository, queue);

		try
		{
			logger.addLogListener(this);

			// First process types
			monitor.subTask("Processing artefact removals");
			processPredicates(new SubProgressMonitor(monitor, 1), state.getModule(), TRACEABLE_ARTEFACT_TYPE_TAG,
					new FactImporter() {
						public void processFact(Term[] args) throws PrologException
						{
							processArtefactType(args[0], args[1]);
						}
					});
			if (monitor.isCanceled())
			{
				return false;
			}

			monitor.subTask("Processing link types");
			processPredicates(new SubProgressMonitor(monitor, 1), state.getModule(), TRACE_LINK_TYPE_TAG,
					new FactImporter() {
						public void processFact(Term[] args) throws PrologException
						{
							processLinkType(args[0], args[1]);
						}
					});
			if (monitor.isCanceled())
			{
				return false;
			}

			if (hasErrors)
			{
				logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Aborted import due to errors creating artefact and/or trace link types."));
				return false;
			}

			// Process removals

			monitor.subTask("Processing artefact removals");
			processPredicates(new SubProgressMonitor(monitor, 1), state.getModule(), REMOVE_TRACEABLE_ARTEFACT_TAG,
					new FactImporter() {
						public void processFact(Term[] args) throws PrologException
						{
							processRemoveArtefact(args[0]);
						}
					});
			if (monitor.isCanceled())
			{
				return false;
			}

			monitor.subTask("Processing link removals");
			processPredicates(new SubProgressMonitor(monitor, 1), state.getModule(), REMOVE_TRACE_LINK_TAG,
					new FactImporter() {
						public void processFact(Term[] args) throws PrologException
						{
							processRemoveLink(args[0]);
						}
					});
			if (monitor.isCanceled())
			{
				return false;
			}

			// Process updates/additions

			monitor.subTask("Processing artefact additions/updates");
			processPredicates(new SubProgressMonitor(monitor, 1), state.getModule(), TRACEABLE_ARTEFACT_TAG,
					new FactImporter() {
						public void processFact(Term[] args) throws PrologException
						{
							processUpdateArtefact(args[0], args[1], args[2], args[3]);
						}
					});
			if (monitor.isCanceled())
			{
				return false;
			}

			monitor.subTask("Processing link additions");
			processPredicates(new SubProgressMonitor(monitor, 1), state.getModule(), TRACE_LINK_TAG,
					new FactImporter() {
						public void processFact(Term[] args) throws PrologException
						{
							processCreateLink(args[0], args[1], args[2], args[3], args[4]);
						}
					});
			if (monitor.isCanceled())
			{
				return false;
			}

			monitor.subTask("Processing properties");
			processPredicates(new SubProgressMonitor(monitor, 1), state.getModule(), ATF_ELEMENT_PROPERTY_TAG,
					new FactImporter() {
						public void processFact(Term[] args) throws PrologException
						{
							processProperties(args[0], args[1], args[2]);
						}
					});
			if (monitor.isCanceled())
			{
				return false;
			}

			IProgressMonitor submon = new SubProgressMonitor(monitor, 2);
			monitor.subTask(String.format("Commiting %d changes", queue.size()));
			submon.beginTask("", queue.size() * 2);
			if (!hasErrors)
			{
				PersistenceManager perman = repository.getPersistenceManager();
				perman.begin();
				for (AtfQueueEntry entry : queue)
				{
					if (monitor.isCanceled())
					{
						return false;
					}
					switch (entry.getAction())
					{
						case ADD:
							perman.add(entry.getObj());
							break;
						case REMOVE:
							perman.remove(entry.getObj());
							break;
						case UPDATE:
							perman.update(entry.getObj());
							break;
					}
					submon.worked(1);
				}
				perman.commit();
				submon.worked(queue.size() / 2);
			}
			submon.done();
		}
		catch (Exception e)
		{
			if (repository.getPersistenceManager().isActive())
			{
				repository.getPersistenceManager().rollback();
			}
			logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		finally
		{
			logger.removeLogListener(this);
			cache = null;
			queue = null;
			monitor.done();
		}
		return !hasErrors;
	}

	/**
	 * Produces predicate initializer header
	 * 
	 * @param state
	 */
	protected Reader predicateInit()
	{
		StringBuilder sb = new StringBuilder();
		for (CompoundTermTag tag : TAGS)
		{
			sb.append(":-discontiguous(");
			sb.append(tag.toString());
			sb.append(").\n:-dynamic(");
			sb.append(tag.toString());
			sb.append(").\n");
		}
		return new StringReader(sb.toString());
	}

	/**
	 * @param tag
	 * @param importer
	 */
	protected void processPredicates(IProgressMonitor monitor, Module module, CompoundTermTag tag, FactImporter importer)
	{
		Predicate pred = module.getDefinedPredicate(tag);
		if (pred != null)
		{
			monitor.beginTask("", pred.getClauses().size());
			// Clause = CompoundTerm -> args[0] = CompoundTerm -> args = data
			for (Term term : pred.getClauses())
			{
				if (monitor.isCanceled())
				{
					break;
				}
				if (!(term instanceof CompoundTerm))
				{
					continue;
				}
				CompoundTerm ct = (CompoundTerm) term;
				if (!TermConstants.clauseTag.equals(ct.tag))
				{
					// TODO: not a clause!?
					monitor.worked(1);
					continue;
				}
				if (!TermConstants.trueAtom.equals(ct.args[1]))
				{
					// TODO: not a fact
					monitor.worked(1);
					continue;
				}
				if (!(ct.args[0] instanceof CompoundTerm))
				{
					monitor.worked(1);
					continue;
				}
				ct = (CompoundTerm) ct.args[0];
				if (!tag.equals(ct.tag))
				{
					// TODO: not the correct tag;
					monitor.worked(1);
					continue;
				}
				try
				{
					importer.processFact(ct.args);
					monitor.worked(1);
				}
				catch (PrologException e)
				{
					monitor.worked(1);
					logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				}
			}
		}
		monitor.done();
	}

	/**
	 * Get the UUID from a term. We don't return the UUID object because the
	 * core of ATF uses Strings, and there are cases where the UUID in the
	 * repository is actually not a valid UUID. In that case the invalid UUID is
	 * returned when it is property formatted.
	 * 
	 * @param uuidTerm
	 * @return
	 * @throws PrologException
	 */
	protected String getUUID(Term uuidTerm) throws PrologException
	{
		if (!(uuidTerm instanceof AtomTerm))
		{
			PrologException.typeError(TermConstants.atomAtom, uuidTerm);
		}
		String data = ((AtomTerm) uuidTerm).value;
		if (data == null || data.length() == 0)
		{
			return null;
		}
		try
		{
			return UUID.fromString(data).toString();
		}
		catch (IllegalArgumentException e)
		{
			if (Pattern.matches(
					"([0-9a-zA-Z]{8})-([0-9a-zA-Z]{4})-([0-9a-zA-Z]{4})-([0-9a-zA-Z]{4})-([0-9a-zA-Z]{12})", data))
			{
				logger.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, String.format(
						"Invalid UUID but with proper formatting: %s", data)));
				return data;
			}
			else
			{
				throw new IllegalArgumentException(String.format("Invalid UUID: %s", data), e);
			}
		}
	}

	/**
	 * Get a string from a term which is "safe" for the ATF. This will trucate
	 * the string according to {@link #MAX_STRING_SIZE}
	 * 
	 * @param term
	 * @return
	 * @throws PrologException
	 */
	protected String getSafeString(Term term, String termName) throws PrologException
	{
		String value = null;
		if (term instanceof AtomTerm)
		{
			value = ((AtomTerm) term).value;
		}
		else
		{
			PrologException.typeError(TermConstants.atomAtom, term);
		}
		if (value == null)
		{
			return "";
		}
		if (value.length() > MAX_STRING_SIZE)
		{
			value = value.substring(value.length() - MAX_STRING_SIZE);
			logger.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, String.format(
					"%s exceeded %d characters, truncated to: %s", (termName == null ? "String" : termName),
					MAX_STRING_SIZE, value)));
		}
		return value;
	}

	/**
	 * @param uuidTerm
	 * @param nameTerm
	 * @throws PrologException
	 */
	protected void processArtefactType(Term uuidTerm, Term nameTerm) throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		String typeName = getSafeString(nameTerm, "Type name");
		TraceableArtefactType type = null;
		if (uuid != null)
		{
			type = cache.getTraceableArtefactType(uuid);
		}
		if (type == null)
		{
			// type doesn't exist -> create it
			try
			{
				type = repository.getTypeManager().createArtefactType(typeName);
			}
			catch (IllegalArgumentException e)
			{
				logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				return;
			}
			if (uuid != null)
			{
				type.setUuid(uuid);
			}
			queue.add(type);
			cache.register(type);
			logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format(
					"Created traceable artefact type '%s' with UUID %s", type.getName(), type.getUuid())));
		}
	}

	/**
	 * @param term
	 * @param term2
	 * @throws PrologException
	 */
	protected void processLinkType(Term uuidTerm, Term nameTerm) throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		String typeName = getSafeString(nameTerm, "Type name");
		TraceLinkType type = null;
		if (uuid != null)
		{
			type = cache.getTraceLinkType(uuid);
		}
		if (type == null)
		{
			// type doesn't exist -> create it
			try
			{
				type = repository.getTypeManager().createLinkType(typeName);
			}
			catch (IllegalArgumentException e)
			{
				logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				return;
			}
			if (uuid != null)
			{
				type.setUuid(uuid);
			}
			queue.add(type);
			cache.register(type);
			logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format(
					"Created trace link type '%s' with UUID %s", type.getName(), type.getUuid())));
		}
	}

	/**
	 * @param uuidTerm
	 * @throws PrologException
	 */
	protected void processRemoveArtefact(Term uuidTerm) throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		TraceableArtefact artefact = cache.getArtefact(uuid);
		if (artefact == null)
		{
			logger.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, String.format(
					"Unable to find traceable artefact with UUID %s", uuid)));
			return;
		}
		queue.remove(artefact);
		logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format(
				"Removed traceable artefact '%s' with UUID %s", artefact.getName(), artefact.getUuid())));
	}

	/**
	 * @param uuidTerm
	 * @throws PrologException
	 */
	protected void processRemoveLink(Term uuidTerm) throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		TraceLink link = cache.getLink(uuid);
		if (link == null)
		{
			logger.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, String.format(
					"Unable to find trace link with UUID %s", uuid)));
			return;
		}
		queue.remove(link);
		logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format("Removed trace link '%s' with UUID %s",
				link.getName(), link.getUuid())));
	}

	/**
	 * @param uuidTerm
	 * @param nameTerm
	 * @param typeTerm
	 * @param uriTerm
	 * @throws PrologException
	 */
	protected void processUpdateArtefact(Term uuidTerm, Term nameTerm, Term typeTerm, Term uriTerm)
			throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		String typeUuid = getUUID(typeTerm);

		TraceableArtefactType type = cache.getTraceableArtefactType(typeUuid);
		if (type == null)
		{
			logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
					"No traceable artefact type found with uuid: %s", typeUuid)));
			return;
		}

		String name = getSafeString(nameTerm, "Artefact name");
		URI uri = null;
		if (uriTerm instanceof AtomTerm)
		{
			try
			{
				uri = new URI(((AtomTerm) uriTerm).value);
			}
			catch (URISyntaxException e)
			{
				logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
						"Invalid URI '%s' for artefact %s (%s)", ((AtomTerm) uriTerm).value, name, uuid), e));
			}
		}
		else
		{
			PrologException.typeError(TermConstants.atomAtom, uriTerm);
		}

		TraceableArtefact artefact = cache.getArtefact(uuid);
		if (artefact == null)
		{
			artefact = repository.getItemManager().createTraceableArtefact(type, name);
			if (uuid != null)
			{
				artefact.setUuid(uuid);
			}
			queue.add(artefact);
			cache.register(artefact);
			logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format("Created new artefact %s (%s)",
					artefact.getName(), artefact.getUuid())));
		}
		else
		{
			artefact.setName(name);
			artefact.setType(type);
			queue.update(artefact);
			logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format("Updated artefact %s (%s)", artefact
					.getName(), artefact.getUuid())));
		}
		artefact.setResourceURI(uri);
	}

	/**
	 * @param uuidTerm
	 * @param nameTerm
	 * @param typeTerm
	 * @param sourcesTerm
	 * @param targetsTerm
	 * @throws PrologException
	 */
	protected void processCreateLink(Term uuidTerm, Term nameTerm, Term typeTerm, Term sourcesTerm, Term targetsTerm)
			throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		String typeUuid = getUUID(typeTerm);
		TraceLinkType type = cache.getTraceLinkType(typeUuid);
		if (type == null)
		{
			logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
					"No trace link type found with uuid: %s", typeUuid)));
			return;
		}

		String name = getSafeString(nameTerm, "Link name");

		TraceableArtefact[] sources = getTraceableArtefact(sourcesTerm);
		TraceableArtefact[] targets = getTraceableArtefact(targetsTerm);

		TraceLink link = cache.getLink(uuid);
		if (link == null)
		{
			link = repository.getItemManager().createTraceLink(sources, targets, type);
			if (uuid != null)
			{
				link.setUuid(uuid);
			}
			if (name != null && name.length() > 0)
			{
				link.setName(name);
			}
			queue.add(link);
			logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format("Created new trace link %s (%s)",
					link.getUuid(), link.getName())));
		}
		else
		{
			// check if identical
			boolean identical = true;
			if (!link.getName().equals(name))
			{
				identical = false;
			}
			else if (link.getType() != type)
			{
				identical = false;
			}
			else if (!link.getSources().containsAll(Arrays.asList(sources)))
			{
				identical = false;
			}
			else if (!link.getTargets().containsAll(Arrays.asList(targets)))
			{
				identical = false;
			}

			if (!identical)
			{
				logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
						"Cannot update existing trace link %s (%s)", link.getUuid(), link.getName())));
			}
			else
			{
				logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format(
						"Ignoring existing trace link %s (%s)", link.getUuid(), link.getName())));
			}
		}
	}

	/**
	 * @param term
	 * @return
	 * @throws PrologException
	 */
	protected TraceableArtefact[] getTraceableArtefact(Term term) throws PrologException
	{
		List<TraceableArtefact> result = new ArrayList<TraceableArtefact>();
		if (!CompoundTerm.isListPair(term))
		{
			PrologException.typeError(TermConstants.listAtom, term);
		}
		List<Term> termCollection = new ArrayList<Term>();
		CompoundTerm.toCollection(term, termCollection);
		for (Term item : termCollection)
		{
			String uuid = getUUID(item);
			TraceableArtefact artefact = cache.getArtefact(uuid);
			if (artefact != null)
			{
				result.add(artefact);
			}
			else
			{
				logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
						"Cannot find artefact with uuid %s", uuid)));
			}
		}
		return result.toArray(new TraceableArtefact[result.size()]);
	}

	protected void processProperties(Term uuidTerm, Term keyTerm, Term valueTerm) throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		if (queue.isRemoved(uuid))
		{
			logger.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, String.format(
					"ATF element with uuid %s has been removed", uuid)));
			return;
		}
		Augmentable aug = cache.get(uuid);
		if (aug == null)
		{
			logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
					"Cannot find an ATF element with uuid %s", uuid)));
			return;
		}

		String key = getSafeString(keyTerm, "Property key");
		String value = getSafeString(valueTerm, "Property value");
		aug.getProperties().put(key, value);
		queue.update(aug);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.runtime.ILogListener#logging(org.eclipse.core.runtime
	 * .IStatus, java.lang.String)
	 */
	public void logging(IStatus status, String plugin)
	{
		if (status.getSeverity() == IStatus.ERROR && status.getPlugin().equals(Activator.PLUGIN_ID))
		{
			hasErrors = true;
		}
	}
}
