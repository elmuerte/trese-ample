/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.modelImpl;

import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureConstraint;
import trese.featuremodels.model.FeatureConstraintType;

/**
 * Implementation of the feature constraint interface
 * 
 * @author Michiel Hendriks
 */
public class FeatureConstraintImpl implements FeatureConstraint
{
	/**
	 * The constraint type
	 */
	protected FeatureConstraintType type;

	/**
	 * The affected features
	 */
	protected Feature lhs, rhs;

	/**
	 * Create a new constraint
	 */
	public FeatureConstraintImpl(FeatureConstraintType type, Feature left, Feature right)
	{
		this.type = type;
		lhs = left;
		rhs = right;
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.FeatureConstraint#getLHS()
	 */
	@Override
	public Feature getLHS()
	{
		return lhs;
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.FeatureConstraint#getRHS()
	 */
	@Override
	public Feature getRHS()
	{
		return rhs;
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.model.FeatureConstraint#getType()
	 */
	@Override
	public FeatureConstraintType getType()
	{
		return type;
	}

}
