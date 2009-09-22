/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf.util;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A reader implementation that reads from a list of readers in sequence. When
 * readerN reaches the EOF it will continue reading from readerN+1.
 * 
 * @author Michiel Hendriks
 */
public class CompositeReader extends Reader
{
	protected Queue<Reader> readers;

	/**
	 * @param rds
	 */
	public CompositeReader(Reader... rds)
	{
		if (rds == null || rds.length == 0)
		{
			throw new NullPointerException("Reader list cannot be null or empty");
		}
		readers = new LinkedList<Reader>();
		for (Reader reader : rds)
		{
			readers.add(reader);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException
	{
		readers.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		while (!readers.isEmpty())
		{
			int res = readers.peek().read(cbuf, off, len);
			if (res == -1)
			{
				readers.poll();
				continue;
			}
			return res;
		}
		return -1;
	}
}
