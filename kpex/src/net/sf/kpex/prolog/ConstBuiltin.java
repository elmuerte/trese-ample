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

/**
 * Template for builtins of arity 0
 */
abstract public class ConstBuiltin extends Const
{
	/**
	 * @param s
	 */
	protected ConstBuiltin(String s)
	{
		super(s);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#exec(net.sf.kpex.prolog.Prog)
	 */
	@Override
	abstract public int exec(Prog p);

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#isBuiltin()
	 */
	@Override
	public boolean isBuiltin()
	{
		return true;
	}
}
