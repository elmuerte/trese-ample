/**
 * !! LICENSE PENDING !!
 */
package groove.gui;

import groove.gui.jgraph.AspectJModel;
import groove.gui.jgraph.JGraph;
import groove.io.AspectGxl;
import groove.io.LayedOutXml;
import groove.view.AspectualView;
import groove.view.aspect.AspectGraph;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * A "editor" window that shows a Groove graph in "view" mode. The nodes can be
 * dragged around, but the graph itself and read-only.
 * 
 * @author Michiel Hendriks
 */
public class EclipseGraphViewer extends EditorPart
{

	public EclipseGraphViewer()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		if (!(input instanceof IFileEditorInput))
		{
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		// reduce flicker?
		System.setProperty("sun.awt.noerasebackground", "true");

		Frame frame = SWT_AWT.new_Frame(composite);

		final AspectGxl layoutGxl = new AspectGxl(new LayedOutXml());
		IFileEditorInput fei = (IFileEditorInput) getEditorInput();
		try
		{
			AspectGraph graph = layoutGxl.unmarshalGraph(fei.getFile().getLocationURI().toURL());

			AspectJModel previewModel = AspectJModel.newInstance(AspectualView.createView(graph), new Options());
			JGraph jGraph = new JGraph(previewModel, false);
			jGraph.setToolTipEnabled(true);
			JScrollPane jGraphPane = new JScrollPane(jGraph);
			jGraphPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
			JComponent previewContent = new JPanel(false);
			previewContent.setLayout(new BorderLayout());
			previewContent.add(jGraphPane);
			frame.add(previewContent);

		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{}
}
