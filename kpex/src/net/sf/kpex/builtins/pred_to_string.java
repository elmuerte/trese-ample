package net.sf.kpex.builtins;

import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * Returns a representation of predicate as a string constant
 */
class pred_to_string extends FunBuiltin
{

	pred_to_string()
	{
		super("pred_to_string", 2);
	}

	@Override
	public int exec(Prog p)
	{
		String key = getArg(0).getKey();
		String listing = p.getDatabase().pred_to_string(key);
		if (null == listing)
		{
			return 0;
		}
		Const R = new Const(listing);
		return putArg(1, R, p);
	}
}
