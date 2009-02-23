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

import gnu.prolog.term.IntegerTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.term.VariableTerm;
import gnu.prolog.vm.BacktrackInfo;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.graph.Edge;
import groove.graph.Node;

/**
 * Get the node or position of a given end.
 * <code>edge_end(Edge,Node,Position)</code>
 * 
 * @author Michiel Hendriks
 */
public class Predicate_edge_end extends GraphPrologCode
{
	/**
	 * Used for enumerating the end nodes
	 * 
	 * @author Michiel Hendriks
	 */
	private class EdgeEndBacktrackInfo extends BacktrackInfo
	{
		Edge edge;
		Term posTerm;
		Term nodeTerm;
		int idx;
		int startUndoPosition;

		EdgeEndBacktrackInfo()
		{
			super(-1, -1);
		}
	}

	/**
	 * @param interpreter
	 * @param bi
	 * @return
	 * @throws PrologException
	 */
	private static int nextSolution(Interpreter interpreter, EdgeEndBacktrackInfo bi) throws PrologException
	{
		while (bi.idx < bi.edge.endCount())
		{
			Term nextEnd = new JavaObjectTerm(bi.edge.end(bi.idx));
			int rc = interpreter.unify(bi.nodeTerm, nextEnd);
			if (rc == FAIL)
			{
				interpreter.undo(bi.startUndoPosition);
				++bi.idx;
				continue;
			}
			if (bi.posTerm != null)
			{
				Term nextPos = new IntegerTerm(bi.idx);
				rc = interpreter.unify(bi.posTerm, nextPos);
				if (rc == FAIL)
				{
					interpreter.undo(bi.startUndoPosition);
					++bi.idx;
					continue;
				}
			}
			interpreter.pushBacktrackInfo(bi);
			return SUCCESS;
		}
		return FAIL;
	}

	public Predicate_edge_end()
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
			EdgeEndBacktrackInfo bi = (EdgeEndBacktrackInfo) interpreter.popBacktrackInfo();
			interpreter.undo(bi.startUndoPosition);
			return nextSolution(interpreter, bi);
		}
		else
		{
			Edge edge = getEdge(args[0]);
			Node node = null;
			if (args[1] instanceof VariableTerm)
			{
				// resolve the Node for a position
			}
			else
			{
				node = getNode(args[1]);
			}

			if (node != null && args.length > 2)
			{
				// unify the position
				IntegerTerm nodePos = new IntegerTerm(edge.endIndex(node));
				return interpreter.unify(args[2], nodePos);
			}
			else if (args.length > 2 && args[2] instanceof IntegerTerm)
			{
				// find the node
				int pos = ((IntegerTerm) args[2]).value;
				if (pos < 0 || pos >= edge.endCount())
				{
					// out of bounds
					return FAIL;
				}
				node = edge.end(pos);
				if (node == null)
				{
					// no such node
					return FAIL;
				}
				JavaObjectTerm nodeTerm = new JavaObjectTerm(node);
				return interpreter.unify(args[1], nodeTerm);
			}
			else
			{
				// return node + index
				EdgeEndBacktrackInfo bi = new EdgeEndBacktrackInfo();
				bi.edge = edge;
				bi.nodeTerm = args[1];
				if (args.length > 2)
				{
					bi.posTerm = args[2];
				}
				bi.idx = 0;
				bi.startUndoPosition = interpreter.getUndoPosition();
				return nextSolution(interpreter, bi);
			}
		}
	}
}
