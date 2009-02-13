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

	public final static Nil aNil = new Nil();
	public final static Const aTrue = new true_();
	public final static Const aFail = new fail_();
	public final static Const aYes = new Const("yes");
	public final static Const aNo = new Const("no");
	public final static Const anEof = new Const("end_of_file");

	public final static Const the(Term X)
	{
		return null == X ? Const.aNo : new Fun("the", X);
	}

	private String sym;

	public Const(String s)
	{
		sym = s.intern();
	}

	@Override
	public final String name()
	{
		return sym;
	}

	public String qname()
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

	@Override
	public String toString()
	{
		return qname();
	}

	@Override
	boolean bind_to(Term that, Trail trail)
	{
		return super.bind_to(that, trail) && ((Const) that).sym == sym;
	}

	@Override
	public boolean eq(Term that)
	{
		return that instanceof Const && ((Const) that).sym == sym;
	}

	@Override
	public String getKey()
	{
		return sym;
	}

	/**
	 * returns an arity normally defined as 0
	 * 
	 * @see Term#CONST
	 */
	@Override
	public int getArity()
	{
		return Term.CONST;
	}

	/**
	 * creates a ConstBuiltin from a Const known to be registered as being a
	 * builtin while returning its argument unchanged if it is just a plain
	 * Prolog constant with no builtin code attached to it
	 */
	public Const toBuiltin()
	{
		if (name().equals(Const.aNil.name()))
		{
			return Const.aNil;
		}
		if (name().equals(Const.aNo.name()))
		{
			return Const.aNo;
		}
		if (name().equals(Const.aYes.name()))
		{
			return Const.aYes;
		}

		ConstBuiltin B = (ConstBuiltin) Init.builtinDict.newBuiltin(this);
		if (null == B)
		{
			// IO.mes("not a builtin:"+this);			return this;
		}
		return B;
	}

	@Override
	public String toUnquoted()
	{
		return name();
	}
}
