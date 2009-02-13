package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * checks if something is a builtin
 */
class is_builtin extends FunBuiltin
{
	is_builtin()
	{
		super("is_builtin", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return getArg(0).isBuiltin() ? 1 : 0;
	}
}
