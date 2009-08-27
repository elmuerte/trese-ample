/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.dev.gen.ui;

import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceableArtefactType;
import net.ample.tracing.ui.models.ArtefactTypeContainerViewModel;
import net.ample.tracing.ui.models.ArtefactTypeViewModel;
import net.ample.tracing.ui.models.RepositoryViewModel;
import net.ample.tracing.ui.models.WorkspaceViewModel;
import net.ample.tracing.ui.views.RepositoryLabelProvider;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import trese.taf.atf.RepositoryManagerContentProvider;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class FilterArtefactTypeWizardPage extends WizardPage
{
	public FilterArtefactTypeWizardPage()
	{
		super("filterTypes");
		setTitle("Select Artefact Types");
		setDescription("Select the types for which artefacts should be created. When none are selected all types will be used.");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent)
	{
		CheckboxTreeViewer types = new CheckboxTreeViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		types.setContentProvider(new RepositoryManagerContentProvider());
		types.setLabelProvider(new RepositoryLabelProvider());
		types.setComparator(new ViewerSorter());
		final ArtefactGenWizard wiz = (ArtefactGenWizard) getWizard();
		RepositoryManager repo = wiz.getRepository();
		types.setInput(new ArtefactTypeContainerViewModel(new RepositoryViewModel(WorkspaceViewModel.getInstance(),
				repo), repo));
		types.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event)
			{
				Object elm = event.getElement();
				TraceableArtefactType type = null;
				if (elm instanceof ArtefactTypeViewModel)
				{
					type = ((ArtefactTypeViewModel) elm).getElement();
				}
				if (type == null)
				{
					return;
				}
				if (event.getChecked())
				{
					wiz.getGeneratorConfig().addType(type);
				}
				else
				{
					wiz.getGeneratorConfig().removeType(type);
				}
			}
		});
		setControl(types.getControl());
	}
}
