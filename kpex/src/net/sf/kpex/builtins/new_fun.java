package net.sf.kpex.builtins;

import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.Fun;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * new_fun(F,N,T) creates a term T based on functor F with arity N and new free
 * varables as arguments
 */
class new_fun extends FunBuiltin
{
	new_fun()
	{
		super("new_fun", 3);
	}

	@Override
	public int exec(Prog p)
	{
		String s = ((Const) getArg(0)).getName();
		int i = getIntArg(1);
		Term T;
		if (i == 0)
		{
			T = new Const(s).toBuiltin();
		}
		else
		{
			Fun F = new Fun(s);
			F.init(i);
			T = F.toBuiltin();
		}
		return putArg(2, T, p);
	}
}
