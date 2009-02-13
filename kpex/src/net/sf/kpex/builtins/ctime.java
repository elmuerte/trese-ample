package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Int;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * returns the real time spent up to now
 */
class ctime extends FunBuiltin
{

	ctime()
	{
		super("ctime", 1);
	}

	private final static long t0 = System.currentTimeMillis();

	@Override
	public int exec(Prog p)
	{
		Term T = new Int(System.currentTimeMillis() - t0);
		return putArg(0, T, p);
	}
}
