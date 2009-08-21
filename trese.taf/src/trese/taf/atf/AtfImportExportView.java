/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import java.io.FileWriter;
import java.io.IOException;
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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class AtfImportExportView extends ViewPart implements Adapter, ISelectionListener
{
	protected CheckboxTreeViewer viewer;
	protected Notifier target;
	protected RepositoryViewModel currentModel;
	protected Button exportBtn;
	protected Label message;
	protected Composite mainPanel;

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
			if (currentModel == null)
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
		mainPanel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		mainPanel.setLayout(layout);

		message = new Label(mainPanel, SWT.NONE | SWT.WRAP | SWT.SHADOW_OUT);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 2;
		message.setLayoutData(layoutData);

		viewer = new CheckboxTreeViewer(mainPanel, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new RepositoryManagerContentProvider());
		viewer.setLabelProvider(new RepositoryLabelProvider());
		viewer.setComparator(new ViewerSorter());
		viewer.getTree().setHeaderVisible(true);

		TreeViewerColumn col1 = new TreeViewerColumn(viewer, SWT.LEFT);
		col1.getColumn().setText("Type");
		col1.getColumn().setWidth(300);
		col1.setLabelProvider(new TreeColumnViewerLabelProvider(viewer.getLabelProvider()));
		TreeViewerColumn col2 = new TreeViewerColumn(viewer, SWT.RIGHT);
		col2.setLabelProvider(new RepositoryItemCounterLabelProvider());
		col2.getColumn().setText("Count");
		col2.getColumn().setWidth(50);

		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		viewer.getControl().setLayoutData(layoutData);
		hookViewerContextMenu();

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

		updateMessage();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(RepositoryBrowser.ID, this);
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
		String result = fd.open();
		if (result != null)
		{
			try
			{
				FileWriter out = new FileWriter(result);
				PrologFactGenerator gen = new PrologFactGenerator(currentModel.getElement(), out);

				List<Constraint> filterTypes = new ArrayList<Constraint>();
				List<Constraint> filterLinks = new ArrayList<Constraint>();

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

				if (!filterTypes.isEmpty())
				{
					gen.setArtefactConstraint(Constraints.or(filterTypes.toArray(new Constraint[filterTypes.size()])));
				}
				if (!filterLinks.isEmpty())
				{
					gen.setLinkConstraint(Constraints.or(filterLinks.toArray(new Constraint[filterLinks.size()])));
				}

				gen.generate();
				out.flush();
				out.close();
				setMessage(String.format("Prolog facts generated in %s", result));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	protected void hookViewerContextMenu()
	{
		MenuManager menuMgr = new MenuManager("trese.taf.atf.importexport.view.repositorybrowser");
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
		manager.add(new Separator("additions"));
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
		exportBtn.setEnabled(currentModel != null && currentModel.getElement().isConnectedToRepository());
		updateMessage();
	}

	/**
	 * 
	 */
	protected void updateMessage()
	{
		if (currentModel == null)
		{
			exportBtn.setEnabled(false);
			setMessage("Select a repository.");
		}
		else if (!currentModel.getElement().isConnectedToRepository())
		{
			exportBtn.setEnabled(false);
			setMessage("No connection has been established to the repository.");
		}
		else
		{
			exportBtn.setEnabled(true);
			setMessage("Check items to filter on types.");
		}
	}
}
