package tarau.jinni;

/**
 * Abstract numeric class, part of the Term hierarchy
 * 
 * @see Int
 * @see Real
 * @see Term
 */
public abstract class Num extends Nonvar
{

	@Override
	public String toString()
	{
		return name();
	}

	abstract public double getValue();
}
