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

import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.IntegerTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;
import gnu.prolog.vm.TermConstants;
import groove.graph.GraphShape;
import groove.graph.Node;
import groove.prolog.builtin.PrologUtils;

/**
 * Get a certain set of edges for a node.
 * <code>node_edge_set(Graph,Node,Position,EdgeSet)</code>
 * 
 * @author Michiel Hendriks
 */
public class Predicate_node_edge_set_pos implements PrologCode
{
	public Predicate_node_edge_set_pos()
	{}

	/*
	 * (non-Javadoc)
	 * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
	 * gnu.prolog.term.Term[])
	 */
	public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args) throws PrologException
	{
		GraphShape graph = null;
		if (args[0] instanceof JavaObjectTerm)
		{
			JavaObjectTerm jot = (JavaObjectTerm) args[0];
			if (!(jot.value instanceof GraphShape))
			{
				PrologException.domainError(PrologUtils.GRAPH_ATOM, args[0]);
			}
			graph = (GraphShape) jot.value;
		}
		else
		{
			PrologException.typeError(PrologUtils.GRAPH_ATOM, args[0]);
		}

		Node node = null;
		if (args[1] instanceof JavaObjectTerm)
		{
			JavaObjectTerm jot = (JavaObjectTerm) args[1];
			if (!(jot.value instanceof Node))
			{
				PrologException.domainError(PrologUtils.NODE_ATOM, args[1]);
			}
			node = (Node) jot.value;
		}
		else
		{
			PrologException.typeError(PrologUtils.NODE_ATOM, args[1]);
		}

		int position = -1;
		if (args[2] instanceof IntegerTerm)
		{
			position = ((IntegerTerm) args[2]).value;
		}
		else
		{
			PrologException.typeError(TermConstants.integerAtom, args[2]);
		}

		if (position < 0)
		{
			return FAIL;
		}

		try
		{
			Term edgeSetTerm = CompoundTerm.getList(PrologUtils.createJOTlist(graph.edgeSet(node, position)));
			return interpreter.unify(edgeSetTerm, args[3]);
		}
		catch (IllegalArgumentException e)
		{
			return FAIL;
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
