/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.internal.ui.parts.FormEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import trese.taf.TafFile;
import trese.taf.ui.dialog.WorkbenchFileSelectionDialog;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class TafEditor extends EditorPart
{
	/**
	 * 
	 * 
	 * @author Michiel Hendriks
	 */
	public class PrologFileProvider implements IContentProvider, IStructuredContentProvider
	{
		/**
		 * @param tafFile
		 */
		public PrologFileProvider()
		{}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{}

		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof TafFile)
			{
				return ((TafFile) inputElement).getPrologFiles().toArray();
			}
			return new Object[0];
		}
	}

	protected FormToolkit toolkit;
	protected ScrolledForm form;
	protected Table proFiles;
	protected TafFile tafFile;
	protected IProject project;

	public TafEditor()
	{}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor)
	{
		try
		{
			tafFile.save();
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		if (!(input instanceof IFileEditorInput)) throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		setSite(site);
		setInput(input);
		setPartName(input.getName());

		IFile inputFile = ((FileEditorInput) input).getFile();
		project = inputFile.getProject();
		tafFile = new TafFile(inputFile);
		try
		{
			tafFile.load();
		}
		catch (CoreException e)
		{
			new PartInitException(e.getStatus());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty()
	{
		return tafFile.isDirty();
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
		form.setText("Traceability Analysis Framework thingamajig"); // FIXME
		toolkit.decorateFormHeading(form.getForm());

		Action runAction = new Action("run", IAction.AS_PUSH_BUTTON) {};
		runAction.setDescription("Execute this TAF query");
		runAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui.ide",
				"icons/full/dtool16/build_exec.gif"));
		form.getToolBarManager().add(runAction);

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

		final FormEntry arch = new FormEntry(mainComp, toolkit, "Architecture", "Browse...", false);
		arch.getButton().addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e)
			{}

			public void widgetSelected(SelectionEvent e)
			{
				WorkbenchFileSelectionDialog dlg = new WorkbenchFileSelectionDialog(getSite().getShell(), Collections
						.singleton("edu.uci.isr.archstudio4.xadlContentBinding"));
				dlg.setTitle("Architecture Selection");
				dlg.setMessage("Select an architecture from the current project.");
				dlg.setInput(project);
				dlg.setAllowMultiple(false);
				if (dlg.open() == Window.OK)
				{
					Object[] res = dlg.getResult();
					if (res.length > 0)
					{
						IFile file = (IFile) res[0];
						tafFile.setXADLFile(file);
						arch.setValue(file.getProjectRelativePath().toString());
						firePropertyChange(PROP_DIRTY);
					}
				}
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

		final TableViewer tableViewer = new TableViewer(proComp, toolkit.getBorderStyle() | SWT.H_SCROLL | SWT.V_SCROLL);
		td = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		td.rowspan = 10;
		tableViewer.getTable().setLayoutData(td);
		tableViewer.setContentProvider(new PrologFileProvider());
		tableViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		tableViewer.setInput(tafFile);

		final Button btnProAdd = toolkit.createButton(proComp, "Add...", SWT.PUSH);
		btnProAdd.setLayoutData(new TableWrapData(TableWrapData.FILL));
		btnProAdd.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e)
			{}

			public void widgetSelected(SelectionEvent e)
			{
				WorkbenchFileSelectionDialog dlg = new WorkbenchFileSelectionDialog(getSite().getShell(), Collections
						.singleton("gnuprologjava.file.prolog"));
				dlg.setTitle("Prolog Selection");
				dlg.setMessage("Select one or more prolog files from the current project to add.");
				dlg.setInput(project);
				dlg.setAllowMultiple(true);
				if (dlg.open() == Window.OK)
				{
					Object[] res = dlg.getResult();
					boolean changed = false;
					List<Object> items = new ArrayList<Object>();
					for (Object o : res)
					{
						if (tafFile.addPrologFile((IFile) o))
						{
							tableViewer.add(o);
							items.add(o);
							changed |= true;
							tableViewer.setSelection(new StructuredSelection(o), false);
						}
					}
					if (changed)
					{
						// tableViewer.setSelection(new
						// StructuredSelection(items), false);
						// tableViewer.refresh();
						tableViewer.getTable().setFocus();
						firePropertyChange(PROP_DIRTY);
					}
				}
			}
		});

		final Button btnProRemove = toolkit.createButton(proComp, "Remove", SWT.PUSH);
		btnProRemove.setLayoutData(new TableWrapData(TableWrapData.FILL));
		btnProRemove.setEnabled(false);
		btnProRemove.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e)
			{}

			public void widgetSelected(SelectionEvent e)
			{
				if (tableViewer.getSelection() instanceof StructuredSelection)
				{
					int index = tableViewer.getTable().getSelectionIndex();
					boolean changed = false;
					StructuredSelection sel = (StructuredSelection) tableViewer.getSelection();
					for (Object o : sel.toArray())
					{
						if (o instanceof IFile)
						{
							if (tafFile.removePrologFile((IFile) o))
							{
								tableViewer.remove(o);
								changed |= true;
							}
						}
					}
					if (changed)
					{
						List<IFile> profiles = tafFile.getPrologFiles();
						tableViewer.setSelection(new StructuredSelection(profiles.get(index < profiles.size() ? index
								: profiles.size() - 1)));
						tableViewer.getTable().setFocus();
						firePropertyChange(PROP_DIRTY);
					}
				}
			}
		});

		final Button btnProUp = toolkit.createButton(proComp, "Up", SWT.PUSH);
		btnProUp.setLayoutData(new TableWrapData(TableWrapData.FILL));
		btnProUp.setEnabled(false);
		final Button btnProDown = toolkit.createButton(proComp, "Down", SWT.PUSH);
		btnProDown.setLayoutData(new TableWrapData(TableWrapData.FILL));
		btnProDown.setEnabled(false);

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				boolean selected = !tableViewer.getSelection().isEmpty();
				btnProRemove.setEnabled(selected);
				btnProUp.setEnabled(selected && tableViewer.getTable().getSelectionIndex() > 0);
				btnProDown.setEnabled(selected
						&& tableViewer.getTable().getSelectionIndex() < tableViewer.getTable().getItemCount() - 1);
			}
		});
	}

	/**
	 * 
	 */
	protected void createGrooveSection()
	{
	// TableWrapData td;
	// Section grooveSection = toolkit.createSection(form.getBody(),
	// ExpandableComposite.TITLE_BAR
	// + ExpandableComposite.TREE_NODE + ExpandableComposite.EXPANDED);
	// grooveSection.setText("Groove");
	// td = new TableWrapData(TableWrapData.FILL_GRAB);
	// grooveSection.setLayoutData(td);
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
