/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.model;

/**
 * The types of constraints between features
 * 
 * @author Michiel Hendriks
 */
public enum FeatureConstraintType
{
	/**
	 * The first feature requires the second feature
	 */
	REQUIRES
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "requires";
		}
	},
	/**
	 * The first feature excludes the second feature
	 */
	EXCLUDES
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "excludes";
		}
	},
}
