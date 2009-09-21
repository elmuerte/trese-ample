/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class Activator extends AbstractUIPlugin
{
	// The plug-in ID
	public static final String PLUGIN_ID = "trese.taf";

	private static Activator plugin;

	/**
	 * @return
	 */
	public static Activator getDefault()
	{
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * @param path
	 * @return
	 */
	public ImageDescriptor getImageDescriptor(String path)
	{
		return imageDescriptorFromPlugin("trese.taf", path);
	}

	/**
	 * @param key
	 * @return
	 */
	public Image getImage(String key)
	{
		ImageRegistry registry = getImageRegistry();
		Image image = registry.get(key);
		if (image == null)
		{
			ImageDescriptor desc = getImageDescriptor(key);
			registry.put(key, desc);
			image = registry.get(key);
		}
		return image;
	}

}
