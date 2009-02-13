package net.sf.kpex.builtins;

import net.sf.kpex.DataBase;
import net.sf.kpex.io.IO;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * consults a file of clauses while adding clauses to existing predicate
 * definitions
 * 
 * @see reconsult
 */
class consult extends FunBuiltin
{
	consult()
	{
		super("consult", 1);
	}

	@Override
	public int exec(Prog p)
	{
		String f = ((Const) getArg(0)).getName();
		IO.trace("consulting: " + f);
		return DataBase.fromFile(f, false) ? 1 : 0;
	}
}
