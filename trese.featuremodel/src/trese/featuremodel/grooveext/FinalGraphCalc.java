/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.grooveext;

import groove.explore.DefaultScenario;
import groove.explore.Scenario;
import groove.explore.result.Acceptor;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.Result;
import groove.explore.strategy.BFSStrategy;
import groove.explore.strategy.DFSStrategy;
import groove.explore.strategy.Strategy;
import groove.graph.Graph;
import groove.lts.GraphState;
import groove.trans.GraphGrammar;
import groove.trans.RuleSystem;

import java.util.Collection;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class FinalGraphCalc extends groove.calc.DefaultGraphCalculator
{
	/**
	 * @param grammar
	 * @param prototype
	 */
	public FinalGraphCalc(GraphGrammar grammar, boolean prototype)
	{
		super(grammar, prototype);
	}

	/**
	 * @param grammar
	 */
	public FinalGraphCalc(GraphGrammar grammar)
	{
		super(grammar);
	}

	/**
	 * @param rules
	 * @param start
	 */
	public FinalGraphCalc(RuleSystem rules, Graph start)
	{
		super(rules, start);
	}

	private Scenario createScenario(Strategy strategy, Acceptor acceptor)
	{
		DefaultScenario scenario = new DefaultScenario(strategy, acceptor);
		return scenario;
	}

	@Override
	public Collection<GraphState> getAllFinal()
	{
		Scenario scenario = createScenario(new BFSStrategy(), new FinalStateAcceptor());
		scenario.prepare(getGTS());
		return scenario.play().getValue();
	}

	@Override
	public GraphState getFinal()
	{
		Scenario scenario = createScenario(new DFSStrategy(), new FinalStateAcceptor(new Result(1)));
		scenario.prepare(getGTS());
		if (scenario.play().getValue().isEmpty())
		{
			return null;
		}
		return scenario.play().getValue().iterator().next();
	}

}
