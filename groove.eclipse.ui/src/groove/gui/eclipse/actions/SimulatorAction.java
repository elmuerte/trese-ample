package groove.gui.eclipse.actions;

import groove.Simulator;
import groove.util.Groove;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class SimulatorAction implements IWorkbenchWindowActionDelegate {

	protected String selectedPath;

	/**
	 * The constructor.
	 */
	public SimulatorAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		String[] args;
		if (selectedPath != null && false) {
			/*
			 * see bug
			 * https://sourceforge.net/tracker/?func=detail&aid=2737601&group_id
			 * =119225&atid=683352
			 */
			args = new String[1];
			args[0] = selectedPath;
		} else {
			args = new String[0];
		}
		Simulator.main(args);
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		selectedPath = null;
		action.setToolTipText("Open the Groove Simulator");
		if (selection instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection) selection).getFirstElement();
			if (o instanceof IFolder) {
				IPath loc = ((IFolder) o).getLocation();
				if (loc != null) {
					if (Groove.RULE_SYSTEM_EXTENSION.equals("."
							+ loc.getFileExtension())) {
						selectedPath = loc.toOSString();
						action.setToolTipText(String
								.format("Open %s in the Groove Simulator",
										selectedPath));
					}
				}
			}
		}
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
	}
}