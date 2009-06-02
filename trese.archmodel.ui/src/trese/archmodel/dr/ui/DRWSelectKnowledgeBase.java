/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.dr.ui;

import groove.prolog.GroovePrologLoadingException;
import groove.prolog.PrologQuery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michiel Hendriks
 * 
 */
public class DRWSelectKnowledgeBase extends WizardPage
{
	protected DesignRationaleWizard wizard;

	protected Text kbFileField;

	protected Text loadOutput;

	protected OutputStream loadOutStream;

	/**
	 * @param pageName
	 */
	protected DRWSelectKnowledgeBase(DesignRationaleWizard parent)
	{
		super("knowledgeBase");
		wizard = parent;
		setTitle("Knowledge Base");
		setDescription("Select the knowledge base containing the design rationale.");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		setControl(container);

		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		label.setLayoutData(gridData);
		label.setText("Select the prolog file to use as knowledge base for the design rationale.");

		kbFileField = new Text(container, SWT.BORDER);
		kbFileField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				updatePageComplete();
			}
		});
		kbFileField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		kbFileField.setEditable(false);

		final Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				browseFile();
			}
		});
		button.setText("Browse...");

		final GridData gridData_2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData_2.horizontalSpan = 2;
		loadOutput = new Text(container, SWT.MULTI);
		loadOutput.setLayoutData(gridData_2);
		loadOutput.setEditable(false);

		loadOutStream = new SWTTextOutputStream(loadOutput);

		updatePageComplete();
	}

	protected void updatePageComplete()
	{
		setPageComplete(false);
		loadOutput.setText("");
		setErrorMessage(null);

		String kbFile = kbFileField.getText();
		if (kbFile != null && kbFile.length() > 0)
		{
			File kb = new File(kbFile);
			if (!kb.exists())
			{
				setErrorMessage(String.format("The file %s does not exist.", kbFile));
				return;
			}
			PrologQuery pq;
			try
			{
				pq = new PrologQuery(wizard.getGrooveState());
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
				setErrorMessage(e1.getMessage());
				return;
			}
			pq.setUserOutput(wizard.getOutputMux());
			wizard.getOutputMux().addStream(loadOutStream);
			try
			{
				pq.init(new FileReader(kb), kb.toString());
			}
			catch (GroovePrologLoadingException e)
			{
				setErrorMessage(e.getMessage());
				return;
			}
			catch (FileNotFoundException e)
			{
				setErrorMessage(e.getMessage());
				return;
			}
			finally
			{
				wizard.getOutputMux().removeStream(loadOutStream);
				try
				{
					loadOutStream.flush();
				}
				catch (IOException e)
				{
					setErrorMessage(e.getMessage());
				}
			}
			wizard.setPrologQuery(pq);
			setPageComplete(true);
		}
	}

	protected void browseFile()
	{
		FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
		fd.setFilterNames(new String[] { "Prolog Files" });
		fd.setFilterExtensions(new String[] { "*.pro;*.pl" });
		String selection = fd.open();
		if (selection != null && selection.length() > 0)
		{
			kbFileField.setText(selection);
		}
	}
}
