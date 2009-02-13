package net.sf.kpex.builtins;

import net.sf.kpex.DataBase;
import net.sf.kpex.prolog.ConstBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * shorthand for reconsulting the last file
 */
class reconsult_again extends ConstBuiltin
{
	reconsult_again()
	{
		super("reconsult_again");
	}

	@Override
	public int exec(Prog p)
	{
		return DataBase.fromFile() ? 1 : 0;
	}
}
