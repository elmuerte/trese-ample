/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog.builtin.algebra;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.FloatTerm;
import gnu.prolog.term.IntegerTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;
import groove.algebra.Algebra;
import groove.algebra.BigDoubleAlgebra;
import groove.algebra.BigIntAlgebra;
import groove.algebra.JavaDoubleAlgebra;
import groove.algebra.JavaIntAlgebra;
import groove.algebra.StringAlgebra;
import groove.graph.algebra.ValueNode;
import groove.prolog.builtin.PrologUtils;

/**
 * <code>convert_valuenode(ValueNode,Term)</code>
 * 
 * @author Michiel Hendriks
 */
public class Predicate_convert_valuenode implements PrologCode
{
	public Predicate_convert_valuenode()
	{}

	/*
	 * (non-Javadoc)
	 * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
	 * gnu.prolog.term.Term[])
	 */
	public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args) throws PrologException
	{
		ValueNode node = null;
		if (args[0] instanceof JavaObjectTerm)
		{
			JavaObjectTerm jot = (JavaObjectTerm) args[0];
			if (!(jot.value instanceof ValueNode))
			{
				PrologException.domainError(PrologUtils.VALUENODE_ATOM, args[0]);
			}
			node = (ValueNode) jot.value;
		}
		else
		{
			PrologException.typeError(PrologUtils.VALUENODE_ATOM, args[0]);
		}

		Term result = null;
		Algebra<?> alg = node.getAlgebra();
		if (alg instanceof StringAlgebra)
		{
			result = AtomTerm.get((String) node.getValue());
		}
		else if (alg instanceof BigIntAlgebra || alg instanceof JavaIntAlgebra)
		{
			Integer val = (Integer) node.getValue();
			result = IntegerTerm.get(val);
		}
		else if (alg instanceof BigDoubleAlgebra || alg instanceof JavaDoubleAlgebra)
		{
			Double val = (Double) node.getValue();
			result = new FloatTerm(val);
		}
		else
		{
			result = new JavaObjectTerm(node.getValue());
		}
		return interpreter.unify(args[1], result);
	}

	/*
	 * (non-Javadoc)
	 * @see gnu.prolog.vm.PrologCode#install(gnu.prolog.vm.Environment)
	 */
	public void install(Environment env)
	{}

	/*
	 * (non-Javadoc)
	 * @see gnu.prolog.vm.PrologCode#uninstall(gnu.prolog.vm.Environment)
	 */
	public void uninstall(Environment env)
	{}
}
