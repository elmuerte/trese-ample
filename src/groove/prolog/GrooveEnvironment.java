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
package groove.prolog;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.PrologException;
import groove.graph.Graph;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class GrooveEnvironment extends Environment
{
	public final static AtomTerm NO_GROOVE_ENV = AtomTerm.get("no_groove_environment");

	/**
	 * Generic error to throw when the groove environment is missing
	 */
	public static void invalidEnvironment() throws PrologException
	{
		throw new PrologException(new CompoundTerm(PrologException.errorTag, new CompoundTerm(CompoundTermTag.get(
				"system_error", 1), GrooveEnvironment.NO_GROOVE_ENV, PrologException.errorAtom),
				PrologException.errorAtom));
	}

	/**
	 * The graph currently under inspection
	 */
	protected Graph graph;

	public GrooveEnvironment()
	{
		super();
	}

	/**
	 * @param value
	 *            the graph to set
	 */
	public void setGraph(Graph value)
	{
		graph = value;
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph()
	{
		return graph;
	}

}
