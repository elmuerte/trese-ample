package net.sf.kpex.builtins;

import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.ListSource;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Source;

/**
 * maps a List to a Source
 */
class list_source extends FunBuiltin
{

	list_source()
	{
		super("list_source", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Source E = new ListSource((Const) getArg(0), p);
		return putArg(1, E, p);
	}
}
