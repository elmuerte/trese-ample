/* !! LICENSE PENDING!!
 * 
 * Copyright (C) 2008 TRESE; University of Twente
 */
package trese.arch.tracing.conversion;

import groove.graph.DefaultLabel;
import groove.graph.Label;

/**
 * Various constants used in the graphs
 * 
 * @author Michiel Hendriks
 */
public final class GraphConstants
{
	public static final Label ATTR_ID = DefaultLabel.createLabel("id");
	public static final Label ATTR_DESCRIPTION = DefaultLabel.createLabel("description");

	public static final Label NODE_INTERFACE_TYPE = DefaultLabel.createLabel("InterfaceType");
	public static final Label NODE_COMPONENT_TYPE = DefaultLabel.createLabel("ComponentType");
	public static final Label NODE_CONNECTOR_TYPE = DefaultLabel.createLabel("ConnectorType");
	public static final Label NODE_SIGNATURE = DefaultLabel.createLabel("Signature");
	public static final Label NODE_SUBARCHITECTURE = DefaultLabel.createLabel("SubArchitecture");
	public static final Label NODE_ARCH_STRUCTURE = DefaultLabel.createLabel("ArchStructure");
	public static final Label NODE_HAS_SUBARCHITECTURE = DefaultLabel.createLabel("HasSubArchitecture");
	public static final Label NODE_COMPONENT = DefaultLabel.createLabel("Component");
	public static final Label NODE_CONNECTOR = DefaultLabel.createLabel("Connector");
	public static final Label NODE_LINK = DefaultLabel.createLabel("Link");
	public static final Label NODE_INTERFACE = DefaultLabel.createLabel("Interface");
	public static final Label NODE_INTERFACEMAPPING = DefaultLabel.createLabel("InterfaceMapping");

	public static final Label EDGE_TYPE = DefaultLabel.createLabel("type");
	public static final Label EDGE_SIGNATURE = DefaultLabel.createLabel("signature");
	public static final Label EDGE_ELEMENT = DefaultLabel.createLabel("element");
	public static final Label EDGE_INTERFACE = DefaultLabel.createLabel("interface");
	public static final Label EDGE_LINK = DefaultLabel.createLabel("link");
	public static final Label EDGE_SUBARCHITECTURE = DefaultLabel.createLabel("subArch");
	public static final Label EDGE_ARCHITECTURE = DefaultLabel.createLabel("arch");
	public static final Label EDGE_MAPPING = DefaultLabel.createLabel("mapping");

	public static final Label DIRECTION_INOUT = DefaultLabel.createLabel("Direction_INOUT");
	public static final Label DIRECTION_OUT = DefaultLabel.createLabel("Direction_OUT");
	public static final Label DIRECTION_IN = DefaultLabel.createLabel("Direction_IN");
	public static final Label DIRECTION_NONE = DefaultLabel.createLabel("Direction_NONE");

	public static final Label SERVICE_PROVIDES = DefaultLabel.createLabel("Provides");
	public static final Label SERVICE_REQUIRES = DefaultLabel.createLabel("Requires");

	public static final Label NODE_VARIANT_COMPONENT_TYPE = DefaultLabel.createLabel("VariantComponentType");
	public static final Label NODE_VARIANT_CONNECTOR_TYPE = DefaultLabel.createLabel("VariantConnectorType");
	public static final Label NODE_VARIANT = DefaultLabel.createLabel("Variant");
	public static final Label EDGE_VARIANT = DefaultLabel.createLabel("variant");

	public static final Label NODE_OPTIONAL = DefaultLabel.createLabel("Optional");
}
