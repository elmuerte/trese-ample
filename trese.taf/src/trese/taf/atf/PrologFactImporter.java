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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import net.ample.tracing.core.Augmentable;
import net.ample.tracing.core.Persistable;
import net.ample.tracing.core.PersistenceManager;
import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceLink;
import net.ample.tracing.core.TraceLinkType;
import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.core.TraceableArtefactType;
import net.ample.tracing.core.query.Constraints;
import net.ample.tracing.core.query.Query;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import trese.taf.Activator;

/**
 * Import artefacts/links/types from a prolog fact database.
 * 
 * To delete artefacts use the predicate delete_traceable_artefact(UUID), to
 * delete links use the predicate delete_trace_link(UUID). When an artefact/link
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
 * trace_link_type/2 predicates. Removing or updating of artefacts is not
 * possible.
 * 
 * @author Michiel Hendriks
 */
public class PrologFactImporter implements ILogListener
{
	// Note: this importer contains a lot of hacks to work around the terrible
	// implementation of the repository. This importer performs it's own caching
	// and transaction handling.

	/**
	 * Used to workaround the useless ATF persistence manager
	 * 
	 * @author Michiel Hendriks
	 */
	public enum RepoActionType
	{
		ADD, UPDATE, REMOVE
	}

	/**
	 * Used to workaround the useless ATF persistence manager
	 * 
	 * @author Michiel Hendriks
	 */
	public class RepositoryAction
	{
		public RepoActionType action;
		public Persistable obj;

		public RepositoryAction(RepoActionType action, Persistable obj)
		{
			super();
			this.action = action;
			this.obj = obj;
		}
	}

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
	protected Map<String, TraceableArtefactType> artTypes;
	protected Map<String, TraceLinkType> linkTypes;
	protected Map<String, TraceableArtefact> arts;
	protected Map<String, TraceLink> links;
	protected Set<String> removed;

	// Queue because ATF does not handle query() during write operations on the
	// repository (which actually makes the whole transaction system completely
	// useless).
	protected Queue<RepositoryAction> actionQueue;

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
		new PrologTextLoader(state, data);
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

		artTypes = new HashMap<String, TraceableArtefactType>();
		linkTypes = new HashMap<String, TraceLinkType>();
		arts = new HashMap<String, TraceableArtefact>();
		links = new HashMap<String, TraceLink>();
		removed = new HashSet<String>();
		actionQueue = new LinkedList<RepositoryAction>();

