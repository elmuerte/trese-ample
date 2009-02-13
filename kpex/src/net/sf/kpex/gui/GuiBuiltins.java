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
package net.sf.kpex.gui;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import net.sf.kpex.Builtins;
import net.sf.kpex.Const;
import net.sf.kpex.Fun;
import net.sf.kpex.FunBuiltin;
import net.sf.kpex.IO;
import net.sf.kpex.Int;
import net.sf.kpex.JavaObject;
import net.sf.kpex.Num;
import net.sf.kpex.Prog;
import net.sf.kpex.Term;


/**
 * Registers Jinni builtins for GUI programs
 */
public class GuiBuiltins extends Builtins
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8516831120904106714L;

	GuiBuiltins()
	{
		super();
		register(new new_frame());
		register(new new_button());
		register(new new_label());
		register(new set_label());
		register(new dialog());
		register(new file_dialog());
		register(new new_panel());
		register(new new_color());
		register(new set_fg());
		register(new set_bg());
		register(new set_color());
		register(new add_to());
		register(new remove_from());
		register(new destroy());
		register(new remove_all());
		register(new set_layout());
		register(new show());
		register(new resize());
		register(new move());

		register(new new_console());
		register(new get_applet());
		register(new new_text());
		register(new get_text());
		register(new add_text());
		register(new clear_text());
		register(new new_image());
		register(new new_canvas());
		register(new draw());
	}

	/*
	 * public static void register(Const proto) {
	 * Init.builtinDict.register(proto); }
	 */

	static LayoutManager to_layout(Term L)
	{
		LayoutManager M = null;
		if (L instanceof Fun)
		{
			Fun F = (Fun) L;
			int x = (int) ((Int) F.getArg(0)).getValue();
			int y = (int) ((Int) F.getArg(1)).getValue();
			M = new GridLayout(x, y, 5, 5);
		}
		else
		{
			String m = ((Const) L).name();
			if (m.equals("border"))
			{
				M = new BorderLayout();
				// IO.mes("border");
			}
			// else if(m.equals("card")) {
			// M=new CardLayout();
			// }
			else
			{
				M = new FlowLayout();
			}
		}
		return M;
	}

	static public void add_it(Container P, Component C)
	{
		LayoutManager M = P.getLayout();
		// IO.mes("layout:"+M);
		if (M instanceof BorderLayout)
		{
			int n = P.countComponents();
			// IO.mes("comp: "+n);
			switch (n)
			{
				case 0:
					P.add("Center", C);
					break;
				case 1:
					P.add("North", C);
					break;
				case 2:
					P.add("South", C);
					break;
				case 3:
					P.add("East", C);
					break;
				case 4:
					P.add("West", C);
					break;
				default:
					IO.errmes("component not added:" + C);
			}
		}
		else
		{
			P.add(C);
		}
	}
}

/**
 * Generic and portable event handling through interfaces If component
 * implements Runable2 than its run/2 method will be called by the action event
 * dispatcher.
 */
interface Runnable2
{
	void run(Event evt, Object arg);
}

interface Runnable1
{
	void run(Object arg);
}

interface JinniContainer
{
	public void add_it(Component C);

	public void show_it();
}

class JinniFrame extends Frame implements JinniContainer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4789492619341742199L;

	JinniFrame(String title, LayoutManager L)
	{
		super(title);
		setLayout(L); // hgap=10,vgap=10
		resize(400, 200); // reasonable initial default size
	}

	public void show_it()
	{
		validate();
		// pack(); do not add this - makes the frame shrink
		super.show();
	}

	public void add_it(Component L)
	{
		GuiBuiltins.add_it(this, L);
	}

	@Override
	public boolean action(Event evt, Object arg)
	{
		if (evt.target instanceof Runnable)
		{
			// new Thread((Runnable)evt.target).start();
			// the Runnable should take care to launch new thread, if needed
			((Runnable) evt.target).run();
		}
		else if (evt.target instanceof Runnable1)
		{
			((Runnable1) evt.target).run(arg);
		}
		else if (evt.target instanceof Runnable2)
		{
			((Runnable2) evt.target).run(evt, arg);
		}
		else
		{
			IO.println("UNEXPECTED  TARGET: " + evt.target);
			return false;
		}
		return true;
	}

	@Override
	public boolean handleEvent(Event event)
	{
		if (event.id == Event.WINDOW_DESTROY)
		{
			removeNotify();
			// Runtime.getRuntime().exit(0);
			// dispose();
		}
		return super.handleEvent(event);
	}

}

