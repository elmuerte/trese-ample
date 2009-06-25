/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class ToggleTafNature implements IObjectActionDelegate
{
	protected Set<IProject> projects;

	public ToggleTafNature()
	{
		projects = new HashSet<IProject>();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		for (IProject project : projects)
		{
			// Cannot modify closed projects.
			if (!project.isOpen())
			{
				continue;
			}

			// Get the description.
			IProjectDescription description;
			try
			{
				description = project.getDescription();
			}
			catch (CoreException e)
			{
				// TODO
				continue;
			}

			// Toggle the nature.
			List<String> newIds = new ArrayList<String>();
			newIds.addAll(Arrays.asList(description.getNatureIds()));

			int index = newIds.indexOf(Activator.NATURE_ID);
			if (index == -1)
			{
				newIds.add(Activator.NATURE_ID);
			}
			else
			{
				newIds.remove(index);
			}

			description.setNatureIds(newIds.toArray(new String[newIds.size()]));

			// Save the description.
			try
			{
				project.setDescription(description, null);
				project.refreshLocal(0, null);
			}
			catch (CoreException e)
			{
				// TODO
			}
		}
	}

	/**
	 * @param selection
	 */
	private void updateSelectedProjects(ISelection selection)
	{
		projects.clear();
		if (!(selection instanceof IStructuredSelection)) return;
		for (Iterator<?> iter = ((IStructuredSelection) selection).iterator(); iter.hasNext();)
		{
			Object elem = iter.next();
			if (!(elem instanceof IResource))
			{
				if (!(elem instanceof IAdaptable)) continue;
				elem = ((IAdaptable) elem).getAdapter(IResource.class);
				if (!(elem instanceof IResource)) continue;
			}
			if (!(elem instanceof IProject))
			{
				elem = ((IResource) elem).getProject();
				if (!(elem instanceof IProject)) continue;
			}
			projects.add((IProject) elem);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		updateSelectedProjects(selection);
		if (projects.size() > 0)
		{
			action.setEnabled(true);
			boolean checked = true;

			for (IProject project : projects)
			{
				try
				{
					checked &= project.isOpen() && project.hasNature(Activator.NATURE_ID);
				}
				catch (CoreException e)
				{
					checked = false;
					// TODO: error
				}
			}

			action.setEnabled(true);
			action.setChecked(checked);
		}
		else
		{
			action.setEnabled(false);
			action.setChecked(false);
		}
	}

}
