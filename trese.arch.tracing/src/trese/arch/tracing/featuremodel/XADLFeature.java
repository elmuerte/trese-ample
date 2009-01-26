/* !! LICENSE PENDING!!
 * 
 * Copyright (C) 2008 TRESE; University of Twente
 */
package trese.arch.tracing.featuremodel;

import trese.arch.tracing.XADLUtils;
import trese.featuremodels.modelImpl.FeatureImpl;
import edu.uci.isr.xarch.IXArchElement;

/**
 * Feture implementation that links to an xADL element
 * 
 * @author Michiel Hendriks
 */
public class XADLFeature extends FeatureImpl
{
	protected IXArchElement xArchElement;

	/**
	 * @param name
	 */
	public XADLFeature(String id, IXArchElement element)
	{
		super(id);
		setDescription(XADLUtils.getDescription(element));
		xArchElement = element;
	}

	/**
	 * @return the xArchElement
	 */
	public IXArchElement getXArchElement()
	{
		return xArchElement;
	}
}
