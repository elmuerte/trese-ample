package net.sf.kpex.builtins;

import net.sf.kpex.DataBase;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.JavaObject;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * Puts a term on the local blackboard
 */
class db_add extends FunBuiltin
{

	db_add()
	{
		super("db_add", 2);
	}

	@Override
	public int exec(Prog p)
	{
		DataBase db = (DataBase) ((JavaObject) getArg(0)).toObject();
		Term X = getArg(1);
		// IO.mes("X==>"+X);
		String key = X.getKey();
		// IO.mes("key==>"+key);
		if (null == key)
		{
			return 0;
		}
		db.out(key, X);
		// IO.mes("res==>"+R);
		return 1;
	}
}
