/* !! LICENSE PENDING!!
 * 
 * Copyright (C) 2008 TRESE; University of Twente
 */
package trese.archmodel.groove;

/**
 * Exception thrown during conversions
 * 
 * @author Michiel Hendriks
 */
public class ConversionException extends Exception
{
	private static final long serialVersionUID = 2691842601320495138L;

	/**
	 * 
	 */
	public ConversionException()
	{
		super();
	}

	/**
	 * @param arg0
	 */
	public ConversionException(String arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public ConversionException(Throwable arg0)
	{
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ConversionException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}
}
