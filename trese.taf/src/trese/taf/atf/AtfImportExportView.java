/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceNotification;
import net.ample.tracing.ui.models.ArtefactTypeContainerViewModel;
import net.ample.tracing.ui.models.LinkTypeContainerViewModel;
import net.ample.tracing.ui.models.RepositoryViewModel;
import net.ample.tracing.ui.models.ViewModel;
import net.ample.tracing.ui.views.RepositoryBrowser;
import net.ample.tracing.ui.views.RepositoryContentProvider;
import net.ample.tracing.ui.views.RepositoryLabelProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
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
	protected TreeViewer viewer;
	protected Notifier target;

	public static class RepositoryManagerContentProvider extends RepositoryContentProvider
	{
		public static final Object[] EMPTY_ARRAY = new Object[0];
		protected RepositoryViewModel model;

		/*
		 * (non-Javadoc)
		 * @see
		 * net.ample.tracing.ui.views.RepositoryContentProvider#getElements(
		 * java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement)
		{
			if (model == null)
			{
				return EMPTY_ARRAY;
			}
			return new Object[] { new ArtefactTypeContainerViewModel(model, model.getElement()),
					new LinkTypeContainerViewModel(model, model.getElement()) };
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * net.ample.tracing.ui.views.RepositoryContentProvider#inputChanged
		 * (org.eclipse.jface.viewers.Viewer, java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			if (newInput != null && newInput instanceof RepositoryViewModel)
			{
				model = (RepositoryViewModel) newInput;
			}
			else
			{
				model = null;
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
		viewer = new TreeViewer(parent, SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setLabelProvider(new RepositoryLabelProvider());
		viewer.setContentProvider(new RepositoryManagerContentProvider());
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(RepositoryBrowser.ID, this);
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
		if (sel != null)
		{
			System.out.println(String.format("%s [%s]", sel, sel.getClass().getName()));
		}
		RepositoryViewModel repModel = null;
		while (sel != null && sel instanceof ViewModel<?>)
		{
			if (sel instanceof RepositoryViewModel)
			{
				repModel = (RepositoryViewModel) sel;
				break;
			}
			sel = ((ViewModel<?>) sel).getParent();
		}
		if (sel == null)
		{
			return;
		}

		Object oldInput = viewer.getInput();
		if (oldInput == repModel)
		{
			return;
		}
		if (oldInput instanceof RepositoryViewModel)
		{
			// TODO: remove listener
		}
		viewer.setInput(repModel);
		// TODO: add listener
	}
}
