package org.eclipse.epsilon.cbp.state2change.handlers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverJavaModelFromJavaProject;
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

		IJavaProject javaProject;
		Resource javaResource;
		DiscoverJavaModelFromJavaProject javaDiscoverer;
		StringOutputStream output;
		IProjectDescription description;
		try {
			File[] fileList = gitCommitsDirectory.listFiles();
			for (File file : fileList) {
				String projectFile = file.getAbsolutePath() + File.separator + ".project";
				Path path = new Path(projectFile);
				if (path.toFile().exists()) {
					System.out.println("Exporting the XMI of " + file.getName());
					description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
					description.setName(file.getName());
					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
					if (!project.exists()) {
						project.create(description, new NullProgressMonitor());
						project.open(new NullProgressMonitor());
					} else {
						project.open(new NullProgressMonitor());
					}
					
					javaProject = JavaCore.create(project);
					try {
						javaProject.open(null);
					} catch (JavaModelException e1) {
						e1.printStackTrace();
					}

					javaDiscoverer = new DiscoverJavaModelFromJavaProject();
					try {
						javaDiscoverer.discoverElement(javaProject, new NullProgressMonitor());
					} catch (DiscoveryException e) {
						e.printStackTrace();
					}

					javaResource = javaDiscoverer.getTargetModel();
					output = new StringOutputStream();
					try {
						javaResource.save(output, null);
						String filePath = targetXmiDirectory.getAbsolutePath() + File.separator + project.getName() + ".xmi";
						File fileXmi = new File(filePath);
						FileUtils.writeStringToFile(fileXmi, output.toString(), Charset.defaultCharset(), false);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					project.close(new NullProgressMonitor());
					project.delete(false, new NullProgressMonitor());
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "State2change", "Generating XMIs finished!");
		return null;
	}
}
