package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Source;
import net.sf.kpex.prolog.Term;

/**
 * Explores a finite iterator and return its successive values as a list.
 */

class source_list extends FunBuiltin
{
	source_list()
	{
		super("source_list", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Source S = (Source) getArg(0);
		Term Xs = S.toList();
		return putArg(1, Xs, p);
	}
}
