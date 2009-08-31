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
import gnu.prolog.vm.buildins.uuid.Predicate_uuid;

import java.io.Reader;
import java.util.List;
import java.util.UUID;

import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceLink;
import net.ample.tracing.core.TraceLinkType;
import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.core.TraceableArtefactType;
import net.ample.tracing.core.query.Constraints;
import net.ample.tracing.core.query.Query;

import org.eclipse.core.runtime.ILog;
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
 * rules as for the artefacts.
 * 
 * Creation of new types can be done using the traceable_artefact_type/2 and
 * trace_link_type/2 predicates. Removing or updating of artefacts is not
 * possible.
 * 
 * @author Michiel Hendriks
 */
public class PrologFactImporter
{
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

	public PrologFactImporter(RepositoryManager repo, ILog log)
	{
		repository = repo;
		logger = log;
	}

	public boolean importFacts(Reader data)
	{
		PrologTextLoaderState state = new PrologTextLoaderState();
		new PrologTextLoader(state, data);
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

		repository.getPersistenceManager().begin();
		try
		{

			// First process types
			processPredicates(state.getModule(), CompoundTermTag.get("traceable_artefact_type", 2), new FactImporter() {
				public void processFact(Term[] args) throws PrologException
				{
					processArtefactType(args[0], args[1]);
				}
			});
			processPredicates(state.getModule(), CompoundTermTag.get("trace_link_type", 2), new FactImporter() {
				public void processFact(Term[] args) throws PrologException
				{
					processLinkType(args[0], args[1]);
				}
			});

			// Process removeals

			processPredicates(state.getModule(), CompoundTermTag.get("remove_traceable_artefact_type", 1),
					new FactImporter() {
						public void processFact(Term[] args) throws PrologException
						{
							processRemoveArtefact(args[0]);
						}
					});
			processPredicates(state.getModule(), CompoundTermTag.get("remove_trace_link_type", 1), new FactImporter() {
				public void processFact(Term[] args) throws PrologException
				{
					processRemoveLink(args[0]);
				}
			});

			// Process updates/additions

			repository.getPersistenceManager().commit();
		}
		catch (Exception e)
		{
			repository.getPersistenceManager().rollback();
			logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		return true;
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
	 * @param uuidTerm
	 * @param nameTerm
	 * @throws PrologException
	 */
	protected void processArtefactType(Term uuidTerm, Term nameTerm) throws PrologException
	{
		UUID uuid = Predicate_uuid.getUUID(uuidTerm);
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
				type.setUuid(uuid.toString());
			}
			repository.getPersistenceManager().add(type);
			// TODO: notice
		}
	}

	/**
	 * @param uuid
	 * @return
	 */
	protected TraceableArtefactType getTraceableArtefactType(UUID uuid)
	{
		if (uuid == null)
		{
			return null;
		}
		Query<TraceableArtefactType> q = repository.getQueryManager().queryOnArtefactTypes();
		q.add(Constraints.hasUuid(uuid.toString()));
		return q.executeUnique();
	}

	/**
	 * @param term
	 * @param term2
	 * @throws PrologException
	 */
	protected void processLinkType(Term uuidTerm, Term nameTerm) throws PrologException
	{
		UUID uuid = Predicate_uuid.getUUID(uuidTerm);
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
				type.setUuid(uuid.toString());
			}
			repository.getPersistenceManager().add(type);
			// TODO: notice
		}
	}

	/**
	 * @param uuid
	 * @return
	 */
	protected TraceLinkType getTraceLinkType(UUID uuid)
	{
		if (uuid == null)
		{
			return null;
		}
		Query<TraceLinkType> q = repository.getQueryManager().queryOnLinkTypes();
		q.add(Constraints.hasUuid(uuid.toString()));
		return q.executeUnique();
	}

	/**
	 * @param uuidTerm
	 * @throws PrologException
	 */
	protected void processRemoveArtefact(Term uuidTerm) throws PrologException
	{
		UUID uuid = Predicate_uuid.getUUID(uuidTerm);
		TraceableArtefact artefact = getArtefact(uuid);
		if (artefact == null)
		{
			// TODO: warning
			return;
		}
		repository.getPersistenceManager().remove(artefact);
		// TODO: notice
	}

	/**
	 * @param uuid
	 * @return
	 */
	protected TraceableArtefact getArtefact(UUID uuid)
	{
		if (uuid == null)
		{
			return null;
		}
		Query<TraceableArtefact> q = repository.getQueryManager().queryOnArtefacts();
		q.add(Constraints.hasUuid(uuid.toString()));
		return q.executeUnique();
	}

	/**
	 * @param uuidTerm
	 * @throws PrologException
	 */
	protected void processRemoveLink(Term uuidTerm) throws PrologException
	{
		UUID uuid = Predicate_uuid.getUUID(uuidTerm);
		TraceLink link = getLink(uuid);
		if (link == null)
		{
			// TODO: warning
			return;
		}
		repository.getPersistenceManager().remove(link);
		// TODO: notice
	}

	/**
	 * @param uuid
	 * @return
	 */
	protected TraceLink getLink(UUID uuid)
	{
		if (uuid == null)
		{
			return null;
		}
		Query<TraceLink> q = repository.getQueryManager().queryOnLinks();
		q.add(Constraints.hasUuid(uuid.toString()));
		return q.executeUnique();
	}
}
