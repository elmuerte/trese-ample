package net.sf.kpex.builtins;

import net.sf.kpex.io.IO;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Int;
import net.sf.kpex.prolog.Num;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Real;
import net.sf.kpex.prolog.Term;

/**
 * Performs simple arithmetic operations like compute('+',1,2,Result)
 */
class compute extends FunBuiltin
{
	compute()
	{
		super("compute", 4);
	}

	@Override
	public int exec(Prog p)
	{

		Term o = getArg(0);
		Term a = getArg(1);
		Term b = getArg(2);
		if (!(o instanceof Const) || !(a instanceof Num) || !(b instanceof Num))
		{
			IO.errmes("bad arithmetic operation (" + o + "): " + a + "," + b + "\nprog: " + p.toString());
		}
		String opname = ((Const) o).getName();
		double x = ((Num) a).getValue();
		double y = ((Num) b).getValue();
		double r;
		char op = opname.charAt(0);
		switch (op)
		{
			case '+':
				r = x + y;
				break;
			case '-':
				r = x - y;
				break;
			case '*':
				r = x * y;
				break;
			case '/':
				r = x / y;
				break;
			case ':':
				r = (int) (x / y);
				break;
			case '%':
				r = x % y;
				break;
			case '?':
				r = x < y ? -1 : x == y ? 0 : 1;
				break; // compares!
			case 'p':
				r = Math.pow(x, y);
				break;
			case 'l':
				r = Math.log(y) / Math.log(x);
				break;
			case 'r':
				r = x * Math.random() + y;
				break;
			case '<':
				r = (long) x << (long) y;
				break;
			case '>':
				r = (long) x >> (long) y;
				break;

			default:
				IO.errmes("bad arithmetic operation <" + op + "> on " + x + " and " + y);
				return 0;
		}
		Num R = Math.floor(r) == r ? (Num) new Int((long) r) : (Num) new Real(r);
		return putArg(3, R, p);
	}
}
