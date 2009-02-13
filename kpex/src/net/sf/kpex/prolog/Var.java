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
 * Part of the Term hierarchy implmenting logical variables. They are subject to
 * reset by application of and undo action keep on the trail stack.
 * 
 * @see Trail
 * @see Prog
 * @see Clause
 * @see Nonvar
 */
public class Var extends Term
{
	protected Term val;

	public Var()
	{
		val = this;
	}

	@Override
	public boolean bind_to(Term x, Trail trail)
	{
		val = x;
		trail.push(this);
		return true;
	}

	@Override
	public boolean eq(Term x)
	{ // not a term compare!
		return ref() == x.ref();
	}

	@Override
	public int getArity()
	{
		return Term.VAR;
	}

	@Override
	public String getKey()
	{
		Term t = ref();
		if (t instanceof Var)
		{
			return null;
		}
		else
		{
			return t.getKey();
		}
	}

	@Override
	public Term ref()
	{
		return unbound() ? this : val.ref();
	}

	@Override
	public String toString()
	{
		return unbound() ? name() : ref().toString();
	}

	@Override
	public void undo()
	{
		val = this;
	}

	protected String name()
	{
		return "_" + Integer.toHexString(hashCode());
	}

	@Override
	Term reaction(Term agent)
	{

		Term R = agent.action(ref());

		if (!(R instanceof Var))
		{
			R = R.reaction(agent);
		}

		return R;
	}

	final boolean unbound()
	{
		return val == this;
	}

	@Override
	boolean unify_to(Term that, Trail trail)
	{
		// expects: this, that are dereferenced
		return val.bind_to(that, trail);
	}
}
