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

import gnu.prolog.database.PrologTextLoader;
import gnu.prolog.database.PrologTextLoaderState;
import gnu.prolog.io.TermReader;

import java.io.Reader;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class PrologStreamLoader extends PrologTextLoader
{

	/**
	 * @param prologTextLoaderState
	 * @param root
	 */
	public PrologStreamLoader(PrologTextLoaderState prologTextLoaderState, Reader stream)
	{
		this(prologTextLoaderState, stream, "input:");
	}

	/**
	 * @param prologTextLoaderState
	 * @param stream
	 * @param streamName
	 *            The stream name
	 */
	public PrologStreamLoader(PrologTextLoaderState prologTextLoaderState, Reader stream, String streamName)
	{
		super(prologTextLoaderState);
		if (streamName == null || streamName.length() == 0)
		{
			streamName = "input:";
		}
		rootFile = streamName;
		currentFile = rootFile;
		try
		{
			currentReader = new TermReader(stream);
		}
		catch (Exception ex)
		{
			logError("could not open stream \'" + currentFile + "\': " + ex.getMessage());
			return;
		}
		processFile();
	}
}
