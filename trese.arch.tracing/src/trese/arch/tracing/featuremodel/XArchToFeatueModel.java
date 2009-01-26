/* !! LICENSE PENDING!!
 * 
 * Copyright (C) 2008 TRESE; University of Twente
 */
package trese.arch.tracing.featuremodel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import trese.arch.tracing.XADLUtils;
import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureGroupRelation;
import trese.featuremodels.model.FeatureRequirement;
import edu.uci.isr.xarch.IXArch;
import edu.uci.isr.xarch.IXArchElement;
import edu.uci.isr.xarch.options.IOptionalComponent;
import edu.uci.isr.xarch.options.IOptionalConnector;
import edu.uci.isr.xarch.types.IArchStructure;
import edu.uci.isr.xarch.types.IComponent;
import edu.uci.isr.xarch.types.IComponentType;
import edu.uci.isr.xarch.types.IConnector;
import edu.uci.isr.xarch.types.IConnectorType;
import edu.uci.isr.xarch.types.ISubArchitecture;
import edu.uci.isr.xarch.variants.IVariant;
import edu.uci.isr.xarch.variants.IVariantComponentType;
import edu.uci.isr.xarch.variants.IVariantConnectorType;

/**
 * Convert a XArch model to a feature model
 * 
 * @author Michiel Hendriks
 */
public class XArchToFeatueModel
{
	/**
	 * Convert all architecture structures to a feature model
	 * 
	 * @param arch
	 * @param structures
	 * @return
	 */
	public static final Set<Feature> convert(IXArch arch, Set<String> structures)
	{
		XArchToFeatueModel conv = new XArchToFeatueModel(arch, structures);
		return conv.internalConvert();
	}

	/**
	 * The file under inspection
	 */
	protected IXArch archFile;

	/**
	 * The structs to convert to feature trees
	 */
	protected Set<String> structs;

	/**
	 * Mapping from element id to the element
	 */
	protected Map<String, IXArchElement> idMap;

	/**
	 * Used to create the unique feature ids. We can not use the ID of the
	 * elements because we might need to copy certain elements in different
	 * subtrees.
	 */
	protected int featureCtr;

	protected XArchToFeatueModel(IXArch arch, Set<String> structures)
	{
		archFile = arch;
		if (structures != null && !structures.isEmpty())
		{
			structs = structures;
		}
		idMap = XADLUtils.createIdMap(arch);
	}

	/**
	 * @return
	 */
	protected Set<Feature> internalConvert()
	{
		Set<Feature> result = new HashSet<Feature>();
		for (Object o : archFile.getAllObjects())
		{
			if (o instanceof IArchStructure)
			{
				if (structs == null || structs.contains(XADLUtils.getId((IArchStructure) o)))
				{
					Feature fm = createFeature((IArchStructure) o);
					if (fm != null)
					{
						result.add(fm);
					}
				}
			}
		}
		return result;
	}

	protected String getUniqueId(IXArchElement elm)
	{
		return "n" + ++featureCtr;
	}

	/**
	 * @param struct
	 * @return
	 */
	protected Feature createFeature(IArchStructure struct)
	{
		XADLFeature result = new XADLFeature(getUniqueId(struct), struct);
		result.setRequirement(FeatureRequirement.MANDATORY);
		addSubFeatures(result, struct);
		return result;
	}

	/**
	 * @param struct
	 * @return
	 */
	protected void addSubFeatures(XADLFeature result, IArchStructure struct)
	{
		for (Object o : struct.getAllComponents())
		{
			if (o instanceof IComponent)
			{
				Feature f = createFeature((IComponent) o);
				if (f != null)
				{
					result.addSubFeature(f);
				}
			}
			if (o instanceof IConnector)
			{
				Feature f = createFeature((IConnector) o);
				if (f != null)
				{
					result.addSubFeature(f);
				}
			}
		}
	}

	/**
	 * @param o
	 * @return
	 */
	protected Feature createFeature(IComponent component)
	{
		XADLFeature result = new XADLFeature(getUniqueId(component), component);
		if (component instanceof IOptionalComponent)
		{
			result.setRequirement(FeatureRequirement.OPTIONAL);
		}
		else
		{
			result.setRequirement(FeatureRequirement.MANDATORY);
		}
		IXArchElement typElm = idMap.get(XADLUtils.getIdFromXMLLink(component.getType()));
		if (typElm instanceof IComponentType)
		{
			addSubFeatures(result, (IComponentType) typElm);
		}
		return result;
	}

	/**
	 * @param result
	 * @param typElm
	 */
	protected void addSubFeatures(XADLFeature baseFeature, IComponentType type)
	{
		if (type instanceof IVariantComponentType)
		{
			baseFeature.setGroupRelation(FeatureGroupRelation.ALTERNATIVE);
			for (Object o : ((IVariantComponentType) type).getAllVariants())
			{
				if (o instanceof IVariant)
				{
					Feature f = createFeature((IVariant) o);
					if (f != null)
					{
						baseFeature.addSubFeature(f);
					}
				}
			}
		}
		else
		{
			ISubArchitecture subarch = type.getSubArchitecture();
			if (subarch != null)
			{
				IXArchElement archElm = idMap.get(XADLUtils.getIdFromXMLLink(subarch.getArchStructure()));
				if (archElm instanceof IArchStructure)
				{
					addSubFeatures(baseFeature, (IArchStructure) archElm);
				}
			}
		}
	}

	/**
	 * @param o
	 * @return
	 */
	protected Feature createFeature(IVariant variant)
	{
		IXArchElement elm = idMap.get(XADLUtils.getIdFromXMLLink(variant.getVariantType()));
		if (elm instanceof IComponentType)
		{
			XADLFeature feature = new XADLFeature(getUniqueId(elm), elm);
			addSubFeatures(feature, (IComponentType) elm);
			return feature;
		}
		else if (elm instanceof IConnectorType)
		{
			XADLFeature feature = new XADLFeature(getUniqueId(elm), elm);
			addSubFeatures(feature, (IConnectorType) elm);
			return feature;
		}
		return null;
	}

	/**
	 * @param o
	 * @return
	 */
	protected Feature createFeature(IConnector connector)
	{
		XADLFeature result = new XADLFeature(getUniqueId(connector), connector);
		if (connector instanceof IOptionalConnector)
		{
			result.setRequirement(FeatureRequirement.OPTIONAL);
		}
		else
		{
			result.setRequirement(FeatureRequirement.MANDATORY);
		}
		IXArchElement typElm = idMap.get(XADLUtils.getIdFromXMLLink(connector.getType()));
		if (typElm instanceof IConnectorType)
		{
			addSubFeatures(result, (IConnectorType) typElm);
		}
		return result;
	}

	/**
	 * @param result
	 * @param typElm
	 */
	protected void addSubFeatures(XADLFeature baseFeature, IConnectorType type)
	{
		if (type instanceof IVariantConnectorType)
		{
			baseFeature.setGroupRelation(FeatureGroupRelation.ALTERNATIVE);
			for (Object o : ((IVariantConnectorType) type).getAllVariants())
			{
				if (o instanceof IVariant)
				{
					Feature f = createFeature((IVariant) o);
					if (f != null)
					{
						baseFeature.addSubFeature(f);
					}
				}
			}
		}
		else
		{
			ISubArchitecture subarch = type.getSubArchitecture();
			if (subarch != null)
			{
				IXArchElement archElm = idMap.get(XADLUtils.getIdFromXMLLink(subarch.getArchStructure()));
				if (archElm instanceof IArchStructure)
				{
					addSubFeatures(baseFeature, (IArchStructure) archElm);
				}
			}
		}
	}
}
