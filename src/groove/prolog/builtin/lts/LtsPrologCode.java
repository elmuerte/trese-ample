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
package groove.prolog.builtin.lts;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.PrologException;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.LTS;
import groove.prolog.builtin.graph.GraphPrologCode;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public abstract class LtsPrologCode extends GraphPrologCode
{
	public static final AtomTerm GRAPHSTATE_ATOM = AtomTerm.get("graph_state");
	public static final AtomTerm TRANSITION_ATOM = AtomTerm.get("transition");
	public static final AtomTerm RULE_ATOM = AtomTerm.get("rule");
	public static final AtomTerm GTS_ATOM = AtomTerm.get("gts");

	protected LtsPrologCode()
	{
		super();
	}

	public static final GraphState getGraphState(Term term) throws PrologException
	{
		if (term instanceof JavaObjectTerm)
		{
			JavaObjectTerm jot = (JavaObjectTerm) term;
			if (!(jot.value instanceof GraphState))
			{
				PrologException.domainError(LtsPrologCode.GRAPHSTATE_ATOM, term);
			}
			return (GraphState) jot.value;
		}
		else
		{
			PrologException.typeError(LtsPrologCode.GRAPHSTATE_ATOM, term);
		}
		return null;
	}

	public static final LTS getLTS(Term term) throws PrologException
	{
		if (term instanceof JavaObjectTerm)
		{
			JavaObjectTerm jot = (JavaObjectTerm) term;
			if (!(jot.value instanceof LTS))
			{
				PrologException.domainError(LtsPrologCode.GTS_ATOM, term);
			}
			return (LTS) jot.value;
		}
		else
		{
			PrologException.typeError(LtsPrologCode.GTS_ATOM, term);
		}
		return null;
	}

	public static final GraphTransition getTransition(Term term) throws PrologException
	{
		if (term instanceof JavaObjectTerm)
		{
			JavaObjectTerm jot = (JavaObjectTerm) term;
			if (!(jot.value instanceof GraphTransition))
			{
				PrologException.domainError(LtsPrologCode.TRANSITION_ATOM, term);
			}
			return (GraphTransition) jot.value;
		}
		else
		{
			PrologException.typeError(LtsPrologCode.TRANSITION_ATOM, term);
		}
		return null;
	}
}
