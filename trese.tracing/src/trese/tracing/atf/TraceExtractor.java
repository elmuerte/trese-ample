/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing.atf;

import java.io.InputStreamReader;
import java.io.Reader;

import net.ample.tracing.core.AbstractTraceExtractor;
import net.ample.tracing.core.RepositoryManager;
import net.ample.tracing.core.TraceLinkType;
import net.ample.tracing.core.TraceableArtefactType;
import net.ample.tracing.core.query.Constraints;
import net.ample.tracing.core.query.Query;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import edu.uci.isr.xarch.IXArch;
import edu.uci.isr.xarch.IXArchImplementation;
import edu.uci.isr.xarch.XArchParseException;
import edu.uci.isr.xarch.XArchUtils;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public abstract class TraceExtractor extends AbstractTraceExtractor
{
	protected RepositoryManager manager;
	
	public void run(RepositoryManager repoMan, IProgressMonitor monitor)
	{
		manager = repoMan;
		IFile xadlFile = selectXADLFile();
		if (xadlFile != null)
		{
			try
			{
				IXArch arch = getXArch(new InputStreamReader(xadlFile.getContents(true)));
				extractArtifacts(arch);
			}
			catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * this method returns a type of traceable artifact existing in a
	 * repository.
	 * 
	 * @param name
	 *            the name of the type of traceable artifact.
	 * @return the traceable artifact type.
	 */
	protected TraceableArtefactType getTraceableArtefactType(String name)
	{
		Query<TraceableArtefactType> query = manager.getQueryManager().queryOnArtefactTypes();
		query.add(Constraints.hasName(name));
		return query.executeUnique();
	}

	/**
	 * this method returns a type of trace link existing in a repository.
	 * 
	 * @param name
	 *            the name of the type of trace link.
	 * @return the trace link type.
	 */
	protected TraceLinkType getTraceLinkType(String name)
	{
		Query<TraceLinkType> query = manager.getQueryManager().queryOnLinkTypes();
		query.add(Constraints.hasName(name));
		return query.executeUnique();
	}

	protected void extractArtifacts(IXArch arch)
	{

	}

	/**
	 * Load the architecture
	 * 
	 * @param source
	 * @return
	 */
	protected IXArch getXArch(Reader source)
	{
		IXArchImplementation impl = XArchUtils.getDefaultXArchImplementation();
		try
		{
			return impl.parse(source);
		}
		catch (XArchParseException e)
		{
			return null;
		}
	}

	protected abstract IFile selectXADLFile();
}
