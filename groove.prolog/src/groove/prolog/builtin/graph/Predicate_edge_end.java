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
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;
import groove.graph.Edge;
import groove.graph.Node;
import groove.prolog.builtin.PrologUtils;

/**
 * Get the node or position of a given end.
 * <code>edge_end(Edge,Position,Node)</code>
 * 
 * @author Michiel Hendriks
 */
public class Predicate_edge_end implements PrologCode
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
			Term nextPos = new IntegerTerm(bi.idx);
			int rc = interpreter.unify(bi.posTerm, nextPos);
			if (rc == FAIL)
			{
				interpreter.undo(bi.startUndoPosition);
				continue;
			}
			Term nextEnd = new JavaObjectTerm(bi.edge.end(bi.idx++));
			rc = interpreter.unify(bi.nodeTerm, nextEnd);
			if (rc == FAIL)
			{
				interpreter.undo(bi.startUndoPosition);
				continue;
			}
			interpreter.pushBacktrackInfo(bi);
			return rc;
		}
		return FAIL;
	}

	public Predicate_edge_end()
	{}

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
			Edge edge = null;
			if (args[0] instanceof JavaObjectTerm)
			{
				JavaObjectTerm jot = (JavaObjectTerm) args[0];
				if (!(jot.value instanceof Edge))
				{
					PrologException.domainError(PrologUtils.EDGE_ATOM, args[0]);
				}
				edge = (Edge) jot.value;
			}
			else
			{
				PrologException.typeError(PrologUtils.EDGE_ATOM, args[0]);
			}

			Node node = null;
			if (args[2] instanceof JavaObjectTerm)
			{
				JavaObjectTerm jot = (JavaObjectTerm) args[2];
				if (!(jot.value instanceof Node))
				{
					PrologException.domainError(PrologUtils.NODE_ATOM, args[2]);
				}
				node = (Node) jot.value;
			}
			else if (args[2] instanceof VariableTerm)
			{
				// resolve the Node for a position
			}
			else
			{
				PrologException.typeError(PrologUtils.NODE_ATOM, args[2]);
			}

			if (node != null)
			{
				// unify the position
				IntegerTerm nodePos = new IntegerTerm(edge.endIndex(node));
				return interpreter.unify(args[1], nodePos);
			}
			else if (args[1] instanceof IntegerTerm)
			{
				// find the node
				int pos = ((IntegerTerm) args[1]).value;
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
				return interpreter.unify(args[2], nodeTerm);
			}
			else
			{
				// return node + index
				EdgeEndBacktrackInfo bi = new EdgeEndBacktrackInfo();
				bi.edge = edge;
				bi.posTerm = args[1];
				bi.nodeTerm = args[2];
				bi.idx = 0;
				bi.startUndoPosition = interpreter.getUndoPosition();
				return nextSolution(interpreter, bi);
			}
		}
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
