/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceLink;
import net.ample.tracing.core.TraceLinkType;
import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.core.TraceableArtefactType;
import net.ample.tracing.core.query.Constraint;
import net.ample.tracing.core.query.Query;

/**
 * Convert traceable artifacts and links to prolog facts. Generated prolog
 * facts:
 * 
 * <pre>
 * traceable_artefact_type(UUID, Name).
 * traceable_artefact(UUID, Name, TypeUUID, ResourceUI).
 * trace_link_type(UUID, Name).
 * trace_link(UUID, Name, TypeUUID, [SourceUUIDs], [TargetUUIDs]).
 * </pre>
 * 
 * @author Michiel Hendriks
 */
public class PrologFactGenerator
{
	/**
	 * The repository to use as source
	 */
	protected RepositoryManager repository;

	/**
	 * Where to write the prolog facts to
	 */
	protected Writer output;

	/**
	 * The constraint to satisfy for artefacts. When null all artefacts are
	 * included.
	 */
	protected Constraint artefactConstraint;

	/**
	 * The constraint to satisfy for links. When null all links are included.
	 */
	protected Constraint linkConstraint;

	/**
	 * Cache to make sure artefacts are only exported once
	 */
	protected Set<TraceableArtefact> visitedArtefacts;

	/**
	 * Cache to make sure links are only exported once
	 */
	protected Set<TraceLink> visitedLinks;

	/**
	 * Cache of already exported artefact types
	 */
	protected Set<TraceableArtefactType> visitedArtefactTypes;

	/**
	 * Cache of exported trace link types
	 */
	protected Set<TraceLinkType> visitedLinkTypes;

	public PrologFactGenerator(RepositoryManager repo, Writer out)
	{
		if (out == null)
		{
			throw new NullPointerException("RepositoryManager cannot be null");
		}
		if (out == null)
		{
			throw new NullPointerException("Output cannot be null");
		}
		repository = repo;
		output = out;
		visitedArtefacts = new HashSet<TraceableArtefact>();
		visitedLinks = new HashSet<TraceLink>();
		visitedArtefactTypes = new HashSet<TraceableArtefactType>();
		visitedLinkTypes = new HashSet<TraceLinkType>();
	}

	/**
	 * @param value
	 *            the artefactConstraint to set
	 */
	public void setArtefactConstraint(Constraint value)
	{
		artefactConstraint = value;
	}

	/**
	 * @return the artefactConstraint
	 */
	public Constraint getArtefactConstraint()
	{
		return artefactConstraint;
	}

	/**
	 * @param value
	 *            the linkConstraint to set
	 */
	public void setLinkConstraint(Constraint value)
	{
		linkConstraint = value;
	}

	/**
	 * Generate the facts
	 * 
	 * @throws IOException
	 */
	public void generate() throws IOException
	{
		writeHeader();
		Collection<TraceableArtefact> artefacts;
		if (artefactConstraint == null)
		{
			artefacts = repository.getRepository().getArtefacts();
		}
		else
		{
			Query<TraceableArtefact> query = repository.getQueryManager().queryOnArtefacts();
			query.add(artefactConstraint);
			artefacts = query.execute();
		}
		for (TraceableArtefact artefact : artefacts)
		{
			generateArtefact(artefact);
		}
	}

	/**
	 * @throws IOException
	 * 
	 */
	protected void writeHeader() throws IOException
	{
		output.write("% Data format: \n");
		output.write("% traceable_artefact_type(UUID, Name).\n");
		output.write("% traceable_artefact(UUID, Name, TypeUUID, ResourceUI).\n");
		output.write("% trace_link_type(UUID, Name).\n");
		output.write("% trace_link(UUID, Name, TypeUUID, [SourceUUIDs], [TargetUUIDs]).\n");
		output.write("\n");
		output.write(":-discontiguous(traceable_artefact_type/2).\n");
		output.write(":-discontiguous(traceable_artefact/4).\n");
		output.write(":-dynamic(traceable_artefact/4).\n");
		output.write(":-discontiguous(trace_link_type/2).\n");
		output.write(":-discontiguous(trace_link/5).\n");
		output.write(":-dynamic(trace_link/5).\n");
		output.write("\n");
	}

