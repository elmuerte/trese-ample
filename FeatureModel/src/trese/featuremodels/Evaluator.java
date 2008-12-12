/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels;

import groove.calc.DefaultGraphCalculator;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Node;
import groove.graph.algebra.ValueNode;
import groove.io.AspectGxl;
import groove.io.AspectualViewGps;
import groove.io.URLLoaderFactory;
import groove.lts.GraphState;
import groove.view.AspectualGraphView;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureGroupRelation;
import trese.featuremodels.model.FeatureModelException;
import trese.featuremodels.model.FeatureRequirement;
import trese.featuremodels.modelImpl.FeatureImpl;

/**
 * Evaluates a feature model
 * 
 * @author Michiel Hendriks
 */
public class Evaluator
{
	/**
	 * The groove grammar to load (as resource)
	 */
	public static final String GROOVE_GRAMMAR = "grammars/featuremodel.gps";

	/**
	 * True when the evaluator has been initialized
	 */
	protected boolean initialized;

	/**
	 * The loaded groove grammar
	 */
	protected DefaultGrammarView grammar;

	/**
	 * Create an evaluator.
	 */
	public Evaluator()
	{}

	/**
	 * Evaluate the feature model from the baseline
	 * 
	 * @param baseLine
	 * @return
	 * @throws FeatureModelException
	 *             Thrown when the feature model is incomplete
	 */
	public Collection<EvaluationResult> eval(Feature baseLine) throws FeatureModelException
	{
		if (!initialized)
		{
			initialize();
		}
		EvaluationResult base = new EvaluationResult(baseLine);
		AspectGraph graph = FeatureGraphCreator.createGraph(base);

		if (true)
		{
			// debug export
			AspectGxl gxl = new AspectGxl();
			try
			{
				gxl.marshalGraph(graph, new File("./featureModel.gst"));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		Collection<GraphState> finalStates = executeRules(graph);
		return extractResults(finalStates, base);
	}

	/**
	 * @param graph
	 * @throws FeatureModelException
	 */
	protected Collection<GraphState> executeRules(AspectGraph graph) throws FeatureModelException
	{
		grammar.setStartGraph(new AspectualGraphView(graph, null));
		try
		{
			DefaultGraphCalculator calc = new DefaultGraphCalculator(grammar.toGrammar());
			return calc.getAllMax();
		}
		catch (FormatException e)
		{
			throw new FeatureModelException(e);
		}
	}

	/**
	 * @param finalStates
	 * @param base
	 * @return
	 * @throws FeatureModelException
	 */
	protected Collection<EvaluationResult> extractResults(Collection<GraphState> finalStates, EvaluationResult base)
			throws FeatureModelException
	{
		DefaultLabel nameLabel = DefaultLabel.createLabel(FeatureGraphCreator.LABEL_NAME);

		Set<EvaluationResult> resultSet = new HashSet<EvaluationResult>();
		for (GraphState graphState : finalStates)
		{
			Graph graph = graphState.getGraph();
			Set<? extends Edge> edges = graph
					.labelEdgeSet(2, DefaultLabel.createLabel(FeatureGraphCreator.LABEL_ERROR));
			if (!edges.isEmpty())
			{
				// contains errors
				continue;
			}

			EvaluationResult result = new EvaluationResult(base);
			edges = graph.labelEdgeSet(2, DefaultLabel.createLabel(FeatureGraphCreator.LABEL_FEATURE_INCLUDED));
			for (Edge edge : edges)
			{
				Node node = edge.opposite();
				String featureName = null;

				for (Edge outEdge : graph.outEdgeSet(node))
				{
					if (outEdge.label().equals(nameLabel))
					{
						node = outEdge.opposite();
						if (node instanceof ValueNode)
						{
							featureName = (String) ((ValueNode) node).getValue();
						}
					}
				}

				if (featureName == null)
				{
					throw new FeatureModelException("Unnamed feature node");
				}

				Feature feature = result.getFeatureByName(featureName);
				if (feature == null)
				{
					throw new FeatureModelException(String.format("Unknown feature included in the result graph: %s",
							featureName));
				}
				result.addIncludedFeature(feature);
			}
			resultSet.add(result);
		}
		return resultSet;
	}

	/**
	 * Initialize the evaluator
	 */
	protected void initialize()
	{
		URL grammarUrl = Evaluator.class.getResource(GROOVE_GRAMMAR);
		if (grammarUrl == null)
		{
			throw new IllegalStateException(String.format("Unable to load groove grammar from: %s%s", Evaluator.class
					.getResource(""), GROOVE_GRAMMAR));
		}
		try
		{
			AspectualViewGps loader = URLLoaderFactory.getLoader(grammarUrl);
			grammar = loader.unmarshal(grammarUrl);
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
		initialized = true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Evaluator eval = new Evaluator();
		eval.initialize();
		try
		{
			FeatureImpl myProduct = new FeatureImpl("_");

			FeatureImpl fA = new FeatureImpl("A");
			fA.setRequirement(FeatureRequirement.MANDATORY);
			fA.setGroupRelation(FeatureGroupRelation.ALTERNATIVE);
			myProduct.addSubFeature(fA);

			FeatureImpl fB = new FeatureImpl("B");
			fA.addSubFeature(fB);

			FeatureImpl fC = new FeatureImpl("C");
			fA.addSubFeature(fC);

			FeatureImpl fD = new FeatureImpl("D");
			fD.setRequirement(FeatureRequirement.OPTIONAL);
			fD.setGroupRelation(FeatureGroupRelation.OR);
			myProduct.addSubFeature(fD);

			FeatureImpl fE = new FeatureImpl("E");
			fD.addSubFeature(fE);

			FeatureImpl fF = new FeatureImpl("F");
			fD.addSubFeature(fF);

			long startTime = System.nanoTime();
			Collection<EvaluationResult> result = eval.eval(myProduct);
			System.out.println(String.format("Required %d ms", (System.nanoTime() - startTime) / 1000000));
			for (EvaluationResult res : result)
			{
				System.out.print("Valid product configuration: ");
				SortedSet<String> features = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
				for (Feature f : res.getIncludedFeatures())
				{
					features.add(f.getName());
				}
				System.out.println(features.toString());
			}
		}
		catch (FeatureModelException e)
		{
			e.printStackTrace();
		}
	}
}
