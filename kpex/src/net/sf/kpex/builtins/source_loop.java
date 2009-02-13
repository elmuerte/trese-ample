package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Source;
import net.sf.kpex.prolog.SourceLoop;

/**
 * Builds a Looping Source from a Source.
 */
class source_loop extends FunBuiltin
{
	source_loop()
	{
		super("source_loop", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Source s = (Source) getArg(0);
		return putArg(1, new SourceLoop(s, p), p);
	}
}
