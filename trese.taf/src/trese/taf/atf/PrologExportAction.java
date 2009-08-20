/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import java.io.FileWriter;
import java.io.IOException;

import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.ui.models.RepositoryViewModel;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
@Deprecated
public class PrologExportAction implements IObjectActionDelegate
{
	protected RepositoryManager repository;
	protected Shell shell;

	public PrologExportAction()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		shell = targetPart.getSite().getShell();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		if (repository != null)
		{
			FileDialog fd = new FileDialog(shell, SWT.SAVE);
			fd.setFilterExtensions(new String[] { "*.pro;*.pl", "*.*" });
			fd.setFilterNames(new String[] { "Prolog Files", "All Files" });
			fd.setOverwrite(true);
			String result = fd.open();
			if (result != null)
			{
				try
				{
					FileWriter out = new FileWriter(result);
					PrologFactGenerator gen = new PrologFactGenerator(repository, out);
					gen.generate();
					out.flush();
					out.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		action.setEnabled(false);
		if (selection instanceof IStructuredSelection)
		{
			for (Object o : ((IStructuredSelection) selection).toList())
			{
				if (o instanceof RepositoryViewModel)
				{
					repository = ((RepositoryViewModel) o).getElement();
					action.setEnabled(repository != null && repository.isConnectedToRepository());
				}
			}
		}
	}
}
