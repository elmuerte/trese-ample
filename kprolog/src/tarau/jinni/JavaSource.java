package tarau.jinni;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Builds Jinni Iterators from Java Sequences and Iterator type classes
 */
class JavaSource extends Source
{
	private Enumeration e;

	JavaSource(Prog p)
	{
		super(p);
		e = null;
	}

	JavaSource(Enumeration e, Prog p)
	{
		super(p);
		this.e = e;
	}

	JavaSource(Vector V, Prog p)
	{
		super(p);
		e = V.elements();
	}

	@Override
	public Term getElement()
	{
		if (null == e || !e.hasMoreElements())
		{
			return null;
		}
		else
		{
			return (Term) e.nextElement();
		}
	}

	@Override
	public void stop()
	{
		e = null;
	}
}
