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

/**
 * A Fluent is a Jinni Object which has its own state, subject to changes over
 * time.
 * 
 */
public class Fluent extends SystemObject
{
	public Fluent(Prog p)
	{
		trailMe(p);
	}

	private boolean persistent = false;

	/**
	 * Dynamically sets the persistence status of this Fluent. A persistent
	 * Fluent will not have its stop method outomatically called upon
	 * backtracking. A typical example would be a file or socket handle saved to
	 * the database to be reused after backtracking.
	 */
	public void setPersistent(boolean persistent)
	{
		this.persistent = persistent;
	}

	/**
	 * returns true if this Fluent is persistent, false otherwise
	 */
	public boolean getPersistent()
	{
		return persistent;
	}

	/**
	 * Adds this Fluent to the parent Solver's trail, which will eventually call
	 * the undo method of the Fluent on backtracking.
	 */
	protected void trailMe(Prog p)
	{
		if (null != p)
		{
			p.getTrail().push(this);
		}
	}

	public void stop()
	{}

	/**
	 * applies a non-persistent Fluent's stop() method on backtracking
	 */
	@Override
	public void undo()
	{
		if (!persistent)
		{
			stop();
		}
	}
}
