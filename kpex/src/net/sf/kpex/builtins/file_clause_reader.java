package net.sf.kpex.builtins;

import net.sf.kpex.io.CharReader;
import net.sf.kpex.io.ClauseReader;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.Fluent;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * opens a reader returning clauses from a file
 */
class file_clause_reader extends FunBuiltin
{
	file_clause_reader()
	{
		super("file_clause_reader", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Term I = getArg(0);
		Fluent f;
		if (I instanceof CharReader)
		{
			f = new ClauseReader((I), p);
		}
		else
		{
			String s = ((Const) getArg(0)).getName();
			f = new ClauseReader(s, p);
		}
		return putArg(1, f, p);
	}
}
