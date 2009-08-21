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
 * 
 * 
 * @author Michiel Hendriks
 */
public class ArtefactDetailViewModel extends ViewModel<TraceableArtefact>
{
	public enum ArtefactDetail
	{
		INCOMING_LINKS, OUTGOING_LINKS, ANCESTORS, DESCENDANTS
	}

	protected ArtefactDetail detail;

	/**
	 * @param parent
	 * @param element
	 */
	public ArtefactDetailViewModel(ArtefactViewModel parent, TraceableArtefact element, ArtefactDetail detailmode)
	{
		super(parent, element);
		detail = detailmode;
	}

	/**
	 * @return the detail
	 */
	public ArtefactDetail getDetail()
	{
		return detail;
	}
}
