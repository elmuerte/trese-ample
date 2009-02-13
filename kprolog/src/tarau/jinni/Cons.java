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
 * List Constructor. Cooperates with terminator Nil.
 * 
 * @see Nil
 */
public class Cons extends Fun
{
	public Cons(String cons, Term x0, Term x1)
	{
		super(cons, x0, x1);
	}

	public Cons(Term x0, Term x1)
	{
		this(".", x0, x1);
	}

	public Term getHead()
	{
		return getArg(0);
	}

	public Term getTail()
	{
		return getArg(1);
	}

	/**
	 * List printer.
	 */
	@Override
	public String toString()
	{
		Term h = getArg(0);
		Term t = getArg(1);
		StringBuffer s = new StringBuffer("[" + watchNull(h));
		for (;;)
		{
			if (t instanceof Nil)
			{
				s.append("]");
				break;
			}
			else if (t instanceof Cons)
			{
				h = ((Cons) t).getArg(0);
				t = ((Cons) t).getArg(1);
				s.append("," + watchNull(h));
			}
			else
			{
				s.append("|" + watchNull(t) + "]");
				break;
			}
		}
		return s.toString();
	}
}
