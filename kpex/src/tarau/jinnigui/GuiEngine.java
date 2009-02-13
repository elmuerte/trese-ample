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
package tarau.jinnigui;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Container;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.net.URL;

import tarau.jinni.IO;
import tarau.jinni.IOPeer;
import tarau.jinni.Init;
import tarau.jinni.Prog;

/**
 * Simple GUI interface to Jinni. To be extended:-)
 */
public class GuiEngine extends Panel implements IOPeer, Runnable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2546367520798617838L;
	private Container container;
	TextField inputField;
	TextField readField;
	private TextArea outputArea;
	static public int textHeight = 12;
	static public int textWidth = 60;
	static private int instance_ctr = 0;

	/**
	 * Creates the Frame together with its program thread and it's Prolog code
	 * represented as a trivial java class. Note that the translatiom
	 * lib.pro->lib.java->lib.class is the simplest way to get Prolog code
	 * fetched into the the application
	 */
	public GuiEngine(Container container)
	{
		super();
		instance_ctr++;
		this.container = container;
		// super("Jinni Engine "+ ++instance_ctr); **
	}

	/**
	 * Gives the illusion to output routines in class IO that they are printing
	 * to a console.
	 * 
	 * @see IO
	 */
	synchronized public void print(String s)
	{
		outputArea.appendText(s);
	}

	public void println(String s)
	{
		print(s + "\n");
	}

	public void traceln(String s)
	{
		println(s);
	}

	public void show_document(URL url, String target)
	{
		if (container instanceof Applet)
		{
			AppletContext browser = ((Applet) container).getAppletContext();
			browser.showDocument(url, target);
		}
		IO.errmes("show_document only works with applets");
	}

	/**
	 * Initializes Jinni input area with this
	 */
	public String firstQuery()
	{
		// return "show_document('http://www.binnetcorp.com').";
		// return ""; // empty by default
		return "member(X,[1,2,3])";
	}

	/**
	 * Frame specific initialization
	 */
	private void initThis()
	{
		outputArea = new TextArea(textHeight, textWidth);
		outputArea.setEditable(false);

		inputField = new TextField(firstQuery(), textWidth);
		readField = new TextField(";", textWidth);

		GridBagLayout gridBag = new GridBagLayout();
		setLayout(gridBag);
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		gridBag.setConstraints(inputField, c);
		inputField.setBackground(Color.lightGray);
		add(inputField);

		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		gridBag.setConstraints(readField, c);
		readField.setBackground(Color.gray);
		add(readField);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 20.0;
		gridBag.setConstraints(outputArea, c);
		outputArea.setBackground(Color.white);
		add(outputArea);

		// resize(400, 300);
		if (container instanceof Applet)
		{
			Applet applet = (Applet) container;
			int width = Integer.parseInt(applet.getParameter("width"));
			int height = Integer.parseInt(applet.getParameter("height"));
			reshape(0, 0, width, height); // within the parents coordinates
		}

		changeFocus(readField, inputField);
	}

	/**
	 * Notifies class IO that we are an GUI mode.
	 */
	void initPeer()
	{
		IO.peer = this; // passes itself to the IO routines
	}

	void resetPeer()
	{
		IO.peer = null; // passes itself to the IO routines
	}

	/**
	 * Avoids spurious reinitialisation.
	 */
	public void run()
	{
		initThis();
		initPeer();
		validate();
		// pack(); $$
		show();
		println("% Starting...");
		// jg.stop();
		// jg.destroy();
	}

	/**
	 * Executes when the applet is stopped.
	 */
	public void stop()
	{
		// servant.disconnect();
		// serverThread.stop();
		resetPeer();
		println("% stoping...");
		clean_up_goal();
	}

	/**
	 * Executes when the applet is destroyed. Cleans up goal threads.
	 */
	public void destroy()
	{
		stop();
		removeAll();
	}

	public void halt()
	{
		stop();
		// Runtime.getRuntime().exit(0); $$
	}

	void clean_up_goal()
	{
		if (IO.peer != null)
		{
			return;
		}
		if (goalThread != null)
		{
			goalThread.stop(); // no need for this
			goalThread = null;
		}
		if (readThread != null)
		{
			readThread.stop();
			readThread = null;
		}
	}

	Thread goalThread = null;
	Thread readThread = null;

	/**
	 * Swtiches between '?-' query input and Prolog read(X) operations.
	 */
	void changeFocus(TextField from, TextField to)
	{
		from.hide();
		to.show();
		validate();
		to.requestFocus();
		to.selectAll();
	}

	/**
	 * Registers a reader thread to operate on readField
	 */
	private boolean addReader(Thread readThread)
	{
		if (this.readThread != null)
		{
			IO.errmes("already reading for thread:" + readThread);
			return false;
		}
		this.readThread = readThread;

		changeFocus(inputField, readField);
		return true;
	}

	/**
	 * returns a String read from readField
	 */
	private String getReadString()
	{
		String s = readField.getText();
		changeFocus(readField, inputField);
		return s;
	}

	/**
	 * Reads a string. Suspends and resumes the caller thread until the string
	 * is available.
	 */
	public String readln()
	{
		if (!addReader(Thread.currentThread()))
		{
			return null;
		}
		Thread.currentThread().suspend();
		String s = getReadString();
		print(s + "\n");
		return s;
	}

	/**
	 * Acts on events from TextFields
	 */
	@Override
	public boolean action(Event evt, Object arg)
	{
		// println(Thread.currentThread()+" :action: "+
		// evt.getClass()+"<"+evt.target+">"+arg);

		if (evt.target == inputField || evt.target == readField)
		{
			initPeer();
			if (readThread == null)
			{
				changeFocus(readField, inputField);
				// println("action thread="+Thread.currentThread());
				// println("got: " +arg);
				clean_up_goal();
				GuiQA qa = new GuiQA(this);
				goalThread = new Thread(qa);
				goalThread.start();
			}
			else
			{
				readThread.resume();
				readThread = null;
				// println("read done:");
				changeFocus(readField, inputField);
			}
		}
		else if (evt.target instanceof Runnable)
		{
			((Runnable) evt.target).run();
		}
		else
		{
			println("TARGET: " + evt.target);
			return false;
		}
		return true;
	}

	@Override
	public boolean handleEvent(Event event)
	{
		if (event.id == Event.WINDOW_DESTROY)
		{
			Runtime.getRuntime().exit(0);
		}
		return super.handleEvent(event);
	}

	public String getInfo()
	{
		return Init.getInfo();
	}
}

/**
 * Query-answerer, operating on GuiEngine and a Prog
 * 
 * @see Interactor
 * @see Prog
 */
class GuiQA implements Runnable
{
	GuiQA(GuiEngine I)
	{
		this.I = I;
	}

	private GuiEngine I;

	/**
	 * Gets a query from inputField and launches a goal to answer it.
	 */

	public void run()
	{
		String query = I.inputField.getText();
		if (0 == query.length())
		{
			return;
		}
		I.println("?- " + query);
		Init.run_query(query);
	}
}
