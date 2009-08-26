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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (detail == null ? 0 : detail.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		LinkDetailViewModel other = (LinkDetailViewModel) obj;
		if (detail == null)
		{
			if (other.detail != null)
			{
				return false;
			}
		}
		else if (!detail.equals(other.detail))
		{
			return false;
		}
		return true;
	}
}
