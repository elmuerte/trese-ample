/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.carmeq.editor;

import java.util.Collections;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.pde.internal.ui.parts.FormEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
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

import trese.carmeq.ui.dialog.WorkbenchFileSelectionDialog;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class CarmeQEditor extends EditorPart
{
	protected FormToolkit toolkit;
	protected ScrolledForm form;
	protected Table proFiles;

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

		FormEntry arch = new FormEntry(mainComp, toolkit, "Architecture", "Browse...", false);
		arch.getButton().addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e)
			{}

			public void widgetSelected(SelectionEvent e)
			{
				WorkbenchFileSelectionDialog dlg = new WorkbenchFileSelectionDialog(getSite().getShell(), Collections
						.singleton(""));
				dlg.setTitle("Architecture Selection");
				dlg.setMessage("Select an architecture from the current project.");
				dlg.setInput(ResourcesPlugin.getWorkspace().getRoot().);
				dlg.setAllowMultiple(false);
				dlg.open();
			}
		});
		new FormEntry(mainComp, toolkit, "Query", null, false);

		createPrologSection();
		createGrooveSection();
	}

	/**
	 * 
	 */
	protected void createPrologSection()
	{
		Section proSection = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR
				+ ExpandableComposite.TREE_NODE + ExpandableComposite.EXPANDED);
		proSection.setText("Prolog Knowledgebase");
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		proSection.setLayoutData(td);

		Composite proComp = new Composite(proSection, 0);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		proComp.setLayout(layout);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		proComp.setLayoutData(td);
		toolkit.adapt(proComp);
		proSection.setClient(proComp);

		TableViewer tableViewer = new TableViewer(proComp, toolkit.getBorderStyle() | SWT.H_SCROLL | SWT.V_SCROLL);
		td = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		td.rowspan = 10;
		tableViewer.getTable().setLayoutData(td);

		final Button btnProAdd = toolkit.createButton(proComp, "Add...", SWT.PUSH);
		btnProAdd.setLayoutData(new TableWrapData(TableWrapData.FILL));
		final Button btnProRemove = toolkit.createButton(proComp, "Remove", SWT.PUSH);
		btnProRemove.setLayoutData(new TableWrapData(TableWrapData.FILL));
		btnProRemove.setEnabled(false);
		final Button btnProUp = toolkit.createButton(proComp, "Up", SWT.PUSH);
		btnProUp.setLayoutData(new TableWrapData(TableWrapData.FILL));
		btnProUp.setEnabled(false);
		final Button btnProDown = toolkit.createButton(proComp, "Down", SWT.PUSH);
		btnProDown.setLayoutData(new TableWrapData(TableWrapData.FILL));
		btnProDown.setEnabled(false);

		// proFiles.addSelectionListener(new SelectionListener() {
		// public void widgetDefaultSelected(SelectionEvent e)
		// {}
		//
		// public void widgetSelected(SelectionEvent e)
		// {
		// boolean selected = proFiles.getSelectionCount() > 0;
		// btnProRemove.setEnabled(selected);
		// btnProUp.setEnabled(selected && proFiles.getSelectionIndex() > 0);
		// btnProDown.setEnabled(selected && proFiles.getSelectionIndex() <
		// proFiles.getItemCount() - 1);
		// }
		// });
	}

	/**
	 * 
	 */
	protected void createGrooveSection()
	{
		TableWrapData td;
		Section grooveSection = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR
				+ ExpandableComposite.TREE_NODE + ExpandableComposite.EXPANDED);
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
