package net.sf.kpex.builtins;

import net.sf.kpex.io.ClauseWriter;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * get standard output (a writer)
 */
class get_stdout extends FunBuiltin
{
	get_stdout()
	{
		super("get_stdout", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new ClauseWriter(p), p);
	}
}
