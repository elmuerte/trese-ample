/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog.engine;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.PrologException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class GrooveEnvironment extends Environment
{
	public final static AtomTerm NO_GROOVE_ENV = AtomTerm.get("no_groove_environment");

	/**
	 * Generic error to throw when the groove environment is missing
	 */
	public static void invalidEnvironment() throws PrologException
	{
		throw new PrologException(new CompoundTerm(PrologException.errorTag, new CompoundTerm(CompoundTermTag.get(
				"system_error", 1), GrooveEnvironment.NO_GROOVE_ENV, PrologException.errorAtom),
				PrologException.errorAtom), null);
	}

	protected GrooveState grooveState;

	{
		// TODO: make this nicer
		// initialize using our subclass, needed for better Eclipse support
		// (i.e. to resolve resources)
		prologTextLoaderState = new GroovePrologTextLoaderState();
	}

	public GrooveEnvironment()
	{
		super();
	}

	public GrooveEnvironment(InputStream stdin, OutputStream stdout)
	{
		super(stdin, stdout);
	}

	/**
	 * @return the grooveState
	 */
	public GrooveState getGrooveState()
	{
		return grooveState;
	}

	/**
	 * @param grooveState
	 *            the grooveState to set
	 */
	public void setGrooveState(GrooveState grooveState)
	{
		this.grooveState = grooveState;
	}

	/** ensure that prolog text designated by term is loaded */
	public synchronized void loadStream(Reader stream, String streamName)
	{
		if (isInitialized())
		{
			throw new IllegalStateException("no files can be loaded after inializtion was run");
		}
		new PrologStreamLoader(getPrologTextLoaderState(), stream, streamName);
	}
}
