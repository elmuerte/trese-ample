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

public class Conj extends Cons
{
	static public final Term getHead(Term T)
	{
		T = T.ref();
		return T instanceof Conj ? ((Conj) T).getArg(0) : T;
	}

	static public final Term getTail(Term T)
	{
		T = T.ref();
		return T instanceof Conj ? ((Conj) T).getArg(1) : Const.aTrue;
	}

	public Conj(Term x0, Term x1)
	{
		super(",", x0, x1);
	}

	public String conjToString()
	{
		Term h = args[0].ref();
		Term t = args[1].ref();
		StringBuffer s = new StringBuffer(watchNull(h));
		for (;;)
		{
			if (!(t instanceof Conj))
			{
				s.append("," + t);
				break;
			}
			else
			{
				h = ((Conj) t).args[0].ref();
				t = ((Conj) t).args[1].ref();
				s.append("," + watchNull(h));
			}
		}
		return s.toString();
	}

	@Override
	public String toString()
	{
		return funToString();
	}
}
