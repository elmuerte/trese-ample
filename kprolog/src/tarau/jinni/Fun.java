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
package tarau.jinni;

/**
 * Implements compound terms
 * 
 * @see Term
 */
public class Fun extends Const
{
	Term args[];

	@Override
	public final int getArity()
	{
		return args.length;
	}

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

	void init(int arity)
	{
		args = new Term[arity];
		for (int i = 0; i < arity; i++)
		{
			args[i] = new Var();
		}
	}

	public final Term getArg(int i)
	{
		return args[i].ref();
	}

	public final int getIntArg(int i)
	{
		return (int) ((Int) getArg(i)).getValue();
	}

	public final void setArg(int i, Term T)
	{
		args[i] = T;
	}

	public final int putArg(int i, Term T, Prog p)
	{
		return getArg(i).unify(T, p.getTrail()) ? 1 : 0;
	}

	public Fun(String s, Term x0)
	{
		this(s, 1);
		args[0] = x0;
	}

	public Fun(String s, Term x0, Term x1)
	{
		this(s, 2);
		args[0] = x0;
		args[1] = x1;
	}

	public Fun(String s, Term x0, Term x1, Term x2)
	{
		this(s, 3);
		args[0] = x0;
		args[1] = x1;
		args[2] = x2;
	}

	public Fun(String s, Term x0, Term x1, Term x2, Term x3)
	{
		this(s, 4);
		args[0] = x0;
		args[1] = x1;
		args[2] = x2;
		args[3] = x3;
	}

	protected final String funToString()
	{
		if (args == null)
		{
			return qname() + "()";
		}
		int l = args.length;
		return qname() + (l <= 0 ? "" : "(" + show_args() + ")");
	}

	@Override
	public String toString()
	{
		return funToString();
	}

	protected static String watchNull(Term x)
	{
		return null == x ? "null" : x.toString();
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

	@Override
	boolean bind_to(Term that, Trail trail)
	{
		return super.bind_to(that, trail) && args.length == ((Fun) that).args.length;
	}

	@Override
	boolean unify_to(Term that, Trail trail)
	{
		if (bind_to(that, trail))
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
			return that.bind_to(this, trail);
		}
	}

	@Override
	Term token()
	{
		return args[0];
	}

	// stuff allowing polymorphic cloning of Fun subclasses
	// without using reflection - should be probaly faster than
	// reflection classes - to check

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

	protected Fun unInitializedClone()
	{
		Fun f = funClone();
		f.args = new Term[args.length];
		return f;
	}

	protected Fun initializedClone()
	{
		Fun f = funClone();
		f.init(args.length);
		return f;
	}

	@Override
	Term reaction(Term that)
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
	Const listify()
	{
		Cons l = new Cons(new Const(name()), Const.aNil);
		Cons curr = l;
		for (int i = 0; i < args.length; i++)
		{
			Cons tail = new Cons(args[i], Const.aNil);
			curr.args[1] = tail;
			curr = tail;
		}
		return l;
	}

	@Override
	Const toBuiltin()
	{
		if (name().equals(":-") && getArity() == 2)
		{
			return new Clause(args[0], args[1]);
		}
		if (name().equals(",") && getArity() == 2)
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
	boolean isClause()
	{
		return getArity() == 2 && name().equals(":-");
	}
}
