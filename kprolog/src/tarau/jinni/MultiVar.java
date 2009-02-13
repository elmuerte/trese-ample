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

import java.util.Stack;

/**
 * Varable-like entity, with a multiple values, in stack order. Set operations
 * are undone on backtraking, when the previous value is restored.
 */
class MultiVar extends Fluent
{
	Stack vals;

	MultiVar(Term T, Prog p)
	{
		super(p);
		vals = new Stack();
		vals.push(T.ref());
	}

	final void set(Term T, Prog p)
	{
		vals.push(T);
		p.getTrail().push(this);
	}

	public Term val()
	{
		return (Term) vals.peek();
	}

	/**
	 * cannot be made presistent
	 */
	@Override
	protected void undo()
	{
		vals.pop();
	}

	@Override
	public String toString()
	{
		return "MultiVar[" + vals.size() + "]->{" + vals.peek().toString() + "}";
	}
}
