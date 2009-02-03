/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing.ui.popup;

import edu.uci.isr.xarch.IXArch;
import groove.graph.DefaultGraph;
import groove.io.AspectGxl;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ContainerGenerator;
import org.eclipse.ui.dialogs.SaveAsDialog;

import trese.archmodel.groove.ConversionException;
import trese.archmodel.groove.XADL2Graph;
import trese.archmodel.ui.popup.actions.ExportGSTRestrict;
import trese.featuremodel.EvaluationResult;
import trese.featuremodel.FeatureGraphCreator;
import trese.featuremodel.loaders.GftLoader;
import trese.featuremodel.model.Feature;
import trese.featuremodel.model.FeatureModelException;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class ExportGFTxADL extends ExportGSTRestrict
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
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action)
	{
		final IFile destFile = selectDestination();
		if (destFile == null)
		{
			return;
		}
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
						if (!convertXADL(xadlFile, graph, progress))
						{
							return;
						}
						if (!convertGFT(gftFile, graph, progress))
						{
							return;
						}

						File dest = destFile.getLocation().toFile();

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
						try
						{
							destFile.getParent().refreshLocal(1, progress.newChild(1));
						}
						catch (CoreException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						// TODO error
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
	 * @return The selected destination of the file
	 */
	protected IFile selectDestination()
	{
		// TODO This sucks, it's not a very userfriendly way to do this, but it
		// is ok for now.
		SaveAsDialog diag = new SaveAsDialog(shell);
		diag.setBlockOnOpen(true);
		diag.open();
		IPath path = diag.getResult();
		if (path != null)
		{
			if (!path.getFileExtension().equals("gst"))
			{
				path.addFileExtension("gst");
			}
			ContainerGenerator gen = new ContainerGenerator(path.removeLastSegments(1));
			IContainer container;
			try
			{
				container = gen.generateContainer(new NullProgressMonitor());
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			IFile result = container.getFile(path.removeFirstSegments(path.segmentCount() - 1));
			return result;
		}
		return null;
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
		if (!value.isEmpty() && value instanceof IStructuredSelection)
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
		action.setEnabled(enabled);
	}

	/**
	 * @param xadlFile
	 * @param graph
	 * @param progress
	 */
	protected boolean convertXADL(IFile xadlFile, DefaultGraph graph, SubMonitor progress)
	{
		progress.subTask("Loading xADL");
		IXArch arch;
		try
		{
			arch = getXArch(new InputStreamReader(xadlFile.getContents(true)));
		}
		catch (CoreException e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return false;
		}
		progress.worked(1);
		if (arch == null)
		{
			// TODO error
			return false;
		}
		Set<String> restrictTo = selectStructures(arch, xadlFile.getName());
		if (restrictTo == null)
		{
			// skip it
			return false;
		}
		if (restrictTo.isEmpty())
		{
			restrictTo = null;
		}
		progress.subTask("Converting xADL to Groove graph");
		try
		{
			XADL2Graph.convert(arch, restrictTo, graph);
		}
		catch (ConversionException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		progress.worked(1);
		return true;
	}

	/**
	 * @param gftFile
	 * @param graph
	 * @param progress
	 * @return
	 */
	protected boolean convertGFT(IFile gftFile, DefaultGraph graph, SubMonitor progress)
	{
		progress.subTask("Loading Generalized Feature Tree");
		GftLoader loader = new GftLoader();
		Feature baseLine;
		try
		{
			baseLine = loader.loadFeatureModel(gftFile.getContents(true));
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		if (baseLine == null)
		{
			// TODO error
			return false;
		}
		progress.worked(1);

		progress.subTask("Converting GFT to Groove graph");
		try
		{
			EvaluationResult base = new EvaluationResult(baseLine);
			FeatureGraphCreator.createGraph(base, graph);
		}
		catch (FeatureModelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		progress.worked(1);
		return true;
	}
}
