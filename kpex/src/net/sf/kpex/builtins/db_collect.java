package net.sf.kpex.builtins;

import net.sf.kpex.DataBase;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.JavaObject;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * collects all matching terms in a (possibly empty) list
 * 
 * @see out
 * @see in
 */
class db_collect extends FunBuiltin
{

	db_collect()
	{
		super("db_collect", 3);
	}

	@Override
	public int exec(Prog p)
	{
		DataBase db = (DataBase) ((JavaObject) getArg(0)).toObject();
		Term X = getArg(1);
		Term R = db.all(X.getKey(), X);
		return putArg(2, R, p);
	}
}
