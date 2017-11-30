package org.eclipse.epsilon.cbp.state2change;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverJavaModelFromJavaProject;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

import eu.artist.migration.mdt.javaee.java.umlclass.Java2UMLDiscoverer;

public class UmlXmiGenerator implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {

		File gitCommitsDirectory = new File("D:/TEMP/test/".replace("/", File.separator));
		File targetXmiDirectory = new File("D:/TEMP/test/".replace("/", File.separator));

		this.generateXmi(gitCommitsDirectory, targetXmiDirectory);

		System.out.println("Finished!");
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// nothing to do
	}

	public void generateXmi(File javaProjectsDirectory, File targetXmiDirectory) throws DiscoveryException {
		IJavaProject javaProject;
		Resource javaResource;
		DiscoverJavaModelFromJavaProject javaDiscoverer;
		Java2UMLDiscoverer umlDiscoverer;
		ByteArrayOutputStream output;
		IProjectDescription description;
		IFile javaFile = null;
		String projectName = "Model";
		FileFilter filter;
		File[] fileList;
		String projectFile;
		IProject project;
		InputStream inputStream;
		Resource umlResource;
		String filePath;
		String[] natureIds;
		try {
			//initialise
			javaDiscoverer = new DiscoverJavaModelFromJavaProject();
			umlDiscoverer = new Java2UMLDiscoverer();
			output = new ByteArrayOutputStream();
			natureIds = new String[] { JavaCore.NATURE_ID };
			
			EClassifier modelClassifier = UMLPackage.eINSTANCE.getEClassifier("Model");
			EClass modelClass = (EClass) modelClassifier;
			EStructuralFeature feature = modelClass.getEStructuralFeature("name");

			EClassifier rtsClassifier = UMLPackage.eINSTANCE.getEClassifier("RedefinableTemplateSignature");
			EClass rtsClass = (EClass) rtsClassifier;

			EClassifier operationClassifier = UMLPackage.eINSTANCE.getEClassifier("Operation");
			EClass operationClass = (EClass) operationClassifier;

			EClassifier iterfaceRealizationClassifier = UMLPackage.eINSTANCE.getEClassifier("InterfaceRealization");
			EClass iterfaceRealizationClass = (EClass) iterfaceRealizationClassifier;
			
			EClassifier commentClassifier = UMLPackage.eINSTANCE.getEClassifier("Comment");
			EClass commentClass = (EClass) commentClassifier;
			
			EClassifier dependencyClassifier = UMLPackage.eINSTANCE.getEClassifier("Dependency");
			EClass dependencyClass = (EClass) dependencyClassifier;
			
			List<EObject> toBeDeletedObjects = new ArrayList<EObject>();
			
			//----
			filter = FileFilterUtils.directoryFileFilter();
			fileList = javaProjectsDirectory.listFiles(filter);
			System.out.println("Processing " + fileList.length + " files(s)");
			for (File file : fileList) {
				System.gc();
				System.out.println("Exporting the XMI of " + file.getName());

				// initialise project description, create one if none existed
				projectFile = file.getAbsolutePath() + File.separator + ".project";
				Path path = new Path(projectFile);
				if (path.toFile().exists()) {
					description = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
					description.setName(projectName);
				} else {
					description = ResourcesPlugin.getWorkspace().newProjectDescription(file.getName());
					path = new Path(file.getAbsolutePath());
					description.setLocation(path);
					description.setNatureIds(natureIds);
					description.setName(projectName);
				}

				// create eclipse project
				 project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
				if (!project.exists()) {
					project.create(description, null);
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
				javaProject = JavaCore.create(project);
				try {
					javaProject.open(null);
				} catch (JavaModelException e1) {
					e1.printStackTrace();
				}

				// discover java elements
				//System.out.println("Creating Java model ...");
				try {
					javaDiscoverer.setDeepAnalysis(false);
					javaDiscoverer.discoverElement(javaProject,  new NullProgressMonitor());
				} catch (DiscoveryException e) {
					e.printStackTrace();
				}
				
				// get and save the model in xmi
				javaResource = javaDiscoverer.getTargetModel();
				output.reset();
				try {
					javaResource.save(output, null);
					javaResource.getContents().clear();
					javaFile = project.getFile(project.getName() + "-java.xmi");
					if (javaFile.exists()) {
						javaFile.delete(true, null);
					}
					inputStream = new ByteArrayInputStream(output.toByteArray());
					javaFile.create(inputStream, true, new NullProgressMonitor());
					inputStream.reset();
					inputStream.close();
					
					output.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// discovery uml elements -- direct way
				//System.out.println("Creating UML model ...");
				umlDiscoverer.discoverElement(javaFile, new NullProgressMonitor());
				umlResource = umlDiscoverer.getTargetModel();
				output.reset();

				// nullify all ids and remove unnecessary elements
				XMIResource xmiResource = (XMIResource) umlResource;
				TreeIterator<EObject> elements = umlResource.getAllContents();

				toBeDeletedObjects.clear();
				EObject element = null;
				while (elements.hasNext()) {
					element = elements.next();
					xmiResource.setID(element, null);

					if (element.eClass().equals(modelClass)) {
						String name = (String) element.eGet(feature);
						if (name.equals("sourcesReferences")) {
							toBeDeletedObjects.add(element);
						}
					} 
					else if (element.eClass().equals(rtsClass)) {
						toBeDeletedObjects.add(element);
					} 
//					else if (element.eClass().equals(operationClass)) {
//						toBeDeletedObjects.add(element);
//					} 
					else if (element.eClass().equals(iterfaceRealizationClass)) {
						toBeDeletedObjects.add(element);
					}
					else if (element.eClass().equals(commentClass)) {
						toBeDeletedObjects.add(element);
					}
					else if (element.eClass().equals(dependencyClass)) {
						toBeDeletedObjects.add(element);
					}
				}
//				System.out.println("Deleting " + toBeDeletedObjects.size() + " element(s)");
//				int x = 0;
				for (EObject eObject : toBeDeletedObjects) {
//					System.out.println(x + ". Deleting " + eObject.toString());
					EcoreUtil.remove(eObject);
					//EcoreUtil.delete(eObject, false);
//					x += 1;
				}
				toBeDeletedObjects.clear();

				
				try {
					//save uml model
					umlResource.save(output, null);
					umlResource.unload();
					filePath = targetXmiDirectory.getAbsolutePath() + File.separator + file.getName() + ".xmi";
					File fileXmi = new File(filePath);
					FileUtils.writeStringToFile(fileXmi, output.toString(), Charset.defaultCharset(), false);
					output.reset();
					//output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					project.close(null);
					project.delete(false, null);
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
