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
import groove.graph.algebra.ValueNode;
import groove.io.AspectualViewGps;
import groove.io.URLLoaderFactory;
import groove.lts.GraphState;
import groove.view.AspectualGraphView;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;
import groove.view.aspect.AspectGraph;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import trese.featuremodels.grooveext.FinalGraphCalc;
import trese.featuremodels.model.Feature;
import trese.featuremodels.model.FeatureModelException;

/**
 * Evaluates a feature model.
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
	 * Create an evaluator. This does not do anything, it just makes sure you
	 * can reuse some standard resources.
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
	public Collection<EvaluationResult> evaluate(Feature baseLine) throws FeatureModelException
	{
		return evaluate(baseLine, false);
	}

	/**
	 * Evaluate the feature model from the baseline
	 * 
	 * @param baseLine
	 * @param findFirst
	 *            If true only find the first valid product configuration.
	 *            Useful for checking if the configuration is valid.
	 * @return
	 * @throws FeatureModelException
	 *             Thrown when the feature model is incomplete
	 */
	public Collection<EvaluationResult> evaluate(Feature baseLine, boolean findFirst) throws FeatureModelException
	{
		if (!initialized)
		{
			initialize();
		}
		EvaluationResult base = new EvaluationResult(baseLine);
		AspectGraph graph = FeatureGraphCreator.createGraph(base);

		// if (true)
		// {
		// // debug export
		// AspectGxl gxl = new AspectGxl();
		// try
		// {
		// gxl.marshalGraph(graph, new File("./featureModel_"
		// + baseLine.getDescription().replaceAll("[^0-9a-zA-Z]+", "_") +
		// ".gst"));
		// }
		// catch (IOException e)
		// {
		// e.printStackTrace();
		// }
		// }

		PrintStream stdout = System.out;
		System.setOut(new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException
			{}
		}));
		Collection<GraphState> finalStates = executeRules(graph, findFirst);
		System.setOut(stdout);
		return extractResults(finalStates, base);
	}

	/**
	 * @param graph
	 * @param findFirst
	 *            If true stop after getting a single result;
	 * @throws FeatureModelException
	 */
	protected Collection<GraphState> executeRules(AspectGraph graph, boolean findFirst) throws FeatureModelException
	{
		grammar.setStartGraph(new AspectualGraphView(graph, null));
		try
		{
			FinalGraphCalc calc = new FinalGraphCalc(grammar.toGrammar());
			if (findFirst)
			{
				return calc.getFinal();
			}
			return calc.getAllFinal();
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
		DefaultLabel nameLabel = DefaultLabel.createLabel(FeatureGraphCreator.LABEL_ID);

		Set<EvaluationResult> resultSet = new HashSet<EvaluationResult>();
		for (GraphState graphState : finalStates)
		{
			Graph graph = graphState.getGraph();
			Set<? extends Edge> edges = graph
					.labelEdgeSet(2, DefaultLabel.createLabel(FeatureGraphCreator.LABEL_ERROR));
			if (!edges.isEmpty())
			{
				// contains errors
				System.out.println("Received an error result");
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

}
