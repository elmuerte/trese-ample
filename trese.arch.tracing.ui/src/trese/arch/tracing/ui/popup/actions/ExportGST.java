package trese.arch.tracing.ui.popup.actions;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
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
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import trese.arch.tracing.conversion.Converter;

public class ExportGST implements IObjectActionDelegate
{

	private Shell shell;

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public ExportGST()
	{
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action)
	{
		if (selection instanceof IStructuredSelection)
		{
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					SubMonitor progress = SubMonitor.convert(monitor);
					Converter converter = new Converter();
					progress.beginTask("Exporting xADL models to Groove GST",
							((IStructuredSelection) selection).size() * 2);
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
								File dest = new File(sourceFile.getRawLocation().makeAbsolute().toFile().toString()
										+ ".gst");
								progress.subTask(String.format("Exporting to: %s", dest.toString()));
								Reader source = new InputStreamReader(sourceFile.getContents());
								converter.convert(source, dest);
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

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection;
		action.setEnabled(!selection.isEmpty());
	}

}
