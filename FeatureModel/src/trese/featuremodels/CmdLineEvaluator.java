/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels;

import groove.graph.Graph;
import groove.io.AspectGxl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureModelException;

/**
 * Commandline interace to the evalutator
 * 
 * @author Michiel Hendriks
 */
public final class CmdLineEvaluator
{
	/**
	 * For debugging
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		Options options = new Options();
		options.addOption("?", "help", false, "Show this message");
		options.addOption("1", "first", false, "Stop when the first valid production configuration has been found.");
		options.addOption("v", false, "Produce more output");
		options.addOption("d", "debug", false, "Produce debug output");
		// options.addOption(null, "dir", true, "Produce debug output");

		boolean showUsage = false;
		boolean verbose = false;
		boolean debug = false;

		Parser parser = new PosixParser();
		CommandLine cmd = null;
		String[] files = new String[0];
		try
		{
			cmd = parser.parse(options, args);
			showUsage |= cmd.hasOption('?');
			verbose |= cmd.hasOption('v');
			debug |= cmd.hasOption('d');
			files = cmd.getArgs();
		}
		catch (ParseException e)
		{
			System.out.println(e.toString());
			showUsage = true;
		}

		if (showUsage || cmd == null || files.length == 0)
		{
			HelpFormatter help = new HelpFormatter();
			help.printHelp("java -jar trese.featuremodels.jar [OPTIONS] FILE [FILE]...", options);
			return;
		}

		Evaluator eval = new Evaluator();
		eval.initialize();
		for (String filename : files)
		{
			try
			{
				File file = new File(filename);
				if (!file.exists())
				{
					System.err.println(String.format("File '%s' does not exist", filename));
					continue;
				}
				Feature baseline = null;

				AspectGxl gxl = new AspectGxl();
				try
				{
					Graph graph = gxl.unmarshalGraph(file);
					baseline = GstToModel.convertGraph(graph);
				}
				catch (IOException e)
				{
					System.err.println(e);
					if (debug)
					{
						e.printStackTrace(System.err);
					}
					continue;
				}

				System.out.println(String.format("File:\t\t\t%s", file.toString()));
				long startTime = System.nanoTime();
				Collection<EvaluationResult> result = eval.evaluate(baseline, cmd.hasOption('1'));
				startTime = System.nanoTime() - startTime;

				System.out.println(String.format("Products:\t\t%d", result.size()));

				if (verbose)
				{
					for (EvaluationResult res : result)
					{
						System.out.print("Configuration:\t\t");
						SortedSet<String> features = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
						for (Feature f : res.getIncludedFeatures())
						{
							// ingore empty baseline
							if (f.getName().length() == 0)
							{
								continue;
							}
							features.add(f.getName());
						}
						System.out.println(features.toString());
					}
				}

				if (debug)
				{
					System.out.println(String.format("Processing time:\t%d ms", startTime / 1000000));
				}
			}
			catch (FeatureModelException e)
			{
				System.err.println(e);
				if (debug)
				{
					e.printStackTrace(System.err);
				}
				continue;
			}
		}
	}
}
