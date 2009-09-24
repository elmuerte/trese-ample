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

import gnu.prolog.io.ParseException;
import gnu.prolog.io.ReadOptions;
import gnu.prolog.io.TermReader;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import gnu.prolog.vm.Interpreter.Goal;
import groove.graph.Graph;
import groove.lts.GraphState;
import groove.prolog.engine.GrooveEnvironment;
import groove.prolog.engine.GrooveState;

import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface to the prolog engine
 * 
 * @author Michiel Hendriks
 */
public class PrologQuery
{
	/**
	 * The groove prolog library, will always be included
	 */
	public static final String GROOVE_PRO = "/groove/prolog/builtin/groove.pro";

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
	protected InternalQueryResult currentResult;

	/**
	 * The current "groove" state to work with
	 */
	protected GrooveState grooveState;

	/**
	 * The stream to use as default output stream
	 */
	protected OutputStream userOutput;

	public PrologQuery()
	{}

	/**
	 * @param queryGraph
	 */
	public PrologQuery(GrooveState grooveState)
	{
		this();
		setGrooveState(grooveState);
	}

	/**
	 * @param userOutput
	 *            the userOutput to set
	 */
	public void setUserOutput(OutputStream userOutput)
	{
		this.userOutput = userOutput;
	}

	/**
	 * @param grooveState
	 */
	public void setGrooveState(GrooveState value)
	{
		grooveState = value;
		if (env != null)
		{
			env.setGrooveState(grooveState);
		}
	}

	/**
	 * @return the initialized
	 */
	public boolean isInitialized()
	{
		return initialized;
	}

	/**
	 * Initialize the environment
	 * 
	 * @throws GroovePrologLoadingException
	 */
	public void init() throws GroovePrologLoadingException
	{
		init(null, null);
	}

	/**
	 * Initialize the environment
	 * 
	 * @param initStream
	 *            Additional code to process during the loading of the
	 *            environment. Typically used to load user code
	 * @param streamName
	 *            The name to use for the provided stream, is used when creating
	 *            errors. It's best to use the name of a file.
	 * @throws GroovePrologLoadingException
	 */
	public void init(Reader initStream, String streamName) throws GroovePrologLoadingException
	{
		if (initialized)
		{
			return;
		}
		initialized = true;
		currentResult = null;
		getEnvironment();
		if (initStream != null)
		{
			env.loadStream(initStream, streamName);
		}
		interpreter = env.createInterpreter();
		env.runIntialization(interpreter);

		if (!env.getLoadingErrors().isEmpty())
		{
			throw new GroovePrologLoadingException(env.getLoadingErrors());
		}
	}

