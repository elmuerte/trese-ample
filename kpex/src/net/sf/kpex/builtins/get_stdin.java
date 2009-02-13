package net.sf.kpex.builtins;

import net.sf.kpex.io.ClauseReader;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * get the standard output (a reader)
 */
class get_stdin extends FunBuiltin
{
	get_stdin()
	{
		super("get_stdin", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new ClauseReader(p), p);
	}
}
