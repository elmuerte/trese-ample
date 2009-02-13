package tarau.jinni;

import java.util.Vector;

/**
 * An Infinite Source. If based on a finite Source, it moves to its the first
 * element after reaching its last element. A SourceLoop returns 'no' if the
 * original Source is empty. In case the original Source is infinite, a
 * SourceLoop will return the same elements as the original Source. (In
 * particular, this happens if the original Source is also a Source loop).
 */
class SourceLoop extends Source
{
	private Vector v;
	Source s;
	private int i;

	SourceLoop(Source s, Prog p)
	{
		super(p);
		this.s = s;
		v = new Vector();
		i = 0;
	}

	private final Term getMemoized()
	{
		if (null == v || v.size() <= 0)
		{
			return null;
		}
		Term T = (Term) v.elementAt(i);
		i = (i + 1) % v.size();
		return T;
	}

	@Override
	public Term getElement()
	{
		Term T = null;
		if (null != s)
		{ // s is alive			T = s.getElement();
			if (null != T)
			{
				v.addElement(T);
			}
			else
			{
				s = null;
			}
		}
		if (null == s)
		{
			T = getMemoized();
		}
		return T;
	}

	@Override
	public void stop()
	{
		v = null;
		s = null;
	}
}
