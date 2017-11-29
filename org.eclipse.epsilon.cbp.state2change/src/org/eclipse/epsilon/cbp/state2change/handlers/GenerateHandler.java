package org.eclipse.epsilon.cbp.state2change.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.epsilon.cbp.state2change.JavaXmiGenerator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class GenerateHandler extends AbstractHandler {

	private File gitCommitsDirectory = new File("D:/TEMP/target/".replace("/", File.separator));
	private File targetXmiDirectory = new File("D:/TEMP/target-xmi/".replace("/", File.separator));

	public GenerateHandler() {
		super();
		if (!gitCommitsDirectory.exists()) {
			targetXmiDirectory.mkdir();
		}
		if (!targetXmiDirectory.exists()) {
			targetXmiDirectory.mkdir();
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		JavaXmiGenerator generator = new JavaXmiGenerator();
		try {
			generator.generateXmi(gitCommitsDirectory, targetXmiDirectory);
		} catch (DiscoveryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "State2change", "Generating XMIs finished!");
		return null;
	}
}
