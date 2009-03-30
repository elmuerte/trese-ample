/**
 * 
 */
package trese.tracing;

import groove.graph.Graph;
import groove.lts.GraphTransition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A trace in the graph transformation system.
 * 
 * @author Michiel Hendriks
 */
public class TraceLink
{
	/**
	 * list of transitions in the GTS which form the whole link
	 */
	protected LinkedList<GraphTransition> tracePath;

	/**
	 * @param path
	 */
	public TraceLink(List<GraphTransition> path)
	{
		tracePath = new LinkedList<GraphTransition>(path);
	}

	/**
	 * @return The path of transitions that make up the path
	 */
	public List<GraphTransition> getTracePath()
	{
		return Collections.unmodifiableList(tracePath);
	}

	/**
	 * @return The graph where this trace starts
	 */
	public Graph getStartGraph()
	{
		return tracePath.getFirst().source().getGraph();
	}

	/**
	 * @return The final resulting graph in this trace
	 */
	public Graph getEndGraph()
	{
		return tracePath.getLast().target().getGraph();
	}
}
