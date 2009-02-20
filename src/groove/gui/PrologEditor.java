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

import groove.lts.GTS;
import groove.lts.GraphState;
import groove.prolog.GroovePrologException;
import groove.prolog.PrologQuery;
import groove.prolog.QueryResult;
import groove.util.Groove;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class PrologEditor extends JPanel
{
	public enum QueryMode
	{
		GRAPH_STATE, LTS
	}

	protected Simulator sim;
	protected PrologQuery prolog;
	protected QueryMode mode = QueryMode.GRAPH_STATE;
	protected boolean consultUserCode = false;

	protected JTextField query;
	protected JTextPane editor;
	protected JTextArea results;
	protected JButton nextResultBtn;
	protected JButton consultBtn;

	public PrologEditor(Simulator simulator)
	{
		super();
		Font editFont = new Font("Monospaced", Font.PLAIN, 12);

		sim = simulator;
		setLayout(new BorderLayout());

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

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
		ltsButton.setEnabled(false);
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

		JButton saveButton = new JButton("Save");
		saveButton.setEnabled(false);
		toolBar.add(saveButton);

		JButton loadButton = new JButton("Load");
		loadButton.setEnabled(false);
		toolBar.add(loadButton);

		toolBar.addSeparator();

		consultBtn = new JButton("Consult");
		consultBtn.setToolTipText("Reconsult the prolog code. This will cancel the current active query.");
		consultBtn.setEnabled(false);
		consultBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				consultBtn.setEnabled(false);
				consultUserCode();
			}
		});
		toolBar.add(consultBtn);

		editor = new JTextPane();
		editor.setFont(editFont);
		editor.setText("");
		editor.setEditable(true);
		editor.setEnabled(true);
		editor.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent arg0)
			{
				consultBtn.setEnabled(true);
			}

			public void insertUpdate(DocumentEvent arg0)
			{
				consultBtn.setEnabled(true);
			}

			public void removeUpdate(DocumentEvent arg0)
			{
				consultBtn.setEnabled(true);
			}
		});

		JPanel editorPane = new JPanel(new BorderLayout());
		editorPane.add(toolBar, BorderLayout.NORTH);
		editorPane.add(new JScrollPane(editor), BorderLayout.CENTER);

		results = new JTextArea();
		results.setFont(editFont);
		results.setText("");
		results.setEditable(false);
		results.setEnabled(true);
		results.setBackground(null);

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
	}

	protected void executeQuery()
	{
		executeQuery(query.getText());
	}

	protected boolean ensureProlog()
	{
		if (prolog == null)
		{
			prolog = new PrologQuery();
			if (consultUserCode)
			{
				String userCode = editor.getText();
				if (userCode != null && userCode.length() > 0)
				{
					prolog.init(new StringReader(userCode), "usercode");
				}
			}
		}
		return false;
	}

	public void executeQuery(String queryString)
	{
		if (queryString == null || queryString.length() == 0)
		{
			return;
		}
		results.setText("?- " + queryString + (queryString.endsWith(".") ? "\n" : ".\n"));

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
				prolog.setGraphState(gs);
				break;
			case LTS:
				GTS gts = sim.getCurrentGTS();
				if (gts == null)
				{
					results.append("Error: no GTS");
					return;
				}
				results.append("LTL not supported yet");
				return;
		}

		try
		{
			processResults(prolog.newQuery(queryString));
		}
		catch (GroovePrologException e)
		{
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
		if (queryResult == null)
		{
			return;
		}
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
		consultUserCode = true;
		nextResultBtn.setVisible(false);
		results.setText("");
		ensureProlog();
	}
}