	/**
	 * @param artefact
	 * @param skipConstraint
	 *            If true skip constraint checking
	 * @throws IOException
	 */
	protected void generateArtefact(TraceableArtefact artefact) throws IOException
	{
		if (visitedArtefacts.contains(artefact))
		{
			return;
		}
		visitedArtefacts.add(artefact);

		generateArtefactType(artefact.getType());

		output.write("traceable_artefact( '");
		output.write(artefact.getUuid());
		output.write("', '");
		output.write(artefact.getName());
		output.write("', '");
		output.write(artefact.getType().getUuid());
		output.write("', '");
		if (artefact.getResourceURI() != null)
		{
			output.write(artefact.getResourceURI().toString());
		}
		output.write("').\n");

		Collection<TraceLink> links = artefact.getOutgoingLinks();
		for (TraceLink link : links)
		{
			generateLink(link);
		}
	}

	/**
	 * @param type
	 * @throws IOException
	 */
	protected void generateArtefactType(TraceableArtefactType type) throws IOException
	{
		if (visitedArtefactTypes.contains(type))
		{
			return;
		}
		visitedArtefactTypes.add(type);

		output.write("traceable_artefact_type( '");
		output.write(type.getUuid());
		output.write("', '");
		output.write(type.getName());
		output.write("').\n");
	}

	/**
	 * @param link
	 * @throws IOException
	 */
	protected void generateLink(TraceLink link) throws IOException
	{
		if (visitedLinks.contains(link))
		{
			return;
		}
		visitedLinks.add(link);
		if (linkConstraint != null)
		{
			if (!linkConstraint.isSatisfied(link))
			{
				return;
			}
		}

		List<TraceableArtefact> sources = filterArtefacts(link.getSources());
		List<TraceableArtefact> targets = filterArtefacts(link.getTargets());

		if (sources.isEmpty() || targets.isEmpty())
		{
			return;
		}

		generateLinkType(link.getType());

		output.write("trace_link( '");
		output.write(link.getUuid());
		output.write("', '");
		output.write(String.format("%s", link.getName()));
		output.write("', '");
		output.write(link.getType().getUuid());
		output.write("', [");
		boolean first = true;
		for (TraceableArtefact artefact : sources)
		{
			if (!first)
			{
				output.write(", ");
			}
			first = false;
			output.write('\'');
			output.write(artefact.getUuid());
			output.write('\'');
		}
		output.write("], [");
		first = true;
		for (TraceableArtefact artefact : targets)
		{
			if (!first)
			{
				output.write(", ");
			}
			first = false;
			output.write('\'');
			output.write(artefact.getUuid());
			output.write('\'');
		}
		output.write("]).\n");
	}

	/**
	 * Filters the list of elements to those that should be included in the
	 * export for a link
	 * 
	 * @param elements
	 * @return
	 * @throws IOException
	 */
	protected List<TraceableArtefact> filterArtefacts(Collection<TraceableArtefact> elements) throws IOException
	{
		List<TraceableArtefact> result = new ArrayList<TraceableArtefact>();
		for (TraceableArtefact element : elements)
		{
			if (artefactConstraint != null)
			{
				if (!artefactConstraint.isSatisfied(element))
				{
					continue;
				}
			}
			generateArtefact(element);
			result.add(element);
		}
		return result;
	}

	/**
	 * @param type
	 * @throws IOException
	 */
	protected void generateLinkType(TraceLinkType type) throws IOException
	{
		if (visitedLinkTypes.contains(type))
		{
			return;
		}
		visitedLinkTypes.add(type);

		output.write("trace_link_type( '");
		output.write(type.getUuid());
		output.write("', '");
		output.write(type.getName());
		output.write("').\n");
	}
}
