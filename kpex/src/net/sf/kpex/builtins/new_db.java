package net.sf.kpex.builtins;

import net.sf.kpex.DataBase;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.JavaObject;
import net.sf.kpex.prolog.Prog;

/**
 * creates new database
 */
class new_db extends FunBuiltin
{
	new_db()
	{
		super("new_db", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new JavaObject(new DataBase()), p);
	}
}
