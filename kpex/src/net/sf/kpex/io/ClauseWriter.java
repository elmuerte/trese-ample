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
package net.sf.kpex.io;

import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.Fun;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * Writer
 */
public class ClauseWriter extends CharWriter
{
	public ClauseWriter(Prog p)
	{
		super(p);
	}

	public ClauseWriter(String f, Prog p)
	{
		super(f, p);
	}

	@Override
	public int putElement(Term t)
	{
		if (null == writer)
		{
			return 0;
		}
		String s = null;
		if (t instanceof Fun && "$string".equals(((Fun) t).name()))
		{
			Const Xs = (Const) ((Fun) t).getArg(0);
			s = Term.charsToString(Xs);
		}
		else
		{
			s = t.pprint();
		}
		IO.print(writer, s);
		return 1;
	}
}
