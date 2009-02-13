/*
 * KernelProlog Expanded - Pure Java based Prolog Engine
 * Copyright (C) 1999  Paul Tarau (original KernelProlog)
 * Copyright (C) 2009  Michiel Hendriks
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package tarau.jinni.gui;

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
