/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.dev.gen.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class NumberOfArtefactsWizardPage extends WizardPage
{

	public NumberOfArtefactsWizardPage()
	{
		super("artefactCount");
		setTitle("Number of artefacts");
		setDescription("Enter the number of artefacts that should be created. The types will be equally distributed over this number.");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE | SWT.FILL);
		comp.setLayout(new GridLayout());
		final Spinner val = new Spinner(comp, SWT.BORDER);
		val.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		val.setMinimum(1);
		val.setMaximum(Integer.MAX_VALUE);
		final ArtefactGenWizard wiz = (ArtefactGenWizard) getWizard();
		val.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e)
			{
				wiz.getGeneratorConfig().setCount(Integer.parseInt(val.getText()));
			}
		});
		setControl(comp);
	}

}
