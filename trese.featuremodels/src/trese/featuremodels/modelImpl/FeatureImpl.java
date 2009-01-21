/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.modelImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureConstraint;
import trese.featuremodels.model.FeatureGroupRelation;
import trese.featuremodels.model.FeatureRequirement;
import trese.featuremodels.model.FeatureStatus;

/**
 * Implementation of a Feature
 * 
 * @author Michiel Hendriks
 */
public class FeatureImpl implements Feature
{
	/**
	 * The name of the feature
	 */
	protected String name;

	/**
	 * True if this feature is included in the project
	 */
	protected FeatureStatus included = FeatureStatus.NONE;

	/**
	 * The parent feature
	 */
	protected Feature parent;

	/**
	 * The requirement of this feature
	 */
	protected FeatureRequirement requirement = FeatureRequirement.GROUP;

	/**
	 * The group requirement of this feature
	 */
	protected FeatureGroupRelation groupRelation = FeatureGroupRelation.NONE;

	/**
	 * The subfeatures
	 */
	protected Collection<Feature> subFeatures;

	/**
	 * Constraints of this feature
	 */
	protected Collection<FeatureConstraint> constraints;

	/**
	 * Create a new feature
	 */
	public FeatureImpl(String name)
	{
		this.name = name;
		subFeatures = new HashSet<Feature>();
		constraints = new HashSet<FeatureConstraint>();
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.Feature#getConstraints()
	 */
	@Override
	public Collection<FeatureConstraint> getConstraints()
	{
		return Collections.unmodifiableCollection(constraints);
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.Feature#getGroupRelation()
	 */
	@Override
	public FeatureGroupRelation getGroupRelation()
	{
		return groupRelation;
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.Feature#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.Feature#getParentFeature()
	 */
	@Override
	public Feature getParentFeature()
	{
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.Feature#getRequirement()
	 */
	@Override
	public FeatureRequirement getRequirement()
	{
		return requirement;
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.Feature#getSubFeatures()
	 */
	@Override
	public Collection<Feature> getSubFeatures()
	{
		return Collections.unmodifiableCollection(subFeatures);
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.Feature#isIncluded()
	 */
	@Override
	public FeatureStatus getStatus()
	{
		return included;
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.Feature#setIncluded(boolean)
	 */
	@Override
	public void setStatus(FeatureStatus value)
	{
		included = value;
	}

	/**
	 * Set the parent of this feature
	 * 
	 * @param value
	 */
	public void setParent(Feature value)
	{
		parent = value;
	}

	/**
	 * @param value
	 *            the requirement to set
	 */
	public void setRequirement(FeatureRequirement value)
	{
		requirement = value;
	}

	/**
	 * @param value
	 *            the groupRelation to set
	 */
	public void setGroupRelation(FeatureGroupRelation value)
	{
		groupRelation = value;
	}

	/**
	 * Add a subfeature
	 * 
	 * @param value
	 */
	public void addSubFeature(Feature value)
	{
		if (value != null)
		{
			subFeatures.add(value);
			if (value instanceof FeatureImpl)
			{
				((FeatureImpl) value).setParent(this);
			}
		}
	}

	/**
	 * Add a constraint to this feature
	 * 
	 * @param value
	 */
	public void addConstraint(FeatureConstraint value)
	{
		if (value != null)
		{
			constraints.add(value);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getName();
	}
}
