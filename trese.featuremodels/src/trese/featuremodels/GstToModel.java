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
import groove.view.FormatException;

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
 * Covert a groove start graph (gst) to a feature model.
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
		Set<? extends Edge> edges = graph.labelEdgeSet(2, DefaultLabel.createLabel(FeatureGraphCreator.LABEL_FEATURE));
		for (Edge edge : edges)
		{
			Node featureNode = edge.source();
			if (featureNodes.containsKey(featureNode))
			{
				continue;
			}

			String featureId = null;
			String featureDesc = null;
			for (Edge outEdge : graph.outEdgeSet(featureNode))
			{
				if (edge.label().text().equals(FeatureGraphCreator.LABEL_NAME))
				{
					Node nameNode = outEdge.opposite();
					for (Edge e : graph.outEdgeSet(nameNode))
					{
						// not great, but works
						if (e.label().text().startsWith("\""))
						{
							try
							{
								featureId = groove.util.ExprParser.toUnquoted(e.label().text(), '"');
							}
							catch (FormatException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
				else if (edge.label().text().equals(FeatureGraphCreator.LABEL_DESCRIPTION))
				{
					Node nameNode = outEdge.opposite();
					for (Edge e : graph.outEdgeSet(nameNode))
					{
						// not great, but works
						if (e.label().text().startsWith("\""))
						{
							try
							{
								featureDesc = groove.util.ExprParser.toUnquoted(e.label().text(), '"');
							}
							catch (FormatException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
			}

			if (featureId != null)
			{
				FeatureImpl feature = new FeatureImpl(featureId);
				if (featureDesc != null)
				{
					feature.setDescription(featureDesc);
				}
				featureNodes.put(featureNode, feature);
			}
			else
			{
				// TODO error
			}
		}

		edges = graph.labelEdgeSet(2, DefaultLabel.createLabel(FeatureGraphCreator.LABEL_BASE_LINE));
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
				if (edge.label().text().equals(FeatureGraphCreator.LABEL_BASE_LINE))
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
