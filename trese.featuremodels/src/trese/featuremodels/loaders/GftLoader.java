/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodels.loaders;

import java.io.InputStream;
import java.net.URL;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;

import trese.featuremodels.gft.GftLexer;
import trese.featuremodels.gft.GftParser;
import trese.featuremodels.model.Feature;

/**
 * Loads generalized feature trees
 * 
 * @author Michiel Hendriks
 */
public class GftLoader implements Loader
{

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.loaders.Loader#acceptsExtension()
	 */
	@Override
	public String[] acceptsExtension()
	{
		return new String[] { "gft" };
	}

	/*
	 * (non-Javadoc)
	 * @see trese.featuremodels.loaders.Loader#loadFeatureModel(java.net.URL)
	 */
	@Override
	public Feature loadFeatureModel(URL location)
	{
		try
		{
			InputStream is = location.openStream();
			GftLexer lx = new GftLexer(new ANTLRInputStream(is));
			TokenStream ts = new CommonTokenStream(lx);
			GftParser parser = new GftParser(ts);
			parser.gft();
			return parser.getRootFeature();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
