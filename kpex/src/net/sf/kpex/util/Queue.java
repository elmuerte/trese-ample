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
package net.sf.kpex.util;

import java.util.Enumeration;
import java.util.Vector;

import net.sf.kpex.io.IO;

/**
 * Generic dynamic Queue with (amortized) O(1) enq/deq (add and remove)
 * operations
 */
public class Queue implements Cloneable
{
	final static int MAX_QUEUE = 1 << 24;
	final static int MIN_QUEUE = 4;

	private boolean busy;
	private int head, tail;
	private Object queue[];

	public Queue()
	{
		this(0);
	}

	public Queue(int size)
	{
		makeIt(size);
	}

	public Queue(Vector V)
	{
		this(V.size() + MIN_QUEUE);
		for (int i = 0; i < V.size(); i++)
		{
			enq(V.elementAt(i));
		}
	}

	/**
	 * Removes the first element of the queue
	 */
	synchronized public final Object deq()
	{
		enterCritical();
		if (tail == head)
		{
			return null;
		}
		if (4 * count() < queue.length)
		{
			requeue("shrinking");
		}
		Object V = queue[head];
		head = inc(head);
		exitCritical();
		return V;
	}

	/**
	 * Adds an element to the end of the queue
	 */
	synchronized public final boolean enq(Object V)
	{
		enterCritical();
		if (inc(tail) == head)
		{ // full !!!
			if (!requeue("expanding"))
			{
				IO.errmes("Warning: queue overflow at:" + V);
				return false;
			}
		}
		queue[tail] = V;
		tail = inc(tail);
		exitCritical();
		return true;
	}

	synchronized public final boolean isEmpty()
	{
		boolean empty;
		enterCritical();
		empty = tail == head;
		exitCritical();
		return empty;
	}

	public Enumeration toEnumeration()
	{
		return toVector().elements();
	}

	@Override
	public String toString()
	{
		return count() + "/" + queue.length + "=>" + toVector().toString();
	}

	synchronized public Vector toVector()
	{
		// enterCritical(); DEADLOCKS!
		Vector v = new Vector();
		for (int i = head; i != tail; i = inc(i))
		{
			v.addElement(queue[i]);
		}
		// exitCritical();
		return v;
	}

	private final int count()
	{
		return head <= tail ? tail - head : queue.length - head + tail;
	}

	private final void enterCritical()
	{
		while (busy)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{}
		}
		busy = true;
	}

	private final void exitCritical()
	{
		busy = false;
		notifyAll();
	}

	private final int inc(int val)
	{
		return (val + 1) % queue.length;
	}

	synchronized private final void makeIt(int size)
	{
		size = size < MIN_QUEUE ? MIN_QUEUE : size;
		queue = new Object[size];
		head = tail = 0;
		busy = false;
	}

	/**
	 * Dynamically resizes the queue
	 */
	private final boolean requeue(String Mes)
	{
		int newSize = 2 * count();
		if (newSize > MAX_QUEUE || newSize < MIN_QUEUE)
		{
			return false;
		}
		IO.trace(Mes + "!!! " + toString());
		Object[] nqueue = new Object[newSize];
		int j = 0;
		for (int i = head; i != tail; i = inc(i))
		{
			nqueue[j++] = queue[i];
		}
		queue = nqueue;
		head = 0;
		tail = j;
		return true;
	}

	/*
	 * synchronized public Queue toClone() { Queue R=null; try {
	 * R=(Queue)clone(); } catch(CloneNotSupportedException e) {
	 * IO.errmes("Queue:toClone() "+e); } return R; }
	 */

}

/*
 * class QueueIterator { QueueIterator(Queue Q) { this.Q=Q.toClone(); } private
 * Queue Q; Object getNext() { return Q.deq(); } }
 */