class new_frame extends FunBuiltin
{
	public new_frame()
	{
		super("new_frame", 3);
	}

	@Override
	public int exec(Prog p)
	{
		String title = getArg(0).toUnquoted();
		LayoutManager L = GuiBuiltins.to_layout(getArg(1));
		Term frameTerm = new JavaObject(new JinniFrame(title, L));
		return putArg(2, frameTerm, p);
	}
}

/*
 * Examples of Jinni GUI components - add more !
 */

/**
 * Button with attached Jinni action. Runs action on new thread, when Button
 * pushed.
 */
class JinniButton extends Button implements Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2357137325209170073L;

	JinniButton(String name, Term action)
	{
		super(name);
		this.name = name;
		this.action = action; // copy() called in corresponding builtin
		prog = null;
	}

	private String name;
	private Term action;
	private Prog prog;

	/**
	 * Passes action to Jinni when Button is pushed
	 */
	public void run()
	{
		if (prog != null)
		{
			prog.stop();
		}
		// prog=Prog.bg(action);
		prog = Prog.new_engine(action, action);
		prog.getElement();
		prog.stop();
	}
}

/**
 * new_button(JinniContainer,Name,Action,Button): creates a Button with label
 * Name and attaches to it an action Action
 */
class new_button extends FunBuiltin
{
	public new_button()
	{
		super("new_button", 4);
	}

	// arg 0=container, arg 1=name, arg 2=action, arg 3=button
	@Override
	public int exec(Prog p)
	{
		JinniContainer C = (JinniContainer) ((JavaObject) getArg(0)).toObject();
		String name = getArg(1).toUnquoted();
		JinniButton JB = new JinniButton(name, getArg(2).copy());
		C.add_it(JB);
		return putArg(3, new JavaObject(JB), p);
	}
}

class new_label extends FunBuiltin
{
	public new_label()
	{
		super("new_label", 3);
	}

	@Override
	public int exec(Prog p)
	{

		// 0=frame 1=name 2=action 3=button
		Container C = (Container) ((JavaObject) getArg(0)).toObject();

		String name = getArg(1).toUnquoted();
		Label L = new Label(name);
		L.setAlignment(Label.CENTER);
		return putArg(2, new JavaObject(C.add(L)), p);
	}
}

/*
 * writes new text to a Label
 */
class set_label extends FunBuiltin
{
	public set_label()
	{
		super("set_label", 2);
	}

	@Override
	public int exec(Prog p)
	{
		// get the handle to the wrapped Label
		JavaObject wrapper = (JavaObject) getArg(0);
		// get the Label
		Label L = (Label) wrapper.toObject();
		L.setText(getArg(1).toUnquoted());
		return 1;
	}
}

