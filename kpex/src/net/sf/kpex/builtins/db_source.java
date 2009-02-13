package net.sf.kpex.builtins;

import net.sf.kpex.DataBase;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.JavaObject;
import net.sf.kpex.prolog.JavaSource;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Source;

/**
 * Maps a DataBase to a Source enumerating its elements
 */
class db_source extends FunBuiltin
{

	db_source()
	{
		super("db_source", 2);
	}

	@Override
	public int exec(Prog p)
	{
		DataBase db = (DataBase) ((JavaObject) getArg(0)).toObject();
		Source S = new JavaSource(db.toEnumeration(), p);
		return putArg(1, S, p);
	}
}
