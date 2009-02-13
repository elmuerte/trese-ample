package tarau.jinni;

/**
 * Maps a Term to an Source for iterating over its arguments
 */
class TermSource extends Source
{
	TermSource(Nonvar val, Prog p)
	{
		super(p);
		this.val = val;
		pos = 0;
	}

	private Nonvar val;
	private int pos;

	@Override
	public Term getElement()
	{
		Term X;
		if (null == val)
		{
			X = null;
		}
		else if (!(val instanceof Fun))
		{
			X = val;
			val = null;
		}
		else if (0 == pos)
		{
			X = new Const(val.name());
		}
		else if (pos <= ((Fun) val).getArity())
		{
			X = ((Fun) val).getArg(pos - 1);
		}
		else
		{
			X = null;
			val = null;
		}
		pos++;
		return X;
	}

	@Override
	public void stop()
	{
		val = null;
	}
}
