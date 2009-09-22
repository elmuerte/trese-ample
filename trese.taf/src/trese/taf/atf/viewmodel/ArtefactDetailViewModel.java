/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf.viewmodel;

import net.ample.tracing.core.TraceableArtefact;
import net.ample.tracing.ui.models.ArtefactViewModel;
import net.ample.tracing.ui.models.ViewModel;

/**
 * A ViewModel node to group certain child nodes based on their relation to a
 * TraceableArtefactViewModel node. This is purely a visual addition, it doesn't
 * relate to any specific element in the model.
 * 
 * @author Michiel Hendriks
 */
public class ArtefactDetailViewModel extends ViewModel<TraceableArtefact>
{
	/**
	 * Defines the type of ArtefactDetailViewModel node
	 * 
	 * @author Michiel Hendriks
	 */
	public enum ArtefactDetail
	{
		/**
		 * Base ViewModel node for all incoming links
		 */
		INCOMING_LINKS,
		/**
		 * Base ViewModel node for all outgoing links
		 */
		OUTGOING_LINKS,
		/**
		 * Base ViewModel node for ancestors
		 */
		ANCESTORS,
		/**
		 * Base ViewModel node for artefact descendants
		 */
		DESCENDANTS
	}

	protected ArtefactDetail detail;

	/**
	 * @param parent
	 * @param element
	 */
	public ArtefactDetailViewModel(ArtefactViewModel parent, TraceableArtefact element, ArtefactDetail detailmode)
	{
		super(parent, element);
		if (detailmode == null)
		{
			throw new NullPointerException("ArtefactDetail cannot be null");
		}
		detail = detailmode;
	}

	/**
	 * @return the detail
	 */
	public ArtefactDetail getDetail()
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
		ArtefactDetailViewModel other = (ArtefactDetailViewModel) obj;
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
