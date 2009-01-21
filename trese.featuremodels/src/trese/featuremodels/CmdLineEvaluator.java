/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

import trese.featuremodels.loaders.BaseLoader;
import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureModelException;

/**
 * Commandline interace to the evalutator
 * 
 * @author Michiel Hendriks
 */
public final class CmdLineEvaluator
{
	public static void main(String[] args)
	{
		Options options = new Options();
		options.addOption("?", "help", false, "Show this message");
		options.addOption("1", "first", false, "Stop when the first valid production configuration has been found.");
		options.addOption("v", false, "Produce more output");
		options.addOption("d", "debug", false, "Produce debug output");
		options.addOption(null, "dir", true, "Process all files in the given directory");

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

		if (cmd.hasOption("dir"))
		{
			if (files.length != 0)
			{
				System.err.println("Files ignored when using the --dir option");
			}

			File scanDir = new File(cmd.getOptionValue("dir"));
			if (!scanDir.exists() || !scanDir.isDirectory())
			{
				System.err.println(String.format("'%s' is not an existing directory", scanDir.toString()));
				return;
			}
			final Set<String> exts = BaseLoader.supportedExtensions();
			files = scanDir.list(new FilenameFilter() {
				@Override
				public boolean accept(File arg0, String arg1)
				{
					String ext = arg1.toString();
					if (ext.indexOf('.') > -1)
					{
						ext = ext.substring(ext.lastIndexOf('.') + 1).toLowerCase();
						return exts.contains(ext);
					}
					return false;
				}
			});
			for (int i = 0; i < files.length; i++)
			{
				files[i] = scanDir.toString() + File.separator + files[i];
			}
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
				try
				{
					baseline = BaseLoader.loadFeatureModel(file.toURI().toURL());
				}
				catch (MalformedURLException e)
				{
					System.err.println(e);
					if (debug)
					{
						e.printStackTrace(System.err);
					}
					continue;
				}
				if (baseline == null)
				{
					System.err.println(String.format("Unsupported file format: %s", filename));
					continue;
				}

				System.out.println(String.format("File:\t\t\t%s", file.toString()));
				long startTime = System.nanoTime();
				Collection<EvaluationResult> result = eval.evaluate(baseline, cmd.hasOption('1'));
				startTime = System.nanoTime() - startTime;

				System.out.println(String.format("Products:\t\t%d", result.size()));

				Collection<Feature> deadFeatures = null;

				if (verbose)
				{
					for (EvaluationResult res : result)
					{
						if (deadFeatures == null)
						{
							deadFeatures = new HashSet<Feature>(res.getAllFeatures());
						}

						Collection<Feature> incFeatures = res.getIncludedFeatures();
						deadFeatures.removeAll(incFeatures);

						System.out.print("Configuration:\t\t");
						SortedSet<String> features = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
						for (Feature f : incFeatures)
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

				if (deadFeatures != null && !deadFeatures.isEmpty())
				{
					System.out.print("Dead Features:\t\t");
					SortedSet<String> features = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
					for (Feature f : deadFeatures)
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
