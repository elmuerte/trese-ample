/* GNU Prolog for Java
 * Copyright (C) 1997-1999  Constantine Plotnikov
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA. The text ol license can be also found
 * at http://www.gnu.org/copyleft/lgpl.html
 */
package gnu.prolog.vm.interpreter;

import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.vm.BacktrackInfo;
import gnu.prolog.vm.PrologException;

/**
 * Restore current state from BacktrackInfo stored in environment
 */
public class IRestoreState extends Instruction
{
	/**
	 * environemt index where state was saved
	 */
	public int environmentIndex;

	/**
	 * execute call instruction within specified sate
	 *
	 * @param state
	 *          state within which instruction will be executed
	 * @return instruction to caller how to execute next instrcuction
	 * @throw PrologException if code is throwng prolog exception
	 */
	@Override
	public int execute(ExecutionState state, BacktrackInfo bi) throws PrologException
	{
		JavaObjectTerm term = (JavaObjectTerm) state.environment[environmentIndex];
		BacktrackInfo rbi = (BacktrackInfo) term.value;
		BacktrackInfo cbi;
		do
		{
			cbi = state.popBacktrackInfo();
		} while (cbi != rbi);
		cbi.undo(state.interpreter);
		return ExecutionState.NEXT;
	}

	/** convert instruction to string */
	@Override
	public String toString()
	{
		return codePosition + ": restore_state " + environmentIndex;
	}

}
