/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.dr.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Michiel Hendriks
 * 
 */
public class DRWExecuteKBQuery extends WizardPage
{
	protected DesignRationaleWizard wizard;

	protected Combo query;

	/**
	 * @param pageName
	 */
	protected DRWExecuteKBQuery(DesignRationaleWizard parent)
	{
		super("kbquery");
		wizard = parent;
		setTitle("Query");
		setDescription("Enter the prolog query to execute on the knowledge base.");
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
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);

		final Label label = new Label(container, SWT.NONE);
		// final GridData gridData = new GridData();
		// gridData.horizontalSpan = 2;
		// label.setLayoutData(gridData);
		label.setText("Prolog query");

		query = new Combo(container, SWT.BORDER);
		query.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		query.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				updatePageComplete();
			}
		});

		updatePageComplete();
	}

	protected void updatePageComplete()
	{
		setPageComplete(query.getText().length() > 0);
		setErrorMessage(null);
	}

	public String getQuery()
	{
		String q = query.getText();
		int idx = query.indexOf(q);
		if (idx > -1)
		{
			query.remove(idx);
		}
		query.add(q);
		return q;
	}
}
