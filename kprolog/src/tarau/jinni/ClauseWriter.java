package tarau.jinni;

/**
 * Writer
 */
class ClauseWriter extends CharWriter
{
	ClauseWriter(String f, Prog p)
	{
		super(f, p);
	}

	ClauseWriter(Prog p)
	{
		super(p);
	}

	@Override
	public int putElement(Term t)
	{
		if (null == writer)
		{
			return 0;
		}
		String s = null;
		if (t instanceof Fun && "$string".equals(((Fun) t).name()))
		{
			Const Xs = (Const) ((Fun) t).getArg(0);
			s = Term.charsToString(Xs);
		}
		else
		{
			s = t.pprint();
		}
		IO.print(writer, s);
		return 1;
	}
}
