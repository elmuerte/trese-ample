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
 * Part of the Term hierarchy implementing logical variables. They are subject
 * to reset by application of and undo action keep on the trail stack.
 * 
 * @see Trail
 * @see Prog
 * @see Clause
 * @see Nonvar
 */
public class Var extends Term
{
	/**
	 * The current value of this variable. Will be "this" when unbound
	 */
	protected Term value;

	public Var()
	{
		value = this;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#bindTo(net.sf.kpex.prolog.Term,
	 * net.sf.kpex.util.Trail)
	 */
	@Override
	public boolean bindTo(Term x, Trail trail)
	{
		value = x;
		trail.push(this);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#eq(net.sf.kpex.prolog.Term)
	 */
	@Override
	public boolean eq(Term x)
	{ // not a term compare!
		return getRef() == x.getRef();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#getArity()
	 */
	@Override
	public int getArity()
	{
		return Term.ARITY_VAR;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#getKey()
	 */
	@Override
	public String getKey()
	{
		Term t = getRef();
		if (t instanceof Var)
		{
			return null;
		}
		else
		{
			return t.getKey();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#getRef()
	 */
	@Override
	public Term getRef()
	{
		return unbound() ? this : value.getRef();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return unbound() ? name() : getRef().toString();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#undoBinding()
	 */
	@Override
	public void undoBinding()
	{
		value = this;
	}

	/**
	 * @return
	 */
	protected String name()
	{
		return "_" + Integer.toHexString(hashCode());
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#reaction(net.sf.kpex.prolog.Term)
	 */
	@Override
	protected Term reaction(Term agent)
	{
		Term R = agent.action(getRef());

		if (!(R instanceof Var))
		{
			R = R.reaction(agent);
		}

		return R;
	}

	/**
	 * @return True if this variable is not bound
	 */
	public final boolean unbound()
	{
		return value == this;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#unifyTo(net.sf.kpex.prolog.Term,
	 * net.sf.kpex.util.Trail)
	 */
	@Override
	protected boolean unifyTo(Term that, Trail trail)
	{
		// expects: this, that are dereferenced
		return value.bindTo(that, trail);
	}
}
