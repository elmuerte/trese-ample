/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.model;

/**
 * A constraint between features
 * 
 * @author Michiel Hendriks
 */
public interface FeatureConstraint
{
	/**
	 * @return The type of constraint
	 */
	FeatureConstraintType getType();

	/**
	 * @return The left hand side of the constraint
	 */
	Feature getLHS();

	/**
	 * @return The right hand side of the constraint
	 */
	Feature getRHS();
}
