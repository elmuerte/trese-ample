package tarau.jinni;

/**
 * Builds Jinni Fluents from Java Streams
 */
class StringSink extends Sink
{
	protected StringBuffer buffer;

	StringSink(Prog p)
	{
		super(p);
		buffer = new StringBuffer();
	}

	@Override
	public int putElement(Term t)
	{
		buffer.append(t.toUnquoted());
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
		return new Const(buffer.toString());
	}
}
