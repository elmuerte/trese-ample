/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import java.util.ArrayList;
import java.util.List;

import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceLink;
import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.core.query.Constraints;
import net.ample.tracing.core.query.Query;
import net.ample.tracing.ui.models.ArtefactContainerViewModel;
import net.ample.tracing.ui.models.ArtefactTypeViewModel;
import net.ample.tracing.ui.models.ArtefactViewModel;
import net.ample.tracing.ui.models.LinkContainerViewModel;
import net.ample.tracing.ui.models.LinkTypeViewModel;
import net.ample.tracing.ui.models.LinkViewModel;
import net.ample.tracing.ui.models.RepositoryViewModel;
import net.ample.tracing.ui.models.ViewModel;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import trese.taf.atf.viewmodel.ArtefactDetailViewModel;
import trese.taf.atf.viewmodel.LinkDetailViewModel;
import trese.taf.atf.viewmodel.ArtefactDetailViewModel.ArtefactDetail;
import trese.taf.atf.viewmodel.LinkDetailViewModel.LinkDetail;

/**
 * A IContentProvider that should details for certain ViewModel elements. For
 * the artefact and link types it will show all elements of that type. For
 * artefacts it will add items for incoming and outgoing links, and add items
 * for ancestors and descendants. For links it shows the sources and targets.
 * 
 * @author Michiel Hendriks
 */
public class RepositoryItemsProvider implements IContentProvider, ITreeContentProvider
{
	public static final Object[] EMPTY_ARRAY = new Object[0];

	protected RepositoryManager manager;
	protected RepositoryViewModel managerVm;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
		managerVm = null;
		manager = null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		manager = null;
		managerVm = null;
		if (newInput instanceof ViewModel<?>)
		{
			ViewModel<?> itm = (ViewModel<?>) newInput;
			while (itm != null)
			{
				if (itm instanceof RepositoryViewModel)
				{
					managerVm = (RepositoryViewModel) itm;
					manager = managerVm.getElement();
					break;
				}
				itm = itm.getParent();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	public Object[] getElements(Object obj)
	{
		if (manager == null || !manager.isConnectedToRepository())
		{
			return EMPTY_ARRAY;
		}

		List<Object> result = new ArrayList<Object>();
		if (obj instanceof ArtefactTypeViewModel)
		{
			ArtefactContainerViewModel container = new ArtefactContainerViewModel(managerVm, manager);
			Query<TraceableArtefact> query = manager.getQueryManager().queryOnArtefacts();
			query.add(Constraints.isOfType(((ArtefactTypeViewModel) obj).getElement()));
			for (TraceableArtefact artefact : query.execute())
			{
				result.add(new ArtefactViewModel(container, artefact));
			}
		}
		else if (obj instanceof LinkTypeViewModel)
		{
			LinkContainerViewModel container = new LinkContainerViewModel(managerVm, manager);
			Query<TraceLink> query = manager.getQueryManager().queryOnLinks();
			query.add(Constraints.isOfType(((LinkTypeViewModel) obj).getElement()));
			for (TraceLink link : query.execute())
			{
				result.add(new LinkViewModel(container, link));
			}
		}
		else if (obj instanceof ArtefactViewModel)
		{
			ArtefactViewModel view = (ArtefactViewModel) obj;
			TraceableArtefact artefact = view.getElement();
			if (!artefact.getIncomingLinks().isEmpty())
			{
				result.add(new ArtefactDetailViewModel(view, artefact, ArtefactDetail.INCOMING_LINKS));
			}
			if (!artefact.getOutgoingLinks().isEmpty())
			{
				result.add(new ArtefactDetailViewModel(view, artefact, ArtefactDetail.OUTGOING_LINKS));
			}
			if (!artefact.getAncestors().isEmpty())
			{
				result.add(new ArtefactDetailViewModel(view, artefact, ArtefactDetail.ANCESTORS));
			}
			if (!artefact.getDescendants().isEmpty())
			{
				result.add(new ArtefactDetailViewModel(view, artefact, ArtefactDetail.DESCENDANTS));
			}
		}
		else if (obj instanceof LinkViewModel)
		{
			LinkViewModel view = (LinkViewModel) obj;
			TraceLink link = view.getElement();
			if (!link.getSources().isEmpty())
			{
				result.add(new LinkDetailViewModel(view, link, LinkDetail.SOURCES));
			}
			if (!link.getTargets().isEmpty())
			{
				result.add(new LinkDetailViewModel(view, link, LinkDetail.TARGETS));
			}
		}
		else if (obj instanceof ArtefactDetailViewModel)
		{
			ArtefactDetailViewModel detailView = (ArtefactDetailViewModel) obj;
			TraceableArtefact artefact = detailView.getElement();
			switch (detailView.getDetail())
			{
				case INCOMING_LINKS:
				{
					LinkContainerViewModel container = new LinkContainerViewModel(managerVm, manager);
					for (TraceLink link : artefact.getIncomingLinks())
					{
						result.add(new LinkViewModel(container, link));
					}
					break;
				}
				case OUTGOING_LINKS:
				{
					LinkContainerViewModel container = new LinkContainerViewModel(managerVm, manager);
					for (TraceLink link : artefact.getOutgoingLinks())
					{
						result.add(new LinkViewModel(container, link));
					}
					break;
				}
				case ANCESTORS:
				{
					ArtefactContainerViewModel container = new ArtefactContainerViewModel(managerVm, manager);
					for (TraceableArtefact subart : artefact.getAncestors())
					{
						result.add(new ArtefactViewModel(container, subart));
					}
					break;
				}
				case DESCENDANTS:
				{
					ArtefactContainerViewModel container = new ArtefactContainerViewModel(managerVm, manager);
					for (TraceableArtefact subart : artefact.getDescendants())
					{
						result.add(new ArtefactViewModel(container, subart));
					}
					break;
				}
			}
		}
		else if (obj instanceof LinkDetailViewModel)
		{
			LinkDetailViewModel detailView = (LinkDetailViewModel) obj;
			TraceLink link = detailView.getElement();
			switch (detailView.getDetail())
			{
				case SOURCES:
				{
					ArtefactContainerViewModel container = new ArtefactContainerViewModel(managerVm, manager);
					for (TraceableArtefact source : link.getSources())
					{
						result.add(new ArtefactViewModel(container, source));
					}
					break;
				}
				case TARGETS:
				{
					ArtefactContainerViewModel container = new ArtefactContainerViewModel(managerVm, manager);
					for (TraceableArtefact target : link.getTargets())
					{
						result.add(new ArtefactViewModel(container, target));
					}
					break;
				}
			}
		}
		return result.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		return getElements(parentElement);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	public Object getParent(Object element)
	{
		if (element instanceof ViewModel<?>)
		{
			return ((ViewModel<?>) element).getParent();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	public boolean hasChildren(Object element)
	{
		if (element instanceof LinkViewModel)
		{
			TraceLink link = ((LinkViewModel) element).getElement();
			return !link.getSources().isEmpty() || !link.getTargets().isEmpty();
		}
		else if (element instanceof ArtefactViewModel)
		{
			TraceableArtefact artefact = ((ArtefactViewModel) element).getElement();
			return !artefact.getIncomingLinks().isEmpty() || !artefact.getOutgoingLinks().isEmpty()
					|| !artefact.getAncestors().isEmpty() || !artefact.getDescendants().isEmpty();
		}
		else if (element instanceof ArtefactDetailViewModel)
		{
			return true;
		}
		else if (element instanceof LinkDetailViewModel)
		{
			return true;
		}
		return false;
	}
}
