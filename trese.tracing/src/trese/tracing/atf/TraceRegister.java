/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing.atf;

import java.io.IOException;

import net.ample.tracing.frontend.core.exceptions.MissingRepositoryException;
import net.ample.tracing.frontend.core.traceregister.AbstractTraceRegister;
import net.ample.tracing.frontend.core.traceregister.ITraceRegister;

import org.eclipse.core.runtime.CoreException;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class TraceRegister extends AbstractTraceRegister implements ITraceRegister
{

	/* (non-Javadoc)
	 * @see net.ample.tracing.frontend.core.traceregister.AbstractTraceRegister#executeRegister()
	 */
	@Override
	public void executeRegister() throws MissingRepositoryException, CoreException, IOException
	{
		
	}
}
