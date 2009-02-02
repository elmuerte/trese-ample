/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.loaders;

import java.io.InputStream;
import java.net.URL;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;

import trese.featuremodel.gft.GftLexer;
import trese.featuremodel.gft.GftParser;
import trese.featuremodel.model.Feature;

/**
 * Loads generalized feature trees
 * 
 * @author Michiel Hendriks
 */
public class GftLoader implements Loader
{

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodel.loaders.Loader#acceptsExtension()
	 */
	@Override
	public String[] acceptsExtension()
	{
		return new String[] { "gft" };
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodel.loaders.Loader#loadFeatureModel(java.net.URL)
	 */
	@Override
	public Feature loadFeatureModel(URL location)
	{
		try
		{
			return loadFeatureModel(location.openStream());
		}
		catch (Exception e)
		{
			// TODO
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * trese.featuremodel.loaders.Loader#loadFeatureModel(java.io.InputStream)
	 */
	@Override
	public Feature loadFeatureModel(InputStream stream) throws UnsupportedOperationException
	{
		try
		{
			GftLexer lx = new GftLexer(new ANTLRInputStream(stream));
			TokenStream ts = new CommonTokenStream(lx);
			GftParser parser = new GftParser(ts);
			parser.gft();
			return parser.getRootFeature();
		}
		catch (Exception e)
		{
			// TODO
			e.printStackTrace();
		}
		return null;
	}

}
