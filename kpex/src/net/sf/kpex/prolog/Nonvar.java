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

import net.sf.kpex.util.Trail;

/**
 * Part of the Prolog Term hierarchy
 * 
 * @see Term
 */
public abstract class Nonvar extends Term
{

	@Override
	public boolean eq(Term that)
	{
		return that instanceof Nonvar && bindTo(that, null);
	}

	public abstract String name();

	@Override
	protected boolean bindTo(Term that, Trail trail)
	{
		return getClass() == that.getClass();
	}

	/**
	 * returns a list representation of the object
	 */
	Const listify()
	{
		return new Cons(this, Const.NIL);
	}

	@Override
	protected
	boolean unifyTo(Term that, Trail trail)
	{
		if (bindTo(that, trail))
		{
			return true;
		}
		else
		{
			return that.bindTo(this, trail);
		}
	}
}
