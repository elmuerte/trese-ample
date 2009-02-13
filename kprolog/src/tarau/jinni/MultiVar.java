package tarau.jinni;

import java.util.Stack;

/**
 * Varable-like entity, with a multiple values, in stack order. Set operations
 * are undone on backtraking, when the previous value is restored.
 */
class MultiVar extends Fluent
{
	Stack vals;

	MultiVar(Term T, Prog p)
	{
		super(p);
		vals = new Stack();
		vals.push(T.ref());
	}

	final void set(Term T, Prog p)
	{
		vals.push(T);
		p.getTrail().push(this);
	}

	public Term val()
	{
		return (Term) vals.peek();
	}

	/**
	 * cannot be made presistent
	 */
	@Override
	protected void undo()
	{
		vals.pop();
	}

	@Override
	public String toString()
	{
		return "MultiVar[" + vals.size() + "]->{" + vals.peek().toString() + "}";
	}
}
