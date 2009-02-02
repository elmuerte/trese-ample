/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.ui.popup.actions;

import groove.io.AspectGxl;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import trese.featuremodel.EvaluationResult;
import trese.featuremodel.FeatureGraphCreator;
import trese.featuremodel.loaders.GftLoader;
import trese.featuremodel.model.Feature;

/**
 * @author Michiel Hendriks
 * 
 */
public class ExportGST implements IObjectActionDelegate
{
	private Shell shell;

	private ISelection selection;

	/**
	 * 
	 */
	public ExportGST()
	{
		super();
	}

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
		if (selection instanceof IStructuredSelection)
		{
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					GftLoader loader = new GftLoader();
					SubMonitor progress = SubMonitor.convert(monitor);
					progress.beginTask("Exporting Generalized Feature Trees to Groove GST",
							((IStructuredSelection) selection).size() * 3);
					for (Object o : ((IStructuredSelection) selection).toList())
					{
						if (progress.isCanceled())
						{
							break;
						}
						if (o instanceof IFile)
						{
							try
							{
								IFile sourceFile = (IFile) o;
								File dest = new File(sourceFile.getRawLocation().makeAbsolute().removeFileExtension()
										.toFile().toString()
										+ ".gst");

								progress.subTask(String.format("Exporting to: %s", dest.toString()));

								Feature baseLine = loader.loadFeatureModel(sourceFile.getContents(true));
								if (baseLine == null)
								{
									// TODO error
									progress.worked(3);
									continue;
								}

								EvaluationResult base = new EvaluationResult(baseLine);
								AspectGraph graph = FeatureGraphCreator.createGraph(base);
								progress.worked(1);

								AspectGxl gxl = new AspectGxl();
								try
								{
									gxl.marshalGraph(graph, dest);
								}
								catch (IOException e)
								{
									e.printStackTrace();
									// TODO error
									progress.worked(2);
									continue;
								}
								progress.worked(1);

								sourceFile.getParent().refreshLocal(1, progress.newChild(1));
								IResource resc = sourceFile.getParent().findMember(dest.getName());
								resc.setDerived(true);
							}
							catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
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
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection;
		action.setEnabled(!selection.isEmpty());
	}

}
