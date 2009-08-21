/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import net.ample.tracing.ui.models.ArtefactTypeContainerViewModel;
import net.ample.tracing.ui.models.LinkTypeContainerViewModel;
import net.ample.tracing.ui.models.RepositoryViewModel;
import net.ample.tracing.ui.views.RepositoryContentProvider;

import org.eclipse.jface.viewers.Viewer;

public class RepositoryManagerContentProvider extends RepositoryContentProvider
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
		if (model.getElement() == null || !model.getElement().isConnectedToRepository())
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