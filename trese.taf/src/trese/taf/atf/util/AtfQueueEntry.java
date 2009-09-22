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
	/**
	 * The kind of action to perform
	 */
	protected AtfQueueAction action;

	/**
	 * The element to perform the action on
	 */
	protected Persistable obj;

	/**
	 * @param action
	 * @param obj
	 */
	public AtfQueueEntry(AtfQueueAction action, Persistable obj)
	{
		if (action == null || action == AtfQueueAction.NONE)
		{
			throw new NullPointerException("Action cannot be null or NONE");
		}
		if (obj == null)
		{
			throw new NullPointerException("Persistable cannot be null");
		}
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
