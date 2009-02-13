package net.sf.kpex.builtins;

import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * lists all the local blackboard to a string (Linda terms + clauses)
 */
class db_to_string extends FunBuiltin
{
	db_to_string()
	{
		super("db_to_string", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new Const(p.getDatabase().prettyPrint()), p);
	}
}
