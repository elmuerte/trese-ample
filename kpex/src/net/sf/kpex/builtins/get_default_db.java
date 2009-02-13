package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.JavaObject;
import net.sf.kpex.prolog.Prog;

/**
 * gets default database
 */
class get_default_db extends FunBuiltin
{
	get_default_db()
	{
		super("get_default_db", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new JavaObject(p.getDatabase()), p);
	}
}
