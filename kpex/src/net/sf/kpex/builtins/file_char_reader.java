package net.sf.kpex.builtins;

import net.sf.kpex.io.CharReader;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.Fluent;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;

/**
 * opens a reader returning the content of a file char by char
 */
class file_char_reader extends FunBuiltin
{
	file_char_reader()
	{
		super("file_char_reader", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Term I = getArg(0);
		Fluent f;
		if (I instanceof CharReader)
		{
			f = new CharReader(((CharReader) I), p);
		}
		else
		{
			String s = ((Const) I).getName();
			f = new CharReader(s, p);
		}
		return putArg(1, f, p);
	}
}
