/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureConstraintType;
import trese.featuremodels.model.FeatureGroupRelation;
import trese.featuremodels.model.FeatureRequirement;
import trese.featuremodels.model.FeatureStatus;
import trese.featuremodels.modelImpl.FeatureConstraintImpl;
import trese.featuremodels.modelImpl.FeatureImpl;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public final class GstToModel
{
	/**
	 * Try to convert a graph to a feature model
	 * 
	 * @param graph
	 * @return
	 */
	public static final Feature convertGraph(Graph graph)
	{
		Map<Node, FeatureImpl> featureNodes = new HashMap<Node, FeatureImpl>();
		Feature baseLine = null;

		// find all features
		Set<? extends Edge> edges = graph.labelEdgeSet(2, DefaultLabel.createLabel(FeatureGraphCreator.LABEL_NAME));
		for (Edge edge : edges)
		{
			Node featureNode = edge.source();
			if (featureNodes.containsKey(featureNode))
			{
				continue;
			}
			Node nameNode = edge.opposite();

			String featureName = null;
			for (Edge e : graph.outEdgeSet(nameNode))
			{
				// not great, but works
				if (e.label().text().startsWith("\""))
				{
					featureName = e.label().text();
					featureName = featureName.substring(1, featureName.length() - 1);
				}
			}

			// if (false)
			// {
			// featureName = (String) ((ValueNode) nameNode).getValue();
			// }
			// else
			// {
			// // not a valid name
			// continue;
			// }
			FeatureImpl feature = new FeatureImpl(featureName);
			featureNodes.put(featureNode, feature);
		}

		edges = graph.labelEdgeSet(2, DefaultLabel.createLabel("BaseLine"));
		for (Edge edge : edges)
		{
			Node featureNode = edge.source();
			if (featureNodes.containsKey(featureNode))
			{
				continue;
			}
			FeatureImpl feature = new FeatureImpl("");
			featureNodes.put(featureNode, feature);
		}

		// process the feature nodes
		for (Entry<Node, FeatureImpl> entry : featureNodes.entrySet())
		{
			FeatureImpl thisFeature = entry.getValue();
			for (Edge edge : graph.outEdgeSet(entry.getKey()))
			{
				if (edge.label().text().equals("BaseLine"))
				{
					if (baseLine == null)
					{
						baseLine = thisFeature;
					}
					else
					{
						// TODO: error, multiple baselines
					}
					continue;
				}
				else if (edge.label().text().equals(FeatureGraphCreator.LABEL_FEATURE_EXCLUDED))
				{
					thisFeature.setStatus(FeatureStatus.EXCLUDED);
				}
				else if (edge.label().text().equals(FeatureGraphCreator.LABEL_FEATURE_EXCLUDED))
				{
					thisFeature.setStatus(FeatureStatus.INCLUDED);
				}

				// relations with others
				FeatureImpl other = featureNodes.get(edge.opposite());
				if (other == null)
				{
					continue;
				}
				if (edge.label().text().equals(FeatureRequirement.MANDATORY.toString()))
				{
					thisFeature.addSubFeature(other);
					other.setRequirement(FeatureRequirement.MANDATORY);
				}
				else if (edge.label().text().equals(FeatureRequirement.OPTIONAL.toString()))
				{
					thisFeature.addSubFeature(other);
					other.setRequirement(FeatureRequirement.OPTIONAL);
				}
				else if (edge.label().text().equals(FeatureGroupRelation.OR.toString()))
				{
					thisFeature.addSubFeature(other);
					thisFeature.setGroupRelation(FeatureGroupRelation.OR);
					other.setRequirement(FeatureRequirement.GROUP);
				}
				else if (edge.label().text().equals(FeatureGroupRelation.ALTERNATIVE.toString()))
				{
					thisFeature.addSubFeature(other);
					thisFeature.setGroupRelation(FeatureGroupRelation.ALTERNATIVE);
					other.setRequirement(FeatureRequirement.GROUP);
				}
				else if (edge.label().text().equals(FeatureConstraintType.REQUIRES.toString()))
				{
					thisFeature.addConstraint(new FeatureConstraintImpl(FeatureConstraintType.REQUIRES, thisFeature,
							other));

				}
				else if (edge.label().text().equals(FeatureConstraintType.EXCLUDES.toString()))
				{
					thisFeature.addConstraint(new FeatureConstraintImpl(FeatureConstraintType.EXCLUDES, thisFeature,
							other));
				}
			}
		}
		return baseLine;
	}
}
