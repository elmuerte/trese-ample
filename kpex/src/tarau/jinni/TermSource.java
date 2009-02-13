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
 * Maps a Term to an Source for iterating over its arguments
 */
class TermSource extends Source
{
	TermSource(Nonvar val, Prog p)
	{
		super(p);
		this.val = val;
		pos = 0;
	}

	private Nonvar val;
	private int pos;

	@Override
	public Term getElement()
	{
		Term X;
		if (null == val)
		{
			X = null;
		}
		else if (!(val instanceof Fun))
		{
			X = val;
			val = null;
		}
		else if (0 == pos)
		{
			X = new Const(val.name());
		}
		else if (pos <= ((Fun) val).getArity())
		{
			X = ((Fun) val).getArg(pos - 1);
		}
		else
		{
			X = null;
			val = null;
		}
		pos++;
		return X;
	}

	@Override
	public void stop()
	{
		val = null;
	}
}
