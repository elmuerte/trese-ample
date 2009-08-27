/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.dev.gen.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.ample.tracing.core.TraceableArtefactType;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class ArtefactGeneratorConfig
{
	protected Set<TraceableArtefactType> types = new HashSet<TraceableArtefactType>();
	protected int count = 0;

	public ArtefactGeneratorConfig()
	{}

	public int getCount()
	{
		return count;
	}

	public Set<TraceableArtefactType> getTypes()
	{
		return Collections.unmodifiableSet(types);
	}

	public void setCount(int value)
	{
		count = value;
	}

	public void setTypes(Collection<TraceableArtefactType> newTypes)
	{
		types.clear();
		if (newTypes != null)
		{
			types.addAll(newTypes);
		}
	}

	public boolean addType(TraceableArtefactType type)
	{
		return types.add(type);
	}

	public boolean removeType(TraceableArtefactType type)
	{
		return types.remove(type);
	}
}
