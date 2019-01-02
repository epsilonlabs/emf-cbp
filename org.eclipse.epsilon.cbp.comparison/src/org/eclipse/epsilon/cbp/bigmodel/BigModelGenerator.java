package org.eclipse.epsilon.cbp.bigmodel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.gmt.modisco.java.emf.JavaPackage;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverJavaModelFromJavaProject;

public class BigModelGenerator {

    String sourcePath;
    String targetPath;
    private Map<Object, Object> saveOptions;

    public BigModelGenerator(String sourcePath, String targetPath) {
	this.sourcePath = sourcePath;
	this.targetPath = targetPath;

	JavaPackage.eINSTANCE.eClass();
	saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
	saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
    }

    private void listFiles(String path, List<File> files) {
	File dir = new File(path);
	for (File file : dir.listFiles()) {
	    if (file.isFile() && file.getName().contains(".project")) {
		files.add(file);
	    } else if (file.isDirectory()) {
		listFiles(file.getAbsolutePath(), files);
	    }
	}
    }

    private List<File> getAllProjectFiles(String sourcePath) {
	List<File> files = new ArrayList<>();
	listFiles(sourcePath, files);
	return files;
    }

    public void generate() throws DiscoveryException, CoreException, IOException {
	System.out.println("Generate Big Models of projects in " + sourcePath);
	File targetFile = new File(targetPath + File.separator + "target.xmi");
	Resource resourceAccumulator = ((new XMIResourceFactoryImpl()).createResource(URI.createFileURI(targetFile.getAbsolutePath())));

	System.out.println("Get all projects' paths ...");
	List<File> projectFiles = getAllProjectFiles(sourcePath);

	System.out.println("Found " + projectFiles.size() + " project files");
	int i = 0;
	for (File projectFile : projectFiles) {
	    i++;
//	    if ( i== 16) {
//		System.out.println();
//	    }
	    System.out.println(i + " of " + projectFiles.size() + ": Processing project " + projectFile.getAbsolutePath());

	    // initialise project description, create one if none existed
	    String projectFileName = projectFile.getAbsolutePath();
	    Path path = new Path(projectFileName);
	    IProjectDescription description;
	    if (path.toFile().exists()) {
		try {
		    description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
		    description.setName(projectFile.getParentFile().getName());
		} catch (Exception e) {
		    description = ResourcesPlugin.getWorkspace().newProjectDescription(projectFile.getName());
		    path = new Path(projectFile.getParentFile().getAbsolutePath());
		    description.setLocation(path);
		    String[] natureIds = new String[] { JavaCore.NATURE_ID };
		    description.setNatureIds(natureIds);
		    description.setName(projectFile.getParentFile().getName());
		}
	    } else {
		description = ResourcesPlugin.getWorkspace().newProjectDescription(projectFile.getName());
		path = new Path(projectFile.getParentFile().getAbsolutePath());
		description.setLocation(path);
		String[] natureIds = new String[] { JavaCore.NATURE_ID };
		description.setNatureIds(natureIds);
		description.setName(projectFile.getParentFile().getName());
	    }

	    // create eclipse project
	    IProject project;
	    try {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
	    } catch (Exception exe) {
		continue;
	    }
	    if (!project.exists()) {
		try {
		    project.create(description, null);
		} catch (Exception exe) {
		    exe.printStackTrace();
		}
		project.open(null);
	    } else {
		try {
		    project.close(null);
		    project.delete(false, null);
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		project.create(description, null);
		project.open(null);
	    }

	    // create java project
	    IJavaProject javaProject = JavaCore.create(project);
	    try {
		javaProject.open(null);
		System.out.println("This is a Java project ...");

		DiscoverJavaModelFromJavaProject javaDiscoverer = new DiscoverJavaModelFromJavaProject();
		javaDiscoverer.setDeepAnalysis(true);
		try {
		    javaDiscoverer.discoverElement(javaProject, new NullProgressMonitor());
		    Resource javaResource = javaDiscoverer.getTargetModel();
		    resourceAccumulator.getContents().addAll(javaResource.getContents());
		    System.out.println("SUCCESS!!!");
		    javaResource.getContents().clear();
		    javaResource.unload();
		} catch (Exception exe) {
		    System.out.println("FAIL!!!");
		    exe.printStackTrace();
		}
	    } catch (JavaModelException e1) {
		// e1.printStackTrace();
	    }

	    // close and delete the project
	    try {
		System.out.println("Closing the project ...");
		project.close(null);
		project.delete(false, null);
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	
	System.out.println("Assigning IDs ...");
	TreeIterator<EObject> iterator = resourceAccumulator.getAllContents();
	String prefix = "O-";
	int counter = 0;
	while(iterator.hasNext()) {
	    ((XMIResource) resourceAccumulator).setID(iterator.next(), prefix + counter++);
	}
	
	System.out.println("Saving the Big Model ...");
	resourceAccumulator.save(saveOptions);
	System.out.println("FINISHED!!!");

    }

}
