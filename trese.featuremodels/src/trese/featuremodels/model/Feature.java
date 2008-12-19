/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.model;

import java.util.Collection;

/**
 * Base interface for all features
 * 
 * @author Michiel Hendriks
 */
public interface Feature
{
	/**
	 * @return The name of the feature. The name of a feature should be unique
	 *         within the system.
	 */
	String getName();

	/**
	 * @return The parent feature, will be null when this is the root feature.
	 */
	Feature getParentFeature();

	/**
	 * @return The requirement of this feature
	 */
	FeatureRequirement getRequirement();

	/**
	 * @return The the sub features
	 */
	Collection<Feature> getSubFeatures();

	/**
	 * @return The relation the subfeatures share
	 */
	FeatureGroupRelation getGroupRelation();

	/**
	 * @return The list of constraints this feature imposes on other features.
	 *         Thus the returned constraints contain this feature in the left
	 *         hand side.
	 */
	Collection<FeatureConstraint> getConstraints();

	/**
	 * @return True if this feature is included in the product
	 */
	FeatureStatus getStatus();

	/**
	 * Set if this feature is included in the product
	 * 
	 * @param value
	 */
	void setStatus(FeatureStatus value);
}
