/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureModelException;

/**
 * The result returned from an evaluation
 * 
 * @author Michiel Hendriks
 */
public class EvaluationResult
{
	/**
	 * The base feature
	 */
	protected Feature baseLine;

	/**
	 * The included features
	 */
	protected Collection<Feature> includedFeatures;

	/**
	 * A quick lookup for features by their name
	 */
	protected Map<String, Feature> featureLookup;

	/**
	 * @param base
	 * @throws FeatureModelException
	 *             Thrown in case of a corrupt feature model
	 */
	public EvaluationResult(Feature base) throws FeatureModelException
	{
		if (base == null)
		{
			throw new NullPointerException("The base feature can not be null");
		}
		baseLine = base;
		includedFeatures = new HashSet<Feature>();
		createFeatureLookup();
	}

	/**
	 * Create a copy
	 * 
	 * @param base
	 */
	protected EvaluationResult(EvaluationResult base)
	{
		baseLine = base.baseLine;
		includedFeatures = new HashSet<Feature>();
		featureLookup = new HashMap<String, Feature>(base.featureLookup);
	}

	/**
	 * Create a feature lookup by name;
	 * 
	 * @throws FeatureModelException
	 *             Thrown when there are inconsistencies in the feature model
	 */
	protected void createFeatureLookup() throws FeatureModelException
	{
		featureLookup = new HashMap<String, Feature>();
		Queue<Feature> todo = new LinkedList<Feature>();
		todo.add(baseLine);
		while (!todo.isEmpty())
		{
			Feature feat = todo.poll();
			featureLookup.put(feat.getName(), feat);
			for (Feature subfeat : feat.getSubFeatures())
			{
				if (featureLookup.containsValue(subfeat))
				{
					continue;
				}
				if (featureLookup.containsKey(subfeat.getName()))
				{
					throw new FeatureModelException(String.format("Duplicate feature name: %s", subfeat.getName()));
				}
				todo.add(subfeat);
			}
		}
	}

	/**
	 * @return the a read only list of included features
	 */
	public Collection<Feature> getIncludedFeatures()
	{
		return Collections.unmodifiableCollection(includedFeatures);
	}

	/**
	 * @param name
	 * @return A feature by its name
	 */
	public Feature getFeatureByName(String name)
	{
		return featureLookup.get(name);
	}

	/**
	 * @return All features in the model
	 */
	public Collection<Feature> getAllFeatures()
	{
		return Collections.unmodifiableCollection(featureLookup.values());
	}

	/**
	 * @return
	 */
	public Feature getBaseLine()
	{
		return baseLine;
	}

	/**
	 * @param feature
	 */
	public void addIncludedFeature(Feature feature)
	{
		if (featureLookup.containsValue(feature))
		{
			includedFeatures.add(feature);
		}
	}
}
