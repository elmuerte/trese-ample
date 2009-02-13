package net.sf.kpex.builtins;

import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Source;
import net.sf.kpex.prolog.Term;

/**
 * Builds a Source from a Term
 */
class source_term extends FunBuiltin
{

	source_term()
	{
		super("source_term", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Source S = (Source) getArg(0);
		Term Xs = ((Const) S.toFun()).toBuiltin();
		return putArg(1, Xs, p);
	}
}
