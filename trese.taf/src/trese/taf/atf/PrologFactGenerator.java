/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import net.ample.tracing.core.Augmentable;
import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceLink;
import net.ample.tracing.core.TraceLinkType;
import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.core.TraceableArtefactType;
import net.ample.tracing.core.query.Constraint;
import net.ample.tracing.core.query.Query;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Convert traceable artifacts and links to prolog facts. Generated prolog
 * facts:
 * 
 * <pre>
 * traceable_artefact_type(UUID, Name).
 * traceable_artefact(UUID, Name, TypeUUID, ResourceURI).
 * trace_link_type(UUID, Name).
 * trace_link(UUID, Name, TypeUUID, [SourceUUIDs], [TargetUUIDs]).
 * atf_element_property(UUID, property, value).
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

	protected boolean exportProperties;

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
	 * @param value
	 *            the exportProperties to set
	 */
	public void setExportProperties(boolean value)
	{
		exportProperties = value;
	}

	/**
	 * @return
	 */
	public boolean getExportProperties()
	{
		return exportProperties;
	}

	/**
	 * Generate the facts
	 * 
	 * @throws IOException
	 */
	public void generate(IProgressMonitor monitor) throws IOException
	{
		if (monitor == null)
		{
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("Exporting artefacts", 10);
		monitor.subTask("Generating header");
		writeHeader();
		monitor.worked(1);

		monitor.subTask("Writing type declarations");
		if (true)
		{
			// write all type declarations of the profile
			// TODO: make configurable?
			writeTypes();
		}
		monitor.worked(1);
		if (monitor.isCanceled())
		{
			return;
		}

		monitor.subTask("Querying artefacts");
		Query<TraceableArtefact> query = repository.getQueryManager().queryOnArtefacts();
		if (artefactConstraint != null)
		{
			query.add(artefactConstraint);
		}
		Collection<TraceableArtefact> lst = query.execute();
		monitor.worked(1);

		monitor.subTask("Exporting artefacts");
		IProgressMonitor submon = new SubProgressMonitor(monitor, 7);
		submon.beginTask("Exporting artefacts", lst.size());
		for (TraceableArtefact artefact : lst)
		{
			if (monitor.isCanceled())
			{
				return;
			}
			generateArtefact(artefact);
			submon.worked(1);
		}
		submon.done();
		monitor.done();
	}

	/**
	 * @throws IOException
	 * 
	 */
	protected void writeHeader() throws IOException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		output.write(String.format("%% %s \n", sdf.format(new Date())));
		output.write("% Data format: \n");
		output.write("% traceable_artefact_type(UUID, Name).\n");
		output.write("% traceable_artefact(UUID, Name, TypeUUID, ResourceURI).\n");
		output.write("% trace_link_type(UUID, Name).\n");
		output.write("% trace_link(UUID, Name, TypeUUID, [SourceUUIDs], [TargetUUIDs]).\n");
		if (exportProperties)
		{
			output.write("% atf_element_property(UUID, property, value).\n");
		}
		output.write("\n");
		output.write(":-multifile(traceable_artefact_type/2).\n");
		output.write(":-discontiguous(traceable_artefact_type/2).\n");
		output.write(":-multifile(traceable_artefact/4).\n");
		output.write(":-discontiguous(traceable_artefact/4).\n");
		output.write(":-dynamic(traceable_artefact/4).\n");
		output.write(":-multifile(trace_link_type/2).\n");
		output.write(":-discontiguous(trace_link_type/2).\n");
		output.write(":-multifile(trace_link/5).\n");
		output.write(":-discontiguous(trace_link/5).\n");
		output.write(":-dynamic(trace_link/5).\n");
		if (exportProperties)
		{
			output.write(":-multifile(atf_element_property/3).\n");
			output.write(":-discontiguous(atf_element_property/3).\n");
			output.write(":-dynamic(atf_element_property/3).\n");
		}
		output.write("\n");
	}

	/**
	 * Write the types to the output
	 * 
	 * @throws IOException
	 */
	protected void writeTypes() throws IOException
	{
		Query<TraceableArtefactType> query = repository.getQueryManager().queryOnArtefactTypes();
		for (TraceableArtefactType type : query.execute())
		{
			generateArtefactType(type);
		}
		output.write("\n");

		Query<TraceLinkType> query2 = repository.getQueryManager().queryOnLinkTypes();
		for (TraceLinkType type : query2.execute())
		{
			generateLinkType(type);
		}
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

		output.write("traceable_artefact('");
		output.write(atomEscape(artefact.getUuid()));
		output.write("', '");
		output.write(atomEscape(artefact.getName()));
		output.write("', '");
		output.write(artefact.getType().getUuid());
		output.write("', '");
		if (artefact.getResourceURI() != null)
		{
			output.write(atomEscape(artefact.getResourceURI().toString()));
		}
		output.write("').\n");

		generateProperties(artefact);

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

		output.write("traceable_artefact_type('");
		output.write(atomEscape(type.getUuid()));
		output.write("', '");
		output.write(atomEscape(type.getName()));
		output.write("').\n");

		generateProperties(type);
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

		output.write("trace_link('");
		output.write(atomEscape(link.getUuid()));
		output.write("', '");
		output.write(atomEscape(link.getName()));
		output.write("', '");
		output.write(atomEscape(link.getType().getUuid()));
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
			output.write(atomEscape(artefact.getUuid()));
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
			output.write(atomEscape(artefact.getUuid()));
			output.write('\'');
		}
		output.write("]).\n");

		generateProperties(link);
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

		output.write("trace_link_type('");
		output.write(atomEscape(type.getUuid()));
		output.write("', '");
		output.write(atomEscape(type.getName()));
		output.write("').\n");

		generateProperties(type);
	}

	protected void generateProperties(Augmentable aug) throws IOException
	{
		if (!exportProperties)
		{
			return;
		}

		for (Entry<String, String> entry : aug.getProperties().entrySet())
		{
			if ("URI".equals(entry.getKey()))
			{
				// this is already included
				continue;
			}
			output.write("atf_element_property('");
			output.write(atomEscape(aug.getUuid()));
			output.write("', '");
			output.write(atomEscape(entry.getKey()));
			output.write("', '");
			output.write(atomEscape(entry.getValue()));
			output.write("').\n");
		}
		output.write("\n");
	}

	/**
	 * @param key
	 * @return
	 */
	protected String atomEscape(String value)
	{
		if (value == null)
		{
			return "";
		}
		return value.replace("\\", "\\\\").replace("'", "\\'");
	}
}
