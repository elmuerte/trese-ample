/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.taf.dev.gen;

import java.io.IOException;
import java.util.Iterator;

import net.ample.tracing.core.AbstractTraceExtractor;
import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.core.TraceableArtefactType;
import net.ample.tracing.core.query.Query;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import trese.taf.dev.gen.ui.ArtefactGenWizard;
import trese.taf.dev.gen.ui.ArtefactGeneratorConfig;

/**
 * @author Michiel Hendriks
 * 
 */
public class Artefacts extends AbstractTraceExtractor
{
	public void run(RepositoryManager repo, IProgressMonitor mon)
	{
		final RepositoryManager repository = repo;
		final IProgressMonitor monitor = mon;

		Display.getDefault().asyncExec(new Runnable() {
			public void run()
			{
				ArtefactGenWizard wizard = new ArtefactGenWizard(repository);
				WizardDialog diag = new WizardDialog(Display.getCurrent().getShells()[0], wizard);
				diag.setBlockOnOpen(true);
				if (diag.open() == Window.CANCEL)
				{
					return;
				}
				generateArtefacts(repository, monitor, wizard.getGeneratorConfig());
			}
		});
	}

	protected void generateArtefacts(RepositoryManager repository, IProgressMonitor monitor,
			ArtefactGeneratorConfig generatorConfig)
	{
		monitor = new SubProgressMonitor(monitor, 1);
		monitor.beginTask("Generating artefacts", generatorConfig.getCount() + 1);
		if (generatorConfig.getTypes().isEmpty())
		{
			Query<TraceableArtefactType> q = repository.getQueryManager().queryOnArtefactTypes();
			generatorConfig.setTypes(q.execute());
		}
		monitor.worked(1);
		Iterator<TraceableArtefactType> types = generatorConfig.getTypes().iterator();
		repository.getPersistenceManager().begin();
		for (int i = 0; i < generatorConfig.getCount(); i++)
		{
			if (monitor.isCanceled())
			{
				repository.getPersistenceManager().rollback();
				return;
			}

			if (!types.hasNext())
			{
				types = generatorConfig.getTypes().iterator();
			}
			TraceableArtefactType type = types.next();
			TraceableArtefact artefact = repository.getItemManager().createTraceableArtefact(type,
					String.format("%s %d [%8h]", type.getName(), i, System.nanoTime()));
			repository.getPersistenceManager().add(artefact);
			monitor.worked(1);
		}
		try
		{
			repository.getPersistenceManager().commit();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		monitor.done();
	}
}
