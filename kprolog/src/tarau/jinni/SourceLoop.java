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

import java.util.Vector;

/**
 * An Infinite Source. If based on a finite Source, it moves to its the first
 * element after reaching its last element. A SourceLoop returns 'no' if the
 * original Source is empty. In case the original Source is infinite, a
 * SourceLoop will return the same elements as the original Source. (In
 * particular, this happens if the original Source is also a Source loop).
 */
class SourceLoop extends Source
{
	private Vector v;
	Source s;
	private int i;

	SourceLoop(Source s, Prog p)
	{
		super(p);
		this.s = s;
		v = new Vector();
		i = 0;
	}

	private final Term getMemoized()
	{
		if (null == v || v.size() <= 0)
		{
			return null;
		}
		Term T = (Term) v.elementAt(i);
		i = (i + 1) % v.size();
		return T;
	}

	@Override
	public Term getElement()
	{
		Term T = null;
		if (null != s)
		{ // s is alive			T = s.getElement();
			if (null != T)
			{
				v.addElement(T);
			}
			else
			{
				s = null;
			}
		}
		if (null == s)
		{
			T = getMemoized();
		}
		return T;
	}

	@Override
	public void stop()
	{
		v = null;
		s = null;
	}
}
