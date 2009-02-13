package net.sf.kpex.builtins;

import net.sf.kpex.io.IO;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * Calls an external program
 */
class system extends FunBuiltin
{
	system()
	{
		super("system", 1);
	}

	@Override
	public int exec(Prog p)
	{
		String cmd = ((Const) getArg(0)).getName();
		return IO.system(cmd);
	}
}
