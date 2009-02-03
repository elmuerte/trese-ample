/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.featuremodel.ui.popup.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import trese.featuremodel.EvaluationResult;
import trese.featuremodel.Evaluator;
import trese.featuremodel.loaders.GftLoader;
import trese.featuremodel.model.Feature;

/**
 * Checks if the GFT has a valid product configuration
 * 
 * @author Michiel Hendriks
 */
public class HasProduct implements IObjectActionDelegate
{
	private Shell shell;

	private ISelection selection;

	/**
	 * 
	 */
	public HasProduct()
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
		final Map<IFile, SortedSet<String>> productConfigs = new LinkedHashMap<IFile, SortedSet<String>>();
		if (selection instanceof IStructuredSelection)
		{
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					Evaluator eval = new Evaluator();
					GftLoader loader = new GftLoader();
					SubMonitor progress = SubMonitor.convert(monitor);
					progress.beginTask("Finding product configurations", ((IStructuredSelection) selection).size());
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
								Feature baseLine = loader.loadFeatureModel(sourceFile.getContents(true));
								Collection<EvaluationResult> result = eval.evaluate(baseLine, true);
								if (result == null || result.isEmpty())
								{
									productConfigs.put(sourceFile, null);
								}
								else
								{
									EvaluationResult res = result.iterator().next();
									SortedSet<String> validconf = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
									for (Feature feat : res.getIncludedFeatures())
									{
										String featName = feat.getDescription();
										if (featName == null || featName.isEmpty())
										{
											feat.getId();
										}
										validconf.add(featName);
									}
									productConfigs.put(sourceFile, validconf);
								}
							}
							catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						progress.worked(1);
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
			finally
			{
				for (Entry<IFile, SortedSet<String>> entry : productConfigs.entrySet())
				{
					SortedSet<String> result = entry.getValue();
					if (result == null)
					{
						MessageDialog.openError(shell, "No Product Configuration Found", String.format(
								"%s does not contain a valid product configuration.", entry.getKey().getName()));
					}
					else
					{
						MessageDialog.openInformation(shell, "Valid Product Configuration Found", String.format(
								"%s contains at least one valid product configuration:\n%s", entry.getKey().getName(),
								result));
					}
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
	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection;
		action.setEnabled(!selection.isEmpty());
	}

}
