package net.sf.kpex.builtins;

import net.sf.kpex.io.IO;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * sets max answer counter for toplevel query if == 0, it will prompt the user
 * for more answers if > 0 it will not print more than IO.maxAnswers if < 0 it
 * will print them out all
 */
class set_max_answers extends FunBuiltin
{
	set_max_answers()
	{
		super("set_max_answers", 1);
	}

	@Override
	public int exec(Prog p)
	{
		IO.maxAnswers = getIntArg(0);
		return 1;
	}
}
