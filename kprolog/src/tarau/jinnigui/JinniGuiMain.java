package tarau.jinnigui;

import tarau.jinni.Init;

public class JinniGuiMain
{
	/**
	 * Used to initialise and start command line application
	 */

	static public boolean init_gui()
	{
		if (!Init.startJinni())
		{
			return false;
		}
		Init.builtinDict = new GuiBuiltins();
		Init.askJinni("reconsult('tarau/jinni/lib.pro')");
		Init.askJinni("reconsult('tarau/jinnigui/gui_lib.pro')");
		return true;
	}

	public static void main(String args[])
	{
		if (!init_gui())
		{
			return;
		}
		if (!Init.run(args))
		{
			return;
		}
		Init.standardTop(); // interactive
	}
}
