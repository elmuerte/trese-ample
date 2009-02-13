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
import net.sf.kpex.util.Trail;

/**
 * Symbolic constant, of arity 0.
 */
public class Const extends Nonvar
{
	public final static Const FAIL = new Fail();
	public final static Const EOF = new Const("end_of_file");
	public final static Nil NIL = new Nil();
	public final static Const NO = new Const("no");
	public final static Const TRUE = new True();
	public final static Const YES = new Const("yes");

	public final static Const the(Term X)
	{
		return null == X ? Const.NO : new Fun("the", X);
	}

	private String sym;

	/**
	 * @param s
	 */
	public Const(String s)
	{
		sym = s.intern();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Nonvar#eq(net.sf.kpex.prolog.Term)
	 */
	@Override
	public boolean eq(Term that)
	{
		return that instanceof Const && ((Const) that).sym == sym;
	}

	/**
	 * returns an arity normally defined as 0
	 * 
	 * @see Term#ARITY_CONST
	 */
	@Override
	public int getArity()
	{
		return Term.ARITY_CONST;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#getKey()
	 */
	@Override
	public String getKey()
	{
		return sym;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Nonvar#name()
	 */
	@Override
	public final String getName()
	{
		return sym;
	}

	/**
	 * @return the quoted name
	 */
	public String quotedName()
	{
		if (0 == sym.length())
		{
			return "''";
		}
		for (int i = 0; i < sym.length(); i++)
		{
			if (!Character.isLowerCase(sym.charAt(i)))
			{
				return '\'' + sym + '\'';
			}
		}
		return sym;
	}

	/**
	 * creates a ConstBuiltin from a Const known to be registered as being a
	 * builtin while returning its argument unchanged if it is just a plain
	 * Prolog constant with no builtin code attached to it
	 */
	// FIXME: should be moved to the builtin dictionary
	public Const toBuiltin()
	{
		if (getName().equals(Const.NIL.getName()))
		{
			return Const.NIL;
		}
		if (getName().equals(Const.NO.getName()))
		{
			return Const.NO;
		}
		if (getName().equals(Const.YES.getName()))
		{
			return Const.YES;
		}

		ConstBuiltin B = (ConstBuiltin) Init.builtinDict.newBuiltin(this);
		if (null == B)
		{
			// IO.mes("not a builtin:"+this);			return this;
		}
		return B;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return quotedName();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#toUnquoted()
	 */
	@Override
	public String toUnquoted()
	{
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Nonvar#bindTo(net.sf.kpex.prolog.Term,
	 * net.sf.kpex.util.Trail)
	 */
	@Override
	protected boolean bindTo(Term that, Trail trail)
	{
		return super.bindTo(that, trail) && ((Const) that).sym == sym;
	}
}
