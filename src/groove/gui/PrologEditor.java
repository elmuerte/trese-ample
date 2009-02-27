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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class PrologEditor extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1728208313657610091L;

	public enum QueryMode
	{
		GRAPH_STATE, LTS
	}

	protected Simulator sim;
	protected PrologQuery prolog;
	protected QueryMode mode = QueryMode.GRAPH_STATE;
	protected boolean doConsultUserCode = false;

	protected JTextField query;
	protected JTextPane editor;
	protected JTextArea results;
	protected JButton nextResultBtn;
	protected JButton consultBtn;
	protected JLabel userCodeConsulted;
	protected JLabel statusBar;
	protected OutputStream userOutput;
	protected JFileChooser prologFileChooser;

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

		query = new JTextField();
		query.setFont(editFont);
		query.setText("");
		query.setEditable(true);
		query.setEnabled(true);
		query.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				executeQuery();
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
		splitPane.setOneTouchExpandable(true);
		splitPane.setTopComponent(editorPane);
		splitPane.setBottomComponent(resultsPane);
		splitPane.setDividerLocation(0);

		add(queryPane, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);

		statusBar = new JLabel(" ");
		add(statusBar, BorderLayout.SOUTH);
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
		executeQuery(query.getText());
	}

	protected boolean ensureProlog()
	{
		statusBar.setText(" ");
		if (prolog == null)
		{
			prolog = new PrologQuery();
			prolog.setUserOutput(userOutput);
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
		}
		return true;
	}

	public void executeQuery(String queryString)
	{
		if (queryString == null || queryString.length() == 0)
		{
			return;
		}
		results.setText("?- " + queryString + "\n");

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
			processResults(prolog.newQuery(queryString));
		}
		catch (GroovePrologException e)
		{
			try
			{
				userOutput.flush();
			}
			catch (IOException e1)
			{}
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			results.append(sw.toString());
		}
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
			try
			{
				userOutput.flush();
			}
			catch (IOException e1)
			{}
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			results.append(sw.toString());
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
		statusBar.setText(String.format("Executed in %fms", queryResult.getExecutionTime() / 1000000.0));
		switch (queryResult.getReturnValue())
		{
			case SUCCESS:
			case SUCCESS_LAST:
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
			default:
				results.append(String.format("Unexpected return value: %s", prolog.lastReturnValue().toString()));
		}
		nextResultBtn.setVisible(prolog.hasNext());
		if (nextResultBtn.isVisible())
		{
			nextResultBtn.grabFocus();
		}
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

		int[] buffer = new int[1024];
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
			buffer = new int[1024];
			pos = 0;
		}
	}
}
