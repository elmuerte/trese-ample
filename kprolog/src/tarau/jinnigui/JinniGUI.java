package tarau.jinnigui;

import java.applet.Applet;

import tarau.jinni.IO;
import tarau.jinni.Init;

public class JinniGUI extends Applet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4630471743888073666L;

	/**
	 * Used to initialise applet
	 */
	@Override
	public void init()
	{
		IO.applet = this;
		if (!JinniGuiMain.init_gui())
		{
			return;
		}
		String command = getParameter("command");
		if (null != command && command.length() != 0)
		{
			Init.askJinni(command);
		}
		else
		{
			Init.askJinni("applet_console"); // default if applet PARAM
												// "command" is absent
		}
		super.init();
	}

	@Override
	public void start()
	{
		IO.println("starting...");
	}

	@Override
	public void stop()
	{
		IO.println("stopping...");
	}

	@Override
	public void destroy()
	{
		IO.println("destroying...");
	}

	public static void main(String args[])
	{
		JinniGuiMain.main(args);
	}
}
