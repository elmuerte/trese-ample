/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.model;

/**
 * Thrown when there is an issue with the feature model
 * 
 * @author Michiel Hendriks
 */
public class FeatureModelException extends Exception
{
	private static final long serialVersionUID = -6042966091161550216L;

	/**
	 * 
	 */
	public FeatureModelException()
	{
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public FeatureModelException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public FeatureModelException(String arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public FeatureModelException(Throwable arg0)
	{
		super(arg0);
	}

}
