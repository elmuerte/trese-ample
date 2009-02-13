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

import net.sf.kpex.io.IO;
import net.sf.kpex.util.HashDict;

/**
 * Term Copier agent. Has its own Variable dictionnary. Uses a generic action
 * propagator which recurses over Terms.
 */
public class Copier extends SystemObject
{
	/**
	 * Extracts the free variables of a Term, using a generic action/reaction
	 * mechanism which takes care of recursing over its structure. It can be
	 * speeded up through specialization.
	 */
	final static Const anAnswer = new Const("answer");

	static Vector ConsToVector(Const Xs)
	{
		Vector V = new Vector();
		Term t = Xs;
		for (;;)
		{
			if (t instanceof Nil)
			{
				break;
			}
			else if (t instanceof Cons)
			{
				Cons c = (Cons) t;
				V.addElement(c.getArg(0));
				t = c.getArg(1);
			}
			else if (t instanceof Const)
			{
				V.addElement(t);
				break;
			}
			else
			{
				V = null;
				IO.errmes("bad Cons in ConsToVector: " + t);
				break;
			}
		}
		// IO.mes("V="+V);
		return V;
	}

	/**
	 * Reifies an Enumeration as a Vector. Vector.elements can give back the
	 * enumeration if needed.
	 * 
	 * @see Copier
	 */
	static Vector EnumerationToVector(Enumeration e)
	{
		Vector V = new Vector();
		while (e.hasMoreElements())
		{
			V.addElement(e.nextElement());
		}
		return V;
	}

	// Term copyMe(Term that) {
	// return that.reaction(this);
	// }

	/**
	 * Converts a reified Enumeration to functor based on name of Const c and
	 * args being the elements of the Enumeration.
	 */

	static Term toFun(Const c, Enumeration e)
	{
		Vector V = EnumerationToVector(e);
		int arity = V.size();
		if (arity == 0)
		{
			return c;
		}
		Fun f = new Fun(c.name(), arity);
		for (int i = 0; i < arity; i++)
		{
			f.args[i] = (Term) V.elementAt(i);
		}
		return f;
	}

	/**
	 * Represents a list [f,a1...,an] as f(a1,...,an)
	 */

	static Fun VectorToFun(Vector V)
	{
		Const f = (Const) V.firstElement();
		int arity = V.size() - 1;
		Fun T = new Fun(f.name(), arity);
		for (int i = 0; i < arity; i++)
		{
			T.args[i] = (Term) V.elementAt(i + 1);
		}
		return T;
	}

	private HashDict dict;

	/**
	 * creates a new Copier together with its related HashDict for variables
	 */
	Copier()
	{
		dict = new HashDict();
	}

	/**
	 * This action only defines what happens here (at this <b> place </b>).
	 * Ageneric mechanism will be used to recurse over Terms in a (truly:-)) OO
	 * style (well, looks more like some Haskell stuff, but who cares).
	 */
	@Override
	Term action(Term place)
	{

		if (place instanceof Var)
		{
			Var root = (Var) dict.get(place);
			if (null == root)
			{
				root = new Var();
				dict.put(place, root);
			}
			place = root;
		}

		return place;
	}

	Term getMyVars(Term that)
	{
		/* Term */that.reaction(this);
		return toFun(anAnswer, dict.keys());
	}
}
