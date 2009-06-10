/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.dr.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Michiel Hendriks
 * 
 */
public class DREditor extends EditorPart
{
	private Composite top = null;
	private CTabFolder container = null;
	private Group queryGroup = null;
	private Text output = null;
	private Combo query = null;
	private Button btnExec = null;
	private ToolBar toolBar = null;

	protected Map<IEditorInput, CTabItem> editors;

	public DREditor()
	{
		editors = new HashMap<IEditorInput, CTabItem>();
	}

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
		setTitleToolTip(input.getToolTipText());
		// TODO Auto-generated method stub
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
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.numColumns = 1;
		top = new Composite(parent, SWT.NONE);
		createQueryGroup();
		createToolBar();
		top.setLayout(gridLayout);
		createContainer();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		query.setFocus();
	}

	/**
	 * This method initializes container
	 * 
	 */
	private void createContainer()
	{
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.horizontalAlignment = GridData.FILL;
		container = new CTabFolder(top, SWT.BORDER);
		container.setLayoutData(gridData1);
		container.addCTabFolder2Listener(new CTabFolder2Listener() {

			public void close(CTabFolderEvent event)
			{
				editors.values().remove(event.item);
			}

			public void maximize(CTabFolderEvent event)
			{}

			public void minimize(CTabFolderEvent event)
			{}

			public void restore(CTabFolderEvent event)
			{}

			public void showList(CTabFolderEvent event)
			{}
		});

		CTabItem cTabItem = new CTabItem(container, SWT.NONE);
		cTabItem.setText("Output");
		output = new Text(container, SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY | SWT.H_SCROLL);
		output.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		output.setFont(JFaceResources.getTextFont());
		cTabItem.setControl(output);
		container.setSelection(cTabItem);
	}

	public void addKnowledgeBase(IEditorInput input)
	{
		if (editors.containsKey(input))
		{
			container.setSelection(editors.get(input));
			return;
		}
		PrologEditor edit = new PrologEditor();
		try
		{
			edit.init(getEditorSite(), input);
		}
		catch (PartInitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Composite cont = new Composite(container, SWT.NONE);
		cont.setLayout(new FillLayout());
		edit.createPartControl(cont);

		CTabItem cTabItem = new CTabItem(container, SWT.CLOSE);
		cTabItem.setText(edit.getTitle());
		cTabItem.setToolTipText(edit.getTitleToolTip());
		cTabItem.setControl(cont);
		container.setSelection(cTabItem);
		editors.put(input, cTabItem);

		// TODO reconsultAll()
	}

	/**
	 * This method initializes queryGroup
	 * 
	 */
	private void createQueryGroup()
	{
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		queryGroup = new Group(top, SWT.NONE);
		queryGroup.setText("Query");
		queryGroup.setLayout(gridLayout1);
		queryGroup.setLayoutData(gridData);
		createQuery();
		btnExec = new Button(queryGroup, SWT.NONE);
		btnExec.setText("execute");
	}

	/**
	 * This method initializes query
	 * 
	 */
	private void createQuery()
	{
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.CENTER;
		query = new Combo(queryGroup, SWT.NONE);
		query.setLayoutData(gridData2);
	}

	/**
	 * This method initializes toolBar
	 * 
	 */
	private void createToolBar()
	{
		toolBar = new ToolBar(top, SWT.FLAT);
		ToolItem btnAdd = new ToolItem(toolBar, SWT.PUSH);
		btnAdd.setText("Add Knowledge Base");
		btnAdd.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e)
			{}

			public void widgetSelected(SelectionEvent e)
			{
				selectKnowledgeBase();
			}
		});
		ToolItem btnSave = new ToolItem(toolBar, SWT.PUSH);
		btnSave.setText("Save");
		ToolItem btnSaveAll = new ToolItem(toolBar, SWT.PUSH);
		btnSaveAll.setText("Save All");
		ToolItem sep1 = new ToolItem(toolBar, SWT.SEPARATOR);
		ToolItem btnConsultAll = new ToolItem(toolBar, SWT.PUSH);
		btnConsultAll.setText("Reconsult All");
	}

	protected void selectKnowledgeBase()
	{
		WorkbenchFileSelectionDialog dlg = new WorkbenchFileSelectionDialog(getSite().getShell(), Collections
				.singleton("gnuprologjava.file.prolog"));
		dlg.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dlg.setTitle("Select a knowledge base");
		dlg.setMessage("Select one or more knowledge bases you want to include in the design rational.");
		dlg.setAllowMultiple(true);
		switch (dlg.open())
		{
			case Window.OK:
				Object[] res = dlg.getResult();
				for (Object obj : res)
				{
					if (obj instanceof IFile)
					{
						addKnowledgeBase(new FileEditorInput((IFile) obj));
					}
				}
				break;
		}
	}
} // @jve:decl-index=0:visual-constraint="10,10,483,264"
