/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf.util;

import net.ample.tracing.core.Persistable;

/**
 *An entry in the queue
 * 
 * @author Michiel Hendriks
 */
public class AtfQueueEntry
{
	protected AtfQueueAction action;
	protected Persistable obj;

	/**
	 * @param action
	 * @param obj
	 */
	public AtfQueueEntry(AtfQueueAction action, Persistable obj)
	{
		this.action = action;
		this.obj = obj;
	}

	/**
	 * @return the action
	 */
	public AtfQueueAction getAction()
	{
		return action;
	}

	/**
	 * @return the obj
	 */
	public Persistable getObj()
	{
		return obj;
	}
}
