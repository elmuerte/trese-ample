package groove.gui.eclipse.actions;

import groove.Editor;
import groove.util.Groove;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorLauncher;
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
public class EditorAction implements IWorkbenchWindowActionDelegate,
		IEditorLauncher {

	protected String selectedFile;

	/**
	 * The constructor.
	 */
	public EditorAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		String[] args;
		if (selectedFile != null) {
			args = new String[1];
			args[0] = selectedFile;
		} else {
			args = new String[0];
		}
		Editor.main(args);
		// TODO: check for changes in the files
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		selectedFile = null;
		action.setToolTipText("Open the Groove Editor");
		if (selection instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection) selection).getFirstElement();
			if (o instanceof IFile) {
				IPath loc = ((IFile) o).getLocation();
				if (loc != null) {
					if (Groove.RULE_EXTENSION.equals("."
							+ loc.getFileExtension())
							|| Groove.STATE_EXTENSION.equals("."
									+ loc.getFileExtension())) {
						selectedFile = loc.toOSString();
						action.setToolTipText(String.format(
								"Open %s in the Groove Editor", selectedFile));
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

	public void open(IPath file) {
		selectedFile = file.toOSString();
		run(null);
	}
}