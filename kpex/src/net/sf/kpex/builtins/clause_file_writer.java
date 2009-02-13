package net.sf.kpex.builtins;

import net.sf.kpex.io.ClauseWriter;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.Fluent;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * opens a writer which puts characters to a file one by one
 */
class clause_file_writer extends FunBuiltin
{
	clause_file_writer()
	{
		super("clause_file_writer", 2);
	}

	@Override
	public int exec(Prog p)
	{
		String s = ((Const) getArg(0)).getName();
		Fluent f = new ClauseWriter(s, p);
		return putArg(1, f, p);
	}
}
