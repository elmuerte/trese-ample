/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.gft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;

import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureConstraintType;
import trese.featuremodels.model.FeatureGroupRelation;
import trese.featuremodels.model.FeatureRequirement;
import trese.featuremodels.modelImpl.FeatureConstraintImpl;
import trese.featuremodels.modelImpl.FeatureImpl;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class GftParserBase extends Parser
{
	/**
	 * Node id to feature mapping;
	 */
	protected Map<String, FeatureImpl> nodeFeatureMap;

	/**
	 * Mapping from the feature name (i.e. description) to the feature
	 */
	protected Map<String, FeatureImpl> nameFeatureMap;

	/**
	 * Constraints that should be included
	 */
	protected Set<String> useConstraints;

	/**
	 * The root feature
	 */
	protected FeatureImpl rootFeature;

	/**
	 * @param arg0
	 */
	public GftParserBase(TokenStream arg0)
	{
		this(arg0, null);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public GftParserBase(TokenStream arg0, RecognizerSharedState arg1)
	{
		super(arg0, arg1);
		nodeFeatureMap = new HashMap<String, FeatureImpl>();
		nameFeatureMap = new HashMap<String, FeatureImpl>();
		useConstraints = new HashSet<String>();
	}

	/**
	 * @return the rootFeature
	 */
	public Feature getRootFeature()
	{
		return rootFeature;
	}

	/**
	 * Get a feature by its ID. Creates a new feature when needed.
	 * 
	 * @param id
	 * @return
	 */
	protected FeatureImpl getFeatureById(String id)
	{
		if (!nodeFeatureMap.containsKey(id))
		{
			FeatureImpl item = new FeatureImpl(id);
			nodeFeatureMap.put(id, item);
			if (rootFeature == null)
			{
				rootFeature = item;
			}
			return item;
		}
		return nodeFeatureMap.get(id);
	}

	/**
	 * Register mandatory and optional features
	 * 
	 * @param id
	 * @param name
	 * @param mand
	 * @param op
	 */
	protected void mandOptFeature(String id, String name, List<String> mand, List<String> op)
	{
		FeatureImpl feature = getFeatureById(id);
		feature.setDescription(name);
		nameFeatureMap.put(name, feature);
		for (String childId : mand)
		{
			FeatureImpl child = getFeatureById(childId);
			feature.addSubFeature(child);
			child.setRequirement(FeatureRequirement.MANDATORY);
		}
		for (String childId : op)
		{
			FeatureImpl child = getFeatureById(childId);
			feature.addSubFeature(child);
			child.setRequirement(FeatureRequirement.OPTIONAL);
		}
	}

	/**
	 * Register a or feature set
	 * 
	 * @param id
	 * @param name
	 * @param children
	 */
	protected void orFeature(String id, String name, List<String> children)
	{
		FeatureImpl feature = getFeatureById(id);
		feature.setDescription(name);
		nameFeatureMap.put(name, feature);
		feature.setGroupRelation(FeatureGroupRelation.OR);
		for (String childId : children)
		{
			FeatureImpl child = getFeatureById(childId);
			feature.addSubFeature(child);
			child.setRequirement(FeatureRequirement.GROUP);
		}
	}

	/**
	 * Register an alternative feature set
	 * 
	 * @param id
	 * @param name
	 * @param children
	 */
	protected void xorFeature(String id, String name, List<String> children)
	{
		FeatureImpl feature = getFeatureById(id);
		feature.setDescription(name);
		nameFeatureMap.put(name, feature);
		feature.setGroupRelation(FeatureGroupRelation.ALTERNATIVE);
		for (String childId : children)
		{
			FeatureImpl child = getFeatureById(childId);
			feature.addSubFeature(child);
			child.setRequirement(FeatureRequirement.GROUP);
		}
	}

	/**
	 * Set the constraints to be used
	 * 
	 * @param constrainIds
	 */
	protected void includeConstraints(List<String> constrainIds)
	{
		useConstraints.addAll(constrainIds);
	}

	/**
	 * Register a constraint
	 * 
	 * @param id
	 * @param type
	 * @param feature1
	 * @param feature2
	 */
	protected void constraint(String id, String type, String feature1, String feature2)
	{
		if (!useConstraints.contains(id))
		{
			return;
		}
		FeatureImpl f1 = nameFeatureMap.get(feature1);
		FeatureImpl f2 = nameFeatureMap.get(feature2);
		FeatureConstraintType ctype = null;
		if (FeatureConstraintType.EXCLUDES.toString().equalsIgnoreCase(type))
		{
			ctype = FeatureConstraintType.EXCLUDES;
		}
		else if (FeatureConstraintType.REQUIRES.toString().equalsIgnoreCase(type))
		{
			ctype = FeatureConstraintType.REQUIRES;
		}
		if (f1 != null && f2 != null && ctype != null)
		{
			f1.addConstraint(new FeatureConstraintImpl(ctype, f1, f2));
		}
		else
		{
			// TODO error
		}
	}
}
