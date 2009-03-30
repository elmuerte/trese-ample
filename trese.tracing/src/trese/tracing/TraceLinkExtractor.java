/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing;

import groove.lts.GTS;

import java.util.Set;

/**
 * 
 *
 * @author Michiel Hendriks
 */
public abstract class TraceLinkExtractor
{
	public abstract Set<TraceLink> extract(GTS gts);
	
	/*
	 * - define some way to group transitions based on?
	 * -- rule names? from where? do they need to be in a specific order?
	 * -- flag nodes in a graph?
	 * 
	 * - how to handle branching?
	 */
}
