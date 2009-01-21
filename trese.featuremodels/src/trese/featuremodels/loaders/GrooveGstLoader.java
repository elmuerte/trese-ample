/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.loaders;

import groove.graph.Graph;
import groove.io.AspectGxl;

import java.io.IOException;
import java.net.URL;

import trese.featuremodels.GstToModel;
import trese.featuremodels.model.Feature;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class GrooveGstLoader implements Loader
{
	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.loaders.Loader#acceptsExtension()
	 */
	@Override
	public String[] acceptsExtension()
	{
		return new String[] { "gst" };
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * trese.featuremodels.loaders.Loader#loadFeatureModel(java.io.InputStream)
	 */
	@Override
	public Feature loadFeatureModel(URL location)
	{
		AspectGxl gxl = new AspectGxl();
		try
		{
			Graph graph = gxl.unmarshalGraph(location);
			return GstToModel.convertGraph(graph);
		}
		catch (IOException e)
		{
			System.err.println(e);
			e.printStackTrace(System.err);
		}
		return null;
	}

}
