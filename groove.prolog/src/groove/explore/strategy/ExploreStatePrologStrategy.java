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
import groove.prolog.GroovePrologException;
import groove.prolog.GroovePrologLoadingException;
import groove.prolog.PrologQuery;
import groove.prolog.QueryResult;
import groove.prolog.engine.GrooveState;
import groove.trans.RuleEvent;

import java.io.StringReader;
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
	protected String resultTerm;

	protected String query;

	protected String usercode;

	protected PrologQuery prolog;

	public ExploreStatePrologStrategy()
	{}

	/*
	 * (non-Javadoc)
	 * @see groove.explore.strategy.AbstractStrategy#updateAtState()
	 */
	@Override
	protected void updateAtState()
	{
	// unused
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
		if (!getGTS().isOpen(startState()))
		{
			return false;
		}
		// rule might have been interrupted
		ExploreCache cache = getCache(true, false);
		Iterator<RuleEvent> matchesIter = getMatchesIterator(cache);
		Set<RuleEvent> matches = new HashSet<RuleEvent>();
		while (matchesIter.hasNext())
		{
			matches.add(matchesIter.next());
		}

		if (prolog == null)
		{
			initializeProlog();
		}
		prolog.setGrooveState(new GrooveState(startState(), matches));
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
				// get the results
				break;
			case NOT_RUN:
			default:
				matches.clear();
				// not possble
		}

		for (RuleEvent re : matches)
		{
			getGenerator().applyMatch(startState(), re, cache);
		}
		// the current state has been fully explored
		// therefore we can close it
		setClosed(startState());
		return false;
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
