/* !! LICENSE PENDING!!
 * 
 * Copyright (C) 2008 TRESE; University of Twente
 */
package trese.arch.tracing.conversion;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import edu.uci.isr.xarch.IXArch;
import edu.uci.isr.xarch.IXArchImplementation;
import edu.uci.isr.xarch.XArchParseException;
import edu.uci.isr.xarch.XArchUtils;
import groove.io.AspectGxl;
import groove.view.aspect.AspectGraph;

/**
 * A commandline converter
 * 
 * @author Michiel Hendriks
 */
public class Converter
{
	public Converter()
	{}

	/**
	 * Convert an input xADL to Groove .gst
	 * 
	 * @param source
	 *            The xADL input
	 * @param dest
	 *            The output .gst
	 * @throws ConversionException
	 * @throws IOException
	 */
	public void convert(File source, File dest) throws ConversionException, IOException
	{
		convert(new FileReader(source), dest);
	}

	/**
	 * @param source
	 * @param dest
	 * @throws ConversionException
	 * @throws IOException
	 */
	public void convert(Reader source, File dest) throws ConversionException, IOException
	{
		IXArchImplementation impl = XArchUtils.getDefaultXArchImplementation();
		try
		{
			convert(impl.parse(source), dest);
		}
		catch (XArchParseException e)
		{
			throw new ConversionException(e);
		}
	}

	/**
	 * Convert the given architecture to the file
	 * 
	 * @param arch
	 * @param dest
	 * @throws ConversionException
	 * @throws IOException
	 */
	public void convert(IXArch arch, File dest) throws ConversionException, IOException
	{
		convert(arch, null, dest);
	}

	/**
	 * Convert the given architecture to the file
	 * 
	 * @param arch
	 * @param restrictToArchId
	 * @param dest
	 * @throws ConversionException
	 * @throws IOException
	 */
	public void convert(IXArch arch, Set<String> restrictToArchId, File dest) throws ConversionException, IOException
	{
		AspectGraph graph = XADL2Graph.convert(arch, restrictToArchId);
		AspectGxl gxl = new AspectGxl();
		gxl.marshalGraph(graph, dest);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Converter converter = new Converter();
		for (String input : args)
		{
			File source = new File(input);
			if (!source.exists())
			{
				System.out.println(String.format("Input file does not exist: %s", source.toString()));
			}
			File dest = new File(input + ".gst");
			try
			{
				converter.convert(source, dest);
			}
			catch (Exception e)
			{
				System.err.println(e.toString());
				e.printStackTrace(System.err);
			}
		}
	}
}
