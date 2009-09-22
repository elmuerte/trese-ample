/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import net.ample.tracing.core.Persistable;
import net.ample.tracing.core.PersistenceManager;

/**
 * A queue of ATF operations, this is managed externally due to the fact that
 * the queue in the {@link PersistenceManager} can not be used when read
 * operations are performed.
 * 
 * @author Michiel Hendriks
 */
public class AtfQueue implements Iterable<AtfQueueEntry>
{
	/**
	 * The current queue
	 */
	protected Queue<AtfQueueEntry> queue;

	/**
	 * Mapping from uuid to actions which are performed
	 */
	protected Map<String, LinkedList<AtfQueueAction>> actions;

	public AtfQueue()
	{
		queue = new LinkedList<AtfQueueEntry>();
		actions = new HashMap<String, LinkedList<AtfQueueAction>>();
	}

	/**
	 * Register an action for a given object
	 * 
	 * @param uuid
	 * @param action
	 */
	protected void registerAction(String uuid, AtfQueueAction action)
	{
		if (action == null)
		{
			return;
		}
		LinkedList<AtfQueueAction> act = actions.get(uuid);
		if (act == null)
		{
			act = new LinkedList<AtfQueueAction>();
			actions.put(uuid, act);
		}
		act.add(action);
	}

	/**
	 * Add an "add" action to the queue
	 * 
	 * @param obj
	 */
	public void add(Persistable obj)
	{
		if (obj == null)
		{
			return;
		}
		AtfQueueAction act = getLastAction(obj.getUuid());
		if (act == AtfQueueAction.ADD || act == AtfQueueAction.UPDATE)
		{
			return;
		}
		queue.add(new AtfQueueEntry(AtfQueueAction.ADD, obj));
		registerAction(obj.getUuid(), AtfQueueAction.ADD);
	}

	/**
	 * Enqueue a remove action
	 * 
	 * @param obj
	 */
	public void remove(Persistable obj)
	{
		if (obj == null)
		{
			return;
		}
		AtfQueueAction act = getLastAction(obj.getUuid());
		if (act == AtfQueueAction.REMOVE)
		{
			return;
		}
		queue.add(new AtfQueueEntry(AtfQueueAction.REMOVE, obj));
		registerAction(obj.getUuid(), AtfQueueAction.REMOVE);
	}

	/**
	 * Queue an update action
	 * 
	 * @param obj
	 */
	public void update(Persistable obj)
	{
		if (obj == null)
		{
			return;
		}
		AtfQueueAction act = getLastAction(obj.getUuid());
		if (act == AtfQueueAction.ADD || act == AtfQueueAction.UPDATE)
		{
			return;
		}
		if (act == AtfQueueAction.REMOVE)
		{
			add(obj);
			return;
		}
		queue.add(new AtfQueueEntry(AtfQueueAction.UPDATE, obj));
		registerAction(obj.getUuid(), AtfQueueAction.UPDATE);
	}

	/**
	 * @param uuid
	 * @return
	 */
	public AtfQueueAction getLastAction(String uuid)
	{
		LinkedList<AtfQueueAction> acts = actions.get(uuid);
		if (acts == null || acts.isEmpty())
		{
			return AtfQueueAction.NONE;
		}
		return acts.getLast();
	}

	/**
	 * Return true if the current object state is "removed"
	 * 
	 * @param uuid
	 */
	public boolean isRemoved(String uuid)
	{
		return getLastAction(uuid) == AtfQueueAction.REMOVE;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<AtfQueueEntry> iterator()
	{
		return queue.iterator();
	}

	/**
	 * @return Number of elements in the queue
	 */
	public int size()
	{
		return queue.size();
	}
}
