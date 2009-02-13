package tarau.jinni;

/**
 * Part of the Term hierarchy implmenting logical variables. They are subject to
 * reset by application of and undo action keep on the trail stack.
 * 
 * @see Trail
 * @see Prog
 * @see Clause
 * @see Nonvar
 */
public class Var extends Term
{
	protected Term val;

	public Var()
	{
		val = this;
	}

	@Override
	public int getArity()
	{
		return Term.VAR;
	}

	final boolean unbound()
	{
		return val == this;
	}

	@Override
	protected Term ref()
	{
		return unbound() ? this : val.ref();
	}

	@Override
	boolean bind_to(Term x, Trail trail)
	{
		val = x;
		trail.push(this);
		return true;
	}

	@Override
	protected void undo()
	{
		val = this;
	}

	@Override
	boolean unify_to(Term that, Trail trail)
	{
		// expects: this, that are dereferenced
		return val.bind_to(that, trail);
	}

	@Override
	boolean eq(Term x)
	{ // not a term compare!
		return ref() == x.ref();
	}

	@Override
	public String getKey()
	{
		Term t = ref();
		if (t instanceof Var)
		{
			return null;
		}
		else
		{
			return t.getKey();
		}
	}

	@Override
	Term reaction(Term agent)
	{

		Term R = agent.action(ref());

		if (!(R instanceof Var))
		{
			R = R.reaction(agent);
		}

		return R;
	}

	protected String name()
	{
		return "_" + Integer.toHexString(hashCode());
	}

	@Override
	public String toString()
	{
		return unbound() ? name() : ref().toString();
	}
}
