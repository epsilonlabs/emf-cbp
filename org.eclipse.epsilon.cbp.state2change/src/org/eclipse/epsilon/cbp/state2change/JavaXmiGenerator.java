package org.eclipse.epsilon.cbp.state2change;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverJavaModelFromJavaProject;

public class JavaXmiGenerator implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {

		File gitCommitsDirectory = new File("D:/TEMP/target/".replace("/", File.separator));
		File targetXmiDirectory = new File("D:/TEMP/target-xmi/".replace("/", File.separator));

		this.generateXmi(gitCommitsDirectory, targetXmiDirectory);

		System.out.println("Finished!");
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// nothing to do
	}

	public void generateXmi(File javaProjectsDirectory, File targetXmiDirectory) {
		IJavaProject javaProject;
		Resource javaResource;
		DiscoverJavaModelFromJavaProject javaDiscoverer;
		StringOutputStream output;
		IProjectDescription description;
		try {
			File[] fileList = javaProjectsDirectory.listFiles();
			for (File file : fileList) {
				System.out.println("Exporting the XMI of " + file.getName());

				// initialise project description, create one if none existed
				String projectFile = file.getAbsolutePath() + File.separator + ".project";
				Path path = new Path(projectFile);
				if (path.toFile().exists()) {
					description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
					description.setName(file.getName().split("-")[0]);
				} else {
					description = ResourcesPlugin.getWorkspace().newProjectDescription(file.getName());
					path = new Path(file.getAbsolutePath());
					description.setLocation(path);
					description.setNatureIds(new String[] { JavaCore.NATURE_ID });
					description.setName(file.getName().split("-")[0]);
				}

				// create eclipse project
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
				if (!project.exists()) {
					project.create(description, new NullProgressMonitor());
					project.open(new NullProgressMonitor());
				} else {
					try {
						project.close(new NullProgressMonitor());
						project.delete(false, new NullProgressMonitor());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					project.create(description, new NullProgressMonitor());
					project.open(new NullProgressMonitor());
				}

				// create java project
				javaProject = JavaCore.create(project);
				try {
					javaProject.open(null);
				} catch (JavaModelException e1) {
					e1.printStackTrace();
				}

				// discover java elements
				javaDiscoverer = new DiscoverJavaModelFromJavaProject();
				try {
					javaDiscoverer.discoverElement(javaProject, new NullProgressMonitor());
				} catch (DiscoveryException e) {
					e.printStackTrace();
				}

				// get and save the model in xmi
				javaResource = javaDiscoverer.getTargetModel();
				output = new StringOutputStream();
				try {
					javaResource.save(output, null);
					String filePath = targetXmiDirectory.getAbsolutePath() + File.separator + file.getName() + ".xmi";
					File fileXmi = new File(filePath);
					FileUtils.writeStringToFile(fileXmi, output.toString(), Charset.defaultCharset(), false);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					project.close(new NullProgressMonitor());
					project.delete(false, new NullProgressMonitor());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
			System.out.println("Finished!");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
