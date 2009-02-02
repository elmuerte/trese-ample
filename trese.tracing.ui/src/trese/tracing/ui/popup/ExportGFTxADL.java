/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing.ui.popup;

import groove.graph.DefaultGraph;
import groove.io.AspectGxl;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
	/**
	 * The content type id for the Generalized Feature Tree
	 */
	public static final String GFT_CONTENT_TYPE = "trese.featuremodel.gft";

	/**
	 * The content type id for xADL files
	 */
	public static final String XADL_CONTENT_TYPE = "edu.uci.isr.archstudio4.xadlContentBinding";

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
		if (selection instanceof IStructuredSelection)
		{
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					SubMonitor progress = SubMonitor.convert(monitor);
					progress.beginTask("Exporting GFT+xADL to Groove GST", 6);
					IFile xadlFile = null;
					IFile gftFile = null;
					for (Object o : ((IStructuredSelection) selection).toList())
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
										if (ct.getId().equals(XADL_CONTENT_TYPE))
										{
											xadlFile = (IFile) o;
										}
										else if (ct.getId().equals(GFT_CONTENT_TYPE))
										{
											gftFile = (IFile) o;
										}
									}
								}
							}
							catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
								return;
							}
						}
					}
					if (xadlFile != null && gftFile != null)
					{
						DefaultGraph graph = new DefaultGraph();

						// load xADL
						// convert xADL

						// load GFT
						// convert GFT

						// TODO prompt for destination?
						File dest = null;

						// save graph
						AspectGraph agraph = AspectGraph.getFactory().fromPlainGraph(graph);
						AspectGxl gxl = new AspectGxl();
						try
						{
							gxl.marshalGraph(agraph, dest);
						}
						catch (IOException e)
						{
							e.printStackTrace();
							// TODO error
							return;
						}
						progress.worked(1);

						// refresh destination
						// sourceFile.getParent().refreshLocal(1,
						// progress.newChild(1));
						// IResource resc =
						// sourceFile.getParent().findMember(dest.getName());
						// resc.setDerived(true);
					}
					else
					{
						// TODO erroe
					}
				}
			};
			try
			{
				new ProgressMonitorDialog(shell).run(true, true, op);
			}
			catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
										if (ct.getId().equals(XADL_CONTENT_TYPE))
										{
											hasGft = true;
										}
										else if (ct.getId().equals(GFT_CONTENT_TYPE))
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
