/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf;

import gnu.prolog.database.AbstractPrologTextLoaderListener;
import gnu.prolog.database.PrologTextLoader;
import gnu.prolog.database.PrologTextLoaderState;
import gnu.prolog.eclipse.IEnvironmentInitializer;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.vm.Environment;
import trese.taf.atf.PrologFactImporter;

/**
 * This initializer makes sure the TAF predicates are always properly defined
 * before a file is loaded.
 * 
 * @author Michiel Hendriks
 */
public class TafPrologEnvInitializer extends AbstractPrologTextLoaderListener implements IEnvironmentInitializer
{
	public TafPrologEnvInitializer()
	{}

	/*
	 * (non-Javadoc)
	 * @seegnu.prolog.eclipse.IEnvironmentInitializer#initialize(gnu.prolog.vm.
	 * Environment)
	 */
	public void initialize(Environment environment)
	{
		if (environment == null)
		{
			throw new NullPointerException("Environment cannot be null");
		}
		environment.getTextLoaderState().addPrologTextLoaderListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * gnu.prolog.database.AbstractPrologTextLoaderListener#beforeProcessFile
	 * (gnu.prolog.database.PrologTextLoader)
	 */
	@Override
	public void beforeProcessFile(PrologTextLoader loader)
	{
		if (loader == null)
		{
			throw new NullPointerException("PrologTextLoader cannot be null");
		}
		PrologTextLoaderState state = loader.getPrologTextLoaderState();
		// Make sure these predicates are always delcared like this
		for (CompoundTermTag tag : PrologFactImporter.TAGS)
		{
			state.declareMultifile(loader, tag);
			state.declareDiscontiguous(loader, tag);
			state.declareDynamic(loader, tag);
		}
	}
}
