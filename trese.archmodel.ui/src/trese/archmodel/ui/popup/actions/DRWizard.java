/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.ui.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;

import trese.archmodel.dr.ui.DesignRationaleWizard;

/**
 * @author Michiel Hendriks
 * 
 */
public class DRWizard implements IObjectActionDelegate
{
	private IWorkbenchPart targetPart;
	private IStructuredSelection selection;

	public DRWizard()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		this.targetPart = targetPart;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		IWorkbenchPartSite site = targetPart.getSite();
		IWorkbenchWindow window = site.getWorkbenchWindow();
		DesignRationaleWizard wizard = new DesignRationaleWizard();
		wizard.init(window.getWorkbench(), selection);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection instanceof IStructuredSelection ? (IStructuredSelection) selection : null;

	}

}
