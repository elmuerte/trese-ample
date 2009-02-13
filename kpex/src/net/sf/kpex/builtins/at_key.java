package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;
import net.sf.kpex.prolog.Var;

/**
 * collects all matching terms in a (possibly empty) list
 */
class at_key extends FunBuiltin
{

	at_key()
	{
		super("at_key", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Term R = p.getDatabase().all(getArg(0).getKey(), new Var());
		return putArg(1, R, p);
	}
}
