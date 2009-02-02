/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.ui.dialogs;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import edu.uci.isr.xarch.IXArch;
import edu.uci.isr.xarch.instance.IDescription;
import edu.uci.isr.xarch.types.IArchStructure;

/**
 * Creates a dialog to select architecture structures from an xADL spec
 * 
 * @author Michiel Hendriks
 * 
 */
public class ArchitectureSelector
{
	protected Shell shell;
	protected IXArch archFile;
	protected String archFileName;
	protected Map<IArchStructure, String> archs;
	protected Object[] dialogResult;
	protected ILabelProvider lblProvider;

	public ArchitectureSelector(Shell shell, IXArch xADLfile, String filename)
	{
		this.shell = shell;
		archFile = xADLfile;
		archFileName = filename;

		lblProvider = new LabelProvider() {
			public String getText(Object arg0)
			{
				if (archs != null)
				{
					return archs.get(arg0);
				}
				return null;
			}
		};
	}

	protected void loadStructures()
	{
		archs = new HashMap<IArchStructure, String>();
		for (Object o : archFile.getAllObjects())
		{
			if (o instanceof IArchStructure)
			{
				String archName = ((IArchStructure) o).getId();
				IDescription desc = ((IArchStructure) o).getDescription();
				if (desc != null && !desc.getValue().isEmpty())
				{
					archName = desc.getValue();
				}
				archs.put((IArchStructure) o, archName);
			}
		}
	}

	/**
	 * Select one or more architectures to restrict to.
	 * 
	 * @return A set of architectures to restrict to, or null to cancel
	 */
	public Set<String> selectMultiple()
	{
		if (archs == null)
		{
			loadStructures();
		}
		if (archs.size() <= 1)
		{
			return Collections.emptySet();
		}

		dialogResult = null;
		Display.getDefault().syncExec(new Runnable() {
			public void run()
			{
				ListSelectionDialog dlg = new ListSelectionDialog(
						shell,
						archs.keySet(),
						new ArrayContentProvider(),
						lblProvider,
						String
								.format(
										"Select the ArchStructures from %s to restrict the output to. Press cancel to export all structures.",
										archFileName));
				dlg.setTitle("Select Architecture Structures");
				dlg.setBlockOnOpen(true);
				switch (dlg.open())
				{
					case Window.OK:
						dialogResult = dlg.getResult();
						break;
					case Window.CANCEL:
						dialogResult = new Object[0];
						break;
					default:
						break;
				}
			}
		});
		if (dialogResult != null)
		{
			Set<String> res = new HashSet<String>();
			for (Object o : dialogResult)
			{
				res.add(((IArchStructure) o).getId());
			}
			return res;
		}
		return null;
	}

	/**
	 * Select a single architecture
	 * 
	 * @return returns null to cancel
	 */
	public String selectSingle()
	{
		if (archs == null)
		{
			loadStructures();
		}
		if (archs.size() <= 1)
		{
			return "";
		}

		dialogResult = null;
		Display.getDefault().syncExec(new Runnable() {
			public void run()
			{
				ListDialog dlg = new ListDialog(shell);
				dlg.setTitle("Select Architecture Structure");
				dlg.setContentProvider(new ArrayContentProvider());
				dlg.setLabelProvider(lblProvider);
				dlg.setBlockOnOpen(true);
				dlg
						.setMessage(String
								.format(
										"Select the ArchStructure from %s to restrict the output to. Press cancel to export all structures.",
										archFileName));
				dlg.setInput(archs.keySet().toArray());
				switch (dlg.open())
				{
					case Window.OK:
						dialogResult = dlg.getResult();
						break;
					case Window.CANCEL:
						dialogResult = new Object[0];
						break;
					default:
						break;
				}
			}
		});

		if (dialogResult != null)
		{
			if (dialogResult.length >= 1)
			{
				return ((IArchStructure) dialogResult[0]).getId();
			}
			else
			{
				return "";
			}
		}

		return null;
	}
}
