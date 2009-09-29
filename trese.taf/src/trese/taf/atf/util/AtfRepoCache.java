/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf.util;

import java.util.HashMap;
import java.util.Map;

import net.ample.tracing.core.Augmentable;
import net.ample.tracing.core.QueryManager;
import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceLink;
import net.ample.tracing.core.TraceLinkType;
import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.core.TraceableArtefactType;
import net.ample.tracing.core.query.Constraints;
import net.ample.tracing.core.query.Query;

/**
 * A caching mechanism to reduce searches in the ATF. Also keeps track of new
 * elements which do not exist in the repository yet.
 * 
 * @author Michiel Hendriks
 */
public class AtfRepoCache
{
	protected Map<String, Augmentable> cache;

	/**
	 * The current queue, used to determine if the object is scheduled to be
	 * removed
	 */
	protected AtfQueue queue;

	protected QueryManager queryManager;

	protected boolean canQuery = true;

	public AtfRepoCache(RepositoryManager repo, AtfQueue repoQueue)
	{
		if (repoQueue == null)
		{
			throw new NullPointerException("AtfQueue can not be null");
		}
		if (repo == null)
		{
			throw new NullPointerException("Repository manager can not be null");
		}
		cache = new HashMap<String, Augmentable>();
		queue = repoQueue;
		queryManager = repo.getQueryManager();
		if (queryManager == null)
		{
			throw new NullPointerException("Repository manager did not return a query manager");
		}
	}

	/**
	 * Enable/disable the functionality to query the system
	 * 
	 * @param value
	 */
	public void setCanQuery(boolean value)
	{
		canQuery = value;
	}

	/**
	 * Register a element to the cache
	 * 
	 * @param obj
	 */
	public void register(Augmentable obj)
	{
		if (obj == null)
		{
			return;
		}
		Augmentable old = cache.put(obj.getUuid(), obj);
		if (old != null)
		{
			// this shouldn't happen
			System.err.println(String.format("[AtfRepoCache] Replaced %s (%s) with %s (%s)", old.getClass().getName(),
					old.getName(), obj.getClass().getName(), obj.getName()));
		}
	}

	/**
	 * Get an element using the uuid. The returned element could be anything.
	 * 
	 * @param uuid
	 * @return
	 */
	public Augmentable get(String uuid)
	{
		if (cache.containsKey(uuid))
		{
			return cache.get(uuid);
		}
		Augmentable aug = getTraceableArtefactType(uuid);
		if (aug != null)
		{
			return aug;
		}
		aug = getTraceLinkType(uuid);
		if (aug != null)
		{
			return aug;
		}
		aug = getArtefact(uuid);
		if (aug != null)
		{
			return aug;
		}
		return getLink(uuid);
	}

	/**
	 * Get a traceable artefact type. Returns null when the element has been
	 * removed, does not exist, or is not a artefact type.
	 * 
	 * @param uuid
	 * @return
	 */
	public TraceableArtefactType getTraceableArtefactType(String uuid)
	{
		if (cache.containsKey(uuid))
		{
			Augmentable aug = cache.get(uuid);
			if (aug instanceof TraceableArtefactType)
			{
				return (TraceableArtefactType) aug;
			}
		}
		if (uuid == null || !canQuery)
		{
			return null;
		}
		Query<TraceableArtefactType> q = queryManager.queryOnArtefactTypes();
		q.add(Constraints.hasUuid(uuid));
		TraceableArtefactType res = q.executeUnique();
		register(res);
		return res;
	}

	/**
	 * Get a trace link type. Returns null when the element has been removed,
	 * does not exist, or is not a trace link type.
	 * 
	 * @param uuid
	 * @return
	 */
	public TraceLinkType getTraceLinkType(String uuid)
	{
		if (cache.containsKey(uuid))
		{
			Augmentable aug = cache.get(uuid);
			if (aug instanceof TraceLinkType)
			{
				return (TraceLinkType) aug;
			}
		}
		if (uuid == null || !canQuery)
		{
			return null;
		}
		Query<TraceLinkType> q = queryManager.queryOnLinkTypes();
		q.add(Constraints.hasUuid(uuid));
		TraceLinkType res = q.executeUnique();
		register(res);
		return res;
	}

	/**
	 * Get a artefact. Returns null when the element has been removed, does not
	 * exist, or is not a artefact.
	 * 
	 * @param uuid
	 * @return
	 */
	public TraceableArtefact getArtefact(String uuid)
	{
		if (queue.isRemoved(uuid))
		{
			return null;
		}
		if (cache.containsKey(uuid))
		{
			Augmentable aug = cache.get(uuid);
			if (aug instanceof TraceableArtefact)
			{
				return (TraceableArtefact) aug;
			}
		}
		if (uuid == null || !canQuery)
		{
			return null;
		}
		Query<TraceableArtefact> q = queryManager.queryOnArtefacts();
		q.add(Constraints.hasUuid(uuid));
		TraceableArtefact res = q.executeUnique();
		register(res);
		return res;
	}

	/**
	 * Get a trace link. Returns null when the element has been removed, does
	 * not exist, or is not a trace link.
	 * 
	 * @param uuid
	 * @return
	 */
	public TraceLink getLink(String uuid)
	{
		if (queue.isRemoved(uuid))
		{
			return null;
		}
		if (cache.containsKey(uuid))
		{
			Augmentable aug = cache.get(uuid);
			if (aug instanceof TraceLink)
			{
				return (TraceLink) aug;
			}
		}
		if (uuid == null || !canQuery)
		{
			return null;
		}
		Query<TraceLink> q = queryManager.queryOnLinks();
		q.add(Constraints.hasUuid(uuid));
		TraceLink res = q.executeUnique();
		register(res);
		return res;
	}
}
