/* GNU Prolog for Java Eclipse Extensions
 * Copyright (C) 2009  Michiel Hendriks; University of Twente
 *
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
package gnu.prolog.eclipse.predicates;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.BacktrackInfo;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class Predicate_project_nature extends EclipsePrologCode
{
	protected static class StringArrayBacktrackInfo extends BacktrackInfo
	{
		public int undoPosition;
		public int pos;
		public String[] array;
		public Term result;

		public StringArrayBacktrackInfo()
		{
			super(-1, -1);
		}
	}

	public Predicate_project_nature()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
	 * gnu.prolog.term.Term[])
	 */
	public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args) throws PrologException
	{
		if (backtrackMode)
		{
			StringArrayBacktrackInfo bi = (StringArrayBacktrackInfo) interpreter.popBacktrackInfo();
			interpreter.undo(bi.undoPosition);
			return nextSolution(interpreter, bi);
		}
		else
		{
			IProject proj = getProject(args[0]);
			IProjectDescription desc;
			try
			{
				desc = proj.getDescription();
				if (desc == null)
				{
					return FAIL;
				}
			}
			catch (CoreException e)
			{
				return FAIL;
			}
			StringArrayBacktrackInfo bi = new StringArrayBacktrackInfo();
			bi.undoPosition = interpreter.getUndoPosition();
			bi.pos = 0;
			bi.array = desc.getNatureIds();
			bi.result = args[1];
			return nextSolution(interpreter, bi);
		}
	}

	/**
	 * @param bi
	 * @return
	 * @throws PrologException
	 */
	protected int nextSolution(Interpreter interpreter, StringArrayBacktrackInfo bi) throws PrologException
	{
		while (bi.pos < bi.array.length)
		{
			String elm = bi.array[bi.pos];
			++bi.pos;
			if (interpreter.unify(bi.result, AtomTerm.get(elm)) == FAIL)
			{
				interpreter.undo(bi.undoPosition);
				continue;
			}
			interpreter.pushBacktrackInfo(bi);
			return SUCCESS;
		}
		return FAIL;
	}
}
