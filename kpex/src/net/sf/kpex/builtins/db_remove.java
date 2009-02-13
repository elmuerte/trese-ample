package net.sf.kpex.builtins;

import net.sf.kpex.DataBase;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.JavaObject;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * removes a matching term if available, fails otherwise
 */
class db_remove extends FunBuiltin
{

	db_remove()
	{
		super("db_remove", 3);
	}

	@Override
	public int exec(Prog p)
	{
		DataBase db = (DataBase) ((JavaObject) getArg(0)).toObject();
		Term X = getArg(1);
		Term R = db.cin(X.getKey(), X);
		return putArg(2, R, p);
	}
}
