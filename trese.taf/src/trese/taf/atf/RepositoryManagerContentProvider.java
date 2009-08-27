/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import net.ample.tracing.ui.models.ArtefactTypeContainerViewModel;
import net.ample.tracing.ui.models.LinkTypeContainerViewModel;
import net.ample.tracing.ui.models.RepositoryViewModel;
import net.ample.tracing.ui.models.ViewModel;
import net.ample.tracing.ui.views.RepositoryContentProvider;

import org.eclipse.jface.viewers.Viewer;

public class RepositoryManagerContentProvider extends RepositoryContentProvider
{
	public static final Object[] EMPTY_ARRAY = new Object[0];

	protected ViewModel<?> root;

	/*
	 * (non-Javadoc)
	 * @see net.ample.tracing.ui.views.RepositoryContentProvider#getElements(
	 * java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement)
	{
		if (root == null)
		{
			return EMPTY_ARRAY;
		}
		RepositoryViewModel repoVM = null;
		ViewModel<?> vm = root;
		while (vm != null)
		{
			if (vm instanceof RepositoryViewModel)
			{
				repoVM = (RepositoryViewModel) vm;
				break;
			}
			vm = vm.getParent();
		}

		if (repoVM == null || repoVM.getElement() == null || !repoVM.getElement().isConnectedToRepository())
		{
			return EMPTY_ARRAY;
		}
		if (root instanceof ArtefactTypeContainerViewModel || root instanceof LinkTypeContainerViewModel)
		{
			return getChildren(root);
		}
		else if (root == repoVM)
		{
			return new Object[] { new ArtefactTypeContainerViewModel(repoVM, repoVM.getElement()),
					new LinkTypeContainerViewModel(repoVM, repoVM.getElement()) };
		}
		return EMPTY_ARRAY;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ample.tracing.ui.views.RepositoryContentProvider#inputChanged
	 * (org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (newInput != null
				&& (newInput instanceof RepositoryViewModel || newInput instanceof ArtefactTypeContainerViewModel || newInput instanceof LinkTypeContainerViewModel))
		{
			root = (ViewModel<?>) newInput;
		}
	}
}
