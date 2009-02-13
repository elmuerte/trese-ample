package net.sf.kpex.builtins;

import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.Fun;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Int;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * arg(I,Term,X) unifies X with the I-the argument of functor T
 */
class arg extends FunBuiltin
{
	arg()
	{
		super("arg", 3);
	}

	@Override
	public int exec(Prog p)
	{
		int i = getIntArg(0);
		Fun F = (Fun) getArg(1);
		Term A = i == 0 ? new Const(F.getName()) : i == -1 ? new Int(F.getArity()) : F.args[i - 1];
		return putArg(2, A, p);
	}
}
