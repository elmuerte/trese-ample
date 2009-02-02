/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing.ui.popup;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class ExportGFTxADL implements IObjectActionDelegate
{
	private Shell shell;

	private IStructuredSelection selection;

	/**
	 * 
	 */
	public ExportGFTxADL()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		shell = targetPart.getSite().getShell();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action)
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection value)
	{
		this.selection = null;
		boolean enabled = false;
		if (!value.isEmpty())
		{
			if (value instanceof IStructuredSelection)
			{
				this.selection = (IStructuredSelection) value;
				if (selection.size() == 2)
				{
					boolean hasGft = false;
					boolean hasxAdl = false;
					for (Object o : selection.toList())
					{
						if (o instanceof IFile)
						{
							try
							{
								IContentDescription cd = ((IFile) o).getContentDescription();
								if (cd != null)
								{
									IContentType ct = cd.getContentType();
									if (ct != null)
									{
										if (ct.getId().equals("edu.uci.isr.archstudio4.xadlContentBinding"))
										{
											hasGft = true;
										}
										else if (ct.getId().equals("trese.featuremodel.gft"))
										{
											hasxAdl = true;
										}
									}
								}
							}
							catch (CoreException e)
							{
								continue;
							}
						}
					}
					enabled = hasGft && hasxAdl;
				}
			}
		}
		action.setEnabled(enabled);
	}

}
