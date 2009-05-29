/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.dr.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michiel Hendriks
 * 
 */
public class OutputStreamMux extends OutputStream
{
	protected List<OutputStream> streams;

	public OutputStreamMux()
	{
		streams = new ArrayList<OutputStream>();
	}

	public boolean addStream(OutputStream stream)
	{
		if (stream != null)
		{
			return streams.add(stream);
		}
		return false;
	}

	public boolean removeStream(OutputStream stream)
	{
		return streams.remove(stream);
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException
	{
		for (OutputStream stream : streams)
		{
			stream.write(b);
		}
	}
}
