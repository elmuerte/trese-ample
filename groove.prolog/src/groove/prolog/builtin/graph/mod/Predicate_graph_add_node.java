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
package groove.prolog.builtin.graph.mod;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import gnu.prolog.vm.TermConstants;
import groove.graph.DefaultLabel;
import groove.graph.Graph;
import groove.graph.Node;
import groove.prolog.engine.GrooveEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class Predicate_graph_add_node extends GraphModPrologCode
{
	public Predicate_graph_add_node()
	{}

	/*
	 * (non-Javadoc)
	 * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
	 * gnu.prolog.term.Term[])
	 */
	public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args) throws PrologException
	{
		Graph graph = null;
		Term labels = null;
		Term destTerm = null;
		if (args.length == 2 && !(args[0] instanceof JavaObjectTerm))
		{
			// special case where it's called as graph_add_node(Labels,Node)
			if (!(interpreter.environment instanceof GrooveEnvironment))
			{
				GrooveEnvironment.invalidEnvironment();
			}
			graph = (Graph) ((GrooveEnvironment) interpreter.environment).getGrooveState().getGraph();
			labels = args[0];
			destTerm = args[1];
		}
		else
		{
			graph = getGraph(args[0]);
			if (args.length == 2)
			{
				destTerm = args[1];
			}
			else
			{
				labels = args[1];
				destTerm = args[2];
			}
		}

		if (graph.isFixed())
		{
			PrologException.domainError(GraphModPrologCode.READ_ONLY_GRAPH_ATOM, args[0]);
		}

		List<String> selfEdges = new ArrayList<String>();
		if (labels != null)
		{
			if (CompoundTerm.isListPair(labels))
			{
				List<Term> termCol = new ArrayList<Term>();
				CompoundTerm.toCollection(labels, termCol);
				for (Term term : termCol)
				{
					if (term instanceof AtomTerm)
					{
						selfEdges.add(((AtomTerm) term).value);
					}
					else
					{
						PrologException.typeError(TermConstants.atomAtom, term);
					}
				}
			}
			else if (labels instanceof AtomTerm)
			{
				selfEdges.add(((AtomTerm) labels).value);
			}
			else
			{
				PrologException.typeError(TermConstants.atomAtom, labels);
			}
		}

		Node node = graph.addNode();
		if (interpreter.unify(destTerm, new JavaObjectTerm(node)) != FAIL)
		{
			for (String edge : selfEdges)
			{
				graph.addEdge(node, DefaultLabel.createLabel(edge), node);
			}
			return SUCCESS_LAST;
		}
		return FAIL;
	}
}
