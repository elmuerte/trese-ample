/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel;

import groove.graph.DefaultGraph;
import groove.graph.DefaultLabel;
import groove.graph.GraphInfo;
import groove.graph.Label;
import groove.graph.Node;
import groove.view.aspect.AspectGraph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import trese.featuremodel.model.Feature;
import trese.featuremodel.model.FeatureConstraint;
import trese.featuremodel.model.FeatureModelException;

/**
 * Creates a groove graph for a feature model
 * 
 * @author Michiel Hendriks
 */
public final class FeatureGraphCreator
{
	/**
	 * Used to identify the baseline of the product
	 */
	public static final String LABEL_ROOT_FEATURE = "RootFeature";

	/**
	 * Generic label used for all feature nodes
	 */
	public static final String LABEL_FEATURE = "Feature";

	/**
	 * Label text used for features which are excluded
	 */
	public static final String LABEL_FEATURE_EXCLUDED = "featureExcluded";

	/**
	 * Label text used for features which are included
	 */
	public static final String LABEL_FEATURE_INCLUDED = "featureIncluded";

	/**
	 * Label used for the name value
	 */
	public static final String LABEL_ID = "id";

	/**
	 * Label used for the name value
	 */
	public static final String LABEL_DESCRIPTION = "description";

	/**
	 * Label used for the error node
	 */
	public static final String LABEL_ERROR = "Error";

	/**
	 * Label used for the error message
	 */
	public static final String LABEL_ERROR_MESSAGE = "ErrorMessage";

	/**
	 * 
	 * @param fromResult
	 * @return the created graph
	 * @throws FeatureModelException
	 *             Thrown when a feature model error was encountered
	 */
	public static final AspectGraph createGraph(EvaluationResult fromResult) throws FeatureModelException
	{
		DefaultGraph graph = new DefaultGraph();
		createGraph(fromResult, graph);
		graph.setInfo(new GraphInfo());
		return AspectGraph.getFactory().fromPlainGraph(graph);
	}

	/**
	 * Add the graph representation of the feature result to the given graph
	 * 
	 * @param fromResult
	 * @param graph
	 * @throws FeatureModelException
	 */
	public static final void createGraph(EvaluationResult fromResult, DefaultGraph graph) throws FeatureModelException
	{
		Feature rootFeature = fromResult.getBaseLine();
		Map<Feature, Node> nodeMapping = new HashMap<Feature, Node>();
		Set<FeatureConstraint> constraints = new HashSet<FeatureConstraint>();

		// create all feature nodes
		for (Feature feature : fromResult.getAllFeatures())
		{
			constraints.addAll(feature.getConstraints());

			// create the feature node
			Node node = graph.addNode();
			nodeMapping.put(feature, node);

			graph.addEdge(node, DefaultLabel.createLabel(LABEL_FEATURE), node);

			// add node containing the name
			Node nameNode = graph.addNode();
			graph.addEdge(node, DefaultLabel.createLabel(LABEL_ID), nameNode);
			graph.addEdge(nameNode, createStringLabel(feature.getId()), nameNode);

			String description = feature.getDescription();
			if (description != null && description.length() != 0)
			{
				// add node containing the name
				Node descNode = graph.addNode();
				graph.addEdge(node, DefaultLabel.createLabel(LABEL_DESCRIPTION), descNode);
				graph.addEdge(descNode, createStringLabel(description), descNode);
			}

			// set possible status
			switch (feature.getStatus())
			{
				case INCLUDED:
					graph.addEdge(node, DefaultLabel.createLabel(LABEL_FEATURE_INCLUDED), node);
					break;
				case EXCLUDED:
					graph.addEdge(node, DefaultLabel.createLabel(LABEL_FEATURE_EXCLUDED), node);
					break;
				case NONE:
					// none
					break;
				default:
					throw new FeatureModelException(String.format("Unknown feature status: %s", feature.getStatus()));
			}
		}

		// create all relations between features
		for (Entry<Feature, Node> entry : nodeMapping.entrySet())
		{
			Feature feature = entry.getKey();
			Feature parent = feature.getParentFeature();

			if (parent == null || feature == rootFeature)
			{
				if (feature != rootFeature)
				{
					throw new FeatureModelException(String.format(
							"Detected root feature \"%s\" is not the base line \"%s\"", feature.getId(), fromResult
									.getBaseLine().getId()));
				}
				graph.addEdge(entry.getValue(), DefaultLabel.createLabel(LABEL_ROOT_FEATURE), entry.getValue());
				continue;
			}

			String relationLabel = null;
			switch (feature.getRequirement())
			{
				case MANDATORY:
				case OPTIONAL:
					relationLabel = feature.getRequirement().toString();
					break;
				case GROUP:
					switch (parent.getGroupRelation())
					{
						case ALTERNATIVE:
						case OR:
							relationLabel = parent.getGroupRelation().toString();
							break;
						case NONE:
							throw new FeatureModelException(String.format(
									"Feature \"%s\" assumes a group relation, but parent \"%s\" does not define one",
									feature.getId(), parent.getId()));
						default:
							throw new FeatureModelException(String.format("Unknown feature group relation: %s", parent
									.getGroupRelation()));
					}
					break;
				default:
					throw new FeatureModelException(String.format("Unknown feature requirement: %s", feature
							.getRequirement()));
			}
			Node parentNode = nodeMapping.get(parent);
			if (parentNode == null)
			{
				throw new FeatureModelException(String.format("Parent feature \"%s\" is not in the feature model",
						parent.getId()));
			}
			graph.addEdge(parentNode, DefaultLabel.createLabel(relationLabel), entry.getValue());
		}

		// add all constraints
		for (FeatureConstraint constraint : constraints)
		{
			Node lhsNode = nodeMapping.get(constraint.getLHS());
			Node rhsNode = nodeMapping.get(constraint.getRHS());
			if (rhsNode == null)
			{
				throw new FeatureModelException(String.format("Constraint feature \"%s\" is not in the feature model",
						constraint.getRHS().getId()));
			}
			String relationLabel = null;
			switch (constraint.getType())
			{
				case EXCLUDES:
				case REQUIRES:
					relationLabel = constraint.getType().toString();
					break;
				default:
					throw new FeatureModelException(String.format("Unknown constraint type: %s", constraint.getType()));
			}
			graph.addEdge(lhsNode, DefaultLabel.createLabel(relationLabel), rhsNode);
		}
	}

	/**
	 * @param feature
	 * @return
	 */
	private static Label createStringLabel(String value)
	{
		return DefaultLabel.createLabel(String.format("string:\"%s\"", groove.util.ExprParser.toEscaped(value,
				Collections.singleton('"'))));
	}
}
