/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.modelImpl;

import trese.featuremodel.model.Feature;
import trese.featuremodel.model.FeatureConstraint;
import trese.featuremodel.model.FeatureConstraintType;

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
	 * @see trese.featuremodel.model.FeatureConstraint#getLHS()
	 */
	public Feature getLHS()
	{
		return lhs;
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodel.model.FeatureConstraint#getRHS()
	 */
	public Feature getRHS()
	{
		return rhs;
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodel.model.FeatureConstraint#getType()
	 */
	public FeatureConstraintType getType()
	{
		return type;
	}

}
