/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.gui;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * An extended Simulator window. It will add the "Prolog" tab
 * 
 * @author Michiel Hendriks
 */
public class SimulatorEx extends Simulator
{
	public SimulatorEx()
	{
		super();
	}

	/**
	 * @param grammarLocation
	 * @throws IOException
	 */
	public SimulatorEx(String grammarLocation) throws IOException
	{
		super(grammarLocation);
	}

	/**
	 * @param grammarLocation
	 * @param startGraphName
	 * @throws IOException
	 */
	public SimulatorEx(String grammarLocation, String startGraphName) throws IOException
	{
		super(grammarLocation, startGraphName);
	}

	protected boolean exInit;

	/*
	 * (non-Javadoc)
	 * @see groove.gui.Simulator#getFrame()
	 */
	@Override
	public JFrame getFrame()
	{
		JFrame result = super.getFrame();
		if (!exInit)
		{
			exInit = true;
			JTabbedPane pane = getGraphViewsPanel();
			pane.addTab("Prolog", getPrologEditor());
			pane.addTab("System Output", new SystemOutput());
		}
		return result;
	}

	/**
	 * Create the prolog editor panel
	 * 
	 * @return
	 */
	protected Component getPrologEditor()
	{
		JPanel pe = new PrologEditor(this);
		return pe;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Simulator simulator;
		try
		{
			if (args.length == 0)
			{
				simulator = new SimulatorEx();
			}
			else if (args.length == 1)
			{
				simulator = new SimulatorEx(args[0]);
			}
			else if (args.length == 2)
			{
				simulator = new SimulatorEx(args[0], args[1]);
			}
			else
			{
				throw new IOException("Usage: Simulator [<production-system> [<start-state>]]");
			}
			simulator.start();
		}
		catch (IOException exc)
		{
			exc.printStackTrace();
			System.out.println(exc.getMessage());
		}
	}
}
