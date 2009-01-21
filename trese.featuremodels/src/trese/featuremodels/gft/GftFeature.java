/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.gft;

import trese.featuremodels.modelImpl.FeatureImpl;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class GftFeature extends FeatureImpl
{
	/**
	 * The description of this feature. It's a nicer name for the feature.
	 */
	protected String description;

	/**
	 * @param name
	 */
	public GftFeature(String name)
	{
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.modelImpl.FeatureImpl#getName()
	 */
	@Override
	public String getName()
	{
		if (description != null && !description.isEmpty())
		{
			return description;
		}
		return super.getName();
	}

	/**
	 * @return the real name of the feature / ID
	 */
	public String getID()
	{
		return name;
	}

	/**
	 * @param value
	 *            the description to set
	 */
	public void setDescription(String value)
	{
		description = value;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

}
