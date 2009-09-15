/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceLink;
import net.ample.tracing.core.TraceNotification;
import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.core.query.Constraint;
import net.ample.tracing.core.query.Constraints;
import net.ample.tracing.core.query.Query;
import net.ample.tracing.ui.models.ArtefactTypeViewModel;
import net.ample.tracing.ui.models.LinkTypeViewModel;
import net.ample.tracing.ui.models.RepositoryViewModel;
import net.ample.tracing.ui.models.ViewModel;
import net.ample.tracing.ui.views.RepositoryBrowser;
import net.ample.tracing.ui.views.RepositoryLabelProvider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import trese.taf.Activator;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class AtfImportExportView extends ViewPart implements Adapter, ISelectionListener
{
	public static final String PREF_ALPHASORT = "trese.taf.atf.importexport.view.alphasort";
	public static final String PREF_COUNTITEMS = "trese.taf.atf.importexport.view.countitems";
	public static final String PREF_EXPORT_PROPS = "trese.taf.atf.importexport.view.exportprops";

	protected CheckboxTreeViewer viewer;
	protected Notifier target;
	protected RepositoryViewModel currentModel;
	protected Button exportBtn, importBtn, exportProps;
	protected Label message;
	protected Composite mainPanel;
	protected boolean showCount;

	protected Action refreshAction;
	protected Action sortAction;
	protected Action showCountAction;

	/**
	 * @author Michiel Hendriks
	 */
	public class RepositoryItemCounterLabelProvider extends CellLabelProvider
	{
		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.jface.viewers.CellLabelProvider#update(org.eclipse.jface
		 * .viewers.ViewerCell)
		 */
		@Override
		public void update(ViewerCell cell)
		{
			if (currentModel == null || !showCount)
			{
				cell.setText("");
				return;
			}
			Object obj = cell.getElement();
			if (obj instanceof ArtefactTypeViewModel)
			{
				ArtefactTypeViewModel vm = (ArtefactTypeViewModel) obj;
				Query<TraceableArtefact> q = currentModel.getElement().getQueryManager().queryOnArtefacts();
				q.add(Constraints.isOfType(vm.getElement()));
				cell.setText(String.format("%d", q.execute().size()));

			}
			else if (obj instanceof LinkTypeViewModel)
			{
				LinkTypeViewModel vm = (LinkTypeViewModel) obj;
				Query<TraceLink> q = currentModel.getElement().getQueryManager().queryOnLinks();
				q.add(Constraints.isOfType(vm.getElement()));
				cell.setText(String.format("%d", q.execute().size()));

			}
		}
	}

	public AtfImportExportView()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		showCount = Activator.getDefault().getPluginPreferences().getBoolean(PREF_COUNTITEMS);

		mainPanel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		mainPanel.setLayout(layout);

		message = new Label(mainPanel, SWT.NONE | SWT.WRAP | SWT.SHADOW_OUT);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		message.setLayoutData(layoutData);

		viewer = new CheckboxTreeViewer(mainPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.VIRTUAL);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(new RepositoryManagerContentProvider());
		viewer.setLabelProvider(new RepositoryLabelProvider());
		if (Activator.getDefault().getPluginPreferences().getBoolean(PREF_ALPHASORT))
		{
			viewer.setComparator(new ViewerSorter());
		}
		viewer.getTree().setHeaderVisible(true);

		TreeViewerColumn col1 = new TreeViewerColumn(viewer, SWT.LEFT);
		col1.getColumn().setText("Type");
		col1.getColumn().setWidth(300);
		col1.setLabelProvider(new TreeColumnViewerLabelProvider(viewer.getLabelProvider()));
		TreeViewerColumn col2 = new TreeViewerColumn(viewer, SWT.RIGHT);
		col2.setLabelProvider(new RepositoryItemCounterLabelProvider());
		col2.getColumn().setText("Count");
		col2.getColumn().setWidth(50);
		col2.getColumn().setToolTipText("Number of elements in the repository with this type.");

		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalSpan = 3;
		viewer.getControl().setLayoutData(layoutData);

		exportBtn = new Button(mainPanel, SWT.PUSH);
		exportBtn.setText("Export Prolog Facts");
		exportBtn.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				exportPrologFacts();
			}
		});
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		exportBtn.setLayoutData(layoutData);

		exportProps = new Button(mainPanel, SWT.CHECK);
		exportProps.setText("Export Properties");
		exportProps.setSelection(Activator.getDefault().getPluginPreferences().getBoolean(PREF_EXPORT_PROPS));
		exportProps.setToolTipText("Also export all properties associated with each element.");
		layoutData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		exportProps.setLayoutData(layoutData);
		exportProps.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				super.widgetSelected(e);
				Activator.getDefault().getPluginPreferences().setValue(PREF_EXPORT_PROPS, exportProps.getSelection());
				Activator.getDefault().savePluginPreferences();
			}
		});

		importBtn = new Button(mainPanel, SWT.PUSH);
		importBtn.setText("Import Prolog Facts");
		importBtn.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				importPrologFacts();
			}
		});
		layoutData = new GridData(GridData.VERTICAL_ALIGN_END);
		importBtn.setLayoutData(layoutData);

		createActions();
		hookViewerContextMenu();
		fillLocalToolBar();

		updateMessage();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(RepositoryBrowser.ID, this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		if (currentModel != null)
		{
			currentModel.getElement().eAdapters().remove(this);
		}
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(RepositoryBrowser.ID, this);
		super.dispose();
	}

	protected void createActions()
	{
		refreshAction = new Action("Refresh") {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.action.Action #run()
			 */
			@Override
			public void run()
			{
				viewer.refresh();
			}
		};
		refreshAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"$nl$/icons/elcl16/refresh.gif"));

		sortAction = new Action("Sort", IAction.AS_CHECK_BOX) {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				if (isChecked())
				{
					viewer.setComparator(new ViewerSorter());
				}
				else
				{
					viewer.setComparator(null);
				}
				Activator.getDefault().getPluginPreferences().setValue(PREF_ALPHASORT, isChecked());
				Activator.getDefault().savePluginPreferences();
			}
		};
		sortAction.setChecked(viewer.getComparator() instanceof ViewerSorter);
		sortAction.setDescription("Sort the entries alphabetical");
		sortAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"$nl$/icons/elcl16/alpha_mode.gif"));

		showCountAction = new Action("Show element count", IAction.AS_CHECK_BOX) {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				showCount = isChecked();
				if (showCount)
				{
					viewer.refresh();
				}
				Activator.getDefault().getPluginPreferences().setValue(PREF_COUNTITEMS, showCount);
				Activator.getDefault().savePluginPreferences();
			}
		};
		showCountAction.setChecked(showCount);
		showCountAction.setDescription("Show the number of elements of a given type in the repository.");
		showCountAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"$nl$/icons/elcl16/elements.gif"));
	}

	protected void setMessage(String msg)
	{
		if (msg != null && msg.length() == 0)
		{
			msg = null;
		}
		if (msg != null)
		{
			if (currentModel != null)
			{
				msg = String.format("[%s] %s", currentModel.getElement().getProject().getName(), msg);
			}
			message.setText(msg);
		}
		else
		{
			message.setText("");
		}
		message.setVisible(msg != null);
		mainPanel.layout(new Control[] { message });
	}

	/**
	 * 
	 */
	protected void exportPrologFacts()
	{
		FileDialog fd = new FileDialog(getSite().getShell(), SWT.SAVE);
		fd.setFilterExtensions(new String[] { "*.pro;*.pl", "*.*" });
		fd.setFilterNames(new String[] { "Prolog Files", "All Files" });
		fd.setOverwrite(true);
		final String result = fd.open();
		if (result != null)
		{
			final List<Constraint> filterTypes = new ArrayList<Constraint>();
			final List<Constraint> filterLinks = new ArrayList<Constraint>();
			final RepositoryManager repoMan = currentModel.getElement();
			final boolean propsExport = exportProps.getSelection();

			Queue<TreeItem> items = new LinkedList<TreeItem>();
			items.addAll(Arrays.asList(viewer.getTree().getItems()));
			while (!items.isEmpty())
			{
				TreeItem item = items.poll();
				items.addAll(Arrays.asList(item.getItems()));
				if (item.getChecked())
				{
					Object data = item.getData();
					if (data instanceof ArtefactTypeViewModel)
					{
						filterTypes.add(Constraints.isOfType(((ArtefactTypeViewModel) data).getElement()));
					}
					else if (data instanceof LinkTypeViewModel)
					{
						filterLinks.add(Constraints.isOfType(((LinkTypeViewModel) data).getElement()));
					}
				}
			}

			try
			{
				new ProgressMonitorDialog(getSite().getShell()).run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
					{
						monitor.beginTask(String.format("Exporting prolog facts to %s", result), 1);
						try
						{
							FileWriter out = new FileWriter(result);
							PrologFactGenerator gen = new PrologFactGenerator(repoMan, out);
							gen.setExportProperties(propsExport);

							if (!filterTypes.isEmpty())
							{
								gen.setArtefactConstraint(Constraints.or(filterTypes.toArray(new Constraint[filterTypes
										.size()])));
							}
							if (!filterLinks.isEmpty())
							{
								gen.setLinkConstraint(Constraints.or(filterLinks.toArray(new Constraint[filterLinks
										.size()])));
							}

							gen.generate(new SubProgressMonitor(monitor, 1));
							out.flush();
							out.close();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						monitor.done();
					}
				});
				setMessage(String.format("Prolog facts generated in %s", result));
			}
			catch (InvocationTargetException e)
			{
				Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getCause().getMessage(), e.getCause());
				Activator.getDefault().getLog().log(status);
				ErrorDialog diag = new ErrorDialog(getSite().getShell(), null, null, status, IStatus.ERROR);
				diag.open();
			}
			catch (InterruptedException e)
			{
				Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getCause().getMessage(), e.getCause());
				Activator.getDefault().getLog().log(status);
				ErrorDialog diag = new ErrorDialog(getSite().getShell(), null, null, status, IStatus.ERROR);
				diag.open();
			}
		}
	}

	protected void importPrologFacts()
	{
		FileDialog fd = new FileDialog(getSite().getShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[] { "*.pro;*.pl", "*.*" });
		fd.setFilterNames(new String[] { "Prolog Files", "All Files" });
		final String result = fd.open();
		if (result != null)
		{
			try
			{
				final Boolean[] success = new Boolean[] { true };
				new ProgressMonitorDialog(getSite().getShell()).run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
					{
						monitor.beginTask(String.format("Importing prolog facts from %s", result), 1);
						FileReader inp = null;
						try
						{
							PrologFactImporter imp = new PrologFactImporter(currentModel.getElement(), Activator
									.getDefault().getLog());
							inp = new FileReader(result);
							success[0] = imp.importFacts(inp, new SubProgressMonitor(monitor, 1));
							inp.close();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						monitor.done();
					}
				});
				if (!success[0])
				{
					ErrorDialog diag = new ErrorDialog(getSite().getShell(), null, null, new Status(IStatus.ERROR,
							Activator.PLUGIN_ID, "Import of prolog facts failed. See the error log for details."),
							IStatus.ERROR);
					diag.open();
				}
			}
			catch (InvocationTargetException e)
			{
				Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getCause().getMessage(), e.getCause());
				Activator.getDefault().getLog().log(status);
				ErrorDialog diag = new ErrorDialog(getSite().getShell(), null, null, status, IStatus.ERROR);
				diag.open();
			}
			catch (InterruptedException e)
			{
				Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getCause().getMessage(), e.getCause());
				Activator.getDefault().getLog().log(status);
				ErrorDialog diag = new ErrorDialog(getSite().getShell(), null, null, status, IStatus.ERROR);
				diag.open();
			}
		}
	}

	/**
	 * 
	 */
	protected void hookViewerContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager)
			{
				fillViewerContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	protected void fillViewerContextMenu(IMenuManager manager)
	{
		manager.add(new Action("Select All") {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				for (TreeItem item : viewer.getTree().getItems())
				{
					viewer.setSubtreeChecked(item.getData(), true);
				}
			}
		});
		manager.add(new Action("Select All Subitems") {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				ISelection sel = viewer.getSelection();
				if (sel instanceof ITreeSelection)
				{
					for (TreePath path : ((ITreeSelection) sel).getPaths())
					{
						viewer.setSubtreeChecked(path, true);
					}
				}
			}
		});
		manager.add(new Action("Deselect All") {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				for (TreeItem item : viewer.getTree().getItems())
				{
					viewer.setSubtreeChecked(item.getData(), false);
				}
			}
		});
		manager.add(new Action("Deselect All Subitems") {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run()
			{
				ISelection sel = viewer.getSelection();
				if (sel instanceof ITreeSelection)
				{
					for (TreePath path : ((ITreeSelection) sel).getPaths())
					{
						viewer.setSubtreeChecked(path, false);
					}
				}
			}
		});
		manager.add(new Separator());
		manager.add(showCountAction);
		manager.add(refreshAction);
		manager.add(new Separator());
		manager.add(new Separator("additions"));
	}

	protected void fillLocalToolBar()
	{
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(sortAction);
		manager.add(showCountAction);
		manager.add(new Separator());
		manager.add(refreshAction);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.common.notify.Adapter#getTarget()
	 */
	public Notifier getTarget()
	{
		return target;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object type)
	{
		return !(type instanceof RepositoryManager);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common
	 * .notify.Notification)
	 */
	public void notifyChanged(Notification notification)
	{
		if (!viewer.getControl().isDisposed())
		{
			switch (notification.getEventType())
			{
				case TraceNotification.CONNECTION_ESTABLISHED:
				case TraceNotification.CONNECTION_CLOSED:
				case TraceNotification.REPOSITORY_INITIALIZED:
					viewer.refresh();
					updateMessage();
					break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common
	 * .notify.Notifier)
	 */
	public void setTarget(Notifier newTarget)
	{
		target = newTarget;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.
	 * IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		Object sel = null;
		if (selection instanceof IStructuredSelection)
		{
			sel = ((IStructuredSelection) selection).getFirstElement();
		}
		currentModel = null;
		while (sel != null && sel instanceof ViewModel<?>)
		{
			if (sel instanceof RepositoryViewModel)
			{
				currentModel = (RepositoryViewModel) sel;
				break;
			}
			sel = ((ViewModel<?>) sel).getParent();
		}
		if (sel == null)
		{
			return;
		}

		Object oldInput = viewer.getInput();
		if (oldInput == currentModel)
		{
			return;
		}
		if (oldInput instanceof RepositoryViewModel)
		{
			((RepositoryViewModel) oldInput).getElement().eAdapters().remove(this);
		}
		viewer.setInput(currentModel);
		if (currentModel != null)
		{
			currentModel.getElement().eAdapters().add(this);
		}
		updateMessage();
	}

	/**
	 * 
	 */
	protected void updateMessage()
	{
		boolean enabled = false;
		if (currentModel == null)
		{
			setMessage("Select a repository.");
		}
		else if (!currentModel.getElement().isConnectedToRepository())
		{
			setMessage("No connection has been established to the repository.");
		}
		else
		{
			enabled = true;
			setMessage("Check items to filter on types.");
		}
		importBtn.setEnabled(enabled);
		exportBtn.setEnabled(enabled);
	}
}
