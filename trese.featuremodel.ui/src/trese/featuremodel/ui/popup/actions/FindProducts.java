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
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import trese.featuremodel.EvaluationResult;
import trese.featuremodel.Evaluator;
import trese.featuremodel.loaders.GftLoader;
import trese.featuremodel.model.Feature;
import trese.featuremodel.model.FeatureModelException;

/**
 * Checks if the GFT has a valid product configuration
 * 
 * @author Michiel Hendriks
 */
public class FindProducts implements IObjectActionDelegate
{
	private Shell shell;

	private ISelection selection;

	private Evaluator eval;
	private GftLoader loader;

	/**
	 * 
	 */
	public FindProducts()
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
		final Map<IFile, SortedSet<String>> productConfigs = new LinkedHashMap<IFile, SortedSet<String>>();
		if (selection instanceof IStructuredSelection)
		{
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					MessageConsole console = new MessageConsole("Feature Model", null);
					console.activate();
					ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
					MessageConsoleStream msgs = console.newMessageStream();
					if (eval == null) eval = new Evaluator();
					if (loader == null) loader = new GftLoader();
					SubMonitor progress = SubMonitor.convert(monitor);
					progress.beginTask("Finding product configurations", ((IStructuredSelection) selection).size() * 2);
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
								progress.worked(1);

								CalculatorThread calc = new CalculatorThread();
								calc.baseLine = baseLine;
								Thread thread = new Thread(calc);
								thread.start();
								SubMonitor submon = progress.newChild(1);
								submon.beginTask(String.format("Calculating product configurations for %s", sourceFile
										.getName()), IProgressMonitor.UNKNOWN);
								while (thread.isAlive())
								{
									if (Runtime.getRuntime().maxMemory() <= Runtime.getRuntime().totalMemory()
											&& Runtime.getRuntime().freeMemory() < 3145728) // 3MB,
									// a
									// bit
									// arbitrary
									{
										thread.interrupt();
										progress.setCanceled(true);
										// TODO: error
										System.err.println("Ran out of memory");
										Runtime.getRuntime().gc();
										break;
									}
									if (progress.isCanceled())
									{
										thread.interrupt();
									}
									else
									{
										Thread.sleep(10);
									}
								}
								submon.done();

								if (progress.isCanceled() || thread.isInterrupted())
								{
									break;
								}

								Collection<EvaluationResult> result = calc.result;

								if (result == null || result.isEmpty())
								{
									productConfigs.put(sourceFile, null);
								}
								else
								{
									msgs.println("Valid products configurations:");
									for (EvaluationResult res : result)
									{
										SortedSet<String> validconf = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
										for (Feature feat : res.getIncludedFeatures())
										{
											String featName = feat.getDescription();
											if (featName == null || featName.length() == 0)
											{
												feat.getId();
											}
											validconf.add(featName);
										}
										msgs.println(validconf.toString());
										productConfigs.put(sourceFile, validconf);
									}
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
				// for (Entry<IFile, SortedSet<String>> entry :
				// productConfigs.entrySet())
				// {
				// SortedSet<String> result = entry.getValue();
				// if (result == null)
				// {
				// MessageDialog.openError(shell,
				// "No Product Configuration Found", String.format(
				// "%s does not contain a valid product configuration.",
				// entry.getKey().getName()));
				// }
				// else
				// {
				// MessageDialog.openInformation(shell,
				// "Valid Product Configuration Found", String.format(
				// "%s contains at least one valid product configuration:\n%s",
				// entry.getKey().getName(),
				// result));
				// }
				// }
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

	class CalculatorThread implements Runnable
	{
		public Collection<EvaluationResult> result;

		public Feature baseLine;

		/*
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			try
			{
				result = eval.evaluate(baseLine);
			}
			catch (FeatureModelException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
