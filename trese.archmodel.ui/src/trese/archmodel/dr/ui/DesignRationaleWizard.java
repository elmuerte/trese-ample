/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.dr.ui;

import groove.prolog.PrologQuery;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Michiel Hendriks
 * 
 */
public class DesignRationaleWizard extends Wizard implements IWorkbenchWizard
{
	protected OutputStreamMux outputMux;

	protected PrologQuery prologQuery;

	public DesignRationaleWizard()
	{
		super();
		setWindowTitle("Design Rationale");
		setNeedsProgressMonitor(true);
		setHelpAvailable(false);
		outputMux = new OutputStreamMux();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages()
	{
		super.addPages();
		addPage(new DRWSelectKnowledgeBase(this));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{

	}

	/**
	 * @return the outputMux
	 */
	public OutputStreamMux getOutputMux()
	{
		return outputMux;
	}

	/**
	 * @param pq
	 */
	public void setPrologQuery(PrologQuery pq)
	{
		prologQuery = pq;
	}

	/**
	 * @return the prologQuery
	 */
	public PrologQuery getPrologQuery()
	{
		return prologQuery;
	}
}
