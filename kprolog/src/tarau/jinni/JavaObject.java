package tarau.jinni;

/**
 * A JavaObject is a Jinni SystemObject with a val slot which containing a
 * wrapped Java object
 */

public class JavaObject extends SystemObject
{
	public JavaObject(Object i)
	{
		// available=true;
		val = i;
	}

	Object val;

	@Override
	public Object toObject()
	{
		return val;
	}

	/*
	 * private boolean available; synchronized public void suspend() {
	 * available=false; while(!available) { try { wait(); }
	 * catch(InterruptedException e) {} } } synchronized public void resume() {
	 * available=true; notifyAll(); }
	 */
}
