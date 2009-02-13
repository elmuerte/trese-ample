package tarau.jinni;

/**
 * A SystemObject is a Jinni Nonvar with system assigned name
 * 
 */
class SystemObject extends Nonvar
{

	static long ctr = 0;

	private long ordinal;

	SystemObject()
	{
		ordinal = ++ctr;
	}

	@Override
	public String name()
	{
		return "{" + getClass().getName() + "." + ordinal + "}";
	}

	@Override
	boolean bind_to(Term that, Trail trail)
	{
		return super.bind_to(that, trail) && ordinal == ((SystemObject) that).ordinal;
	}

	@Override
	public String toString()
	{
		return name();
	}

	@Override
	public final int getArity()
	{
		return Term.JAVA;
	}
}
