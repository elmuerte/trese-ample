/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.model;

/**
 * The requirement of this feature in relation to the inclusion of the parent.
 * The {@link #GROUP} requirement is used in conjuction with the
 * {@link FeatureGroupRelation}
 * 
 * @author Michiel Hendriks
 */
public enum FeatureRequirement
{
	/**
	 * The parent determines the requirement, in this case the
	 * {@link FeatureGroupRelation} applies. A feature can not have a
	 * requirement other than {@link #PARENT} when the parent feature has a
	 * group relation.
	 */
	GROUP,
	/**
	 * A mandatory feature, must always be present when its parent feature is
	 * present
	 */
	MANDATORY
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "mandatory";
		}
	},
	/**
	 * An optional feature, does not have to be present when the parent feature
	 * is present
	 */
	OPTIONAL
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "optional";
		}
	}
}
