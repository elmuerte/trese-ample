package trese.arch.tracing.ui.popup.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

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

import trese.arch.tracing.featuremodel.XArchToFeatueModel;
import trese.arch.tracing.ui.dialogs.ArchitectureSelector;
import trese.featuremodels.gft.SaveAsGFT;
import trese.featuremodels.model.Feature;
import edu.uci.isr.xarch.IXArch;
import edu.uci.isr.xarch.IXArchImplementation;
import edu.uci.isr.xarch.XArchParseException;
import edu.uci.isr.xarch.XArchUtils;

public class ExportGFTRestrict implements IObjectActionDelegate
{

	private Shell shell;

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public ExportGFTRestrict()
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
					progress.beginTask("Exporting xADL models to GFT", ((IStructuredSelection) selection).size() * 4);
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
								Reader source = new InputStreamReader(sourceFile.getContents());

								progress.subTask("Loading xADL");
								IXArch arch = getXArch(source);
								progress.worked(1);
								if (arch == null)
								{
									// TODO error
									progress.worked(3);
									continue;
								}
								Set<String> restrictTo = selectStructures(arch, sourceFile.getName());
								if (restrictTo == null)
								{
									// skip it
									progress.worked(3);
									continue;
								}
								if (restrictTo.isEmpty())
								{
									restrictTo = null;
								}

								Set<Feature> result = XArchToFeatueModel.convert(arch, restrictTo);
								progress.worked(1);

								SubMonitor submon = progress.newChild(1);
								submon.beginTask("Saving results", result.size() * 2);
								for (Feature f : result)
								{
									File dest = new File(sourceFile.getRawLocation().makeAbsolute()
											.removeFileExtension().toFile().toString()
											+ "." + f.getDescription().replaceAll("[^0-9a-zA-Z]+", "_") + ".gft");
									SaveAsGFT.save(f, new FileOutputStream(dest));
									submon.worked(1);

									sourceFile.getParent().refreshLocal(1, submon.newChild(1));
									IResource resc = sourceFile.getParent().findMember(dest.getName());
									resc.setDerived(true);
								}
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
	 * Load the architecture
	 * 
	 * @param source
	 * @return
	 */
	protected IXArch getXArch(Reader source)
	{
		IXArchImplementation impl = XArchUtils.getDefaultXArchImplementation();
		try
		{
			return impl.parse(source);
		}
		catch (XArchParseException e)
		{
			return null;
		}
	}

	/**
	 * Create a selection dialog
	 * 
	 * @param arch
	 * @param archFile
	 * @return
	 */
	protected Set<String> selectStructures(final IXArch arch, final String archFile)
	{
		ArchitectureSelector asel = new ArchitectureSelector(shell, arch, archFile);
		return asel.selectMultiple();
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
