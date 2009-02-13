package tarau.jinni;

/**
 * Part of the Term hierarchy, implementing double float point numbers.
 * 
 * @see Term
 * @see Nonvar
 */
public class Real extends Num
{
	public Real(double i)
	{
		val = i;
	}

	double val;

	@Override
	public String name()
	{
		return "" + val;
	}

	@Override
	boolean bind_to(Term that, Trail trail)
	{
		return super.bind_to(that, trail) && val == ((Real) that).val;
	}

	@Override
	public final int getArity()
	{
		return Term.REAL;
	}

	@Override
	public final double getValue()
	{
		return val;
	}
}
