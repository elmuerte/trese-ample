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
package groove.prolog.engine;

import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.lts.GTS;
import groove.lts.GraphState;

/**
 * The current state in groove.
 * 
 * @author Michiel Hendriks
 */
public class GrooveState
{
	protected GraphShape graph;
	protected GraphState state;
	protected GTS gts;

	public GrooveState(Graph forGraph)
	{
		graph = forGraph;
	}

	public GrooveState(GraphState forState)
	{
		this(forState.getGraph());
		state = forState;
	}

	public GrooveState(GTS forGTS)
	{
		graph = gts = forGTS;
	}

	/**
	 * @return the graph
	 */
	public GraphShape getGraph()
	{
		return graph;
	}

	/**
	 * @return the state
	 */
	public GraphState getState()
	{
		return state;
	}

	/**
	 * @return the gts
	 */
	public GTS getGts()
	{
		return gts;
	}
}
