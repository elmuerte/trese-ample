/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2008 University of Twente.
 */
package trese.tracing.atf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Display;

import trese.tracing.ui.dialog.WorkbenchFileSelectionDialog;
import trese.tracing.ui.popup.ExportGFTxADL;

/**
 * 
 * @author Michiel Hendriks
 */
public class XADLExtractor extends AbstractXADLExtractor
{
	/*
	 * (non-Javadoc)
	 * @see trese.tracing.atf.AbstractXADLExtractor#selectXADLFile()
	 */
	@Override
	protected IFile selectXADLFile()
	{
		final List<Object> result = new ArrayList<Object>();
		Display.getDefault().syncExec(new Runnable() {
			public void run()
			{
				WorkbenchFileSelectionDialog diag = new WorkbenchFileSelectionDialog(null, Collections
						.singleton(ExportGFTxADL.XADL_CONTENT_TYPE));
				diag.setInput(ResourcesPlugin.getWorkspace().getRoot());
				diag.setTitle("Select an xADL document");
				diag.setMessage("Select the xADL document you want to extract artifacts from.");
				diag.setAllowMultiple(false);
				diag.open();
				Object[] res = diag.getResult();
				if (res != null && res.length > 0)
				{
					result.add(res[0]);
				}
			}
		});
		if (result.size() == 0)
		{
			return null;
		}
		else
		{
			return (IFile) result.get(0);
		}
	}

}
