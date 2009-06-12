/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package groove.gui.eclipse;

import groove.graph.Graph;
import groove.graph.GraphProperties;
import groove.io.DefaultGxl;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class GprDecorator extends LabelProvider implements ILightweightLabelDecorator
{
	public GprDecorator()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang
	 * .Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration)
	{
		if (element instanceof IFile == false)
		{
			return;
		}
		IFile file = (IFile) element;

		DefaultGxl gxl = new DefaultGxl();
		try
		{
			File fl = new File(file.getRawLocationURI());
			Graph graph = gxl.unmarshalGraph(fl);
			final boolean isenabled = GraphProperties.isEnabled(graph);
			final int prio = GraphProperties.getPriority(graph);
			if (prio > 0)
			{
				decoration.addSuffix("  ^" + prio + "");
			}
			if (!isenabled)
			{
				decoration.addSuffix(" [disabled]");
			}
		}
		catch (IOException e)
		{}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.BaseLabelProvider#addListener(org.eclipse.jface
	 * .viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	public void dispose()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.BaseLabelProvider#isLabelProperty(java.lang
	 * .Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.BaseLabelProvider#removeListener(org.eclipse
	 * .jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{}

}
