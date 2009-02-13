package tarau.jinni;

import java.util.Vector;

/**
 * Builds Jinni Fluents from Java Streams
 */
class TermCollector extends Sink
{
	protected Vector buffer;
	private Prog p;

	TermCollector(Prog p)
	{
		super(p);
		this.p = p;
		buffer = new Vector();
	}

	@Override
	public int putElement(Term T)
	{
		buffer.addElement(T);
		return 1;
	}

	@Override
	public void stop()
	{
		buffer = null;
	}

	@Override
	public Term collect()
	{
		return new JavaSource(buffer, p);
	}
}
