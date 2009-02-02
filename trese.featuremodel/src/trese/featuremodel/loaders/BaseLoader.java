/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.loaders;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trese.featuremodel.model.Feature;

/**
 * Load a feature model from a specific location
 * 
 * @author Michiel Hendriks
 */
public final class BaseLoader
{
	private static final Map<String, List<Loader>> extLoaders;

	static
	{
		extLoaders = new HashMap<String, List<Loader>>();
		addLoader(GrooveGstLoader.class);
		addLoader(GftLoader.class);
	}

	/**
	 * @return All supported extensions
	 */
	public static Set<String> supportedExtensions()
	{
		Set<String> res = new HashSet<String>(extLoaders.keySet());
		res.remove(null);
		return res;
	}

	/**
	 * Load the feature model from a specific location. This will automatically
	 * try to find the correct loader.
	 * 
	 * @param location
	 * @return
	 */
	public static Feature loadFeatureModel(URL location)
	{
		Feature result = null;
		String ext = location.getPath();
		if (ext.indexOf('.') > -1)
		{
			ext = ext.substring(ext.lastIndexOf('.') + 1).toLowerCase();
			result = loadFeatureModel(location, extLoaders.get(ext));
		}
		if (result != null)
		{
			return result;
		}
		result = loadFeatureModel(location, extLoaders.get(null));
		return result;
	}

	/**
	 * Try all loaders until a result is returned.
	 * 
	 * @param location
	 * @param loaders
	 * @return
	 */
	private static Feature loadFeatureModel(URL location, List<Loader> loaders)
	{
		for (Loader loader : loaders)
		{
			Feature result = loader.loadFeatureModel(location);
			if (result != null)
			{
				return result;
			}
		}
		return null;
	}

	/**
	 * Register a new loader instance
	 * 
	 * @param loaderClass
	 */
	public static void addLoader(Class<? extends Loader> loaderClass)
	{
		Loader loader;
		try
		{
			loader = loaderClass.newInstance();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		String[] exts = loader.acceptsExtension();
		if (exts != null)
		{
			for (String ext : exts)
			{
				ext = ext.toLowerCase();
				List<Loader> ldrs = extLoaders.get(ext);
				if (ldrs == null)
				{
					ldrs = new ArrayList<Loader>();
					extLoaders.put(ext, ldrs);
				}
				ldrs.add(loader);
			}
		}
		else
		{
			List<Loader> ldrs = extLoaders.get(null);
			if (ldrs == null)
			{
				ldrs = new ArrayList<Loader>();
				extLoaders.put(null, ldrs);
			}
			ldrs.add(loader);
		}
	}
}
