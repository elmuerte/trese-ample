/* !! LICENSE PENDING!!
 * 
 * Copyright (C) 2008 TRESE; University of Twente
 */
package trese.archmodel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import edu.uci.isr.xarch.IXArch;
import edu.uci.isr.xarch.IXArchElement;
import edu.uci.isr.xarch.instance.IDescription;
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

/**
 * 
 * @author Michiel Hendriks
 */
public final class XADLUtils
{
	/**
	 * Create an id -> IXArchElement map for the given XArch
	 * 
	 * @param arch
	 * @return
	 */
	public static final Map<String, IXArchElement> createIdMap(IXArch arch)
	{
		Map<String, IXArchElement> result = new HashMap<String, IXArchElement>();
		for (Object o : arch.getAllObjects())
		{
			if (o instanceof IArchStructure)
			{
				IArchStructure as = (IArchStructure) o;
				result.put(as.getId(), as);
				for (Object so : as.getAllComponents())
				{
					if (so instanceof IComponent)
					{
						result.put(((IComponent) so).getId(), (IXArchElement) so);
						for (Object iso : ((IComponent) so).getAllInterfaces())
						{
							if (iso instanceof IInterface)
							{
								result.put(((IInterface) iso).getId(), (IXArchElement) iso);
							}
						}
					}
				}
				for (Object so : as.getAllConnectors())
				{
					if (so instanceof IConnector)
					{
						result.put(((IConnector) so).getId(), (IXArchElement) so);
						for (Object iso : ((IConnector) so).getAllInterfaces())
						{
							if (iso instanceof IInterface)
							{
								result.put(((IInterface) iso).getId(), (IXArchElement) iso);
							}
						}
					}
				}
				for (Object so : as.getAllLinks())
				{
					if (so instanceof ILink)
					{
						result.put(((ILink) so).getId(), (IXArchElement) so);
					}
				}
			}
			else if (o instanceof IArchTypes)
			{
				IArchTypes as = (IArchTypes) o;
				for (Object so : as.getAllComponentTypes())
				{
					if (so instanceof IComponentType)
					{
						result.put(((IComponentType) so).getId(), (IXArchElement) so);
						for (Object iso : ((IComponentType) so).getAllSignatures())
						{
							if (iso instanceof ISignature)
							{
								result.put(((ISignature) iso).getId(), (IXArchElement) iso);
							}
						}
					}
				}
				for (Object so : as.getAllConnectorTypes())
				{
					if (so instanceof IConnectorType)
					{
						result.put(((IConnectorType) so).getId(), (IXArchElement) so);
						for (Object iso : ((IConnectorType) so).getAllSignatures())
						{
							if (iso instanceof ISignature)
							{
								result.put(((ISignature) iso).getId(), (IXArchElement) iso);
							}
						}
					}
				}
				for (Object so : as.getAllInterfaceTypes())
				{
					if (so instanceof IInterfaceType)
					{
						result.put(((IInterfaceType) so).getId(), (IXArchElement) so);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Return the id of the element if it has one
	 * 
	 * @param element
	 * @return
	 */
	public static String getId(IXArchElement element)
	{
		if (element instanceof IArchStructure)
		{
			return ((IArchStructure) element).getId();
		}
		else if (element instanceof IComponent)
		{
			return ((IComponent) element).getId();
		}
		else if (element instanceof IConnector)
		{
			return ((IConnector) element).getId();
		}
		else if (element instanceof IInterface)
		{
			return ((IInterface) element).getId();
		}
		else if (element instanceof ILink)
		{
			return ((ILink) element).getId();
		}
		else if (element instanceof IComponentType)
		{
			return ((IComponentType) element).getId();
		}
		else if (element instanceof IConnectorType)
		{
			return ((IConnectorType) element).getId();
		}
		else if (element instanceof ISignature)
		{
			return ((ISignature) element).getId();
		}
		else if (element instanceof IInterfaceType)
		{
			return ((IInterfaceType) element).getId();
		}
		return null;
	}

	/**
	 * Return the description of the element (if it has one)
	 * 
	 * @param element
	 * @return
	 */
	public static String getDescription(IXArchElement element)
	{
		IDescription desc = null;
		if (element instanceof IArchStructure)
		{
			desc = ((IArchStructure) element).getDescription();
		}
		else if (element instanceof IComponent)
		{
			desc = ((IComponent) element).getDescription();
		}
		else if (element instanceof IConnector)
		{
			desc = ((IConnector) element).getDescription();
		}
		else if (element instanceof IInterface)
		{
			desc = ((IInterface) element).getDescription();
		}
		else if (element instanceof ILink)
		{
			desc = ((ILink) element).getDescription();
		}
		else if (element instanceof IComponentType)
		{
			desc = ((IComponentType) element).getDescription();
		}
		else if (element instanceof IConnectorType)
		{
			desc = ((IConnectorType) element).getDescription();
		}
		else if (element instanceof ISignature)
		{
			desc = ((ISignature) element).getDescription();
		}
		else if (element instanceof IInterfaceType)
		{
			desc = ((IInterfaceType) element).getDescription();
		}
		if (desc != null)
		{
			return desc.getValue();
		}
		return null;
	}

	public static String getIdFromXMLLink(IXMLLink type)
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
				return null;
				// TODO:
				// throw new
				// ConversionException(String.format("Can not handle cross document references (%s)",
				// uri
				// .toString()));
			}
			return uri.getFragment();
		}
		catch (URISyntaxException e)
		{
			return null;
			// TODO: throw new ConversionException(e);
		}
	}
}
