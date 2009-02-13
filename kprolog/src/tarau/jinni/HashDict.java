package tarau.jinni;

import java.util.Hashtable;

/**
 * General purpose dictionary
 */
public class HashDict extends Hashtable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8000951204961602534L;

	public String name()
	{
		return getClass().getName() + hashCode();
	}

	public String stat()
	{
		return "BlackBoard: " + size();
	}
}
