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
import java.io.Reader;
import java.io.StringReader;

/**
 * Builds Jinni Fluents from Java Streams
 */
class CharReader extends Source
{
	protected Reader reader;

	CharReader(Reader reader, Prog p)
	{
		super(p);
		this.reader = reader;
	}

	CharReader(String f, Prog p)
	{
		super(p);
		makeReader(f);
	}

	CharReader(Term t, Prog p)
	{
		super(p);
		reader = new StringReader(t.toUnquoted());
	}

	CharReader(Prog p)
	{
		this(IO.input, p);
	}

	protected void makeReader(String f)
	{
		reader = IO.url_or_file(f);
	}

	@Override
	public Term getElement()
	{
		if (IO.input == reader)
		{
			String s = IO.promptln(">:");
			if (null == s || s.length() == 0)
			{
				return null;
			}
			return new Const(s);
		}

		if (null == reader)
		{
			return null;
		}
		int c = -1;
		try
		{
			c = reader.read();
		}
		catch (IOException e)
		{}
		if (-1 == c)
		{
			stop();
			return null;
		}
		else
		{
			return new Int(c);
		}
	}

	@Override
	public void stop()
	{
		if (null != reader && IO.input != reader)
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{}
			reader = null;
		}
	}
}
