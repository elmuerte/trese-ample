/* !! LICENSE PENDING!!
 * 
 * Copyright (C) 2008 TRESE; University of Twente
 */
package trese.arch.tracing.conversion;

import edu.uci.isr.xarch.IXArch;
import edu.uci.isr.xarch.instance.IDescription;
import edu.uci.isr.xarch.instance.IDirection;
import edu.uci.isr.xarch.instance.IDirectionSimpleType;
import edu.uci.isr.xarch.instance.IPoint;
import edu.uci.isr.xarch.instance.IXMLLink;
import edu.uci.isr.xarch.options.IOptional;
import edu.uci.isr.xarch.options.IOptionalComponent;
import edu.uci.isr.xarch.options.IOptionalConnector;
import edu.uci.isr.xarch.options.IOptionalInterface;
import edu.uci.isr.xarch.options.IOptionalLink;
import edu.uci.isr.xarch.options.IOptionalSignature;
import edu.uci.isr.xarch.options.IOptionalSignatureInterfaceMapping;
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
import edu.uci.isr.xarch.types.ISignatureInterfaceMapping;
import edu.uci.isr.xarch.types.ISignatureServiceSimpleType;
import edu.uci.isr.xarch.types.ISignatureServiceType;
import edu.uci.isr.xarch.types.ISubArchitecture;
import edu.uci.isr.xarch.variants.IVariant;
import edu.uci.isr.xarch.variants.IVariantComponentType;
import edu.uci.isr.xarch.variants.IVariantConnectorType;
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
		// FIXME doesn't handle quotes in the strings
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
	 * List of nodes who's full construction is pending (these have been created
	 * by {@link #getNodeById(String)} and are not yet in the {@link #idMap}
	 */
	protected Map<String, Node> pendingNodes;

	/**
	 * @param architecture
	 */
	protected XADL2Graph(IXArch architecture)
	{
		arch = architecture;
		graph = new DefaultGraph();
		idMap = new HashMap<String, Node>();
		pendingNodes = new HashMap<String, Node>();
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
		Node node = getNodeById(id);
		idMap.put(id, node);
		pendingNodes.remove(id);

		if (description != null && description.getValue() != null && !description.getValue().isEmpty())
		{
			// the "description" value
			Node valueNode = graph.addNode();
			graph.addEdge(valueNode, createStringLabel(description.getValue()), valueNode);
			graph.addEdge(node, GraphConstants.ATTR_DESCRIPTION, valueNode);
		}
		return node;
	}

	/**
	 * Get the node by a given id.
	 * 
	 * @param id
	 * @return
	 */
	protected Node getNodeById(String id)
	{
		Node result = idMap.get(id);
		if (result == null)
		{
			result = pendingNodes.get(id);
		}
		if (result == null)
		{
			result = graph.addNode();

			Node valueNode = graph.addNode();
			graph.addEdge(valueNode, createStringLabel(id), valueNode);
			graph.addEdge(result, GraphConstants.ATTR_ID, valueNode);

			pendingNodes.put(id, result);
		}
		return result;
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

		if (compType instanceof IVariantComponentType)
		{
			updateVariantComponentTypeNode((IVariantComponentType) compType, node);
		}

		ISubArchitecture subArch = compType.getSubArchitecture();
		if (subArch != null)
		{
			createSubArchitecture(subArch, node);
		}
	}

	/**
	 * @param compType
	 * @param node
	 * @throws ConversionException
	 */
	protected void updateVariantComponentTypeNode(IVariantComponentType compType, Node node) throws ConversionException
	{
		graph.addEdge(node, GraphConstants.NODE_VARIANT_COMPONENT_TYPE, node);

		for (Object o : compType.getAllVariants())
		{
			if (o instanceof IVariant)
			{
				createVariantNode((IVariant) o, node);
			}
		}
	}

	/**
	 * @param o
	 * @param node
	 * @throws ConversionException
	 */
	protected void createVariantNode(IVariant var, Node parentNode) throws ConversionException
	{
		Node node = graph.addNode();
		graph.addEdge(node, GraphConstants.NODE_VARIANT, node);
		graph.addEdge(parentNode, GraphConstants.EDGE_VARIANT, node);

		// TODO: guard
		Node typeNode = resolveXMLinkToNode(var.getVariantType());
		if (typeNode != null)
		{
			graph.addEdge(node, GraphConstants.EDGE_TYPE, typeNode);
		}
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

		setDirection(node, sign.getDirection());

		ISignatureServiceType servType = sign.getServiceType();
		if (servType != null)
		{
			if (ISignatureServiceSimpleType.ENUM_PROVIDES.equalsIgnoreCase(servType.getValue()))
			{
				graph.addEdge(node, GraphConstants.SERVICE_PROVIDES, node);
			}
			else if (ISignatureServiceSimpleType.ENUM_REQUIRES.equalsIgnoreCase(servType.getValue()))
			{
				graph.addEdge(node, GraphConstants.SERVICE_REQUIRES, node);
			}
		}

		if (sign instanceof IOptionalSignature)
		{
			makeOptional(((IOptionalSignature) sign).getOptional(), node);
		}
	}

	/**
	 * @param node
	 * @param direction
	 */
	protected void setDirection(Node node, IDirection direction)
	{
		if (direction == null)
		{
			return;
		}
		String dir = direction.getValue();
		if (IDirectionSimpleType.ENUM_INOUT.equalsIgnoreCase(dir))
		{
			graph.addEdge(node, GraphConstants.DIRECTION_INOUT, node);
		}
		else if (IDirectionSimpleType.ENUM_OUT.equalsIgnoreCase(dir))
		{
			graph.addEdge(node, GraphConstants.DIRECTION_OUT, node);
		}
		else if (IDirectionSimpleType.ENUM_IN.equalsIgnoreCase(dir))
		{
			graph.addEdge(node, GraphConstants.DIRECTION_IN, node);
		}
		else if (IDirectionSimpleType.ENUM_NONE.equalsIgnoreCase(dir))
		{
			graph.addEdge(node, GraphConstants.DIRECTION_NONE, node);
		}
	}

	/**
	 * @param subArch
	 * @param node
	 * @throws ConversionException
	 */
	protected void createSubArchitecture(ISubArchitecture subArch, Node parentNode) throws ConversionException
	{
		Node node = graph.addNode();
		graph.addEdge(node, GraphConstants.NODE_SUBARCHITECTURE, node);
		graph.addEdge(parentNode, GraphConstants.EDGE_SUBARCHITECTURE, node);

		Node archNode = resolveXMLinkToNode(subArch.getArchStructure());
		if (archNode != null)
		{
			graph.addEdge(node, GraphConstants.EDGE_ARCHITECTURE, archNode);
		}
		for (Object o : subArch.getAllSignatureInterfaceMappings())
		{
			if (o instanceof ISignatureInterfaceMapping)
			{
				createSignatureInterfaceMapping((ISignatureInterfaceMapping) o, node);
			}
		}
	}

	/**
	 * @param o
	 * @param node
	 * @throws ConversionException
	 */
	protected void createSignatureInterfaceMapping(ISignatureInterfaceMapping mapping, Node parentNode)
			throws ConversionException
	{
		Node node = createDefaultNode(mapping.getId(), mapping.getDescription());
		graph.addEdge(node, GraphConstants.NODE_INTERFACEMAPPING, node);
		graph.addEdge(parentNode, GraphConstants.EDGE_MAPPING, node);

		Node iface = resolveXMLinkToNode(mapping.getInnerInterface());
		if (iface != null)
		{
			graph.addEdge(node, GraphConstants.EDGE_INTERFACE, iface);
		}

		iface = resolveXMLinkToNode(mapping.getOuterSignature());
		if (iface != null)
		{
			graph.addEdge(node, GraphConstants.EDGE_SIGNATURE, iface);
		}

		if (mapping instanceof IOptionalSignatureInterfaceMapping)
		{
			makeOptional(((IOptionalSignatureInterfaceMapping) mapping).getOptional(), node);
		}
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

		if (compType instanceof IVariantConnectorType)
		{
			updateVariantConnectorTypeNode((IVariantConnectorType) compType, node);
		}

		ISubArchitecture subArch = compType.getSubArchitecture();
		if (subArch != null)
		{
			createSubArchitecture(subArch, node);
		}
	}

	/**
	 * @param compType
	 * @param node
	 * @throws ConversionException
	 */
	protected void updateVariantConnectorTypeNode(IVariantConnectorType compType, Node node) throws ConversionException
	{
		graph.addEdge(node, GraphConstants.NODE_VARIANT_CONNECTOR_TYPE, node);

		for (Object o : compType.getAllVariants())
		{
			if (o instanceof IVariant)
			{
				createVariantNode((IVariant) o, node);
			}
		}
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
		// Groups?
		// Groups do not appear to be use in terms of the graph, they are just
		// an arbitrary grouping of elements, they do not contain any semantics.
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

		if (comp instanceof IOptionalComponent)
		{
			makeOptional(((IOptionalComponent) comp).getOptional(), node);
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

		if (iface instanceof IOptionalInterface)
		{
			makeOptional(((IOptionalInterface) iface).getOptional(), node);
		}

		// signature
		typeNode = resolveXMLinkToNode(iface.getSignature());
		if (typeNode != null)
		{
			graph.addEdge(node, GraphConstants.EDGE_SIGNATURE, typeNode);
		}

		setDirection(node, iface.getDirection());
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

		if (conn instanceof IOptionalConnector)
		{
			makeOptional(((IOptionalConnector) conn).getOptional(), node);
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

		if (link instanceof IOptionalLink)
		{
			makeOptional(((IOptionalLink) link).getOptional(), node);
		}

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
	 * Makes the node an optional node
	 * 
	 * @param node
	 * @throws ConversionException
	 */
	protected void makeOptional(IOptional optional, Node node) throws ConversionException
	{
		if (optional == null)
		{
			return;
		}
		graph.addEdge(node, GraphConstants.NODE_OPTIONAL, node);
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
			Node result = getNodeById(uri.getFragment());
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
