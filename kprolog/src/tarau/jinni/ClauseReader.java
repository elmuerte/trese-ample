package tarau.jinni;

import java.io.IOException;
import java.io.Reader;

/**
 * Builds Jinni Fluents from Java Streams
 */
class ClauseReader extends CharReader
{
	protected Parser parser;

	ClauseReader(Reader reader, Prog p)
	{
		super(reader, p);
		make_parser("from shared reader");
	}

	ClauseReader(String f, Prog p)
	{
		super(f, p);
		make_parser(f);
	}

	ClauseReader(Prog p)
	{
		super(p);
		make_parser("standard input");
	}

	/**
	 * parses from a string representation of a term
	 */
	ClauseReader(Term t, Prog p)
	{
		super(t, p);
		make_parser("string parser");
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

	@Override
	public void stop()
	{
		super.stop();
		parser = null;
	}
}
