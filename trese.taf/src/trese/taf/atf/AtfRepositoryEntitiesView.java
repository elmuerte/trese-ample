/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceNotification;
import net.ample.tracing.ui.properties.RepositoryPropertySourceProvider;
import net.ample.tracing.ui.views.RepositoryBrowser;
import net.ample.tracing.ui.views.RepositoryLabelProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class AtfRepositoryEntitiesView extends ViewPart implements Adapter, ISelectionListener
{
	protected TableViewer items;
	protected PropertySheetPage properties;
	protected Notifier target;

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
		items = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		items.setLabelProvider(new RepositoryLabelProvider());
		items.setContentProvider(new RepositoryItemsProvider());
		getSite().setSelectionProvider(items);

		properties = new PropertySheetPage();
		properties.setPropertySourceProvider(new RepositoryPropertySourceProvider());

		hookViewerContextMenu();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(RepositoryBrowser.ID, this);
	}

	/**
	 * 
	 */
	protected void hookViewerContextMenu()
	{
		MenuManager menuMgr = new MenuManager("trese.taf.atf.repositoryentities.menu");
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
		items.getControl().setFocus();
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
	 * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter)
	{
		if (adapter.equals(IPropertySheetPage.class))
		{
			return properties;
		}
		return super.getAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common
	 * .notify.Notification)
	 */
	public void notifyChanged(Notification notification)
	{
		if (!items.getControl().isDisposed())
		{
			switch (notification.getEventType())
			{
				case TraceNotification.CONNECTION_ESTABLISHED:
				case TraceNotification.CONNECTION_CLOSED:
				case TraceNotification.REPOSITORY_INITIALIZED:
					items.refresh();
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
		if (items.getInput() == sel)
		{
			return;
		}
		items.setInput(sel);
	}
}
