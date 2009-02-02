/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.model;

/**
 * The status of a feature in the product
 * 
 * @author Michiel Hendriks
 */
public enum FeatureStatus
{
	/**
	 * No status has been set
	 */
	NONE,
	/**
	 * This feature is present in the product
	 */
	INCLUDED,
	/**
	 * This feature is not present in the product
	 */
	EXCLUDED,
}