class JinniDialog extends Dialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5231580879803534331L;
	TextField field;
	Frame parent;
	Button setButton;
	dialog caller;
	private boolean finished;

	JinniDialog(Frame parent, String title, dialog caller, int x, int y)
	{
		super(parent, title, false);
		this.parent = parent;
		this.caller = caller;
		setFinished(false);
		move(x, y);

		// Create middle section.
		Panel p1 = new Panel();
		Label label = new Label("Enter your answer:");
		p1.add(label);
		field = new TextField(40);
		p1.add(field);
		add("Center", p1);

		// Create bottom row.
		Panel p2 = new Panel();
		p2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		setButton = new Button("Submit");
		// Button b = new Button("Cancel");
		p2.add(setButton);
		// p2.add(b);
		add("South", p2);

		// Initialize this dialog to its preferred size.
		validate();
		pack();
	}

	synchronized private void setFinished(boolean x)
	{
		finished = x;
		notify();
	}

	synchronized void waitAction()
	{
		while (!finished)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{}
		}
	}

	@Override
	public boolean action(Event event, Object arg)
	{
		if (event.target == setButton || event.target == field)
		{
			caller.setAnswer(field.getText());
		}
		field.selectAll();
		hide();
		setFinished(true);
		return true;
	}

	@Override
	public boolean handleEvent(Event event)
	{
		if (event.id == Event.WINDOW_DESTROY)
		{
			// Runtime.getRuntime().exit(0);
			caller.setAnswer(null);
			setFinished(true);
			dispose();
		}
		return super.handleEvent(event);
	}
}

/**
 * dialog(Query,X,Y,Answer): ask a question and collects trhe answer through a
 * modal dialog in a window at X,Y
 */
class dialog extends FunBuiltin
{
	public dialog()
	{
		super("dialog", 4);
	}

	private String answer = null;

	public void setAnswer(String answer)
	{
		this.answer = answer;
	}

	@Override
	public int exec(Prog p)
	{
		Frame F = new Frame();
		String title = getArg(0).toUnquoted();
		int x = (int) ((Int) getArg(1)).getValue();
		int y = (int) ((Int) getArg(2)).getValue();
		JinniDialog D = new JinniDialog(F, title, this, x, y);
		D.show();
		D.waitAction();
		F.dispose();
		if (null == answer)
		{
			return 0;
		}
		return putArg(3, new Const(answer), p);
	}
}

class JinniPanel extends Panel implements JinniContainer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3496261746421099804L;

	JinniPanel(LayoutManager L)
	{
		super();
		setLayout(L);
	}

	JinniPanel()
	{
		this(new FlowLayout());
	}

	public void show_it()
	{
		validate();
		super.show();
	}

	public void add_it(Component C)
	{
		GuiBuiltins.add_it(this, C);
	}
}

class file_dialog extends FunBuiltin
{
	public file_dialog()
	{
		super("file_dialog", 3);
	}

	@Override
	public int exec(Prog p)
	{
		Frame C = new Frame("File Dialog");
		int mode = (int) ((Int) getArg(0)).getValue();
		FileDialog D;
		if (0 == mode)
		{
			D = new FileDialog(C, "Jinni Read File Dialog", FileDialog.LOAD);
		}
		else
		{
			D = new FileDialog(C, "Jinni File Saving Dialog", FileDialog.SAVE);
		}
		D.show();
		String fname = D.getFile();
		if (null == fname)
		{
			return 0;
		}
		String dname = D.getDirectory();
		if (null == dname)
		{
			return 0;
		}
		int ok = putArg(1, new Const(dname), p);
		if (1 == ok)
		{
			ok = putArg(2, new Const(fname), p);
		}
		D.dispose();
		C.dispose();
		return ok;
	}
}

class new_panel extends FunBuiltin
{
	public new_panel()
	{
		super("new_panel", 3);
	}

	@Override
	public int exec(Prog p)
	{
		JinniContainer C = (JinniContainer) ((JavaObject) getArg(0)).toObject();
		LayoutManager L = GuiBuiltins.to_layout(getArg(1));
		JinniPanel P = new JinniPanel(L);
		C.add_it(P);
		return putArg(2, new JavaObject(P), p);
	}
}

class JinniText extends TextArea
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4596891470605107274L;

	JinniText(String oldText, int lines, int cols)
	{
		super(oldText, lines, cols);
	}

	@Override
	public boolean handleEvent(Event event)
	{
		if (event.id == Event.KEY_PRESS)
		{
			setBackground(Color.white);
		}
		return super.handleEvent(event);
	}
}

