/*
 * KernelProlog Expanded - Pure Java based Prolog Engine
 * Copyright (C) 1999  Paul Tarau (original KernelProlog)
 * Copyright (C) 2009  Michiel Hendriks
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.sf.kpex.prolog;

import java.util.Enumeration;

import net.sf.kpex.Init;
import net.sf.kpex.io.IO;

/**
 * For a given clause g= A0:-<Guard>,A1,A2...,An, used as resolvent iterates
 * over its possible unfoldings (LD-resolution steps) with clauses of the form
 * B0:-B1,...,Bm in the default database. For each such step, a new clause
 * (A0:-B1,...,Bm,A2...,An)mgu(A1,B0) is built and returned by the Unfolder's
 * getElement method. Before the actual unfolding operations, builtins in Guard
 * are executed, possibly providing bindings for some variables or failing. In
 * case of failure of Guard or of unification, getElement() returns null.
 */
public class Unfolder extends Source
{
	private Enumeration e;
	private Clause goal;
	private int oldtop;
	private Prog prog;

	/**
	 * Creates an Unfolder based on goal clause g for resolution step in program
	 * p
	 */
	public Unfolder(Clause g, Prog p)
	{
		super(p);
		goal = g;
		prog = p;
		e = null;
		trace_goal(g);
		reduceBuiltins();
		if (null != goal)
		{
			Term first = goal.getFirst();
			if (null != first)
			{
				oldtop = prog.getTrail().size();
				e = Init.default_db.toEnumerationFor(first.getKey());
				if (!e.hasMoreElements())
				{
					trace_nomatch(first);
				}
			}
		}
		else
		{
			trace_failing(g);
		}
	}

	/**
	 * Returns a new clause by unfolding the goal with a matching clause in the
	 * database, or null if no such clause exists.
	 */
	@Override
	public Term getElement()
	{
		if (null == e)
		{
			return null;
		}
		Clause unfolded_goal = null;
		while (e.hasMoreElements())
		{
			Term T = (Term) e.nextElement();
			if (!(T instanceof Clause))
			{
				continue;
			}
			// resolution step, over goal/resolvent of the form:
			// Answer:-G1,G2,...,Gn.
			prog.getTrail().unwind(oldtop);
			unfolded_goal = T.toClause().unfold_with_goal(goal, prog.getTrail());
			if (null != unfolded_goal)
			{
				break;
			}
		}
		return unfolded_goal;
	};

	/**
	 * Stops production of more alternatives by setting the clause enumerator to
	 * null
	 */
	@Override
	public void stop()
	{
		e = null;
	}

	/**
	 * Returns a string representation of this unfolder, based on the original
	 * clause it is based on.
	 */
	@Override
	public String toString()
	{
		return null == goal ? "{Unfolder}" : "{Unfolder=> " + goal.pprint() + "}";
	}

	/**
	 * Overrides default trailing by empty action
	 */
	@Override
	protected void trailMe(Prog p)
	{
	// IO.mes("not trailing"+this);
	}

	/**
	 * Extracts an answer at the end of an AND-derivation
	 */
	Clause getAnswer()
	{
		if (null != goal && goal.getBody() instanceof true_)
		{
			return goal.ccopy();
		}
		else
		{
			return null;
		}
	}

	/**
	 * Checks if this clause is the last one, allowing LCO
	 */
	final boolean notLastClause()
	{
		return null != e && e.hasMoreElements();
	}

	/**
	 * Reduces builtin functions after the neck of a clause, before a "real"
	 * atom is unfolded
	 */
	final void reduceBuiltins()
	{
		for (;;)
		{
			Term first = goal.getFirst();
			if (null == first)
			{
				break; // cannot reduce further
			}
			if (first instanceof Conj)
			{ // advances to next (possibly) inline builtin
				goal = new Clause(goal.getHead(), Clause.appendConj(first, goal.getRest()));
				first = goal.getFirst();
			}

			int ok = first.exec(prog); // (possibly) executes builtin

			switch (ok)
			{

				case -1: // nothing to do, this is not a builtin
					break;

				case 1: // builtin suceeds
					// IO.mes("advancing: "+goal);
					goal = new Clause(goal.getHead(), goal.getRest());
					continue; // advance

				case 0: // builtin fails
					goal = null;
					break; // get out

				default: // unexpected code: programming error
					IO.errmes("Bad return code:" + ok + ") in builtin: " + first);
					goal = null;
					break;
			}
			// IO.mes("leaving reduceBuiltins: "+goal);
			break; // leaves loop
		}
	}

	/**
	 * Tracer on exiting g
	 */
	final void trace_failing(Clause g)
	{
		switch (Prog.tracing)
		{
			case 2:
				IO.println("FAILING CALL IN<<<: " + g.getFirst());
				break;
			case 3:
				IO.println("FAILING CALL IN<<<: " + g.pprint());
				break;
		}
	}

	/**
	 * Tracer on entering g
	 */
	final void trace_goal(Clause g)
	{
		switch (Prog.tracing)
		{
			case 2:
				IO.println(">>>: " + g.getFirst());
				break;
			case 3:
				IO.println(">>>: " + g.pprint());
				break;
		}
	}

	/**
	 * Tracer for undefined predicates
	 */
	final void trace_nomatch(Term first)
	{
		if (Prog.tracing > 0)
		{
			IO.println("*** UNDEFINED CALL: " + first.pprint());
		}
	}
}
