/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.dr.ui;

import edu.uci.isr.xarch.IXArchImplementation;
import edu.uci.isr.xarch.XArchUtils;
import gnu.prolog.database.Predicate;
import gnu.prolog.term.CompoundTermTag;
import groove.graph.Graph;
import groove.prolog.GroovePrologLoadingException;
import groove.prolog.PrologQuery;
import groove.prolog.engine.GrooveState;

import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import trese.archmodel.groove.XADL2Graph;

/**
 * @author Michiel Hendriks
 * 
 */
public class DesignRationaleWizard extends Wizard implements IWorkbenchWizard
{
	protected OutputStreamMux outputMux;

	protected PrologQuery prologQuery;

	protected DRWExecuteKBQuery queryPage;

	protected DRWReason reasonPage;

	protected GrooveState grooveState;

	protected IFile selectedFile;

	public DesignRationaleWizard()
	{
		super();
		setWindowTitle("Design Rationale");
		setNeedsProgressMonitor(true);
		setHelpAvailable(false);
		outputMux = new OutputStreamMux();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages()
	{
		super.addPages();
		addPage(new DRWSelectKnowledgeBase(this));
		queryPage = new DRWExecuteKBQuery(this);
		addPage(queryPage);
		reasonPage = new DRWReason(this);
		addPage(reasonPage);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.
	 * IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page)
	{
		if (page == queryPage)
		{
			return null;
		}
		return super.getNextPage(page);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		if (getContainer().getCurrentPage() != reasonPage)
		{
			getContainer().showPage(reasonPage);
			reasonPage.executeQuery(queryPage.getQuery(), prologQuery, outputMux);
			return false;
		}
		return getContainer().getCurrentPage() == reasonPage;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		if (selection.getFirstElement() instanceof IFile)
		{
			selectedFile = (IFile) selection.getFirstElement();
		}
	}

	/**
	 * @return the outputMux
	 */
	public OutputStreamMux getOutputMux()
	{
		return outputMux;
	}

	/**
	 * @param pq
	 */
	public void setPrologQuery(PrologQuery pq)
	{
		prologQuery = pq;
		CompoundTermTag ctt = CompoundTermTag.get("designrationale_query", 1);
		Predicate pred;
		try
		{
			pred = prologQuery.getEnvironment().getModule().getDefinedPredicate(ctt);
			if (pred == null)
			{
				pred = prologQuery.getEnvironment().getModule().createDefinedPredicate(ctt);
				pred.setType(Predicate.BUILD_IN);
				pred.setJavaClassName(Predicate_designrationale_query.class.getName());
			}
		}
		catch (GroovePrologLoadingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the prologQuery
	 */
	public PrologQuery getPrologQuery()
	{
		return prologQuery;
	}

	/**
	 * @return the grooveState
	 * @throws Exception
	 */
	public GrooveState getGrooveState() throws Exception
	{
		if (grooveState == null && selectedFile != null)
		{
			IXArchImplementation impl = XArchUtils.getDefaultXArchImplementation();
			Graph graph = XADL2Graph.convert(impl.parse(new InputStreamReader(selectedFile.getContents())));
			grooveState = new GrooveState(graph);
		}
		return grooveState;
	}
}
