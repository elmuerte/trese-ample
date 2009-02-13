package net.sf.kpex.builtins;

import net.sf.kpex.io.IO;
import net.sf.kpex.prolog.ConstBuiltin;
import net.sf.kpex.prolog.Prog;

/**
 * does its best to halt the program:-) to be thoroughly tested with Applets
 */
class halt extends ConstBuiltin
{
	halt()
	{
		super("halt");
	}

	@Override
	public int exec(Prog p)
	{
		if (IO.applet != null)
		{ // applet
			IO.peer.halt();
			p.stop();
		}
		else
		{
			Runtime.getRuntime().exit(0);
		}
		return 1;
	}
}
