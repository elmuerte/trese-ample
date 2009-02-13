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
import java.util.Vector;

import net.sf.kpex.io.IO;
import net.sf.kpex.prolog.Clause;
import net.sf.kpex.prolog.Conj;
import net.sf.kpex.prolog.Cons;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.Fun;
import net.sf.kpex.prolog.Int;
import net.sf.kpex.prolog.Nonvar;
import net.sf.kpex.prolog.Term;
import net.sf.kpex.prolog.Var;
import net.sf.kpex.util.HashDict;

/**
 * Simplified Prolog parser: Synatax supported: a0:-a1,...,an. - no operators (
 * except toplevel :- and ,) - use quotes to create special symbol names, i.e.
 * compute('+',1,2, Result) and write(':-'(a,','(b,c)))
 */
public class Parser extends Lexer
{
	public static Clause clsFromString(String s)
	{
		if (null == s)
		{
			return null;
		}
		s = patchEOFString(s);
		Clause t = null;
		try
		{
			Parser p;
			p = new Parser(s);
			t = p.readClause();
		}
		catch (Exception e)
		{ // nothing expected to catch
			IO.errmes("unexpected parsing error", e);
		}
		if (t.dict == null)
		{
			t.setGround(false);
		}
		else
		{
			t.setGround(t.dict.isEmpty());
		}
		return t;
	}

	static public final boolean isError(Clause C)
	{
		Term H = C.getHead();
		if (H instanceof Fun && "error".equals(((Fun) H).getName()) && H.getArity() == 3
				&& !(((Fun) H).args[0].getRef() instanceof Var))
		{
			return true;
		}
		return false;
	}

	static public final void showError(Clause C)
	{
		IO.errmes("*** " + C);
	}

	static protected final Clause toClause(Term T, HashDict dict)
	{
		Clause C = T.toClause(); // adds ...:-true if missing
		C.dict = dict;
		return C;
	}

	static final Clause errorClause(Exception e, String type, int line, boolean verbose)
	{

		String mes = e.getMessage();
		if (null == mes)
		{
			mes = "unknown_error";
		}
		Fun f = new Fun("error", new Const(type), new Const(mes), new Fun("line", new Int(line)));
		Clause C = new Clause(f, Const.TRUE);
		if (verbose)
		{
			IO.errmes(type + " error at line:" + line);
			IO.errmes(C.prettyPrint(), e);
		}
		return C;
	}

	private static final String patchEOFString(String s)
	{
		if (!(s.lastIndexOf(".") >= s.length() - 2))
		{
			s = s + ".";
		}
		return s;
	}

	public Parser(Reader I) throws IOException
	{
		super(I);
	}

	public Parser(String s) throws Exception
	{
		super(s);
	}

	/*
	 * used in prolog2java
	 */
	public Parser(String p, String s) throws IOException
	{
		super(p, s);
	}

	/**
	 * Main Parser interface: reads a clause together with variable name
	 * information
	 */
	public Clause readClause()
	{
		Clause t = null;
		boolean verbose = false;
		try
		{
			t = readClauseOrEOF();
			// IO.mes("GOT Clause:"+t);
		}
		/**
		 * catch built exception clauses which are defined in lib.pro - allowing
		 * to recover or be quiet about such errors.
		 */
		catch (ParserException e)
		{
			t = errorClause(e, "syntax_error", lineno(), verbose);
			try
			{
				while (!atEOC() && !atEOF())
				{
					next();
				}
			}
			catch (IOException toIgnore)
			{}
		}
		catch (IOException e)
		{
			t = errorClause(e, "io_exception", lineno(), verbose);
		}
		catch (Exception e)
		{
			t = errorClause(e, "unexpected_syntax_exception", lineno(), true);
		}
		return t;
	}

	protected Term getTerm() throws IOException
	{
		Term n = next();
		return getTerm(n);
	}

