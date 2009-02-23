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
package groove.prolog.builtin.graph;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import gnu.prolog.vm.TermConstants;
import groove.graph.DefaultLabel;
import groove.graph.GraphShape;
import groove.graph.Label;
import groove.prolog.builtin.PrologCollectionIterator;

/**
 * Get an edge with a given label <code>label_edge(Graph,Label,Edge)</code>
 * 
 * @author Michiel Hendriks
 */
public class Predicate_label_edge extends GraphPrologCode
{
	public Predicate_label_edge()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
	 * gnu.prolog.term.Term[])
	 */
	public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args) throws PrologException
	{
		if (backtrackMode)
		{
			PrologCollectionIterator it = (PrologCollectionIterator) interpreter.popBacktrackInfo();
			interpreter.undo(it.getUndoPosition());
			return it.nextSolution(interpreter);
		}
		else
		{
			GraphShape graph = getGraphShape(args[0]);

			Label label = null;
			if (args[1] instanceof AtomTerm)
			{
				label = DefaultLabel.createLabel(((AtomTerm) args[1]).value);
			}
			else
			{
				PrologException.typeError(TermConstants.atomAtom, args[1]);
			}

			PrologCollectionIterator it = new PrologCollectionIterator(graph.labelEdgeSet(2, label), args[2],
					interpreter.getUndoPosition());
			return it.nextSolution(interpreter);
		}
	}
}