/**
 * new(ParentContainer,oldText,JavaObject): initialises a textArea with oldText
 * and returns a handle to it
 * 
 * ARGS: 1=Parent Container 2=initial text content 3=rows 4=cols 5=returned
 * handles
 */
class new_text extends FunBuiltin
{
	public new_text()
	{
		super("new_text", 5);
	}

	@Override
	public int exec(Prog p)
	{
		JinniContainer C = (JinniContainer) ((JavaObject) getArg(0)).toObject();
		String oldText = getArg(1).toUnquoted();
		int rows = (int) ((Int) getArg(2)).getValue();
		int cols = (int) ((Int) getArg(3)).getValue();
		JinniText T = new JinniText(oldText, rows, cols);
		C.add_it(T);
		return putArg(4, new JavaObject(T), p);
	}
}

/**
 * get_text(JinniText,Answer): collects the cpntent of thext area to new
 * constant Answer
 */
class get_text extends FunBuiltin
{
	public get_text()
	{
		super("get_text", 2);
	}

	@Override
	public int exec(Prog p)
	{
		JinniText T = (JinniText) ((JavaObject) getArg(0)).toObject();
		String content = T.getText();
		return putArg(1, new Const(content), p);
	}
}

/**
 * add_text
 */
class clear_text extends FunBuiltin
{
	public clear_text()
	{
		super("clear_text", 1);
	}

	@Override
	public int exec(Prog p)
	{
		JinniText T = (JinniText) ((JavaObject) getArg(0)).toObject();
		T.setText("");
		return 1;
	}
}

/**
 * add_text
 */
class add_text extends FunBuiltin
{
	public add_text()
	{
		super("add_text", 2);
	}

	@Override
	public int exec(Prog p)
	{
		JinniText T = (JinniText) ((JavaObject) getArg(0)).toObject();
		String content = getArg(1).toUnquoted();
		T.appendText(content);
		return 1;
	}
}

class new_color extends FunBuiltin
{
	public new_color()
	{
		super("new_color", 4);
	}

	@Override
	public int exec(Prog p)
	{
		double r = ((Num) getArg(0)).getValue();
		double g = ((Num) getArg(1)).getValue();
		double b = ((Num) getArg(2)).getValue();
		if (r > 1 || r < 0)
		{
			IO.errmes("new_color arg 1 should be in 0..1->" + r);
		}
		if (g > 1 || g < 0)
		{
			IO.errmes("new_color arg 2 should be in 0..1->" + g);
		}
		if (b > 1 || b < 0)
		{
			IO.errmes("new_color arg 3 should be in 0..1->" + b);
		}
		int R = (int) (r * 255.0);
		int G = (int) (g * 255.0);
		int B = (int) (b * 255.0);
		Color C = new Color(R, G, B);
		Term ColorTerm = new JavaObject(C);
		return putArg(3, ColorTerm, p);
	}
}

class set_fg extends FunBuiltin
{
	public set_fg()
	{
		super("set_fg", 2);
	}

	@Override
	public int exec(Prog p)
	{
		// get the handle and then the wrapped Component
		Component component = (Component) ((JavaObject) getArg(0)).toObject();
		Color color = (Color) ((JavaObject) getArg(1)).toObject();
		component.setForeground(color);

		return 1;
	}
}

class set_color extends FunBuiltin
{
	public set_color()
	{
		super("set_color", 2);
	}

	@Override
	public int exec(Prog p)
	{
		JinniCanvas component = (JinniCanvas) ((JavaObject) getArg(0)).toObject();
		Color color = (Color) ((JavaObject) getArg(1)).toObject();
		component.setColor(color);
		return 1;
	}
}

class set_bg extends FunBuiltin
{
	public set_bg()
	{
		super("set_bg", 2);
	}

