package tarau.jinni;

import java.util.Vector;

abstract public class Source extends Fluent
{

	public Source(Prog p)
	{
		super(p);
	}

	abstract public Term getElement();

	Const toList()
	{
		Term head = getElement();
		if (null == head)
		{
			return Const.aNil;
		}
		Cons l = new Cons(head, Const.aNil);
		Cons curr = l;
		for (;;)
		{
			head = getElement();
			if (null == head)
			{
				break;
			}
			Cons tail = new Cons(head, Const.aNil);
			curr.args[1] = tail;
			curr = tail;
		}
		return l;
	}

	Term toFun()
	{
		Vector V = new Vector();
		Term X;
		while (null != (X = getElement()))
		{
			V.addElement(X);
		}
		return Copier.VectorToFun(V);
	}
}