	protected final Term getTerm(Term n) throws IOException
	{
		Term t = n.token();
		if (n instanceof VarToken || n instanceof IntToken || n instanceof RealToken || n instanceof ConstToken)
		{
			// is just OK as it is
		}
		else if (n instanceof StringToken)
		{
			t = ((Nonvar) ((StringToken) n).args[0]).toChars();
			// IO.mes("getTerm:stringToken-->"+t);

		}
		else if (n instanceof LbraToken)
		{
			t = getList();
		}
		else if (n instanceof FunToken)
		{
			Fun f = (Fun) t;
			f.args = getArgs();
			t = f.toBuiltin();
		}
		else
		{
			throw new ParserException("var,int,real,constant,'[' or functor", "bad term", n);
		}
		return t;
	}

	private final Term[] getArgs() throws IOException
	{
		Term n = next();
		if (!(n instanceof LparToken))
		{
			throw new ParserException("'('", "in getArgs", n);
		}
		Vector v = new Vector();
		Term t = getTerm();
		v.addElement(t);
		for (;;)
		{
			n = next();
			if (n instanceof RparToken)
			{
				Term args[] = new Term[v.size()];
				v.copyInto(args);
				return args;
			}
			else if (n instanceof CommaToken)
			{
				t = getTerm();
				v.addElement(t);
			}
			else
			{
				throw new ParserException("',' or ')'", "bad arg", n);
			}
		}
	}

	private final Term getConjCont(Term curr) throws IOException
	{

		Term n = next();
		Term t = null;
		if (n instanceof EocToken)
		{
			t = curr;
		}
		else if (n instanceof CommaToken)
		{
			Term other = getTerm();
			t = new Conj(curr, getConjCont(other));
		}
		if (null == t)
		{
			throw new ParserException("'.'", "bad body element", n);
		}
		return t;
	}

	private final Term getList() throws IOException
	{
		Term n = next();
		if (n instanceof RbraToken)
		{
			return Const.NIL;
		}
		Term t = getTerm(n);
		return getListCont(t);
	}

	private final Term getListCont(Term curr) throws IOException
	{
		// IO.trace("curr: "+curr);
		Term n = next();
		Term t = null;
		if (n instanceof RbraToken)
		{
			t = new Cons(curr, Const.NIL);
		}
		else if (n instanceof BarToken)
		{
			t = new Cons(curr, getTerm());
			n = next();
			if (!(n instanceof RbraToken))
			{
				throw new ParserException("']'", "bad list end after '|'", n);
			}
		}
		else if (n instanceof CommaToken)
		{
			Term other = getTerm();
			t = new Cons(curr, getListCont(other));
		}
		if (t == null)
		{
			throw new ParserException("| or ]", "bad list continuation", n);
		}
		return t;
	}

	private Clause readClauseOrEOF() throws IOException
	{

		dict = new HashDict();

		Term n = next();

		// IO.mes("readClauseOrEOF 0:"+n);

		if (n instanceof EofToken)
		{
			return null; // $$toClause(n.token(),dict);
		}

		if (n instanceof IffToken)
		{
			n = next();
			Term t = getTerm(n);
			Term bs = getConjCont(t);
			Clause C = new Clause(new Const("init"), bs);
			C.dict = dict;
			return C;
		}

		Term h = getTerm(n);

		// IO.mes("readClauseOrEOF 1:"+h);

		n = next();

		// IO.mes("readClauseOrEOF 2:"+n);

		if (n instanceof EocToken || n instanceof EofToken)
		{
			return toClause(h, dict);
		}

		// IO.mes("readClauseOrEOF 3:"+b);

		Clause C = null;
		if (n instanceof IffToken)
		{
			Term t = getTerm();
			Term bs = getConjCont(t);
			C = new Clause(h, bs);
			C.dict = dict;
		}
		else if (n instanceof CommaToken)
		{
			Term b = getTerm();
			Term bs = getConjCont(b);
			C = toClause(new Conj(h, bs), dict);
		}
		else
		{
			throw new ParserException("':-' or '.' or ','", "bad body element", n);
		}
		return C;
	}

}
