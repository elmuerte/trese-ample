/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.gft;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureConstraint;

/**
 * Save a feature tree to an output stream in the GFT format
 * 
 * @author Michiel Hendriks
 */
public class SaveAsGFT
{
	public static boolean save(Feature root, OutputStream toStream)
	{
		SaveAsGFT sag = new SaveAsGFT(root, toStream);
		return sag.save();
	}

	protected Feature root;
	protected PrintStream output;

	/**
	 * All found constraints
	 */
	protected Set<FeatureConstraint> constraints;

	/**
	 * The features that were saved. Used to filter constraints
	 */
	protected Set<Feature> features;

	protected SaveAsGFT(Feature rootFeature, OutputStream os)
	{
		constraints = new HashSet<FeatureConstraint>();
		root = rootFeature;
		output = new PrintStream(os);
	}

	protected boolean save()
	{
		return saveFeature(root);
	}

	/**
	 * @param root2
	 * @return
	 */
	protected boolean saveFeature(Feature feature)
	{
		if (feature == root)
		{
			output.print("f_tree");
		}
		else
		{
			// ....
		}
		// ....
		return false;
	}
}
