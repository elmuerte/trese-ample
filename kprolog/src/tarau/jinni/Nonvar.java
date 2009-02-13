package tarau.jinni;

/**
 * Part of the Prolog Term hierarchy
 * 
 * @see Term
 */
public abstract class Nonvar extends Term
{

	public abstract String name();

	@Override
	boolean bind_to(Term that, Trail trail)
	{
		return getClass() == that.getClass();
	}

	@Override
	boolean unify_to(Term that, Trail trail)
	{
		if (bind_to(that, trail))
		{
			return true;
		}
		else
		{
			return that.bind_to(this, trail);
		}
	}

	@Override
	boolean eq(Term that)
	{
		return that instanceof Nonvar && bind_to(that, null);
	}

	/**
	 * returns a list representation of the object
	 */
	Const listify()
	{
		return new Cons(this, Const.aNil);
	}
}
