package net.sf.kpex.builtins;

import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Int;
import net.sf.kpex.prolog.Nonvar;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Real;

/**
 * converts a name to a list of chars
 */
class chars_to_name extends FunBuiltin
{
	chars_to_name()
	{
		super("chars_to_name", 3);
	}

	@Override
	public int exec(Prog p)
	{
		int convert = getIntArg(0);
		String s = charsToString((Nonvar) getArg(1));
		Nonvar T = new Const(s);
		if (convert > 0)
		{
			try
			{
				double r = Double.valueOf(s).doubleValue();
				if (Math.floor(r) == r)
				{
					T = new Int((long) r);
				}
				else
				{
					T = new Real(r);
				}
			}
			catch (NumberFormatException e)
			{}
		}
		return putArg(2, T, p);
	}
}
