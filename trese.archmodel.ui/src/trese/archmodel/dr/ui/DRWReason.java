/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.dr.ui;

import groove.prolog.PrologQuery;
import groove.prolog.QueryResult;

import java.io.OutputStream;

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
