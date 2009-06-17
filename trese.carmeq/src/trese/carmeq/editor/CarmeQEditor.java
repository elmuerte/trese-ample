/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.carmeq.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.internal.ui.parts.FormEntry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class CarmeQEditor extends EditorPart
{
	protected FormToolkit toolkit;
	protected ScrolledForm form;

	public CarmeQEditor()
	{}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
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
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		// TODO Auto-generated method stub
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
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("CarmeQ thingamajig"); // FIXME
		toolkit.decorateFormHeading(form.getForm());

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);

		Composite mainComp = new Composite(form.getBody(), 0);
		layout = new TableWrapLayout();
		layout.numColumns = 3;
		mainComp.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		mainComp.setLayoutData(td);
		toolkit.adapt(mainComp);

		new FormEntry(mainComp, toolkit, "Architecture", "Browse...", false);
		new FormEntry(mainComp, toolkit, "Query", null, false);

		Section proSection = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR
				+ ExpandableComposite.CLIENT_INDENT + ExpandableComposite.TREE_NODE + ExpandableComposite.EXPANDED);
		proSection.setText("Prolog Files");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		proSection.setLayoutData(td);

		Section grooveSection = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR
				+ ExpandableComposite.CLIENT_INDENT + ExpandableComposite.TREE_NODE + ExpandableComposite.EXPANDED);
		grooveSection.setText("Groove");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		grooveSection.setLayoutData(td);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		form.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		toolkit.dispose();
		super.dispose();
	}

}