	@Override
	public int exec(Prog p)
	{
		// get the handle and then the wrapped Component
		Component component = (Component) ((JavaObject) getArg(0)).toObject();
		Color color = (Color) ((JavaObject) getArg(1)).toObject();
		component.setBackground(color);
		return 1;
	}
}

/**
 * add_to(Container,What,Where) adds component What at a place Where. Where is a
 * numerical position (-1 to indicate at the end). or a direction keyword:
 * 'North', 'South', 'Center' etc.)
 */
class add_to extends FunBuiltin
{
	public add_to()
	{
		super("add_to", 3);
	}

	@Override
	public int exec(Prog p)
	{
		Container container = (Container) ((JavaObject) getArg(0)).toObject();
		Component component = (Component) ((JavaObject) getArg(1)).toObject();
		Term Where = getArg(2);
		if (Where instanceof Const)
		{
			String direction = ((Const) Where).name();
			container.add(direction, component);
		}
		else
		{
			int position = (int) ((Int) Where).getValue();
			container.add(component, position); // -1 means at end
		}
		return 1;
	}
}

class remove_from extends FunBuiltin
{
	public remove_from()
	{
		super("remove_from", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Container container = (Container) ((JavaObject) getArg(0)).toObject();
		Component component = (Component) ((JavaObject) getArg(1)).toObject();
		container.remove(component);
		return 1;
	}
}

class destroy extends FunBuiltin
{
	public destroy()
	{
		super("destroy", 1);
	}

	@Override
	public int exec(Prog p)
	{
		Component C = (Component) ((JavaObject) getArg(0)).toObject();
		C.removeNotify();
		return 1;
	}
}

class remove_all extends FunBuiltin
{
	public remove_all()
	{
		super("remove_all", 1);
	}

	@Override
	public int exec(Prog p)
	{
		Container C = (Container) ((JavaObject) getArg(0)).toObject();
		C.removeAll();
		return 1;
	}
}

class set_layout extends FunBuiltin
{
	public set_layout()
	{
		super("set_layout", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Container C = (Container) ((JavaObject) getArg(0)).toObject();
		C.removeAll();
		String layoutName = getArg(1).toUnquoted();
		if ("border".equals(layoutName))
		{
			C.setLayout(new BorderLayout());
		}
		else if ("grid".equals(layoutName))
		{
			C.setLayout(new GridLayout(1, 1));
		}
		else if ("flow".equals(layoutName))
		{
			C.setLayout(new FlowLayout());
		}
		else
		{
			return 0;
		}
		return 1;
	}
}

class show extends FunBuiltin
{
	public show()
	{
		super("show", 1);
	}

	@Override
	public int exec(Prog p)
	{
		JinniContainer C = (JinniContainer) ((JavaObject) getArg(0)).toObject();
		C.show_it();
		return 1;
	}
}

class resize extends FunBuiltin
{
	public resize()
	{
		super("resize", 3);
	}

	@Override
	public int exec(Prog p)
	{
		Component C = (Component) ((JavaObject) getArg(0)).toObject();
		int h = (int) ((Int) getArg(1)).getValue();
		int v = (int) ((Int) getArg(2)).getValue();
		C.resize(h, v);
		return 1;
	}
}

class move extends FunBuiltin
{
	public move()
	{
		super("move", 3);
	}

	@Override
	public int exec(Prog p)
	{
		Component C = (Component) ((JavaObject) getArg(0)).toObject();
		int hpos = (int) ((Int) getArg(1)).getValue();
		int vpos = (int) ((Int) getArg(2)).getValue();
		C.move(hpos, vpos);
		return 1;
	}
}

/*
 * Jinni Console providing a simple toplevel inherits run() from GuiEngine which
 * is used to start it on its own thread.
 */

class JinniConsole extends GuiEngine
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3518107717262149235L;
	private String first;

	JinniConsole(Container container, String first)
	{
		super(container);
		this.first = first;
	}

	/**
	 * Initializes Jinni input area with this
	 */
	@Override
	public String firstQuery()
	{
		return first;
	}
}

class new_console extends FunBuiltin
{
	public new_console()
	{
		super("new_console", 3);
	}

