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

/**
 * Used in implementing uniform replacement of variables with new constants.
 * useful for printing out with nicer variable names.
 * 
 * @see Var
 * @see Clause
 */
class VarNumberer extends SystemObject
{
	HashDict dict;
	int ctr;

	VarNumberer()
	{
		dict = new HashDict();
		ctr = 0;
	}

	@Override
	Term action(Term place)
	{
		place = place.ref();
		// IO.trace(">>action: "+place);
		if (place instanceof Var)
		{
			Const root = (Const) dict.get(place);
			if (null == root)
			{
				root = new PseudoVar(ctr++);
				dict.put(place, root);
			}
			place = root;
		}
		// IO.trace("<<action: "+place);
		return place;
	}
}
