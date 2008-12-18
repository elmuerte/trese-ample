/* !! LICENSE PENDING!!
 * 
 * Copyright (C) 2008 TRESE; University of Twente
 */
package trese.arch.tracing.conversion;

import edu.uci.isr.xarch.IXArch;
import edu.uci.isr.xarch.instance.IDescription;
import edu.uci.isr.xarch.instance.IPoint;
import edu.uci.isr.xarch.instance.IXMLLink;
import edu.uci.isr.xarch.types.IArchStructure;
import edu.uci.isr.xarch.types.IArchTypes;
import edu.uci.isr.xarch.types.IComponent;
import edu.uci.isr.xarch.types.IComponentType;
import edu.uci.isr.xarch.types.IConnector;
import edu.uci.isr.xarch.types.IConnectorType;
import edu.uci.isr.xarch.types.IInterface;
import edu.uci.isr.xarch.types.IInterfaceType;
import edu.uci.isr.xarch.types.ILink;
import edu.uci.isr.xarch.types.ISignature;
import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.graph.Node;
import groove.view.aspect.AspectGraph;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Convert a xADL(2.0) model to a Graph used by the groove transformation rules
 * 
 * @author Michiel Hendriks
 */
public class XADL2Graph
{
	/**
	 * Convert the provided xADL structure to a Groove Graph
	 * 
	 * @param arch
	 * @return A groove graph
	 * @throws ConversionException
	 *             thrown when an error was encountered during conversion
	 */
	public static final AspectGraph convert(IXArch arch) throws ConversionException
	{
		XADL2Graph converter = new XADL2Graph(arch);
		return converter.internalConvert();
	}

	/**
	 * Create a string value label
	 * 
	 * @param value
	 * @return
	 */
	public static final Label createStringLabel(String value)
	{
		return DefaultLabel.createLabel(String.format("string:\"%s\"", value));
	}

	/**
	 * The architecture to convert
	 */
	protected IXArch arch;

	/**
	 * The graph going to be constructed. This will be converted to an
	 * AspectGraph at the end.
	 */
	protected DefaultGraph graph;

	/**
	 * A map from an xarchinstance:id to graph node
	 */
	protected Map<String, Node> idMap;

	/**
	 * @param architecture
	 */
	protected XADL2Graph(IXArch architecture)
	{
		arch = architecture;
		graph = new DefaultGraph();
		idMap = new HashMap<String, Node>();
	}

	/**
	 * Performs the actual conversion
	 * 
	 * @param arch
	 * @return
	 * @throws ConversionException
	 */
	protected AspectGraph internalConvert() throws ConversionException
	{
		createTypeNodes();
		createStructureNodes();
		return AspectGraph.getFactory().fromPlainGraph(graph);
	}

	/**
	 * 
	 */
	protected void createTypeNodes() throws ConversionException
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
	protected Node createDefaultNode(String id, IDescription description)
	{
		Node node = graph.addNode();
		idMap.put(id, node);

		// the "id" value
		Node valueNode = graph.addNode();
		graph.addEdge(valueNode, createStringLabel(id), valueNode);
		graph.addEdge(node, GraphConstants.ATTR_ID, valueNode);

		if (description != null && description.getValue() != null && !description.getValue().isEmpty())
		{
			// the "description" value
			valueNode = graph.addNode();
			graph.addEdge(valueNode, createStringLabel(description.getValue()), valueNode);
			graph.addEdge(node, GraphConstants.ATTR_DESCRIPTION, valueNode);
		}
		return node;
	}

	/**
	 * Create all nodes for the interface types declared in the IArchtTypes
	 * structure
	 * 
	 * @param types
	 */
	protected void createInterfaceTypeNodes(IArchTypes types) throws ConversionException
	{
		for (Object o : types.getAllInterfaceTypes())
		{
			if (o instanceof IInterfaceType)
			{
				createInterfaceTypeNode((IInterfaceType) o);
			}
		}
	}

	/**
	 * Create a node for a given interface type
	 * 
	 * @param interfaceType
	 */
	protected void createInterfaceTypeNode(IInterfaceType interfaceType) throws ConversionException
	{
		Node node = createDefaultNode(interfaceType.getId(), interfaceType.getDescription());
		graph.addEdge(node, GraphConstants.NODE_INTERFACE_TYPE, node);
	}

	/**
	 * Create all nodes for the component types declared in the IArchtTypes
	 * structure
	 * 
	 * @param types
	 */
	protected void createComponentTypeNodes(IArchTypes types) throws ConversionException
	{
		for (Object o : types.getAllComponentTypes())
		{
			if (o instanceof IComponentType)
			{
				createComponentTypeNode((IComponentType) o);
			}
		}
	}

	/**
	 * @param compType
	 */
	protected void createComponentTypeNode(IComponentType compType) throws ConversionException
	{
		Node node = createDefaultNode(compType.getId(), compType.getDescription());
		graph.addEdge(node, GraphConstants.NODE_COMPONENT_TYPE, node);

		for (Object o : compType.getAllSignatures())
		{
			if (o instanceof ISignature)
			{
				createSignatureNode((ISignature) o, node);
			}
		}

		// TODO subarch
	}

	/**
	 * @param sign
	 * @param ownerNode
	 */
	protected void createSignatureNode(ISignature sign, Node ownerNode) throws ConversionException
	{
		Node node = createDefaultNode(sign.getId(), sign.getDescription());
		graph.addEdge(node, GraphConstants.NODE_SIGNATURE, node);

		graph.addEdge(ownerNode, GraphConstants.EDGE_SIGNATURE, node);

		// type
		Node typeNode = resolveXMLinkToNode(sign.getType());
		if (typeNode != null)
		{
			graph.addEdge(node, GraphConstants.EDGE_TYPE, typeNode);
		}

		// TODO direction
		// TODO service type
	}

