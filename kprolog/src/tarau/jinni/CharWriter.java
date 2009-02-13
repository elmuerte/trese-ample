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

import java.io.IOException;
import java.io.Writer;

/**
 * Writer
 */
class CharWriter extends Sink
{
	protected Writer writer;

	CharWriter(String f, Prog p)
	{
		super(p);
		writer = IO.toFileWriter(f);
	}

	CharWriter(Prog p)
	{
		super(p);
		writer = IO.output;
	}

	@Override
	public int putElement(Term t)
	{
		if (null == writer)
		{
			return 0;
		}
		try
		{
			char c = (char) ((Int) t).intValue();
			writer.write(c);
		}
		catch (IOException e)
		{
			return 0;
		}
		return 1;
	}

	@Override
	public void stop()
	{
		if (null != writer && IO.output != writer)
		{
			try
			{
				writer.close();
			}
			catch (IOException e)
			{}
			writer = null;
		}
	}
}
