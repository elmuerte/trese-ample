/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import net.ample.tracing.ui.properties.RepositoryPropertySourceProvider;
import net.ample.tracing.ui.views.RepositoryBrowser;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

import trese.taf.Activator;

/**
 * This view shows artfecats, links and their references elements based on the
 * selected element from the "repository browser" view.
 * 
 * @author Michiel Hendriks
 */
public class AtfRepositoryEntitiesView extends ViewPart implements ISelectionListener
{
	protected TreeViewer items;
	protected PropertySheetPage properties;

	protected Action sortAction;
	protected Action refreshAction;

	public AtfRepositoryEntitiesView()
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
		createActions();

		items = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		items.setLabelProvider(new RepositoryLabelProviderEx());
		items.setContentProvider(new RepositoryItemsProvider());
		items.setComparator(new ViewerSorter());
		getSite().setSelectionProvider(items);

		properties = new PropertySheetPage();
		properties.setPropertySourceProvider(new RepositoryPropertySourceProvider());

		hookViewerContextMenu();
		fillLocalToolBar();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(RepositoryBrowser.ID, this);
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
				items.refresh();
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
					items.setComparator(new ViewerSorter());
				}
				else
				{
					items.setComparator(null);
				}
			}
		};
		sortAction.setChecked(true);
		sortAction.setDescription("Sort the entries alphabetical");
		sortAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"$nl$/icons/elcl16/alpha_mode.gif"));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(RepositoryBrowser.ID, this);
		super.dispose();
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
		Menu menu = menuMgr.createContextMenu(items.getControl());
		items.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, items);
	}

	protected void fillViewerContextMenu(IMenuManager manager)
	{
		manager.add(refreshAction);
		manager.add(new Separator());
		manager.add(new Separator("additions"));
	}

	protected void fillLocalToolBar()
	{
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(sortAction);
		manager.add(refreshAction);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		items.getControl().setFocus();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("unchecked") Class adapter)
	{
		if (adapter.equals(IPropertySheetPage.class))
		{
			return properties;
		}
		return super.getAdapter(adapter);
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
		if (items.getInput() == sel)
		{
			return;
		}
		if (sel == null)
		{
			items.setSelection(null);
		}
		items.setInput(sel);
	}
}
