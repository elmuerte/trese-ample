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

abstract public class Source extends Fluent
{

	public Source(Prog p)
	{
		super(p);
	}

	abstract public Term getElement();

	Const toList()
	{
		Term head = getElement();
		if (null == head)
		{
			return Const.aNil;
		}
		Cons l = new Cons(head, Const.aNil);
		Cons curr = l;
		for (;;)
		{
			head = getElement();
			if (null == head)
			{
				break;
			}
			Cons tail = new Cons(head, Const.aNil);
			curr.args[1] = tail;
			curr = tail;
		}
		return l;
	}

	Term toFun()
	{
		Vector V = new Vector();
		Term X;
		while (null != (X = getElement()))
		{
			V.addElement(X);
		}
		return Copier.VectorToFun(V);
	}
}
