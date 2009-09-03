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
 * 
 * 
 * @author Michiel Hendriks
 */
public class AtfRepoCache
{
	protected Map<String, Augmentable> cache;

	protected AtfQueue queue;
	protected QueryManager queryManager;

	public AtfRepoCache(RepositoryManager repo, AtfQueue repoQueue)
	{
		cache = new HashMap<String, Augmentable>();
		queue = repoQueue;
		queryManager = repo.getQueryManager();
	}

	public void register(Augmentable obj)
	{
		if (obj == null)
		{
			return;
		}
		Augmentable old = cache.put(obj.getUuid(), obj);
		if (old != null)
		{
			// ...
			System.err.println(String.format("[AtfRepoCache] Replaced %s (%s) with %s (%s)", old.getClass().getName(),
					old.getName(), obj.getClass().getName(), obj.getName()));
		}
	}

	/**
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
		if (uuid == null)
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
		if (uuid == null)
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
		if (uuid == null)
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
		if (uuid == null)
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