	/**
	 * Execute a new prolog query
	 * 
	 * @param term
	 * @return
	 * @throws GroovePrologException
	 */
	public QueryResult newQuery(String term) throws GroovePrologException
	{
		if (!initialized)
		{
			init();
		}
		if (currentResult != null)
		{
			// terminate the previous goal
			if (currentResult.getReturnValue() == QueryReturnValue.SUCCESS)
			{
				interpreter.stop(currentResult.getGoal());
			}
		}
		ReadOptions readOpts = new ReadOptions();
		readOpts.operatorSet = env.getOperatorSet();
		TermReader termReader = new TermReader(new StringReader(term));
		try
		{
			Term goalTerm = termReader.readTermEof(readOpts);
			Goal goal = interpreter.prepareGoal(goalTerm);
			currentResult = new InternalQueryResult(goal, term);
			currentResult.rawVars = readOpts.variableNames;
			return next();
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

	/**
	 * @return The current result of the prolog engine
	 */
	public QueryResult current()
	{
		return currentResult;
	}

	/**
	 * Get the next results
	 * 
	 * @return Null if there is no next result
	 * @throws GroovePrologException
	 */
	public QueryResult next() throws GroovePrologException
	{
		if (currentResult == null)
		{
			// TODO: error
			return null;
		}
		if (currentResult.isLastResult())
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
		if (currentResult.getReturnValue() != QueryReturnValue.NOT_RUN)
		{
			currentResult = new InternalQueryResult(currentResult);
		}
		currentResult.setReturnValue(QueryReturnValue.fromInt(rc));
		currentResult.setExecutionTime(stopTime - startTime);
		if (currentResult.getReturnValue() != QueryReturnValue.FAIL
				&& currentResult.getReturnValue() != QueryReturnValue.HALT)
		{
			currentResult.setVariables(TermConverter.convert(currentResult.rawVars));
		}
		return currentResult;
	}

	/**
	 * @return True if there is a next result
	 */
	public boolean hasNext()
	{
		return currentResult != null && !currentResult.isLastResult();
	}

	/**
	 * @return The last return code
	 */
	public QueryReturnValue lastReturnValue()
	{
		if (currentResult != null)
		{
			return currentResult.getReturnValue();
		}
		return QueryReturnValue.NOT_RUN;
	}

	/**
	 * The result object returned on {@link PrologQuery#newQuery(String)} and
	 * {@link PrologQuery#next()}
	 * 
	 * @author Michiel Hendriks
	 */
	protected static class InternalQueryResult implements QueryResult
	{
		protected Goal goal;
		protected String query = "";
		protected QueryReturnValue returnValue = QueryReturnValue.NOT_RUN;
		protected long executionTime = -1;
		protected InternalQueryResult previousResult;
		protected InternalQueryResult nextResult;
		protected Map<String, Object> variables = new HashMap<String, Object>();

		/**
		 * Unprocessed variables
		 */
		protected Map<String, Term> rawVars;

		protected InternalQueryResult(Goal queryQoal, String queryString)
		{
			goal = queryQoal;
			query = queryString;
		}

		protected InternalQueryResult(InternalQueryResult previous)
		{
			previousResult = previous;
			previousResult.nextResult = this;
			goal = previousResult.goal;
			query = previousResult.query;
			rawVars = previousResult.rawVars;
		}

		/**
		 * @return
		 */
		protected Goal getGoal()
		{
			return goal;
		}

		/**
		 * @param value
		 *            the executionTime to set
		 */
		protected void setExecutionTime(long value)
		{
			executionTime = value;
		}

		/**
		 * @param value
		 *            the returnValue to set
		 */
		protected void setReturnValue(QueryReturnValue value)
		{
			returnValue = value;
		}

		/*
		 * (non-Javadoc)
		 * @see groove.prolog.QueryResult#getExecutionTime()
		 */
		public long getExecutionTime()
		{
			return executionTime;
		}

		/**
		 * @param values
		 *            the variables to set
		 */
		public void setVariables(Map<String, Object> values)
		{
			variables = new HashMap<String, Object>(values);
		}

		/*
		 * (non-Javadoc)
		 * @see groove.prolog.QueryResult#getReturnValue()
		 */
		public QueryReturnValue getReturnValue()
		{
			return returnValue;
		}

		/*
		 * (non-Javadoc)
		 * @see groove.prolog.QueryResult#getVariables()
		 */
		public Map<String, Object> getVariables()
		{
			return Collections.unmodifiableMap(variables);
		}

		/*
		 * (non-Javadoc)
		 * @see groove.prolog.QueryResult#isLastResult()
		 */
		public boolean isLastResult()
		{
			return returnValue == QueryReturnValue.SUCCESS_LAST || returnValue == QueryReturnValue.FAIL
					|| returnValue == QueryReturnValue.HALT;
		}

		/*
		 * (non-Javadoc)
		 * @see groove.prolog.QueryResult#nextResult()
		 */
		public QueryResult getNextResult()
		{
			return nextResult;
		}

		/*
		 * (non-Javadoc)
		 * @see groove.prolog.QueryResult#previousResult()
		 */
		public QueryResult getPreviousResult()
		{
			return previousResult;
		}

		/*
		 * (non-Javadoc)
		 * @see groove.prolog.QueryResult#queryString()
		 */
		public String getQuery()
		{
			return query;
		}
	}

	/**
	 * Create the prolog environment. This will initialize the environment in
	 * the standard groove environment. It can be used when you need to make
	 * changes to the environment before loading user code.
	 * 
	 * @return
	 * @throws GroovePrologLoadingException
	 */
	public Environment getEnvironment() throws GroovePrologLoadingException
	{
		if (env == null)
		{
			env = new GrooveEnvironment(null, userOutput);
			env.setGrooveState(grooveState);
			CompoundTerm term = new CompoundTerm(AtomTerm.get("resource"), new Term[] { AtomTerm.get(GROOVE_PRO) });
			env.ensureLoaded(term);
		}
		return env;
	}
}
