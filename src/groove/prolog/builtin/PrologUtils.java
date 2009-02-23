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
package groove.prolog.builtin;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class
 * 
 * @author Michiel Hendriks
 */
public class PrologUtils
{
	public static final AtomTerm GRAPH_ATOM = AtomTerm.get("graph");
	public static final AtomTerm NODE_ATOM = AtomTerm.get("node");
	public static final AtomTerm EDGE_ATOM = AtomTerm.get("edge");

	public static final AtomTerm GRAPHSTATE_ATOM = AtomTerm.get("graph_state");
	public static final AtomTerm TRANSITION_ATOM = AtomTerm.get("transition");
	public static final AtomTerm RULEEVENT_ATOM = AtomTerm.get("rule_event");
	public static final AtomTerm RULE_ATOM = AtomTerm.get("rule");
	public static final AtomTerm GTS_ATOM = AtomTerm.get("gts");

	public static final AtomTerm VALUENODE_ATOM = AtomTerm.get("valuenode");

	/**
	 * Create a list of JavaObjectTerms from the given collection
	 * 
	 * @param elements
	 * @return
	 */
	public static final List<Term> createJOTlist(Collection<?> elements)
	{
		List<Term> result = new ArrayList<Term>();
		for (Object o : elements)
		{
			result.add(new JavaObjectTerm(o));
		}
		return result;
	}

	/**
	 * Create a list of JavaObjectTerms from the given collection
	 * 
	 * @param elements
	 * @return
	 */
	public static final List<Term> createJOTlist(Object[] elements)
	{
		List<Term> result = new ArrayList<Term>();
		for (Object o : elements)
		{
			result.add(new JavaObjectTerm(o));
		}
		return result;
	}

	private PrologUtils()
	{}
}
