/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.loaders;

import groove.graph.Graph;
import groove.io.AspectGxl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import trese.featuremodel.GstToModel;
import trese.featuremodel.model.Feature;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class GrooveGstLoader implements Loader
{
	/*
	 * (non-Javadoc)
	 * @see trese.featuremodel.loaders.Loader#acceptsExtension()
	 */
	public String[] acceptsExtension()
	{
		return new String[] { "gst" };
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * trese.featuremodel.loaders.Loader#loadFeatureModel(java.io.InputStream)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see
	 * trese.featuremodel.loaders.Loader#loadFeatureModel(java.io.InputStream)
	 */
	public Feature loadFeatureModel(InputStream stream) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}

}
