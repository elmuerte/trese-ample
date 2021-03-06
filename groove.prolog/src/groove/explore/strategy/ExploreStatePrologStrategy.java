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
package groove.explore.strategy;

import groove.explore.util.ExploreCache;
import groove.explore.util.RandomNewStateChooser;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.prolog.GroovePrologException;
import groove.prolog.GroovePrologLoadingException;
import groove.prolog.PrologQuery;
import groove.prolog.QueryResult;
import groove.prolog.engine.GrooveState;
import groove.trans.RuleEvent;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Similar to {@link ExploreStateStrategy} except that it uses a prolog query to
 * reduce the set of RuleEvents.
 * 
 * @author Michiel Hendriks
 */
public class ExploreStatePrologStrategy extends AbstractStrategy implements PrologStrategy
{
	/**
	 * The name of the term that will contain the result, defaults to Result
	 */
	protected String resultTerm;

	/**
	 * The query to execute
	 */
	protected String query;

	/**
	 * Additional prolog code to load befor executing the query
	 */
	protected String usercode;

	protected PrologQuery prolog;

	protected final RandomNewStateChooser collector = new RandomNewStateChooser();

	public ExploreStatePrologStrategy()
	{}

	/*
	 * (non-Javadoc)
	 * @see groove.explore.strategy.AbstractStrategy#prepare(groove.lts.GTS,
	 * groove.lts.GraphState)
	 */
	@Override
	public void prepare(GTS gts, GraphState state)
	{
		super.prepare(gts, state);
		gts.addGraphListener(collector);
	}

	/*
	 * (non-Javadoc)
	 * @see groove.explore.strategy.AbstractStrategy#updateAtState()
	 */
	@Override
	protected void updateAtState()
	{
		// TODO pick the first state
		if (collector.pickRandomNewState() != null)
		{
			atState = collector.pickRandomNewState();
			return;
		}
		// backtracking
		GraphState s = atState;
		do
		{
			s = parentOf(s);
			atState = s == null ? null : getFirstOpenSuccessor(s);
		} while (s != null && atState == null);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * groove.explore.strategy.PrologStrategy#setPrologQuery(java.lang.String)
	 */
	public boolean setPrologQuery(String resultTerm, String query)
	{
		return setPrologQuery(resultTerm, query, null);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * groove.explore.strategy.PrologStrategy#setPrologQuery(java.lang.String,
	 * java.lang.String)
	 */
	public boolean setPrologQuery(String resultTerm, String query, String usercode)
	{
		if (resultTerm == null || resultTerm.length() == 0)
		{
			resultTerm = "Result";
		}
		this.resultTerm = resultTerm;
		this.query = query;
		this.usercode = usercode;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see groove.explore.strategy.Strategy#next()
	 */
	public boolean next()
	{
		if (getAtState() == null)
		{
			getGTS().removeGraphListener(collector);
			return false;
		}
		// rule might have been interrupted
		ExploreCache cache = getCache(false, false);
		Iterator<RuleEvent> matchesIter = getMatchesIterator(cache);
		collector.reset();
		Set<RuleEvent> matches = new HashSet<RuleEvent>();
		while (matchesIter.hasNext())
		{
			matches.add(matchesIter.next());
		}

		if (query != null && query.length() > 0)
		{
			if (prolog == null)
			{
				initializeProlog();
			}
			prolog.setGrooveState(new GrooveState(getGTS(), getAtState(), matches));
			QueryResult result;
			try
			{
				result = prolog.newQuery(query);
			}
			catch (GroovePrologException e)
			{
				// TODO make nice
				e.printStackTrace();
				setClosed(startState());
				return false;
			}
			switch (result.getReturnValue())
			{
				case FAIL:
				case HALT:
					matches.clear();
					break;
				case SUCCESS:
				case SUCCESS_LAST:
					matches.clear();
					Object res = result.getVariables().get(resultTerm);
					if (res instanceof Collection)
					{
						for (Object o : (Collection<?>) res)
						{
							if (o instanceof RuleEvent)
							{
								matches.add((RuleEvent) o);
							}
						}
					}
					else if (res instanceof RuleEvent)
					{
						matches.add((RuleEvent) res);
					}
					else
					{
						// nothing
					}
					break;
				case NOT_RUN:
				default:
					matches.clear();
					// not possble
			}
		}

		for (RuleEvent re : matches)
		{
			getGenerator().applyMatch(getAtState(), re, cache);
		}
		// the current state has been fully explored
		// therefore we can close it
		setClosed(getAtState());
		updateAtState();
		return true;
	}

	/**
	 * Initialize the prolog environment
	 */
	protected void initializeProlog()
	{
		prolog = new PrologQuery();
		if (usercode != null && usercode.length() > 0)
		{
			try
			{
				prolog.init(new StringReader(usercode), "user_code");
			}
			catch (GroovePrologLoadingException e)
			{
				e.printStackTrace();
			}
		}
	}
}
