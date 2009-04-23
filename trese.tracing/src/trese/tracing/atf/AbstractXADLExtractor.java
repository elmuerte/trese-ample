/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing.atf;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.ample.tracing.core.AbstractTraceExtractor;
import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceLinkType;
import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.core.TraceableArtefactType;
import net.ample.tracing.core.TypeManager;
import net.ample.tracing.core.query.Constraints;
import net.ample.tracing.core.query.Query;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import trese.archmodel.groove.ConversionException;
import trese.tracing.PluginActivator;
import edu.uci.isr.xarch.IXArch;
import edu.uci.isr.xarch.IXArchImplementation;
import edu.uci.isr.xarch.XArchParseException;
import edu.uci.isr.xarch.XArchUtils;
import edu.uci.isr.xarch.instance.IDescription;
import edu.uci.isr.xarch.instance.IXMLLink;
import edu.uci.isr.xarch.types.IArchStructure;
import edu.uci.isr.xarch.types.IArchTypes;
import edu.uci.isr.xarch.types.IComponent;
import edu.uci.isr.xarch.types.IComponentType;
import edu.uci.isr.xarch.types.IConnector;
import edu.uci.isr.xarch.types.IConnectorType;
import edu.uci.isr.xarch.types.IInterfaceType;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractXADLExtractor extends AbstractTraceExtractor
{
	public static final UUID TAT_ARCHSTRUCTURE = UUID.fromString("48128e30-2f23-11de-8c30-0800200c9a66");
	public static final UUID TAT_COMPONENT = UUID.fromString("2936e730-2e57-11de-8c30-0800200c9a66");
	public static final UUID TAT_CONNECTOR = UUID.fromString("2936e731-2e57-11de-8c30-0800200c9a66");
	public static final UUID TAT_INTERFACE = UUID.fromString("2936e732-2e57-11de-8c30-0800200c9a66");
	public static final UUID TAT_COMPONENT_TYPE = UUID.fromString("2936e733-2e57-11de-8c30-0800200c9a66");
	public static final UUID TAT_CONNECTOR_TYPE = UUID.fromString("2936e734-2e57-11de-8c30-0800200c9a66");
	public static final UUID TAT_INTERFACE_TYPE = UUID.fromString("2936e735-2e57-11de-8c30-0800200c9a66");

	protected RepositoryManager manager;

	protected Map<UUID, TraceableArtefactType> typeMap;

	/**
	 * A map from an xarchinstance:id to graph node
	 */
	protected Map<String, TraceableArtefact> idMap;

	/**
	 * List of pending artefacts
	 */
	protected Map<TraceableArtefact, Object> pending;

	protected URI uri;

	public void run(RepositoryManager repoMan, IProgressMonitor monitor)
	{
		manager = repoMan;
		IFile xadlFile = selectXADLFile();
		if (xadlFile != null)
		{
			try
			{
				uri = xadlFile.getLocationURI();
				IXArch arch = getXArch(new InputStreamReader(xadlFile.getContents(true)));
				extractArtifacts(arch);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Load the architecture
	 * 
	 * @param source
	 * @return
	 */
	protected IXArch getXArch(Reader source)
	{
		IXArchImplementation impl = XArchUtils.getDefaultXArchImplementation();
		try
		{
			return impl.parse(source);
		}
		catch (XArchParseException e)
		{
			return null;
		}
	}

	protected abstract IFile selectXADLFile();

	/**
	 * this method returns a type of trace link existing in a repository.
	 * 
	 * @param name
	 *            the name of the type of trace link.
	 * @return the trace link type.
	 */
	protected TraceLinkType getTraceLinkType(String name)
	{
		Query<TraceLinkType> query = manager.getQueryManager().queryOnLinkTypes();
		query.add(Constraints.hasName(name));
		return query.executeUnique();
	}

	protected void extractArtifacts(IXArch arch) throws CoreException
	{
		TypeManager typeMan = manager.getTypeManager();
		typeMap = new HashMap<UUID, TraceableArtefactType>();
		typeMap.put(TAT_ARCHSTRUCTURE, typeMan.findArtefactTypeByUuid(TAT_ARCHSTRUCTURE));
		typeMap.put(TAT_COMPONENT, typeMan.findArtefactTypeByUuid(TAT_COMPONENT));
		typeMap.put(TAT_CONNECTOR, typeMan.findArtefactTypeByUuid(TAT_CONNECTOR));
		typeMap.put(TAT_INTERFACE, typeMan.findArtefactTypeByUuid(TAT_INTERFACE));
		typeMap.put(TAT_COMPONENT_TYPE, typeMan.findArtefactTypeByUuid(TAT_COMPONENT_TYPE));
		typeMap.put(TAT_CONNECTOR_TYPE, typeMan.findArtefactTypeByUuid(TAT_CONNECTOR_TYPE));
		typeMap.put(TAT_INTERFACE_TYPE, typeMan.findArtefactTypeByUuid(TAT_INTERFACE_TYPE));

		manager.getPersistenceManager().begin();

		idMap = new HashMap<String, TraceableArtefact>();
		pending = new HashMap<TraceableArtefact, Object>();

		createTypeNodes(arch);
		createStructureNodes(arch);

		// go through the pending items to finish them off

		try
		{
			manager.getPersistenceManager().commit();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	protected void createTypeNodes(IXArch arch) throws CoreException
	{
		for (Object o : arch.getAllObjects())
		{
			if (o instanceof IArchTypes)
			{
				createInterfaceTypeNodes((IArchTypes) o);
				createComponentTypeNodes((IArchTypes) o);
				createConnectorTypeNodes((IArchTypes) o);
			}
		}
	}

	/**
	 * Create a node with the default attributes
	 * 
	 * @param id
	 * @param description
	 * @return
	 */
	protected TraceableArtefact createArtefact(UUID artType, String id, IDescription description)
	{
		String artName = id;
		if (description != null && description.getValue() != null && description.getValue().length() != 0)
		{
			artName = description.getValue();
		}
		TraceableArtefact art = manager.getItemManager().createTraceableArtefact(typeMap.get(artType), artName);
		art.setResourceURI(uri);
		art.getProperties().put("id", id);
		idMap.put(id, art);
		manager.getPersistenceManager().add(art);
		return art;
	}

	/**
	 * Create all nodes for the interface types declared in the IArchtTypes
	 * structure
	 * 
	 * @param types
	 */
	protected void createInterfaceTypeNodes(IArchTypes types) throws CoreException
	{
		for (Object o : types.getAllInterfaceTypes())
		{
			if (o instanceof IInterfaceType)
			{
				IInterfaceType interfaceType = (IInterfaceType) o;
				pending.put(createArtefact(TAT_INTERFACE_TYPE, interfaceType.getId(), interfaceType.getDescription()),
						o);
			}
		}
	}

	/**
	 * Create all nodes for the component types declared in the IArchtTypes
	 * structure
	 * 
	 * @param types
	 */
	protected void createComponentTypeNodes(IArchTypes types) throws CoreException
	{
		for (Object o : types.getAllComponentTypes())
		{
			if (o instanceof IComponentType)
			{
				IComponentType compType = (IComponentType) o;
				pending.put(createArtefact(TAT_COMPONENT_TYPE, compType.getId(), compType.getDescription()), o);
			}
		}
	}

	/**
	 * Create all nodes for the connector types declared in the IArchtTypes
	 * structure
	 * 
	 * @param types
	 */
	protected void createConnectorTypeNodes(IArchTypes types) throws CoreException
	{
		for (Object o : types.getAllConnectorTypes())
		{
			if (o instanceof IConnectorType)
			{
				IConnectorType connType = (IConnectorType) o;
				pending.put(createArtefact(TAT_CONNECTOR_TYPE, connType.getId(), connType.getDescription()), o);
			}
		}
	}

	/**
	 * Create the structure nodes
	 */
	protected void createStructureNodes(IXArch arch) throws CoreException
	{
		for (Object o : arch.getAllObjects())
		{
			if (o instanceof IArchStructure)
			{
				createStructureNode((IArchStructure) o);
			}
		}
	}

	/**
	 * @param arch
	 * @throws ConversionException
	 */
	protected void createStructureNode(IArchStructure arch) throws CoreException
	{
		pending.put(createArtefact(TAT_ARCHSTRUCTURE, arch.getId(), arch.getDescription()), arch);

		for (Object o : arch.getAllComponents())
		{
			if (o instanceof IComponent)
			{
				IComponent comp = (IComponent) o;
				pending.put(createArtefact(TAT_COMPONENT, comp.getId(), comp.getDescription()), o);
			}
		}
		for (Object o : arch.getAllConnectors())
		{
			if (o instanceof IConnector)
			{
				IConnector conn = (IConnector) o;
				pending.put(createArtefact(TAT_CONNECTOR, conn.getId(), conn.getDescription()), o);
			}
		}
	}

	/**
	 * @param type
	 * @return
	 */
	protected TraceableArtefact resolveXMLinkToNode(IXMLLink type) throws CoreException
	{
		if (type == null)
		{
			return null;
		}
		try
		{
			URI uri = new URI(type.getHref());
			if (uri.getPath().length() != 0)
			{
				throw new CoreException(new Status(IStatus.ERROR, PluginActivator.PLUGIN_ID, String.format(
						"Can not handle cross document references (%s)", uri.toString())));
			}
			TraceableArtefact result = idMap.get(uri.getFragment());
			if (result == null)
			{
				throw new CoreException(new Status(IStatus.ERROR, PluginActivator.PLUGIN_ID, String.format(
						"Unable to find a node for id '%s'", uri.toString())));
			}
			return result;
		}
		catch (URISyntaxException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, PluginActivator.PLUGIN_ID, e.getMessage(), e));
		}
	}
}
