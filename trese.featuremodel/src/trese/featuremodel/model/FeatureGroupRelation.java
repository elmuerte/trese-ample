/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.model;

/**
 * The relation between the group of subfeatures
 * 
 * @author Michiel Hendriks
 */
public enum FeatureGroupRelation
{
	/**
	 * There is no group relation between the features. This is used when the
	 * subfeatures define their own {@link FeatureRequirement}
	 */
	NONE,
	/**
	 * Exactly one of the subfeatures features should active
	 */
	ALTERNATIVE
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "alt";
		}
	},
	/**
	 * One or more features should be active
	 */
	OR
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "or";
		}
	},
}
