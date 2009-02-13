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

import net.sf.kpex.util.Queue;

/**
 * Merges a List of Sources into a new Source which (fairly) iterates over them
 * breadth first.
 */
public class SourceMerger extends JavaSource
{
	public SourceMerger(Const Xs, Prog p)
	{
		super(p);
		Q = new Queue(Copier.ConsToVector(Xs));
	}

	private Queue Q;

	@Override
	public Term getElement()
	{
		if (null == Q)
		{
			return null;
		}
		while (!Q.isEmpty())
		{
			Source current = (Source) Q.deq();
			if (null == current)
			{
				continue;
			}
			Term T = current.getElement();
			if (null == T)
			{
				continue;
			}
			Q.enq(current);
			return T;
		}
		return null;
	}
}
