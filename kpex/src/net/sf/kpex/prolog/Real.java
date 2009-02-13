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
 * Part of the Term hierarchy, implementing double float point numbers.
 * 
 * @see Term
 * @see Nonvar
 */
public class Real extends Num
{
	protected double value;

	public Real(double i)
	{
		value = i;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#getArity()
	 */
	@Override
	public final int getArity()
	{
		return Term.ARITY_REAL;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Num#getValue()
	 */
	@Override
	public final double getValue()
	{
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Nonvar#name()
	 */
	@Override
	public String name()
	{
		return Double.toString(value);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Nonvar#bindTo(net.sf.kpex.prolog.Term,
	 * net.sf.kpex.util.Trail)
	 */
	@Override
	protected boolean bindTo(Term that, Trail trail)
	{
		return super.bindTo(that, trail) && value == ((Real) that).value;
	}
}
