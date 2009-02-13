package tarau.jinni;

/**
 * Template for builtins of arity 0
 */

abstract public class ConstBuiltin extends Const
{

	public ConstBuiltin(String s)
	{
		super(s);
	}

	@Override
	abstract public int exec(Prog p);

	@Override
	public boolean isBuiltin()
	{
		return true;
	}
}
