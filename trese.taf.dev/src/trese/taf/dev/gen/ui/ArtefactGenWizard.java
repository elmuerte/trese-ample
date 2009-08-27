/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.dev.gen.ui;

import net.ample.tracing.core.RepositoryManager;

import org.eclipse.jface.wizard.Wizard;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class ArtefactGenWizard extends Wizard
{
	protected RepositoryManager repository;

	protected ArtefactGeneratorConfig config;

	public ArtefactGenWizard(RepositoryManager repo)
	{
		super();
		repository = repo;
		config = new ArtefactGeneratorConfig();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages()
	{
		setWindowTitle("Generate Random Artefacts");
		addPage(new FilterArtefactTypeWizardPage());
		addPage(new NumberOfArtefactsWizardPage());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		return true;
	}

	/**
	 * @return
	 */
	public RepositoryManager getRepository()
	{
		return repository;
	}

	/**
	 * @return
	 */
	public ArtefactGeneratorConfig getGeneratorConfig()
	{
		return config;
	}
}
