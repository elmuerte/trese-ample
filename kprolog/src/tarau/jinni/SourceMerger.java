package tarau.jinni;

/**
 * Merges a List of Sources into a new Source which (fairly) iterates over them
 * breadth first.
 */
class SourceMerger extends JavaSource
{
	SourceMerger(Const Xs, Prog p)
	{
		super(p);
		Q = new Queue(Copier.ConsToVector(Xs));
	}

	private Queue Q;

	@Override
	public Term getElement()
	{
		if (null == Q)
		{
			return null;
		}
		while (!Q.isEmpty())
		{
			Source current = (Source) Q.deq();
			if (null == current)
			{
				continue;
			}
			Term T = current.getElement();
			if (null == T)
			{
				continue;
			}
			Q.enq(current);
			return T;
		}
		return null;
	}
}
