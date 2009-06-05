/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.dr.ui;

import gnu.prolog.io.TermWriter;
import gnu.prolog.term.Term;
import groove.prolog.PrologQuery;
import groove.prolog.QueryResult;

import java.io.OutputStream;
import java.util.Map.Entry;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michiel Hendriks
 * 
 */
public class DRWReason extends WizardPage
{

	protected Text result;

	protected OutputStream resultStream;

	/**
	 * @param pageName
	 */
	public DRWReason(DesignRationaleWizard parent)
	{
		super("drwreason");
		setTitle("Design Rationale Reasoning");
		setDescription("The result of the executed query. You can go back to execute a different query.");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent)
	{
		result = new Text(parent, SWT.BORDER + SWT.MULTI);
		result.setEditable(false);
		setControl(result);

		resultStream = new SWTTextOutputStream(result);
	}

	/**
	 * @param query
	 * @param prologQuery
	 * @param outputMux
	 */
	public void executeQuery(String query, PrologQuery prologQuery, OutputStreamMux outputMux)
	{
		setErrorMessage(null);
		result.setText(String.format("-? %s.\n", query));
		outputMux.addStream(resultStream);
		try
		{
			QueryResult qr = prologQuery.newQuery(query);
			resultStream.flush();
			if (!result.getText().endsWith("\n"))
			{
				result.append("\n");
			}
			switch (qr.getReturnValue())
			{
				case SUCCESS:
				case SUCCESS_LAST:
					for (Entry<String, Object> entry : qr.getVariables().entrySet())
					{
						result.append(entry.getKey());
						result.append(" = ");
						if (entry.getValue() instanceof Term)
						{
							result.append(TermWriter.toString((Term) entry.getValue()));
						}
						else
						{
							result.append("" + entry.getValue());
						}
						result.append("\n");
					}
					result.append("\nYes");
					break;
				default:
					result.append("\nNo");
					break;
			}
		}
		catch (Exception e)
		{
			setErrorMessage(e.getMessage());
			if (!result.getText().endsWith("\n"))
			{
				result.append("Exception:\n");
				result.append(e.getMessage());
			}
		}
		finally
		{
			outputMux.removeStream(resultStream);
		}
	}

}
