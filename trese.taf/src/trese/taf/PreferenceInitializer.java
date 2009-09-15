/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import trese.taf.atf.AtfImportExportView;
import trese.taf.atf.AtfRepositoryEntitiesView;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	public PreferenceInitializer()
	{}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences pref = new DefaultScope().getNode(Activator.PLUGIN_ID);
		pref.putBoolean(AtfRepositoryEntitiesView.PREF_ALPHASORT, true);
		pref.putBoolean(AtfImportExportView.PREF_ALPHASORT, true);
		pref.putBoolean(AtfImportExportView.PREF_COUNTITEMS, true);
		pref.putBoolean(AtfImportExportView.PREF_EXPORT_PROPS, true);
	}

}
