package tarau.jinni;

/**
 * Template for builtins of arity >0
 */

abstract public class FunBuiltin extends Fun
{
	public FunBuiltin(String f, int i)
	{
		super(f, i);
	}

	@Override
	abstract public int exec(Prog p);

	@Override
	public boolean isBuiltin()
	{
		return true;
	}
}
