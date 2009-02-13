package tarau.jinni;

/**
 * Lazy List: produces Cons-like sequences, based on a Source. Saving a lazy
 * list to the database does not make too much sense as it will be discarded
 * when backtracking over its creation point. Note that a Lazy List has its own
 * trail, and is only discarded when backtracking over its creation point.
 */
public class LazyList extends Cons
{
	public LazyList(Term head, Source source, Trail trail)
	{
		super(head, new Var());
		this.source = source;
		bound = false;
		this.trail = trail;
	}

	private Source source;
	private boolean bound;
	private Trail trail;

	/**
	 * advances the Lazy List, pulling out elements of the Source as needed
	 */
	private final void advance()
	{
		if (bound)
		{
			return;
		}
		Term nextHead = source.getElement();
		Const thisTail;
		if (null == nextHead)
		{
			thisTail = getNull();
		}
		else
		{
			thisTail = new LazyList(nextHead.copy(), source, trail);
		}
		((Var) getArg(1)).unify(thisTail, trail);
		bound = true;
	}

	/**
	 * Advances the tail of a lazy list. Note that they inherit getHead() from
	 * Cons.
	 */

	@Override
	public Term getTail()
	{
		advance();
		return super.getTail();
	}

	/**
	 * this permissive definition for bind_to allows a Lazy List to Unify with
	 * any 2 arg constructor chain
	 */
	@Override
	boolean bind_to(Term that, Trail trail)
	{
		return that instanceof Fun && 2 == that.getArity();
	}

	public Const getNull()
	{
		return Const.aNil;
	}

	@Override
	protected void undo()
	{
		// if(source.getPersistent()) return;		trail.unwind(0);
		source.stop();
		source = null;
	}
}
