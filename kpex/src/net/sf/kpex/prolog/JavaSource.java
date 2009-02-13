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

import java.util.Enumeration;
import java.util.Vector;

/**
 * Builds Jinni Iterators from Java Sequences and Iterator type classes
 */
public class JavaSource extends Source
{
	private Enumeration e;

	public JavaSource(Enumeration e, Prog p)
	{
		super(p);
		this.e = e;
	}

	JavaSource(Prog p)
	{
		super(p);
		e = null;
	}

	JavaSource(Vector V, Prog p)
	{
		super(p);
		e = V.elements();
	}

	@Override
	public Term getElement()
	{
		if (null == e || !e.hasMoreElements())
		{
			return null;
		}
		else
		{
			return (Term) e.nextElement();
		}
	}

	@Override
	public void stop()
	{
		e = null;
	}
}
