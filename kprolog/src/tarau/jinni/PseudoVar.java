package tarau.jinni;

/**
 * Special constants, used to Name variables
 * 
 * @see Term
 * @see Var
 */
class PseudoVar extends Const
{
	PseudoVar(int i)
	{
		super("V_" + i);
	}

	PseudoVar(String s)
	{
		super(s);
	}

	@Override
	public String toString()
	{
		return name();
	}
}
