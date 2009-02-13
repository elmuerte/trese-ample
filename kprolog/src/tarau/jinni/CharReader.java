package tarau.jinni;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Builds Jinni Fluents from Java Streams
 */
class CharReader extends Source
{
	protected Reader reader;

	CharReader(Reader reader, Prog p)
	{
		super(p);
		this.reader = reader;
	}

	CharReader(String f, Prog p)
	{
		super(p);
		makeReader(f);
	}

	CharReader(Term t, Prog p)
	{
		super(p);
		reader = new StringReader(t.toUnquoted());
	}

	CharReader(Prog p)
	{
		this(IO.input, p);
	}

	protected void makeReader(String f)
	{
		reader = IO.url_or_file(f);
	}

	@Override
	public Term getElement()
	{
		if (IO.input == reader)
		{
			String s = IO.promptln(">:");
			if (null == s || s.length() == 0)
			{
				return null;
			}
			return new Const(s);
		}

		if (null == reader)
		{
			return null;
		}
		int c = -1;
		try
		{
			c = reader.read();
		}
		catch (IOException e)
		{}
		if (-1 == c)
		{
			stop();
			return null;
		}
		else
		{
			return new Int(c);
		}
	}

	@Override
	public void stop()
	{
		if (null != reader && IO.input != reader)
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{}
			reader = null;
		}
	}
}
