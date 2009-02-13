package tarau.jinni;

public class Int extends Num
{
	public Int(long i)
	{
		val = i;
	}

	long val;

	@Override
	public String name()
	{
		return "" + val;
	}

	@Override
	boolean bind_to(Term that, Trail trail)
	{
		return super.bind_to(that, trail) && (double) val == (double) ((Int) that).val;
		// unbelievable but true: converting
		// to double is the only way to convince
		// Microsoft's jview that 1==1
		// $$ casting to double to be removed
		// once they get it right
	}

	@Override
	public final int getArity()
	{
		return Term.INT;
	}

	public final long longValue()
	{
		return val;
	}

	public final int intValue()
	{
		return (int) val;
	}

	@Override
	public final double getValue()
	{
		return val;
	}
}