		try
		{
			logger.addLogListener(this);

			// First process types
			monitor.subTask("Processing artefact types");
			processPredicates(state.getModule(), CompoundTermTag.get("traceable_artefact_type", 2), new FactImporter() {
				public void processFact(Term[] args) throws PrologException
				{
					processArtefactType(args[0], args[1]);
				}
			});
			monitor.worked(1);

			monitor.subTask("Processing link types");
			processPredicates(state.getModule(), CompoundTermTag.get("trace_link_type", 2), new FactImporter() {
				public void processFact(Term[] args) throws PrologException
				{
					processLinkType(args[0], args[1]);
				}
			});
			monitor.worked(1);

			// Process removals

			monitor.subTask("Processing artefact removals");
			processPredicates(state.getModule(), CompoundTermTag.get("remove_traceable_artefact_type", 1),
					new FactImporter() {
						public void processFact(Term[] args) throws PrologException
						{
							processRemoveArtefact(args[0]);
						}
					});
			monitor.worked(1);

			monitor.subTask("Processing link removals");
			processPredicates(state.getModule(), CompoundTermTag.get("remove_trace_link_type", 1), new FactImporter() {
				public void processFact(Term[] args) throws PrologException
				{
					processRemoveLink(args[0]);
				}
			});
			monitor.worked(1);

			// Process updates/additions

			monitor.subTask("Processing artefact additions/updates");
			processPredicates(state.getModule(), CompoundTermTag.get("traceable_artefact", 4), new FactImporter() {
				public void processFact(Term[] args) throws PrologException
				{
					processUpdateArtefact(args[0], args[1], args[2], args[3]);
				}
			});
			monitor.worked(1);

			monitor.subTask("Processing link additions");
			processPredicates(state.getModule(), CompoundTermTag.get("trace_link", 5), new FactImporter() {
				public void processFact(Term[] args) throws PrologException
				{
					processCreateLink(args[0], args[1], args[2], args[3], args[4]);
				}
			});
			monitor.worked(1);

			monitor.subTask("Processing properties");
			processPredicates(state.getModule(), CompoundTermTag.get("atf_element_property", 3), new FactImporter() {
				public void processFact(Term[] args) throws PrologException
				{
					processProperties(args[0], args[1], args[2]);
				}
			});
			monitor.worked(1);

			monitor.subTask("Commiting changes");
			if (!hasErrors)
			{
				PersistenceManager perman = repository.getPersistenceManager();
				perman.begin();
				for (RepositoryAction act : actionQueue)
				{
					switch (act.action)
					{
						case ADD:
							perman.add(act.obj);
							break;
						case REMOVE:
							perman.remove(act.obj);
							break;
						case UPDATE:
							perman.update(act.obj);
							break;
					}
				}
				perman.commit();
			}
			monitor.worked(2);
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
			artTypes = null;
			linkTypes = null;
			actionQueue = null;
			arts = null;
			links = null;
			removed = null;
			monitor.done();
		}
		return !hasErrors;
	}

	/**
	 * @param tag
	 * @param importer
	 */
	protected void processPredicates(Module module, CompoundTermTag tag, FactImporter importer)
	{
		Predicate pred = module.getDefinedPredicate(tag);
		if (pred != null)
		{
			// Clause = CompoundTerm -> args[0] = CompoundTerm -> args = data
			for (Term term : pred.getClauses())
			{
				if (!(term instanceof CompoundTerm))
				{
					continue;
				}
				CompoundTerm ct = (CompoundTerm) term;
				if (!TermConstants.clauseTag.equals(ct.tag))
				{
					// TODO: not a clause!?
					continue;
				}
				if (!TermConstants.trueAtom.equals(ct.args[1]))
				{
					// TODO: not a fact
					continue;
				}
				if (!(ct.args[0] instanceof CompoundTerm))
				{
					continue;
				}
				ct = (CompoundTerm) ct.args[0];
				if (!tag.equals(ct.tag))
				{
					// TODO: not the correct tag;
					continue;
				}
				try
				{
					importer.processFact(ct.args);
				}
				catch (PrologException e)
				{
					logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				}
			}
		}
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
	 * @param uuidTerm
	 * @param nameTerm
	 * @throws PrologException
	 */
	protected void processArtefactType(Term uuidTerm, Term nameTerm) throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		String typeName = null;
		if (nameTerm instanceof AtomTerm)
		{
			typeName = ((AtomTerm) nameTerm).value;
		}
		else
		{
			PrologException.typeError(TermConstants.atomAtom, nameTerm);
		}
		TraceableArtefactType type = null;
		if (uuid != null)
		{
			type = getTraceableArtefactType(uuid);
		}
		if (type == null)
		{
			// type doesn't exist -> create it
			type = repository.getTypeManager().createArtefactType(typeName);
			if (uuid != null)
			{
				type.setUuid(uuid);
			}
			actionQueue.add(new RepositoryAction(RepoActionType.ADD, type));
			artTypes.put(uuid, type);
			logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format(
					"Created traceable artefact type '%s' with UUID %s", type.getName(), type.getUuid())));
		}
	}

	/**
	 * @param uuid
	 * @return
	 */
	protected TraceableArtefactType getTraceableArtefactType(String uuid)
	{
		if (artTypes.containsKey(uuid))
		{
			return artTypes.get(uuid);
		}
		if (uuid == null)
		{
			return null;
		}
		Query<TraceableArtefactType> q = repository.getQueryManager().queryOnArtefactTypes();
		q.add(Constraints.hasUuid(uuid));
		TraceableArtefactType res = q.executeUnique();
		artTypes.put(uuid, res);
		return res;
	}

	/**
	 * @param term
	 * @param term2
	 * @throws PrologException
	 */
	protected void processLinkType(Term uuidTerm, Term nameTerm) throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		String typeName = null;
		TraceLinkType type = null;
		if (nameTerm instanceof AtomTerm)
		{
			typeName = ((AtomTerm) nameTerm).value;
		}
		else
		{
			PrologException.typeError(TermConstants.atomAtom, nameTerm);
		}
		if (uuid != null)
		{
			type = getTraceLinkType(uuid);
		}
		if (type == null)
		{
			// type doesn't exist -> create it
			type = repository.getTypeManager().createLinkType(typeName);
			if (uuid != null)
			{
				type.setUuid(uuid);
			}
			actionQueue.add(new RepositoryAction(RepoActionType.ADD, type));
			linkTypes.put(uuid, type);
			logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format(
					"Created trace link type '%s' with UUID %s", type.getName(), type.getUuid())));
		}
	}

	/**
	 * @param uuid
	 * @return
	 */
	protected TraceLinkType getTraceLinkType(String uuid)
	{
		if (linkTypes.containsKey(uuid))
		{
			return linkTypes.get(uuid);
		}
		if (uuid == null)
		{
			return null;
		}
		Query<TraceLinkType> q = repository.getQueryManager().queryOnLinkTypes();
		q.add(Constraints.hasUuid(uuid));
		TraceLinkType res = q.executeUnique();
		linkTypes.put(uuid, res);
		return res;
	}

	/**
	 * @param uuidTerm
	 * @throws PrologException
	 */
	protected void processRemoveArtefact(Term uuidTerm) throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		TraceableArtefact artefact = getArtefact(uuid);
		if (artefact == null)
		{
			logger.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, String.format(
					"Unable to find traceable artefact with UUID %s", uuid)));
			return;
		}
		actionQueue.add(new RepositoryAction(RepoActionType.REMOVE, artefact));
		removed.add(uuid);
		logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format(
				"Removed traceable artefact '%s' with UUID %s", artefact.getName(), artefact.getUuid())));
	}

	/**
	 * @param uuid
	 * @return
	 */
	protected TraceableArtefact getArtefact(String uuid)
	{
		if (removed.contains(uuid))
		{
			return null;
		}
		if (arts.containsKey(uuid))
		{
			return arts.get(uuid);
		}
		if (uuid == null)
		{
			return null;
		}
		Query<TraceableArtefact> q = repository.getQueryManager().queryOnArtefacts();
		q.add(Constraints.hasUuid(uuid));
		TraceableArtefact res = q.executeUnique();
		arts.put(uuid, res);
		return res;
	}

	/**
	 * @param uuidTerm
	 * @throws PrologException
	 */
	protected void processRemoveLink(Term uuidTerm) throws PrologException
	{
		String uuid = getUUID(uuidTerm);
		TraceLink link = getLink(uuid);
		if (link == null)
		{
			logger.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, String.format(
					"Unable to find trace link with UUID %s", uuid)));
			return;
		}
		actionQueue.add(new RepositoryAction(RepoActionType.REMOVE, link));
		removed.add(uuid);
		logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format("Removed trace link '%s' with UUID %s",
				link.getName(), link.getUuid())));
	}

	/**
	 * @param uuid
	 * @return
	 */
	protected TraceLink getLink(String uuid)
	{
		if (removed.contains(uuid))
		{
			return null;
		}
		if (links.containsKey(uuid))
		{
			return links.get(uuid);
		}
		if (uuid == null)
		{
			return null;
		}
		Query<TraceLink> q = repository.getQueryManager().queryOnLinks();
		q.add(Constraints.hasUuid(uuid));
		TraceLink res = q.executeUnique();
		links.put(uuid, res);
		return res;
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

		TraceableArtefactType type = getTraceableArtefactType(typeUuid);
		if (type == null)
		{
			logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
					"No traceable artefact type found with uuid: %s", typeUuid)));
			return;
		}

		String name = null;
		if (nameTerm instanceof AtomTerm)
		{
			name = ((AtomTerm) nameTerm).value;
		}
		else
		{
			PrologException.typeError(TermConstants.atomAtom, nameTerm);
		}

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

		TraceableArtefact artefact = getArtefact(uuid);
		if (artefact == null)
		{
			artefact = repository.getItemManager().createTraceableArtefact(type, name);
			if (uuid != null)
			{
				artefact.setUuid(uuid);
			}
			actionQueue.add(new RepositoryAction(RepoActionType.ADD, artefact));
			arts.put(uuid, artefact);
			removed.remove(uuid);
			logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format("Created new artefact %s (%s)",
					artefact.getName(), artefact.getUuid())));
		}
		else
		{
			artefact.setName(name);
			artefact.setType(type);
			actionQueue.add(new RepositoryAction(RepoActionType.UPDATE, artefact));
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
		TraceLinkType type = getTraceLinkType(typeUuid);
		if (type == null)
		{
			logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
					"No trace link type found with uuid: %s", typeUuid)));
			return;
		}

		String name = null;
		if (nameTerm instanceof AtomTerm)
		{
			name = ((AtomTerm) nameTerm).value;
		}
		else
		{
			PrologException.typeError(TermConstants.atomAtom, nameTerm);
		}

		TraceLink link = getLink(uuid);
		if (link == null)
		{
			TraceableArtefact[] sources = getTraceableArtefact(sourcesTerm);
			TraceableArtefact[] targets = getTraceableArtefact(targetsTerm);
			link = repository.getItemManager().createTraceLink(sources, targets, type);
			if (uuid != null)
			{
				link.setUuid(uuid);
			}
			if (name != null && name.length() > 0)
			{
				link.setName(name);
			}
			actionQueue.add(new RepositoryAction(RepoActionType.ADD, link));
			removed.remove(uuid);
			logger.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, String.format("Created new trace link %s", link
					.getUuid())));
		}
		else
		{
			logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
					"Cannot update existing trace link %s", link.getUuid())));
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
			TraceableArtefact artefact = getArtefact(uuid);
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
		if (removed.contains(uuid))
		{
			logger.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, String.format(
					"ATF element with uuid %s has been removed", uuid)));
			return;
		}
		// TODO: optimize this
		Augmentable aug = getTraceableArtefactType(uuid);
		if (aug == null)
		{
			aug = getTraceLinkType(uuid);
		}
		if (aug == null)
		{
			aug = getArtefact(uuid);
		}
		if (aug == null)
		{
			aug = getLink(uuid);
		}
		if (aug == null)
		{
			logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format(
					"Cannot find an ATF element with uuid %s", uuid)));
			return;
		}

		String key = null;
		if (keyTerm instanceof AtomTerm)
		{
			key = ((AtomTerm) keyTerm).value;
		}
		else
		{
			PrologException.typeError(TermConstants.atomAtom, keyTerm);
		}
		String value = null;
		if (valueTerm instanceof AtomTerm)
		{
			value = ((AtomTerm) valueTerm).value;
		}
		else
		{
			PrologException.typeError(TermConstants.atomAtom, valueTerm);
		}
		aug.getProperties().put(key, value);
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
