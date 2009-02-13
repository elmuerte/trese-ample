package net.sf.kpex.builtins;

import net.sf.kpex.io.IO;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * Dumps the current Java Stack
 */
class stack_dump extends FunBuiltin
{

	stack_dump()
	{
		super("stack_dump", 1);
	}

	@Override
	public int exec(Prog p)
	{
		String s = getArg(0).toString();
		IO.errmes("User requested dump", new Exception(s));
		return 1;
	}
}
