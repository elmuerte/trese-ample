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

import net.sf.kpex.Init;
import net.sf.kpex.io.IO;
import net.sf.kpex.util.Trail;

/**
 * Implements compound terms
 * 
 * @see Term
 */
public class Fun extends Const
{
	protected static String watchNull(Term x)
	{
		return null == x ? "null" : x.toString();
	}

	public Term args[];

	public Fun(String s)
	{
		super(s);
		args = null;
	}

	/*
	 * public Fun(int arity) { //setDefaultName(); args=new Term[arity]; }
	 */

	public Fun(String s, int arity)
	{
		super(s);
		args = new Term[arity];
	}

	public Fun(String s, Term... terms)
	{
		this(s, terms.length);
		args = new Term[terms.length];
		for (int i = 0; i < terms.length; i++)
		{
			args[i] = terms[i];
		}
	}

	public final Term getArg(int i)
	{
		return args[i].getRef();
	}

	@Override
	public final int getArity()
	{
		return args.length;
	}

	public final int getIntArg(int i)
	{
		return (int) ((Int) getArg(i)).getValue();
	}

	public void init(int arity)
	{
		args = new Term[arity];
		for (int i = 0; i < arity; i++)
		{
			args[i] = new Var();
		}
	}

	@Override
	public Const listify()
	{
		Cons l = new Cons(new Const(getName()), Const.NIL);
		Cons curr = l;
		for (int i = 0; i < args.length; i++)
		{
			Cons tail = new Cons(args[i], Const.NIL);
			curr.args[1] = tail;
			curr = tail;
		}
		return l;
	}

	public final int putArg(int i, Term T, Prog p)
	{
		return getArg(i).unify(T, p.getTrail()) ? 1 : 0;
	}

	public final void setArg(int i, Term T)
	{
		args[i] = T;
	}

	@Override
	public Const toBuiltin()
	{
		if (getName().equals(":-") && getArity() == 2)
		{
			return new Clause(args[0], args[1]);
		}
		if (getName().equals(",") && getArity() == 2)
		{
			return new Conj(args[0], args[1]);
		}
		FunBuiltin B = (FunBuiltin) Init.builtinDict.newBuiltin(this);
		if (null == B)
		{
			return this;
		}
		B = (FunBuiltin) B.funClone();
		B.args = args;
		return B;
	}

	@Override
	public Term token()
	{
		return args[0];
	}

	@Override
	public String toString()
	{
		return funToString();
	}

	final protected Fun funClone()
	{
		Fun f = null;

		try
		{
			// use of clone is needed for "polymorphic" copy
			f = (Fun) clone();
		}
		catch (CloneNotSupportedException e)
		{
			IO.errmes("clone: " + e);
		}

		return f;
	}

	protected final String funToString()
	{
		if (args == null)
		{
			return quotedName() + "()";
		}
		int l = args.length;
		return quotedName() + (l <= 0 ? "" : "(" + show_args() + ")");
	}

	// stuff allowing polymorphic cloning of Fun subclasses
	// without using reflection - should be probaly faster than
	// reflection classes - to check

	protected Fun initializedClone()
	{
		Fun f = funClone();
		f.init(args.length);
		return f;
	}

	protected Fun unInitializedClone()
	{
		Fun f = funClone();
		f.args = new Term[args.length];
		return f;
	}

	@Override
	protected boolean bindTo(Term that, Trail trail)
	{
		return super.bindTo(that, trail) && args.length == ((Fun) that).args.length;
	}

	@Override
	protected boolean isClause()
	{
		return getArity() == 2 && getName().equals(":-");
	}

	@Override
	protected Term reaction(Term that)
	{
		// IO.mes("TRACE>> "+name());
		Fun f = funClone();
		f.args = new Term[args.length];
		for (int i = 0; i < args.length; i++)
		{
			f.args[i] = args[i].reaction(that);
		}
		return f;
	}

	@Override
	protected
	boolean unifyTo(Term that, Trail trail)
	{
		if (bindTo(that, trail))
		{
			for (int i = 0; i < args.length; i++)
			{
				if (!args[i].unify(((Fun) that).args[i], trail))
				{
					return false;
				}
			}
			return true;
		}
		else
		{
			return that.bindTo(this, trail);
		}
	}

	private String show_args()
	{
		StringBuffer s = new StringBuffer(watchNull(args[0]));
		for (int i = 1; i < args.length; i++)
		{
			s.append("," + watchNull(args[i]));
		}
		return s.toString();
	}
}
