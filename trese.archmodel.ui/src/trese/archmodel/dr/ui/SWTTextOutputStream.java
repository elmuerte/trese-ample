/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.dr.ui;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.widgets.Text;

/**
 * @author Michiel Hendriks
 * 
 */
public class SWTTextOutputStream extends OutputStream
{
	Text dest;

	static final int BUFFER_SIZE = 512;
	int[] buffer = new int[BUFFER_SIZE];
	int pos = 0;

	public SWTTextOutputStream(Text toArea)
	{
		dest = toArea;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int arg0) throws IOException
	{
		buffer[pos++] = arg0;
		if (pos >= buffer.length)
		{
			flush();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException
	{
		if (pos == 0)
		{
			return;
		}
		dest.append(new String(buffer, 0, pos));
		buffer = new int[BUFFER_SIZE];
		pos = 0;
	}
}
