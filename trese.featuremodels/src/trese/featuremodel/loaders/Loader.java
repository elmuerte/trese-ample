/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.loaders;

import java.io.InputStream;
import java.net.URL;

import trese.featuremodel.model.Feature;

/**
 * Interface for all feature model loaders.
 * 
 * @author Michiel Hendriks
 */
public interface Loader
{
	/**
	 * @return A list of file extensions that this loader accepts. Return null
	 *         to accept any extension.
	 */
	String[] acceptsExtension();

	/**
	 * Load a feature model from a given input stream
	 * 
	 * @param data
	 * @return
	 */
	Feature loadFeatureModel(URL location);

	/**
	 * Load a feature from an inputstream
	 * 
	 * @param stream
	 * @return
	 * @throws UnsupportedOperationException
	 */
	Feature loadFeatureModel(InputStream stream) throws UnsupportedOperationException;
}
