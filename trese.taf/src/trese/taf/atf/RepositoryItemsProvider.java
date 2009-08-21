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
import net.ample.tracing.core.query.Constraint;
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
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class RepositoryItemsProvider implements IContentProvider, IStructuredContentProvider
{
	public static final Object[] EMPTY_ARRAY = new Object[0];

	enum ViewMode
	{
		VM_Artefacts, VM_TraceLinks,
	}

	protected RepositoryManager manager;
	protected RepositoryViewModel managerVm;
	protected Constraint constraint;
	protected ViewMode mode;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
		managerVm = null;
		manager = null;
		constraint = null;
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
		if (newInput instanceof ArtefactTypeViewModel)
		{
			constraint = Constraints.isOfType(((ArtefactTypeViewModel) newInput).getElement());
			mode = ViewMode.VM_Artefacts;
		}
		else if (newInput instanceof LinkTypeViewModel)
		{
			constraint = Constraints.isOfType(((LinkTypeViewModel) newInput).getElement());
			mode = ViewMode.VM_TraceLinks;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		if (manager == null || constraint == null)
		{
			return EMPTY_ARRAY;
		}
		if (!manager.isConnectedToRepository())
		{
			return EMPTY_ARRAY;
		}
		List<Object> result = new ArrayList<Object>();
		switch (mode)
		{
			case VM_Artefacts:
			{
				ArtefactContainerViewModel container = new ArtefactContainerViewModel(managerVm, manager);
				Query<TraceableArtefact> query = manager.getQueryManager().queryOnArtefacts();
				query.add(constraint);
				for (TraceableArtefact artefact : query.execute())
				{
					result.add(new ArtefactViewModel(container, artefact));
				}
				break;
			}
			case VM_TraceLinks:
			{
				LinkContainerViewModel container = new LinkContainerViewModel(managerVm, manager);
				Query<TraceLink> query = manager.getQueryManager().queryOnLinks();
				query.add(constraint);
				for (TraceLink artefact : query.execute())
				{
					result.add(new LinkViewModel(container, artefact));
				}
				break;
			}
			default:
				return EMPTY_ARRAY;
		}
		return result.toArray();
	}
}
