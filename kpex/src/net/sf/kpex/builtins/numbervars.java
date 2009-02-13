package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * returns a copy of a Term with variables uniformly replaced with constants
 */
class numbervars extends FunBuiltin
{
	numbervars()
	{
		super("numbervars", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Term T = getArg(0).numberVars();
		return putArg(1, T, p);
	}
}
