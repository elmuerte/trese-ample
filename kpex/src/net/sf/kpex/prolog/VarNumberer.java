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

import java.util.HashMap;
import java.util.Map;

/**
 * Used in implementing uniform replacement of variables with new constants.
 * useful for printing out with nicer variable names.
 * 
 * @see Var
 * @see Clause
 */
public class VarNumberer extends SystemObject
{
	protected int ctr;
	protected Map<Term, Const> dict;

	public VarNumberer()
	{
		dict = new HashMap<Term, Const>();
		ctr = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.kpex.prolog.Term#action(net.sf.kpex.prolog.Term)
	 */
	@Override
	protected Term action(Term place)
	{
		place = place.getRef();
		if (place instanceof Var)
		{
			Const root = dict.get(place);
			if (root == null)
			{
				root = new PseudoVar(ctr++);
				dict.put(place, root);
			}
			place = root;
		}
		return place;
	}
}
