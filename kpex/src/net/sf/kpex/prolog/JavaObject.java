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
 * A JavaObject is a Jinni SystemObject with a val slot which containing a
 * wrapped Java object
 */

public class JavaObject extends SystemObject
{
	public JavaObject(Object i)
	{
		// available=true;
		val = i;
	}

	Object val;

	@Override
	public Object toObject()
	{
		return val;
	}

	/*
	 * private boolean available; synchronized public void suspend() {
	 * available=false; while(!available) { try { wait(); }
	 * catch(InterruptedException e) {} } } synchronized public void resume() {
	 * available=true; notifyAll(); }
	 */
}
