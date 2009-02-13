package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Nonvar;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * converts a name to a list of chars
 */
class name_to_chars extends FunBuiltin
{
	name_to_chars()
	{
		super("name_to_chars", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Term Cs = ((Nonvar) getArg(0)).toChars();
		return putArg(1, Cs, p);
	}
}
