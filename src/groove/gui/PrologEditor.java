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

import gnu.prolog.database.Module;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.PrologException;
import groove.explore.Scenario;
import groove.explore.ScenarioFactory;
import groove.explore.result.Acceptor;
import groove.explore.result.PrologCondition;
import groove.explore.strategy.ConditionalBFSStrategy;
import groove.explore.strategy.ExploreStatePrologStrategy;
import groove.io.ExtensionFilter;
import groove.io.GrooveFileChooser;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.prolog.GroovePrologException;
import groove.prolog.GroovePrologLoadingException;
import groove.prolog.PrologQuery;
import groove.prolog.QueryResult;
import groove.prolog.engine.GrooveState;
import groove.util.Groove;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class PrologEditor extends JPanel
{
	private static final long serialVersionUID = 1728208313657610091L;

	public enum QueryMode
	{
		GRAPH_STATE, LTS
	}

	protected int solutionCount;
	protected Simulator sim;
	protected PrologQuery prolog;
	protected QueryMode mode = QueryMode.GRAPH_STATE;
	protected boolean doConsultUserCode = false;
	protected PrologCondition prologCondition;

	protected JComboBox query;
	protected JTextComponent queryEdit;
	protected JTextPane editor;
	protected JTextArea results;
	protected JButton nextResultBtn;
	protected JButton consultBtn;
	protected JLabel userCodeConsulted;
	protected JLabel statusBar;
	protected OutputStream userOutput;
	protected JFileChooser prologFileChooser;
	protected JTree predicateTree;
	protected DefaultMutableTreeNode predRootNode;

	public PrologEditor(Simulator simulator)
	{
		super();
		Font editFont = new Font("Monospaced", Font.PLAIN, 12);

		sim = simulator;
		setLayout(new BorderLayout());

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(new JLabel("Query:"));
		toolBar.addSeparator();

		JToggleButton graphButton = new JToggleButton(Groove.GRAPH_FRAME_ICON, true);
		toolBar.add(graphButton);
		graphButton.setToolTipText("Query the graph state currently visible in the graph panel.");
		graphButton.setEnabled(true);
		graphButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				mode = QueryMode.GRAPH_STATE;
			}
		});

		JToggleButton ltsButton = new JToggleButton(Groove.LTS_FRAME_ICON);
		toolBar.add(ltsButton);
		ltsButton.setToolTipText("Query the LTS");
		ltsButton.setEnabled(true);
		ltsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				mode = QueryMode.LTS;
			}
		});

		ButtonGroup modeSelection = new ButtonGroup();
		modeSelection.add(graphButton);
		modeSelection.add(ltsButton);

		final JPopupMenu explorePopup = new JPopupMenu();
		explorePopup.add(new JMenuItem(createExploreGraphStateAction()));
		explorePopup.add(new JMenuItem(createExploreRuleEventsAction()));

		JButton exploreBtn = new JButton("Explore");
		exploreBtn.setToolTipText("Explore the LTL for each state which has a result with the given query.");
		exploreBtn.addMouseListener(new MouseAdapter() {
			void postToolbarMenu(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					Component c = (Component) e.getSource();
					explorePopup.show(c, 0, c.getHeight());
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				postToolbarMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				postToolbarMenu(e);
			}
		});
		exploreBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				Component c = (Component) e.getSource();
				explorePopup.show(c, 0, c.getHeight());
			}
		});

		toolBar.addSeparator();
		toolBar.add(exploreBtn);

		query = new JComboBox();
		query.setFont(editFont);
		query.setEditable(true);
		query.setEnabled(true);
		queryEdit = (JTextComponent) query.getEditor().getEditorComponent();
		queryEdit.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					executeQuery();
				}
			}
		});

		JButton execQuery = new JButton("Execute");
		execQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				executeQuery();
			}
		});

		JPanel queryPane = new JPanel(new BorderLayout());
		queryPane.add(toolBar, BorderLayout.NORTH);
		queryPane.add(query, BorderLayout.CENTER);
		queryPane.add(execQuery, BorderLayout.EAST);

		toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(new JLabel("User code:"));
		toolBar.addSeparator();

		JButton loadButton = new JButton(new ImageIcon(Groove.getResource("open.gif")));
		loadButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				int result = getPrologFileChooser().showOpenDialog(sim.getFrame());
				// now load, if so required
				if (result == JFileChooser.APPROVE_OPTION)
				{
					File fl = getPrologFileChooser().getSelectedFile();
					try
					{
						FileInputStream fis = new FileInputStream(fl);
						editor.read(fis, null);
						SwingUtilities.invokeLater(new Runnable() {
							public void run()
							{
								setEditorListeners();
								consultUserCode();
							}
						});
					}
					catch (IOException eex)
					{}
				}
			}
		});
		toolBar.add(loadButton);

		JButton saveButton = new JButton(new ImageIcon(Groove.getResource("save.gif")));
		saveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				do
				{
					int result = getPrologFileChooser().showSaveDialog(sim.getFrame());
					// now load, if so required
					if (result == JFileChooser.APPROVE_OPTION)
					{
						File fl = getPrologFileChooser().getSelectedFile();
						if (fl.exists())
						{
							int overwrite = JOptionPane.showConfirmDialog(sim.getFrame(), "Overwrite existing file \""
									+ fl.getName() + "\"?");
							if (overwrite == JOptionPane.NO_OPTION)
							{
								continue;
							}
							else if (overwrite == JOptionPane.CANCEL_OPTION)
							{
								return;
							}
						}
						try
						{
							editor.write(new FileWriter(fl));
						}
						catch (IOException eex)
						{}
						return;
					}
					return;
				} while (true);
			}
		});
		toolBar.add(saveButton);

		toolBar.addSeparator();

		consultBtn = new JButton("Consult");
		consultBtn.setToolTipText("Reconsult the prolog code. This will cancel the current active query.");
		consultBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				consultUserCode();
			}
		});
		toolBar.add(consultBtn);

		userCodeConsulted = new JLabel("");
		userCodeConsulted.setFont(userCodeConsulted.getFont().deriveFont(Font.BOLD));
		toolBar.addSeparator();
		toolBar.add(userCodeConsulted);

		editor = new JTextPane();
		editor.setFont(editFont);
		editor.setText("");
		editor.setEditable(true);
		editor.setEnabled(true);
		setEditorListeners();

		JPanel editorPane = new JPanel(new BorderLayout());
		editorPane.add(toolBar, BorderLayout.NORTH);
		editorPane.add(new JScrollPane(editor), BorderLayout.CENTER);

		results = new JTextArea();
		results.setFont(editFont);
		results.setText("");
		results.setEditable(false);
		results.setEnabled(true);
		results.setBackground(null);
		userOutput = new JTextAreaOutputStream(results);
		Environment.setDefaultOutputStream(userOutput);

		nextResultBtn = new JButton("More?");
		nextResultBtn.setFont(nextResultBtn.getFont().deriveFont(Font.BOLD));
		nextResultBtn.setVisible(false);
		nextResultBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				nextResults();
			}
		});

		JPanel resultsPane = new JPanel(new BorderLayout());
		resultsPane.add(new JScrollPane(results), BorderLayout.CENTER);
		resultsPane.add(nextResultBtn, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setBorder(null);
		splitPane.setOneTouchExpandable(true);
		splitPane.setTopComponent(editorPane);
		splitPane.setBottomComponent(resultsPane);
		splitPane.setDividerLocation(0);

		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(queryPane, BorderLayout.NORTH);
		mainPane.add(splitPane, BorderLayout.CENTER);

		predRootNode = new DefaultMutableTreeNode("Predicates", true);
		predRootNode.add(new DefaultMutableTreeNode("Press 'consult' to load the predicates"));
		predicateTree = new JTree(predRootNode);
		predicateTree.setRootVisible(false);
		predicateTree.setShowsRootHandles(true);
		predicateTree.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1)
				{
					TreePath sel = predicateTree.getSelectionPath();
					if (sel != null)
					{
						Object o = sel.getLastPathComponent();
						if (o instanceof DefaultMutableTreeNode)
						{
							o = ((DefaultMutableTreeNode) o).getUserObject();
							if (o instanceof CompoundTermTag)
							{
								CompoundTermTag tag = (CompoundTermTag) o;
								StringBuilder sb = new StringBuilder(query.getSelectedItem().toString());
								if (sb.length() > 0 && !sb.toString().endsWith(","))
								{
									sb.append(',');
								}
								sb.append(tag.functor.value);
								if (tag.arity > 0)
								{
									sb.append('(');
									for (int i = 0; i < tag.arity; i++)
									{
										if (i > 0)
										{
											sb.append(',');
										}
										sb.append('_');
									}
									sb.append(')');
								}
								query.setSelectedItem(sb.toString());
							}
						}
					}
				}
			}

			public void mouseEntered(MouseEvent e)
			{}

			public void mouseExited(MouseEvent e)
			{}

			public void mousePressed(MouseEvent e)
			{}

			public void mouseReleased(MouseEvent e)
			{}
		});
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) predicateTree.getCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);

		JSplitPane sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		sp2.setResizeWeight(0.3);
		sp2.setBorder(null);
		sp2.setOneTouchExpandable(true);
		sp2.setRightComponent(new JScrollPane(predicateTree));
		sp2.setLeftComponent(mainPane);
		sp2.setDividerLocation(Integer.MAX_VALUE);

		add(sp2, BorderLayout.CENTER);

		statusBar = new JLabel(" ");
		add(statusBar, BorderLayout.SOUTH);
	}

	/**
	 * @return
	 */
	protected Action createExploreRuleEventsAction()
	{
		final ExploreStatePrologStrategy strat = new ExploreStatePrologStrategy();
		Scenario scen = ScenarioFactory.getScenario(strat, new Acceptor(), "Explore by selecting rule events.",
				"Select Rule Events Exploration");
		final Action innerAct = sim.createLaunchScenarioAction(scen);
		Action act = new Action() {
			public void addPropertyChangeListener(PropertyChangeListener listener)
			{
				innerAct.addPropertyChangeListener(listener);
			}

			public Object getValue(String key)
			{
				return innerAct.getValue(key);
			}

			public boolean isEnabled()
			{
				return innerAct.isEnabled();
			}

			public void putValue(String key, Object value)
			{
				innerAct.putValue(key, value);
			}

			public void removePropertyChangeListener(PropertyChangeListener listener)
			{
				innerAct.removePropertyChangeListener(listener);
			}

			public void setEnabled(boolean b)
			{
				innerAct.setEnabled(b);
			}

			public void actionPerformed(ActionEvent e)
			{
				if (queryEdit.getText().length() == 0)
				{
					return;
				}
				if (sim.getCurrentState() == null)
				{
					return;
				}
				results.setText("");
				strat.setPrologQuery(null, queryEdit.getText(), editor.getText());
				innerAct.actionPerformed(e);
			}
		};
		return act;
	}

	/**
	 * @return
	 */
	protected Action createExploreGraphStateAction()
	{
		ConditionalBFSStrategy strat = new ConditionalBFSStrategy();
		prologCondition = new PrologCondition();
		strat.setExploreCondition(prologCondition);
		Scenario scen = ScenarioFactory.getScenario(strat, new Acceptor(), "Explore by accepting graph states.",
				"Accept Graph State Exploration");
		final Action innerAct = sim.createLaunchScenarioAction(scen);
		Action act = new Action() {
			public void addPropertyChangeListener(PropertyChangeListener listener)
			{
				innerAct.addPropertyChangeListener(listener);
			}

			public Object getValue(String key)
			{
				return innerAct.getValue(key);
			}

			public boolean isEnabled()
			{
				return innerAct.isEnabled();
			}

			public void putValue(String key, Object value)
			{
				innerAct.putValue(key, value);
			}

			public void removePropertyChangeListener(PropertyChangeListener listener)
			{
				innerAct.removePropertyChangeListener(listener);
			}

			public void setEnabled(boolean b)
			{
				innerAct.setEnabled(b);
			}

			public void actionPerformed(ActionEvent e)
			{
				if (queryEdit.getText().length() == 0)
				{
					return;
				}
				if (sim.getCurrentState() == null)
				{
					return;
				}
				results.setText("");
				prologCondition.setCondition(queryEdit.getText());
				prologCondition.setUsercode(editor.getText());
				innerAct.actionPerformed(e);
			}
		};
		return act;
	}

	/**
	 * 
	 */
	protected void setEditorListeners()
	{
		editor.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent arg0)
			{
				userCodeConsulted.setText("Modified");
			}

			public void insertUpdate(DocumentEvent arg0)
			{
				userCodeConsulted.setText("Modified");
			}

			public void removeUpdate(DocumentEvent arg0)
			{
				userCodeConsulted.setText("Modified");
			}
		});
	}

	/**
	 * 
	 */
	JFileChooser getPrologFileChooser()
	{
		if (prologFileChooser == null)
		{
			prologFileChooser = new GrooveFileChooser();
			ExtensionFilter prologFilter = new ExtensionFilter("Prolog files", ".pro");
			prologFileChooser.addChoosableFileFilter(prologFilter);
			prologFileChooser.addChoosableFileFilter(new ExtensionFilter("Prolog files", ".pl"));
			prologFileChooser.setFileFilter(prologFilter);
		}
		return prologFileChooser;
	}

	protected void executeQuery()
	{
		executeQuery(queryEdit.getText());
	}

	protected boolean ensureProlog()
	{
		statusBar.setText(" ");
		if (prolog == null)
		{
			prolog = new PrologQuery();
			if (doConsultUserCode)
			{
				String userCode = editor.getText();
				if (userCode != null && userCode.length() > 0)
				{
					try
					{
						prolog.init(new StringReader(userCode), "user_input");
						userCodeConsulted.setText("User code consulted");
						statusBar.setText("User code accepted");
					}
					catch (GroovePrologLoadingException e)
					{
						userCodeConsulted.setText("Error");
						results.append("\nError loading the prolog engine:\n");
						results.append(e.getMessage());
						prolog = null;
						doConsultUserCode = false;
						return false;
					}
				}
			}
			try
			{
				updatePredicateTree(prolog.getEnvironment().getModule());
			}
			catch (GroovePrologLoadingException e)
			{
				results.append("\nError loading the prolog engine:\n");
				results.append(e.getMessage());
				prolog = null;
				return false;
			}
		}
		return true;
	}

	/**
	 * @param module
	 */
	protected void updatePredicateTree(Module module)
	{
		predRootNode.removeAllChildren();
		Map<AtomTerm, DefaultMutableTreeNode> nodes = new HashMap<AtomTerm, DefaultMutableTreeNode>();
		SortedSet<CompoundTermTag> tags = new TreeSet<CompoundTermTag>(new Comparator<CompoundTermTag>() {

			public int compare(CompoundTermTag o1, CompoundTermTag o2)
			{
				int rc = o1.functor.value.compareTo(o2.functor.value);
				if (rc == 0)
				{
					rc = o1.arity - o2.arity;
				}
				return rc;
			}
		});
		tags.addAll(module.getPredicateTags());
		for (CompoundTermTag tag : tags)
		{
			DefaultMutableTreeNode baseNode = nodes.get(tag.functor);
			if (baseNode == null)
			{
				baseNode = new DefaultMutableTreeNode(tag);
				predRootNode.add(baseNode);
				nodes.put(tag.functor, baseNode);
			}
			else
			{
				if (baseNode.getChildCount() == 0)
				{
					baseNode.add(new DefaultMutableTreeNode(baseNode.getUserObject()));
					baseNode.setUserObject(tag.functor.value);
				}
				DefaultMutableTreeNode predNode = new DefaultMutableTreeNode(tag);
				baseNode.add(predNode);
			}
		}
		((DefaultTreeModel) predicateTree.getModel()).reload();
		predicateTree.expandPath(new TreePath(predRootNode.getPath()));
	}

	public void executeQuery(String queryString)
	{
		if (queryString == null)
		{
			return;
		}
		queryString = queryString.trim();
		if (queryString.length() == 0)
		{
			return;
		}
		if (queryString.endsWith("."))
		{
			queryString = queryString.substring(0, queryString.length() - 1);
		}
		// if (query.getSelectedIndex() == -1)
		// {
		// query.insertItemAt(queryString, 0);
		// }
		query.removeItem(queryString);
		query.insertItemAt(queryString, 0);
		query.setSelectedIndex(0);
		results.setText("?- " + queryString + "\n");

		// System.setOut(new PrintStream(userOutput));
		// System.setErr(new PrintStream(userOutput));

		if (!ensureProlog())
		{
			return;
		}

		switch (mode)
		{
			case GRAPH_STATE:
				GraphState gs = sim.getCurrentState();
				if (gs == null)
				{
					results.append("Error: no graph");
					return;
				}
				prolog.setGrooveState(new GrooveState(gs));
				break;
			case LTS:
				GTS gts = sim.getCurrentGTS();
				if (gts == null)
				{
					results.append("Error: no LTS");
					return;
				}
				prolog.setGrooveState(new GrooveState(gts, sim.getCurrentState()));
				break;
		}

		try
		{
			solutionCount = 0;
			processResults(prolog.newQuery(queryString));
		}
		catch (GroovePrologException e)
		{
			handelPrologException(e);
		}
	}

	protected void handelPrologException(Throwable e)
	{
		try
		{
			userOutput.flush();
		}
		catch (IOException e1)
		{}
		if (e.getCause() instanceof PrologException)
		{
			PrologException pe = (PrologException) e.getCause();
			if (pe.getCause() == null)
			{
				results.append(e.getCause().getMessage());
				return;
			}
			else
			{
				e = pe;
			}
		}
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		results.append(sw.toString());
	}

	public void nextResults()
	{
		if (prolog == null || !prolog.hasNext())
		{
			return;
		}
		results.append("\n");
		try
		{
			processResults(prolog.next());
		}
		catch (GroovePrologException e)
		{
			handelPrologException(e);
		}
	}

	/**
	 * @param newQuery
	 */
	protected void processResults(QueryResult queryResult)
	{
		try
		{
			userOutput.flush();
		}
		catch (IOException e)
		{}
		if (queryResult == null)
		{
			return;
		}
		if (!results.getText().endsWith("\n"))
		{
			results.append("\n");
		}
		switch (queryResult.getReturnValue())
		{
			case SUCCESS:
			case SUCCESS_LAST:
				++solutionCount;
				for (Entry<String, Object> entry : queryResult.getVariables().entrySet())
				{
					results.append(entry.getKey());
					results.append(" = ");
					results.append("" + entry.getValue());
					results.append("\n");
				}
				results.append("Yes\n");
				break;
			case FAIL:
				results.append("No\n");
				break;
			case HALT:
				results.append("Interpreter was halted\n");
				break;
			default:
				results.append(String.format("Unexpected return value: %s", prolog.lastReturnValue().toString()));
		}
		nextResultBtn.setVisible(prolog.hasNext());
		if (nextResultBtn.isVisible())
		{
			nextResultBtn.grabFocus();
		}
		statusBar.setText(String.format("%d solution(s); Executed in %fms", solutionCount, queryResult
				.getExecutionTime() / 1000000.0));
	}

	protected void consultUserCode()
	{
		prolog = null;
		doConsultUserCode = true;
		nextResultBtn.setVisible(false);
		results.setText("");
		ensureProlog();
	}

	static class JTextAreaOutputStream extends OutputStream
	{
		JTextArea dest;

		static final int BUFFER_SIZE = 512;
		int[] buffer = new int[BUFFER_SIZE];
		int pos = 0;

		JTextAreaOutputStream(JTextArea toArea)
		{
			dest = toArea;
		}

		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(int arg0) throws IOException
		{
			buffer[pos++] = arg0;
			if (pos >= buffer.length)
			{
				flush();
			}
		}

		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#flush()
		 */
		@Override
		public void flush() throws IOException
		{
			if (pos == 0)
			{
				return;
			}
			dest.append(new String(buffer, 0, pos));
			buffer = new int[BUFFER_SIZE];
			pos = 0;
		}
	}
}
