/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.archmodel.ui.popup.actions;

import java.util.Collections;
import java.util.Set;

import edu.uci.isr.xarch.IXArch;

/**
 * @author Michiel Hendriks
 * 
 */
public class ExportGST extends ExportGSTRestrict
{
	/**
	 * Constructor for Action1.
	 */
	public ExportGST()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * trese.archmodel.ui.popup.actions.ExportGSTRestrict#selectStructures
	 * (edu.uci.isr.xarch.IXArch, java.lang.String)
	 */
	@Override
	protected Set<String> selectStructures(IXArch arch, String archFile)
	{
		return Collections.emptySet();
	}
}
