package net.sf.kpex.builtins;

import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Int;
import net.sf.kpex.prolog.IntegerSource;
import net.sf.kpex.prolog.Prog;

/**
 * Creates an Integer Source which advances at most Fuel (infinite if Fule==0)
 * Steps computing each time x:= a*x+b. Called as:
 * integer_source(Fuel,A,X,B,NewSource)
 */
class integer_source extends FunBuiltin
{

	integer_source()
	{
		super("integer_source", 5);
	}

	@Override
	public int exec(Prog p)
	{
		IntegerSource E = new IntegerSource(((Int) getArg(0)).longValue(), ((Int) getArg(1)).longValue(),
				((Int) getArg(2)).longValue(), ((Int) getArg(3)).longValue(), p);
		return putArg(4, E, p);
	}
}
