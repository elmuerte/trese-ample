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

/**
 * A SystemObject is a Jinni Nonvar with system assigned name
 * 
 */
class SystemObject extends Nonvar
{

	static long ctr = 0;

	private long ordinal;

	SystemObject()
	{
		ordinal = ++ctr;
	}

	@Override
	public String name()
	{
		return "{" + getClass().getName() + "." + ordinal + "}";
	}

	@Override
	boolean bind_to(Term that, Trail trail)
	{
		return super.bind_to(that, trail) && ordinal == ((SystemObject) that).ordinal;
	}

	@Override
	public String toString()
	{
		return name();
	}

	@Override
	public final int getArity()
	{
		return Term.JAVA;
	}
}
