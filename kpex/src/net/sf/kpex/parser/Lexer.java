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
package net.sf.kpex.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

import net.sf.kpex.io.IO;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.Int;
import net.sf.kpex.prolog.Term;
import net.sf.kpex.prolog.Var;
import net.sf.kpex.util.HashDict;

/**
 * Lexicographic analyser reading from a stream
 */
class Lexer extends StreamTokenizer
{
	protected final static String ANONYMOUS = "_".intern();

	protected Reader input;

	protected HashDict dict;

	protected boolean inClause = false;

	public Lexer() throws IOException
	{
		this(IO.input);
	}

	public Lexer(Reader I) throws IOException
	{
		super(I);
		input = I;
		parseNumbers();
		eolIsSignificant(true);
		ordinaryChar('.');
		ordinaryChar('-'); // creates problems with -1 etc.
		ordinaryChar('/');
		quoteChar('\'');
		quoteChar('\"');
		wordChar('$');
		wordChar('_');
		slashStarComments(true);
		commentChar('%');
		dict = new HashDict();
	}

	/**
	 * String based constructor. Used in queries ended by \n + prolog2java.
	 */

	public Lexer(String s) throws Exception
	{
		this(IO.string_to_stream(s));
	}

	/**
	 * Path+File name based constructor Used in prolog2java
	 */

	public Lexer(String path, String s) throws IOException
	{
		this(IO.url_or_file(path + s)); // stream
	}

	public boolean atEOF()
	{
		boolean yes = TT_EOF == ttype;
		if (yes)
		{
			try
			{
				input.close();
			}
			catch (IOException e)
			{
				IO.trace("unable to close atEOF");
			}
		}
		return yes;
	}

	protected final Term makeConst(String s)
	{
		return new ConstToken(s);
	}

	protected Term next() throws IOException
	{
		int n = nextToken();
		inClause = true;
		Term T;
		switch (n)
		{
			case TT_WORD:
				T = getWord(false);
				break;

			case '\'':
				T = getWord(true);
				break;

			case TT_NUMBER:
				T = makeNumber(nval);
				break;

			case TT_EOF:
				T = new EofToken();
				inClause = false;
				break;

			case TT_EOL:
				T = next();
				break;

			case '-':
				if (TT_NUMBER == nextToken())
				{
					T = makeNumber(-nval);
				}
				else
				{
					pushBack();
					T = makeConst(charToString(n));
				}

				break;

			case ':':
				if ('-' == nextToken())
				{
					T = new IffToken(":-");
				}
				else
				{
					pushBack();
					T = makeConst(charToString(n));
				}
				break;

			case '.':
				int c = nextToken();
				if (TT_EOL == c || TT_EOF == c)
				{
					inClause = false;
					// dict.clear(); ///!!!: this looses Var names
					T = new EocToken();
				}
				else
				{
					pushBack();
					T = makeConst(charToString(n)); // !!!: sval is gone
				}
				break;

			case '\"':
				T = new StringToken((ConstToken) getWord(true));
				break;

			case '(':
				T = new LparToken();
				break;
			case ')':
				T = new RparToken();
				break;
			case '[':
				T = new LbraToken();
				break;
			case ']':
				T = new RbraToken();
				break;
			case '|':
				T = new BarToken();
				break;

			case ',':
				T = new CommaToken();
				break;
			default:
				T = makeConst(charToString(n));
		}
		// IO.mes("TOKEN:"+T);
		return T;
	}

	boolean atEOC()
	{
		return !inClause;
	}

	private final String charToString(int c)
	{
		return Character.toString((char) c);
	}

	private Term getWord(boolean quoted) throws IOException
	{
		Term T;
		if (quoted && 0 == sval.length())
		{
			T = makeConst("");
		}
		else
		{
			char c = sval.charAt(0);
			if (!quoted && (Character.isUpperCase(c) || '_' == c))
			{
				T = makeVar(sval);
			}
			else
			{ // nonvar
				String s = sval;
				int nt = nextToken();
				pushBack();
				T = '(' == nt ? makeFun(s) : makeConst(s);
			}
		}
		return T;
	}

	private final Term makeFun(String s)
	{
		return new FunToken(s);
	}

	private final Term makeInt(double n)
	{
		return new IntToken((int) n);
	}

	private final Term makeNumber(double nval)
	{
		Term T;
		if (Math.floor(nval) == nval)
		{
			T = makeInt(nval);
		}
		else
		{
			T = makeReal(nval);
		}
		return T;
	}

	private final Term makeReal(double n)
	{
		return new RealToken(n);
	}

	private final Term makeVar(String s)
	{
		s = s.intern();
		Var X;
		long occ;
		if (s == ANONYMOUS)
		{
			occ = 0;
			X = new Var();
			s = X.toString();
		}
		else
		{
			X = (Var) dict.get(s);
			if (X == null)
			{
				occ = 1;
				X = new Var();
			}
			else
			{
				occ = ((Int) dict.get(X)).longValue();
				occ++;
			}
		}
		Int I = new Int(occ);
		dict.put(X, I);
		dict.put(s, X);
		return new VarToken(X, new Const(s), I);
	}

	private final void wordChar(char c)
	{
		wordChars(c, c);
	}
}