	/**
	 * Create all nodes for the connector types declared in the IArchtTypes
	 * structure
	 * 
	 * @param types
	 */
	protected void createConnectorTypeNodes(IArchTypes types) throws ConversionException
	{
		for (Object o : types.getAllConnectorTypes())
		{
			if (o instanceof IConnectorType)
			{
				createConnectorTypeNode((IConnectorType) o);
			}
		}
	}

	/**
	 * @param compType
	 */
	protected void createConnectorTypeNode(IConnectorType compType) throws ConversionException
	{
		Node node = createDefaultNode(compType.getId(), compType.getDescription());
		graph.addEdge(node, GraphConstants.NODE_CONNECTOR_TYPE, node);

		for (Object o : compType.getAllSignatures())
		{
			if (o instanceof ISignature)
			{
				createSignatureNode((ISignature) o, node);
			}
		}

		// TODO subarch
	}

	/**
	 * Create the structure nodes
	 */
	protected void createStructureNodes() throws ConversionException
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
	protected void createStructureNode(IArchStructure arch) throws ConversionException
	{
		Node node = createDefaultNode(arch.getId(), arch.getDescription());
		graph.addEdge(node, GraphConstants.NODE_ARCH_STRUCTURE, node);

		for (Object o : arch.getAllComponents())
		{
			if (o instanceof IComponent)
			{
				createComponentNode((IComponent) o, node);
			}
		}
		for (Object o : arch.getAllConnectors())
		{
			if (o instanceof IConnector)
			{
				createConnectorNode((IConnector) o, node);
			}
		}
		for (Object o : arch.getAllLinks())
		{
			if (o instanceof ILink)
			{
				createLinkNode((ILink) o, node);
			}
		}
		// TODO groups
	}

	/**
	 * @param comp
	 * @throws ConversionException
	 */
	protected void createComponentNode(IComponent comp, Node parentNode) throws ConversionException
	{
		Node node = createDefaultNode(comp.getId(), comp.getDescription());
		graph.addEdge(node, GraphConstants.NODE_COMPONENT, node);

		graph.addEdge(parentNode, GraphConstants.EDGE_ELEMENT, node);

		// type
		Node typeNode = resolveXMLinkToNode(comp.getType());
		if (typeNode != null)
		{
			graph.addEdge(node, GraphConstants.EDGE_TYPE, typeNode);
		}

		for (Object o : comp.getAllInterfaces())
		{
			if (o instanceof IInterface)
			{
				createInterfaceNode((IInterface) o, node);
			}
		}
	}

	/**
	 * @param o
	 * @param node
	 * @throws ConversionException
	 */
	protected void createInterfaceNode(IInterface iface, Node ownerNode) throws ConversionException
	{
		Node node = createDefaultNode(iface.getId(), iface.getDescription());
		graph.addEdge(node, GraphConstants.NODE_INTERFACE, node);

		graph.addEdge(ownerNode, GraphConstants.EDGE_INTERFACE, node);

		// type
		Node typeNode = resolveXMLinkToNode(iface.getType());
		if (typeNode != null)
		{
			graph.addEdge(node, GraphConstants.EDGE_TYPE, typeNode);
		}

		// signature
		typeNode = resolveXMLinkToNode(iface.getSignature());
		if (typeNode != null)
		{
			graph.addEdge(node, GraphConstants.EDGE_SIGNATURE, typeNode);
		}

		// TODO direction
	}

	/**
	 * @param conn
	 * @throws ConversionException
	 */
	protected void createConnectorNode(IConnector conn, Node parentNode) throws ConversionException
	{
		Node node = createDefaultNode(conn.getId(), conn.getDescription());
		graph.addEdge(node, GraphConstants.NODE_CONNECTOR, node);

		graph.addEdge(parentNode, GraphConstants.EDGE_ELEMENT, node);

		// type
		Node typeNode = resolveXMLinkToNode(conn.getType());
		if (typeNode != null)
		{
			graph.addEdge(node, GraphConstants.EDGE_TYPE, typeNode);
		}

		for (Object o : conn.getAllInterfaces())
		{
			if (o instanceof IInterface)
			{
				createInterfaceNode((IInterface) o, node);
			}
		}
	}

	/**
	 * @param link
	 * @throws ConversionException
	 */
	protected void createLinkNode(ILink link, Node parentNode) throws ConversionException
	{
		Node node = createDefaultNode(link.getId(), link.getDescription());
		graph.addEdge(node, GraphConstants.NODE_LINK, node);

		graph.addEdge(parentNode, GraphConstants.EDGE_ELEMENT, node);

		for (Object o : link.getAllPoints())
		{
			if (o instanceof IPoint)
			{
				IPoint point = (IPoint) o;
				Node linkTo = resolveXMLinkToNode(point.getAnchorOnInterface());
				graph.addEdge(node, GraphConstants.EDGE_LINK, linkTo);
			}
		}
	}

	/**
	 * @param type
	 * @return
	 */
	protected Node resolveXMLinkToNode(IXMLLink type) throws ConversionException
	{
		if (type == null)
		{
			return null;
		}
		try
		{
			URI uri = new URI(type.getHref());
			if (!uri.getPath().isEmpty())
			{
				throw new ConversionException(String.format("Can not handle cross document references (%s)", uri
						.toString()));
			}
			Node result = idMap.get(uri.getFragment());
			if (result == null)
			{
				throw new ConversionException(String.format("Unable to find a node for id '%s'", uri.toString()));
			}
			return result;
		}
		catch (URISyntaxException e)
		{
			throw new ConversionException(e);
		}
	}
}
