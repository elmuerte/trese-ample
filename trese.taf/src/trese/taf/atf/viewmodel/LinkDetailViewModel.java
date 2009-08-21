/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf.viewmodel;

import net.ample.tracing.core.TraceLink;
import net.ample.tracing.ui.models.LinkViewModel;
import net.ample.tracing.ui.models.ViewModel;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class LinkDetailViewModel extends ViewModel<TraceLink>
{
	public enum LinkDetail
	{
		TARGETS, SOURCES,
	}

	protected LinkDetail detail;

	/**
	 * @param parent
	 * @param element
	 */
	public LinkDetailViewModel(LinkViewModel parent, TraceLink element, LinkDetail detailmode)
	{
		super(parent, element);
		detail = detailmode;
	}

	/**
	 * @return the detail
	 */
	public LinkDetail getDetail()
	{
		return detail;
	}
}
