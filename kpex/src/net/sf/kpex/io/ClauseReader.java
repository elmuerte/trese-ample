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
package net.sf.kpex.io;

import java.io.IOException;
import java.io.Reader;

import net.sf.kpex.prolog.Clause;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.Fun;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * Builds Jinni Fluents from Java Streams
 */
public class ClauseReader extends CharReader
{
	static Fun extract_info(Clause C)
	{
		if (null == C)
		{
			return null;
		}
		Term Vs = C.varsOf();
		Clause SuperC = new Clause(Vs, C);
		SuperC.dict = C.dict;
		Clause NamedSuperC = SuperC.cnumbervars(false);
		Term Ns = NamedSuperC.getHead();
		Term NamedC = NamedSuperC.getBody();
		return new Fun("clause", C, Vs, NamedC, Ns);
	}

	protected Parser parser;

	public ClauseReader(Prog p)
	{
		super(p);
		make_parser("standard input");
	}

	public ClauseReader(Reader reader, Prog p)
	{
		super(reader, p);
		make_parser("from shared reader");
	}

	public ClauseReader(String f, Prog p)
	{
		super(f, p);
		make_parser(f);
	}

	/**
	 * parses from a string representation of a term
	 */
	public ClauseReader(Term t, Prog p)
	{
		super(t, p);
		make_parser("string parser");
	}

	@Override
	public Term getElement()
	{
		Clause C = null;
		if (// IO.peer!=null &&		reader.equals(IO.input))
		{
			String s = IO.promptln(">:");
			if (null == s || 0 == s.length())
			{
				C = null;
			}
			else
			{
				C = new Clause(s);
			}
		}
		else if (null != parser)
		{
			if (parser.atEOF())
			{
				C = null;
				stop();
			}
			else
			{
				C = parser.readClause();
			}
			if (C != null && C.getHead().eq(Const.anEof))
			{
				C = null;
				stop();
			}
		}
		return extract_info(C);
	}

	@Override
	public void stop()
	{
		super.stop();
		parser = null;
	}

	void make_parser(String f)
	{
		if (null != reader)
		{
			try
			{
				parser = new Parser(reader);
			}
			catch (IOException e)
			{
				IO.errmes("unable to build parser for: " + f);
			}
		}
		else
		{
			parser = null;
		}
	}
}
