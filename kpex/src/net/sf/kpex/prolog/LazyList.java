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
 * Lazy List: produces Cons-like sequences, based on a Source. Saving a lazy
 * list to the database does not make too much sense as it will be discarded
 * when backtracking over its creation point. Note that a Lazy List has its own
 * trail, and is only discarded when backtracking over its creation point.
 */
public class LazyList extends Cons
{
	private boolean bound;

	private Source source;
	private Trail trail;

	public LazyList(Term head, Source source, Trail trail)
	{
		super(head, new Var());
		this.source = source;
		bound = false;
		this.trail = trail;
	}

	public Const getNull()
	{
		return Const.aNil;
	}

	/**
	 * Advances the tail of a lazy list. Note that they inherit getHead() from
	 * Cons.
	 */

	@Override
	public Term getTail()
	{
		advance();
		return super.getTail();
	}

	@Override
	public void undo()
	{
		// if(source.getPersistent()) return;		trail.unwind(0);
		source.stop();
		source = null;
	}

	/**
	 * this permissive definition for bind_to allows a Lazy List to Unify with
	 * any 2 arg constructor chain
	 */
	@Override
	boolean bind_to(Term that, Trail trail)
	{
		return that instanceof Fun && 2 == that.getArity();
	}

	/**
	 * advances the Lazy List, pulling out elements of the Source as needed
	 */
	private final void advance()
	{
		if (bound)
		{
			return;
		}
		Term nextHead = source.getElement();
		Const thisTail;
		if (null == nextHead)
		{
			thisTail = getNull();
		}
		else
		{
			thisTail = new LazyList(nextHead.copy(), source, trail);
		}
		((Var) getArg(1)).unify(thisTail, trail);
		bound = true;
	}
}
