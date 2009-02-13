package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Nonvar;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.TermSource;

/**
 * maps a Term to a Source
 */
class term_source extends FunBuiltin
{

	term_source()
	{
		super("term_source", 2);
	}

	@Override
	public int exec(Prog p)
	{
		TermSource E = new TermSource((Nonvar) getArg(0), p);
		return putArg(1, E, p);
	}
}
