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

import java.util.Vector;

/**
 * Builds Jinni Fluents from Java Streams
 */
public class TermCollector extends Sink
{
	protected Vector buffer;
	private Prog p;

	public TermCollector(Prog p)
	{
		super(p);
		this.p = p;
		buffer = new Vector();
	}

	@Override
	public int putElement(Term T)
	{
		buffer.addElement(T);
		return 1;
	}

	@Override
	public void stop()
	{
		buffer = null;
	}

	@Override
	public Term collect()
	{
		return new JavaSource(buffer, p);
	}
}
