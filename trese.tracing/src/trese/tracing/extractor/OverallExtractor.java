/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing.extractor;

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import trese.tracing.TraceLink;

/**
 * Creates trace links based on the start and end nodes. For each end in the GTS
 * it will create an trace link
 * 
 * @author Michiel Hendriks
 */
public class OverallExtractor extends AbstractExtractor
{
	/*
	 * (non-Javadoc)
	 * @see trese.tracing.extractor.AbstractExtractor#extract(groove.lts.GTS)
	 */
	@Override
	public Set<TraceLink> extract(GTS gts)
	{
		Set<TraceLink> results = new HashSet<TraceLink>();

		Queue<LinkedList<GraphTransition>> queue = new LinkedList<LinkedList<GraphTransition>>();

		// seed the list with the initial transitions
		for (GraphTransition gt : gts.startState().getTransitionSet())
		{
			LinkedList<GraphTransition> curList = new LinkedList<GraphTransition>();
			curList.add(gt);
			queue.add(curList);
		}

		Collection<GraphState> finals = gts.getFinalStates();
		while (!queue.isEmpty())
		{
			LinkedList<GraphTransition> curList = queue.remove();
			GraphState curState = curList.getLast().target();
			while (curState != null)
			{
				GraphTransition next = null;
				for (GraphTransition gt : curState.getTransitionSet())
				{
					if (next == null)
					{
						// the first transition will be were we continue
						next = gt;
					}
					else
					{
						LinkedList<GraphTransition> branch = new LinkedList<GraphTransition>(curList);
						branch.add(gt);
						if (finals.contains(gt.target()))
						{
							// points to a final state, no more exploring to do
							results.add(new TraceLink(branch));
						}
						else
						{
							// add to the queue of "to be explored traces"
							queue.add(branch);
						}
					}
				}
				curList.add(next);
				if (finals.contains(next.target()))
				{
					// points to a final state, no more exploring to do
					results.add(new TraceLink(curList));
					// end this trace, and continue with the next
					curState = null;
				}
				else
				{
					curState = next.target();
				}
			}
		}

		return results;
	}
}
