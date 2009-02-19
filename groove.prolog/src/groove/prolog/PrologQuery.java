/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog;

import gnu.prolog.database.PrologTextLoaderError;
import gnu.prolog.io.ParseException;
import gnu.prolog.io.ReadOptions;
import gnu.prolog.io.TermReader;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;
import gnu.prolog.vm.Interpreter.Goal;
import groove.graph.Graph;
import groove.lts.GraphState;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * Interface to the prolog engine
 * 
 * @author Michiel Hendriks
 */
public class PrologQuery
{
	public static final String GROOVE_PRO = "/groove/prolog/builtin/groove.pro";

	private static final int NOT_RUN = -255;

	/**
	 * The graph that will be queried.
	 */
	protected Graph graph;

	/**
	 * The graph state that will be queried.
	 */
	protected GraphState graphState;

	/**
	 * Will be true when the interface has been initialized.
	 */
	protected boolean initialized;

	/**
	 * The used environment
	 */
	protected GrooveEnvironment env;

	/**
	 * duh
	 */
	protected Interpreter interpreter;

	/**
	 * The current result of the query
	 */
	protected QueryResult currentResult;

	/**
	 * @param queryGraph
	 */
	public PrologQuery(Graph queryGraph)
	{
		setGraph(queryGraph);
	}

	/**
	 * @param queryGraphState
	 */
	public PrologQuery(GraphState queryGraphState)
	{
		setGraphState(queryGraphState);
	}

	/**
	 * @param value
	 *            the graph to set
	 */
	public void setGraph(Graph value)
	{
		graph = value;
		if (env != null)
		{
			env.setGraph(value);
		}
	}

	/**
	 * @param value
	 *            the graphState to set
	 */
	public void setGraphState(GraphState value)
	{
		graphState = value;
		setGraph(value.getGraph());
	}

	/**
	 * Initialize the environment
	 */
	protected void init()
	{
		if (initialized)
		{
			return;
		}
		initialized = true;
		currentResult = null;
		env = new GrooveEnvironment();
		env.setGraph(graph);
		CompoundTerm term = new CompoundTerm(AtomTerm.get("resource"), new Term[] { AtomTerm.get(GROOVE_PRO) });
		env.ensureLoaded(term);
		interpreter = env.createInterpreter();
		env.runIntialization(interpreter);
		for (PrologTextLoaderError err : (List<PrologTextLoaderError>) env.getLoadingErrors())
		{
			// TODO: make invalid
			System.err.println(err);
			// err.printStackTrace();
		}
	}

	/**
	 * Execute a new prolog query
	 * 
	 * @param term
	 * @return
	 * @throws GroovePrologException
	 */
	public Map<String, Term> newQuery(String term) throws GroovePrologException
	{
		if (!initialized)
		{
			init();
		}
		if (currentResult != null)
		{
			// terminate the previous goal
			if (currentResult.returnValue == PrologCode.SUCCESS)
			{
				interpreter.stop(currentResult.goal);
			}
		}
		ReadOptions readOpts = new ReadOptions();
		readOpts.operatorSet = env.getOperatorSet();
		TermReader termReader = new TermReader(new StringReader(term));
		try
		{
			Term goalTerm = termReader.readTermEof(readOpts);
			Goal goal = interpreter.prepareGoal(goalTerm);
			currentResult = new QueryResult(goal);
			currentResult.variables = readOpts.variableNames;
			return next();
			// int result;
			// do
			// {
			// long startTime = System.nanoTime();
			// result = interpreter.execute(goal);
			// long stopTime = System.nanoTime();
			// System.out.println("Execution time: " + (stopTime - startTime) /
			// 1000000.0 + "ms");
			//
			// if (result != PrologCode.FAIL)
			// {
			// WriteOptions wr_ops = new WriteOptions();
			// wr_ops.operatorSet = new OperatorSet();
			// Iterator ivars = readOpts.variableNames.keySet().iterator();
			// while (ivars.hasNext())
			// {
			// String name = (String) ivars.next();
			// out.print(name + " = ");
			// out.print(wr_ops, ((Term)
			// readOpts.variableNames.get(name)).dereference());
			// out.println();
			// }
			// }
			// else
			// {
			// out.print("No more results");
			// }
			// out.println();
			// out.flush();
			// } while (result == PrologCode.SUCCESS);
			//
			// return null;
		}
		catch (ParseException e)
		{
			throw new GroovePrologException(e);
		}
		catch (PrologException e)
		{
			throw new GroovePrologException(e);
		}
	}

	public Map<String, Term> next() throws GroovePrologException
	{
		if (currentResult == null)
		{
			// TODO: error
			return null;
		}
		if (currentResult.returnValue != PrologCode.SUCCESS && currentResult.returnValue != NOT_RUN)
		{
			// no more results
			return null;
		}

		long startTime = System.nanoTime();
		int rc;
		try
		{
			rc = interpreter.execute(currentResult.goal);
		}
		catch (PrologException e)
		{
			throw new GroovePrologException(e);
		}
		long stopTime = System.nanoTime();
		if (currentResult.returnValue != NOT_RUN)
		{
			currentResult = new QueryResult(currentResult);
		}
		currentResult.returnValue = rc;
		currentResult.time = stopTime - startTime;
		return currentResult.variables;
	}

	/**
	 * @return True if there is a next result
	 */
	public boolean hasNext()
	{
		return (currentResult != null) && (lastReturnValue() == PrologCode.SUCCESS);
	}

	/**
	 * @return The last return code
	 */
	public int lastReturnValue()
	{
		if (currentResult != null)
		{
			return currentResult.returnValue;
		}
		return NOT_RUN;
	}

	/**
	 * @return The time in nano seconds the last query took
	 */
	public long lastExecutionTime()
	{
		if (currentResult != null)
		{
			return currentResult.time;
		}
		return -1;
	}

	public/*
		 * Result data store.
		 * @author Michiel Hendriks
		 */
	class QueryResult
	{
		/**
		 * The previous result
		 */
		QueryResult previous;

		/**
		 * The next result
		 */
		QueryResult next;

		/**
		 * The goal which is being executed
		 */
		Goal goal;

		/**
		 * The last result value
		 */
		int returnValue = NOT_RUN;

		/**
		 * The time taken to calculate the result (in nano seconds)
		 */
		long time = 0;

		/**
		 * The variables in the query
		 */
		Map<String, Term> variables;

		/**
		 * @param queryGoal
		 */
		public QueryResult(Goal queryGoal)
		{
			goal = queryGoal;
		}

		public QueryResult(QueryResult previousResult)
		{
			this(previousResult.goal);
			previous = previousResult;
			previous.next = this;
			variables = previousResult.variables;
		}
	}
}
