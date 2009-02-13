package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * controls trace levels for debugging
 */
class set_trace extends FunBuiltin
{
	set_trace()
	{
		super("set_trace", 1);
	}

	@Override
	public int exec(Prog p)
	{
		Prog.tracing = getIntArg(0);
		return 1;
	}
}
