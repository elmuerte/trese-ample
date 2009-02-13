package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Int;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * gets an arity for any term: n>0 for f(A1,...,An) 0 for a constant like a -1
 * for a variable like X -2 for an integer like 13 -3 for real like 3.14 -4 for
 * a wrapped JavaObject;
 * 
 * @see Term#getArity
 */
class get_arity extends FunBuiltin
{
	get_arity()
	{
		super("get_arity", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Int N = new Int(getArg(0).getArity());
		return putArg(1, N, p);
	}
}
