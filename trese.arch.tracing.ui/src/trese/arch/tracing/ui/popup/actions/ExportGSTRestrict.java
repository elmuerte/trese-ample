package trese.arch.tracing.ui.popup.actions;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ListDialog;

import trese.arch.tracing.conversion.Converter;
import edu.uci.isr.xarch.IXArch;
import edu.uci.isr.xarch.IXArchImplementation;
import edu.uci.isr.xarch.XArchParseException;
import edu.uci.isr.xarch.XArchUtils;
import edu.uci.isr.xarch.instance.IDescription;
import edu.uci.isr.xarch.types.IArchStructure;

public class ExportGSTRestrict implements IObjectActionDelegate
{

	private Shell shell;

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public ExportGSTRestrict()
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
					// IProgressMonitor progress = new NullProgressMonitor();
					Converter converter = new Converter();
					progress.beginTask("Exporting xADL models to Groove GST",
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
								File dest = new File(sourceFile.getRawLocation().makeAbsolute().toFile().toString()
										+ ".gst");
								Reader source = new InputStreamReader(sourceFile.getContents());

								progress.subTask("Loading xADL");
								IXArch arch = getXArch(source);
								progress.worked(1);
								if (arch == null)
								{
									// TODO error
									progress.worked(1);
									continue;
								}
								String restrictTo = selectStructure(arch, sourceFile.getName());
								if (restrictTo == null)
								{
									// skip it
									progress.worked(1);
									continue;
								}
								if (restrictTo.isEmpty())
								{
									restrictTo = null;
								}

								progress.subTask(String.format("Exporting to: %s", dest.toString()));
								converter.convert(arch, restrictTo, dest);
								progress.worked(1);
								sourceFile.getParent().refreshLocal(1, progress);
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
	protected String selectStructure(final IXArch arch, final String archFile)
	{
		final Map<IArchStructure, String> archs = new HashMap<IArchStructure, String>();
		for (Object o : arch.getAllObjects())
		{
			if (o instanceof IArchStructure)
			{
				String archName = ((IArchStructure) o).getId();
				IDescription desc = ((IArchStructure) o).getDescription();
				if (desc != null && !desc.getValue().isEmpty())
				{
					archName = desc.getValue();
				}
				archs.put((IArchStructure) o, archName);
			}
		}
		if (archs.size() <= 1)
		{
			return "";
		}

		final List<String> result = new ArrayList<String>();

		Display.getDefault().asyncExec(new Runnable() {
			public void run()
			{
				ILabelProvider lblProvider = new LabelProvider() {
					public String getText(Object arg0)
					{
						return archs.get(arg0);
					}
				};

				ListDialog dlg = new ListDialog(shell);
				dlg.setTitle("Restrict to ArchStructure");
				dlg.setContentProvider(new ArrayContentProvider());
				dlg.setLabelProvider(lblProvider);
				dlg.setBlockOnOpen(true);
				dlg
						.setMessage(String
								.format(
										"Select the ArchStructure from %s to restrict the output to. Press cancel to export all structures.",
										archFile));
				dlg.setInput(archs.keySet().toArray());
				switch (dlg.open())
				{
					case Window.OK:
						Object[] res = dlg.getResult();
						if (res.length == 1)
						{
							result.add(((IArchStructure) res[0]).getId());
						}
						else
						{
							result.add("");
						}
						break;
					case Window.CANCEL:
						result.add("");
						break;
					default:
						break;
				}
			}
		});
		if (result.size() == 1)
		{
			return result.get(0);
		}
		return null;
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
