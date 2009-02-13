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

import net.sf.kpex.Builtins;
import net.sf.kpex.DataBase;
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
	public static int tracing = 1;

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
			A = Const.NO;
		}
		return A;
	}

	static public Prog new_engine(Term X, Term G)
	{
		Clause C = new Clause(X, G);
		Prog p = new Prog(C, null);
		return p;
	}

	protected DataBase database = Init.default_db;

	protected Builtins builtins = Init.builtinDict;

	protected Stack orStack;

	protected Prog parent;

	protected Trail trail;

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

		// TODO: not the proper way for errors
		if (database == null)
		{
			IO.assertion("null Init.bboard");
		}
		if (builtins == null)
		{
			IO.assertion("null Init.builtinDict");
		}
	}

	/**
	 * @return the builtins
	 */
	public Builtins getBuiltins()
	{
		if (builtins != null)
		{
			return builtins;
		}
		else if (parent != null)
		{
			return parent.getBuiltins();
		}
		return null;
	}

	/**
	 * @return the database
	 */
	public DataBase getDatabase()
	{
		if (database != null)
		{
			return database;
		}
		else if (parent != null)
		{
			return parent.getDatabase();
		}
		return null;
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
