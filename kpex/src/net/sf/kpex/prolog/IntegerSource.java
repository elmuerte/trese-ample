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
 * creates a source of integers based on x=a*x+b formula
 */
public class IntegerSource extends Source
{

	private long a;

	private long b;
	private long fuel;
	private long x;

	public IntegerSource(long fuel, long a, long x, long b, Prog p)
	{
		super(p);
		this.fuel = fuel;
		this.a = a;
		this.b = b;
		this.x = x;
	}

	@Override
	public Term getElement()
	{
		if (fuel <= 0)
		{
			return null;
		}
		Int R = new Int(x);
		x = a * x + b;
		--fuel;
		return R;
	}

	@Override
	public void stop()
	{
		fuel = 0;
	}

	@Override
	public String toString()
	{
		return "{(x->" + a + "*x+" + b + ")[" + fuel + "]=" + x + "}";
	}

}
