package tarau.jinni;

/**
 * creates a source of integers based on x=a*x+b formula
 */
class IntegerSource extends Source
{

	IntegerSource(long fuel, long a, long x, long b, Prog p)
	{
		super(p);
		this.fuel = fuel;
		this.a = a;
		this.b = b;
		this.x = x;
	}

	private long fuel;
	private long a;
	private long b;
	private long x;

	@Override
	public Term getElement()
	{
		if (fuel <= 0)
		{
			return null;
		}
		Int R = new Int(x);
		x = a * x + b;
		--fuel;
		return R;
	}

	@Override
	public void stop()
	{
		fuel = 0;
	}

	@Override
	public String toString()
	{
		return "{(x->" + a + "*x+" + b + ")[" + fuel + "]=" + x + "}";
	}

}
