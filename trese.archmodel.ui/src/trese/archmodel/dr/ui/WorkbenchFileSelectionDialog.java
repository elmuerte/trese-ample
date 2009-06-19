/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.archmodel.dr.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class WorkbenchFileSelectionDialog extends ElementTreeSelectionDialog implements ISelectionStatusValidator
{
	protected Collection<String> contentIds;

	public static final boolean isFileWithContentId(IFile file, Collection<String> contentIds)
	{
		try
		{
			IContentDescription cd = file.getContentDescription();
			if (cd != null)
			{
				IContentType ct = cd.getContentType();
				if (ct != null)
				{
					if (contentIds.contains(ct.getId()))
					{
						return true;
					}
				}
			}
		}
		catch (CoreException e)
		{}
		return false;
	}

	public WorkbenchFileSelectionDialog(Shell shell, Collection<String> contentIdFilter)
	{
		super(shell, WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(), new FilteredWorkbenchProvider(
				contentIdFilter));
		contentIds = contentIdFilter;
		setValidator(this);
		setComparator(new ResourceComparator(ResourceComparator.NAME));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object
	 * [])
	 */
	public IStatus validate(Object[] selection)
	{
		if (selection.length == 0)
		{
			return new Status(IStatus.ERROR, "trese.tracing.ui", "");
		}
		for (Object o : selection)
		{
			if (o instanceof IFile)
			{
				if (!isFileWithContentId((IFile) o, contentIds))
				{
					return new Status(IStatus.ERROR, "trese.tracing.ui", "Invalid selection");
				}
			}
			else
			{
				return new Status(IStatus.ERROR, "trese.tracing.ui", "");
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * 
	 * 
	 * @author Michiel Hendriks
	 */
	private static final class FilteredWorkbenchProvider extends WorkbenchContentProvider
	{
		protected Collection<String> contentIds;

		public FilteredWorkbenchProvider(Collection<String> contentIdFilter)
		{
			super();
			contentIds = contentIdFilter;
		}

		public Object[] getChildren(Object o)
		{
			if (o instanceof IContainer)
			{
				IResource[] members = null;
				try
				{
					members = ((IContainer) o).members();
				}
				catch (CoreException e)
				{
					// just return an empty set of children
					return new Object[0];
				}

				// filter out the desired resource types
				ArrayList<Object> results = new ArrayList<Object>();
				for (int i = 0; i < members.length; i++)
				{
					// And the test bits with the resource types to see if
					// they are what we want
					if ((members[i].getType() & (IResource.FOLDER | IResource.PROJECT | IResource.ROOT)) > 0)
					{
						results.add(members[i]);
					}
					else if ((members[i].getType() & IResource.FILE) > 0)
					{
						if (isFileWithContentId((IFile) members[i], contentIds))
						{
							results.add(members[i]);
						}
					}
				}
				return results.toArray();
			}
			// input element case
			if (o instanceof ArrayList)
			{
				return ((ArrayList<?>) o).toArray();
			}
			return new Object[0];
		}
	}
}