	@Override
	public int exec(Prog p)
	{
		Container C = (Container) ((JavaObject) getArg(0)).toObject();
		String query = getArg(1).toUnquoted();
		JinniConsole E = new JinniConsole(C, query);
		C.add(E);
		C.show();
		JavaObject I = new JavaObject(E);
		new Thread(E).start();
		return putArg(2, I, p);
	}
}

/**
 * detects if applet and gets applet container
 */
class get_applet extends FunBuiltin
{
	public get_applet()
	{
		super("get_applet", 1);
	}

	@Override
	public int exec(Prog p)
	{
		if (null == IO.applet)
		{
			return 0;
		}
		Applet applet = IO.applet;
		return putArg(0, new JavaObject(applet), p);
	}
}

class JinniImagePanel extends Canvas
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8841110499264247827L;
	private String sourceName;
	private Image image;
	private int width;
	private int height;

	JinniImagePanel(String sourceName, int width, int height)
	{
		this.sourceName = sourceName;
		this.width = width;
		this.height = height;
		if (null != IO.applet)
		{
			Applet applet = IO.applet;
			URL url = applet.getCodeBase();
			image = applet.getImage(url, sourceName);
		}
		else
		{
			image = Toolkit.getDefaultToolkit().getImage(sourceName);
		}
	}

	// see also (inherited) ImageObserver

	@Override
	public void paint(Graphics g)
	{
		if (width <= 0 || height <= 0)
		{
			width = image.getWidth(this);
			height = image.getHeight(this);
		}
		resize(width, height);
		g.drawImage(image, 0, 0, width, height, this);
	}
}

class new_image extends FunBuiltin
{
	public new_image()
	{
		super("new_image", 5);
	}

	@Override
	public int exec(Prog p)
	{
		JinniContainer C = (JinniContainer) ((JavaObject) getArg(0)).toObject();
		String src = ((Const) getArg(1)).name();
		int width = (int) ((Int) getArg(2)).getValue();
		int height = (int) ((Int) getArg(3)).getValue();
		JinniImagePanel P = new JinniImagePanel(src, width, height);
		C.add_it(P);
		JavaObject JP = new JavaObject(P);
		return putArg(4, JP, p);
	}
}

class JinniCanvas extends Canvas
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6258588438036078670L;

	JinniCanvas()
	{
		V = new Vector();
	}

	Vector V;

	Color color = null;

	void setColor(Color color)
	{
		this.color = color;
	}

	void to_draw(Fun C)
	{
		V.addElement(C);
	}

	// see also (inherited) ImageObserver

	@Override
	public void paint(Graphics g)
	{
		if (null != color)
		{
			g.setColor(color);
		}
		Enumeration E = V.elements();
		while (E.hasMoreElements())
		{
			Fun T = (Fun) E.nextElement();
			IO.mes("drawing: " + T);
			int x = T.getIntArg(0);
			int y = T.getIntArg(1);
			int dx = T.getIntArg(2);
			int dy = T.getIntArg(3);
			g.fill3DRect(x, y, dx, dy, true);
		}
	}
}

class new_canvas extends FunBuiltin
{
	public new_canvas()
	{
		super("new_canvas", 2);
	}

	@Override
	public int exec(Prog p)
	{
		JinniContainer C = (JinniContainer) ((JavaObject) getArg(0)).toObject();
		JinniCanvas P = new JinniCanvas();
		C.add_it(P);
		JavaObject JP = new JavaObject(P);
		return putArg(1, JP, p);
	}
}

class draw extends FunBuiltin
{
	public draw()
	{
		super("draw", 2);
	}

	@Override
	public int exec(Prog p)
	{
		JinniCanvas C = (JinniCanvas) ((JavaObject) getArg(0)).toObject();
		Fun D = (Fun) getArg(1);
		C.to_draw(D);
		return 1;
	}
}
