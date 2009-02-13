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
package net.sf.kpex;

import java.util.Stack;

/**
 * Implements a stack of undo actions for backtracking, and in particular,
 * resetting a Var's val fiels to unbound (i.e. this).
 * 
 * @see Prog
 * @see Clause
 * @see Term
 * @see Var
 */
public class Trail extends Stack
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7383604147299422937L;

	Trail()
	{
		super();
	};

	public String name()
	{
		return "trail" + hashCode() % 64;
	}

	public String pprint()
	{
		return name() + "\n" + super.toString() + "\n";
	}

	/**
	 * Used to undo bindings after unification, if we intend to leave no side
	 * effects.
	 */

	synchronized final public void unwind(int to)
	{
		// IO.mes("unwind TRAIL: "+name()+": "+size()+"=>"+to);
		// if(to>size())
		// IO.assertion("unwind attempted from smaller to larger top");
		for (int i = size() - to; i > 0; i--)
		{
			Term V = (Term) pop();
			V.undo();
		}
	}

	public String stat()
	{
		return "Trail=" + size();
	}
}
