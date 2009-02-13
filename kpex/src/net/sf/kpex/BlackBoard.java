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
package net.sf.kpex;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import net.sf.kpex.io.IO;
import net.sf.kpex.prolog.Term;
import net.sf.kpex.util.HashDict;
import net.sf.kpex.util.Queue;

/**
 * This class implementes generic multiple tuples by key operations for use by
 * the PrologBlackBoard class implementing Linda operations on Prolog terms. It
 * uses the Queue class for keeping elemetns of type Term sharing the same key.
 * 
 * @see PrologBlackBoard
 * @see Queue
 * @see Term
 */
public class BlackBoard extends HashDict
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7095762186404217338L;

	/**
	 * creates a new BlackBoard
	 * 
	 * @see Term
	 */
	public BlackBoard()
	{
		super();
	}

	/**
	 * Removes the first Term having key k or the first enumerated key if k is
	 * null
	 */
	synchronized private final Term pick(String k)
	{
		if (k == null)
		{
			Enumeration e = keys();
			if (!e.hasMoreElements())
			{
				return null;
			}
			k = (String) e.nextElement();
			// IO.trace("$$Got key:"+k+this);
		}
		Queue Q = (Queue) get(k);
		if (Q == null)
		{
			return null;
		}
		Term T = (Term) Q.deq();
		if (Q.isEmpty())
		{
			remove(k);
			// IO.trace("$$Removed key:"+k+this);
		}
		return T;
	}

	private final void addBack(String k, Vector V)
	{
		for (Enumeration e = V.elements(); e.hasMoreElements();)
		{
			// cannot be here if k==null
			add(k, (Term) e.nextElement());
		}
	}

	/**
	 * Removes the first matching Term or Clause from the blackboard, to be used
	 * by Linda in/1 operation in PrologBlackBoard
	 * 
	 * @see PrologBlackBoard#in()
	 */

	// synchronized
	protected final Term take(String k, Term pattern)
	{
		Vector V = new Vector();
		Term t;
		while (true)
		{
			t = pick(k);
			if (null == t)
			{
				break;
			}
			// IO.trace("$$After pick: t="+t+this);
			if (t.matches(pattern))
			{
				break;
			}
			else
			{
				V.addElement(t);
			}
		}
		addBack(k, V);
		return t;
	}

	/**
	 * Adds a Term or Clause to the the blackboard, to be used by Linda out/1
	 * operation
	 * 
	 * @see PrologBlackBoard
	 */
	synchronized protected final void add(String k, Term value)
	{
		Queue Q = (Queue) get(k);
		if (Q == null)
		{
			Q = new Queue();
			put(k, Q);
		}
		if (!Q.enq(value))
		{
			IO.errmes("Queue full, key:" + k);
			// IO.trace("$$Added key/val:"+k+"/"+value+"=>"+this);
		}
	}

	/**
	 * This gives an enumeration view for the sequence of objects kept under key
	 * k.
	 */
	synchronized public Enumeration toEnumerationFor(String k)
	{
		Queue Q = (Queue) get(k);
		Vector V = Q == null ? new Vector() : Q.toVector();
		return V.elements();
	}

	synchronized public Enumeration toEnumeration()
	{
		return new BBoardEnumerator(elements());
	}

}

/**
 * Generates an Enumeration view of the blackboard
 * 
 * @see Enumeration
 */

class BBoardEnumerator extends Object implements Enumeration
{
	BBoardEnumerator(Enumeration EH)
	{
		EQ = null;
		this.EH = EH; // elements();
	}

	private Enumeration EQ;
	private Enumeration EH;

	synchronized public boolean hasMoreElements()
	{
		if ((EQ == null || !EQ.hasMoreElements()) && EH.hasMoreElements())
		{
			EQ = ((Queue) EH.nextElement()).toEnumeration();
		}
		return EQ != null && EQ.hasMoreElements();
	}

	synchronized public Object nextElement()
	{
		if (hasMoreElements())
		{
			return EQ.nextElement();
		}
		throw new NoSuchElementException("BBoardEnumerator");
	}

}
