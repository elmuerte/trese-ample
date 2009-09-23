/**
 * 
 */
package groove.gui.eclipse;

import groove.util.Groove;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Provides an icon to the directory which is a groove production system.
 * 
 * @author Michiel Hendriks
 * 
 */
public class GpsDecorator implements ILightweightLabelDecorator
{
	private static final ImageDescriptor GPS_ICON;

	static
	{
		GPS_ICON = AbstractUIPlugin.imageDescriptorFromPlugin("groove.eclipse.ui", "icons/gps.gif");
	}

	public GpsDecorator()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang
	 * .Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration)
	{
		if (element instanceof IFolder == false)
		{
			return;
		}
		IFolder folder = (IFolder) element;
		if (Groove.RULE_SYSTEM_EXTENSION.equals("." + folder.getFileExtension()))
		{
			// // also add the image descriptor as a session property so
			// // that it will be
			// // picked up by the workbench label provider upon the next
			// // update.
			// try {
			// folder.setSessionProperty(WorkbenchFile.IMAGE_CACHE_KEY,
			// GPS_ICON);
			// } catch (CoreException e) {
			// // ignore - not being able to cache the image is not
			// // fatal
			// }
			decoration.addOverlay(GPS_ICON, IDecoration.BOTTOM_LEFT);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.
	 * jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose()
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang
	 * .Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
	 * .jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{}

}
