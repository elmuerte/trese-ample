/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing.extractor;

import groove.lts.GTS;

import java.util.Set;

import trese.tracing.TraceLink;

/**
 * 
 *
 * @author Michiel Hendriks
 */
public abstract class AbstractExtractor
{
	public abstract Set<TraceLink> extract(GTS gts);
}
