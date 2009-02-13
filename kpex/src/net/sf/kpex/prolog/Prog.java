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

import java.util.Stack;

import net.sf.kpex.Init;
import net.sf.kpex.io.IO;
import net.sf.kpex.util.Trail;

/**
 * Basic toplevel Prolog Engine. Loads and executes Prolog programs and spawns
 * threads executing on new Prolog Engine objects as well as networking threads
 * and synchronized local and remote Linda transactions
 */
public class Prog extends Source implements Runnable
{
	// CONSTRUCTORS

	public static int tracing = 1;

	// INSTANCE FIELDS

	static public Term ask_engine(Prog p)
	{
		return p.getElement();
	}

	/**
	 * Computes a copy of the first solution X of Goal G.
	 */

	static public Term firstSolution(Term X, Term G)
	{
		Prog p = new_engine(X, G);
		Term A = ask_engine(p);
		if (A != null)
		{
			A = new Fun("the", A);
			p.stop();
		}
		else
		{
			A = Const.aNo;
		}
		return A;
	}

	static public Prog new_engine(Term X, Term G)
	{
		Clause C = new Clause(X, G);
		Prog p = new Prog(C, null);
		return p;
	}

	private Stack orStack;

	private Prog parent;

	// CLASS FIELDS

	private Trail trail;

	// INSTANCE METHODS

	/**
	 * Creates a Prog starting execution with argument "goal"
	 */
	public Prog(Clause goal, Prog parent)
	{
		super(parent);
		this.parent = parent;
		goal = goal.ccopy();
		trail = new Trail();
		orStack = new Stack();
		if (null != goal)
		{
			orStack.push(new Unfolder(goal, this));
		}

		if (null == Init.default_db)
		{
			IO.assertion("null Init.bboard");
		}
		if (null == Init.builtinDict)
		{
			IO.assertion("null Init.builtinDict");
		}
	}

	@Override
	public Term getElement()
	{
		if (null == orStack)
		{
			return null;
		}
		Clause answer = null;
		while (!orStack.isEmpty())
		{
			Unfolder I = (Unfolder) orStack.pop();
			answer = I.getAnswer();
			if (null != answer)
			{
				break;
			}
			Clause goal = (Clause) I.getElement();
			if (null != goal)
			{
				if (I.notLastClause())
				{
					orStack.push(I);
				}
				else
				{
					I.stop();
				}
				if (null == answer)
				{
					orStack.push(new Unfolder(goal, this));
				}
			}
		}
		Term head;
		if (null == answer)
		{
			head = null;
			stop();
		}
		else
		{
			head = answer.getHead();
		}
		return head;
	}

	public final Prog getParent()
	{
		return parent;
	}

	public final Trail getTrail()
	{
		return trail;
	}

	public void run()
	{
		for (;;)
		{
			Term Answer = getElement();
			if (null == Answer)
			{
				break;
			}
		}
	}

	@Override
	public void stop()
	{
		if (null != trail)
		{
			trail.unwind(0);
			trail = null;
		}
		orStack = null;
	}
}
