package tarau.jinni;

import java.io.IOException;
import java.io.Writer;

/**
 * Writer
 */
class CharWriter extends Sink
{
	protected Writer writer;

	CharWriter(String f, Prog p)
	{
		super(p);
		writer = IO.toFileWriter(f);
	}

	CharWriter(Prog p)
	{
		super(p);
		writer = IO.output;
	}

	@Override
	public int putElement(Term t)
	{
		if (null == writer)
		{
			return 0;
		}
		try
		{
			char c = (char) ((Int) t).intValue();
			writer.write(c);
		}
		catch (IOException e)
		{
			return 0;
		}
		return 1;
	}

	@Override
	public void stop()
	{
		if (null != writer && IO.output != writer)
		{
			try
			{
				writer.close();
			}
			catch (IOException e)
			{}
			writer = null;
		}
	}
}
