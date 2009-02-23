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
package groove.prolog.builtin.lts;

import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;
import groove.lts.GraphTransition;
import groove.prolog.builtin.PrologUtils;

/**
 * <code>transition_source(Transition,GraphState)</code>
 * 
 * @author Michiel Hendriks
 */
public class Predicate_transition_source implements PrologCode
{
	public Predicate_transition_source()
	{}

	/*
	 * (non-Javadoc)
	 * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
	 * gnu.prolog.term.Term[])
	 */
	public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args) throws PrologException
	{
		GraphTransition transition = null;
		if (args[0] instanceof JavaObjectTerm)
		{
			JavaObjectTerm jot = (JavaObjectTerm) args[0];
			if (!(jot.value instanceof GraphTransition))
			{
				PrologException.domainError(PrologUtils.TRANSITION_ATOM, args[0]);
			}
			transition = (GraphTransition) jot.value;
		}
		else
		{
			PrologException.typeError(PrologUtils.TRANSITION_ATOM, args[0]);
		}
		Term result = new JavaObjectTerm(transition.source());
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
