/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf;

import net.ample.tracing.ui.views.RepositoryLabelProvider;

import org.eclipse.swt.graphics.Image;

import trese.taf.Activator;
import trese.taf.atf.viewmodel.ArtefactDetailViewModel;
import trese.taf.atf.viewmodel.LinkDetailViewModel;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class RepositoryLabelProviderEx extends RepositoryLabelProvider
{
	public RepositoryLabelProviderEx()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.ample.tracing.ui.views.RepositoryLabelProvider#getText(java.lang.
	 * Object)
	 */
	@Override
	public String getText(Object obj)
	{
		if (obj instanceof ArtefactDetailViewModel)
		{
			switch (((ArtefactDetailViewModel) obj).getDetail())
			{
				case ANCESTORS:
					return "Ancestors";
				case DESCENDANTS:
					return "Descendants";
				case INCOMING_LINKS:
					return "Incoming Links";
				case OUTGOING_LINKS:
					return "Outgoing Links";
				default:
					return "???";
			}
		}
		else if (obj instanceof LinkDetailViewModel)
		{
			switch (((LinkDetailViewModel) obj).getDetail())
			{
				case SOURCES:
					return "Sources";
				case TARGETS:
					return "Targets";
				default:
					return "???";
			}
		}
		return super.getText(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.ample.tracing.ui.views.RepositoryLabelProvider#getImage(java.lang
	 * .Object)
	 */
	@Override
	public Image getImage(Object obj)
	{
		if (obj instanceof ArtefactDetailViewModel)
		{
			switch (((ArtefactDetailViewModel) obj).getDetail())
			{
				case ANCESTORS:
					return Activator.getDefault().getImage("icons/obj/artefact_ancestors.png");
				case DESCENDANTS:
					return Activator.getDefault().getImage("icons/obj/artefact_descendants.png");
				case INCOMING_LINKS:
					return Activator.getDefault().getImage("icons/obj/artefact_incoming_links.png");
				case OUTGOING_LINKS:
					return Activator.getDefault().getImage("icons/obj/artefact_outgoing_links.png");
				default:
					return null;
			}
		}
		else if (obj instanceof LinkDetailViewModel)
		{
			switch (((LinkDetailViewModel) obj).getDetail())
			{
				case SOURCES:
					return Activator.getDefault().getImage("icons/obj/links_sources.png");
				case TARGETS:
					return Activator.getDefault().getImage("icons/obj/links_targets.png");
				default:
					return null;
			}
		}
		return super.getImage(obj);
	}
}
