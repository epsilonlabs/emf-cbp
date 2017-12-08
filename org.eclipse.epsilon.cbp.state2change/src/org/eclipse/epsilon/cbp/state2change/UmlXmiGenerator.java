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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.gmt.modisco.omg.kdm.code.CodePackage;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.java.discoverer.DiscoverJavaModelFromJavaProject;
import org.eclipse.modisco.java.discoverer.DiscoverKDMModelFromJavaModel;
import org.eclipse.modisco.kdm.uml2converter.DiscoverUmlModelFromKdmModel;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.StructuralFeature;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.resource.UMLResource;

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
		Resource javaResource;
		Resource kdmResource;
		Resource umlResource;
		Resource cbpResource; 
		DiscoverJavaModelFromJavaProject javaDiscoverer;
		DiscoverUmlModelFromKdmModel umlDiscoverer;
		DiscoverKDMModelFromJavaModel kdmDiscoverer;
		IProjectDescription description;
		IJavaProject javaProject;
		IFile javaFile = null;
		IFile kdmFile = null;
		File[] fileList;
		FileFilter filter;
		IProject project;
		ByteArrayOutputStream output;
		InputStream inputStream;
		String[] natureIds;
		String projectFile;
		String filePath;
		String projectName = "Model";

		try {
			// initialise
			javaDiscoverer = new DiscoverJavaModelFromJavaProject();
			umlDiscoverer = new DiscoverUmlModelFromKdmModel();
			kdmDiscoverer = new DiscoverKDMModelFromJavaModel();
			
			output = new ByteArrayOutputStream();
			natureIds = new String[] { JavaCore.NATURE_ID };
			
			EClassifier classUnitClassifier = CodePackage.eINSTANCE.getEClassifier("ClassUnit");
			EClass classUnitClass = (EClass) classUnitClassifier;
			EStructuralFeature nameFeature = classUnitClass.getEStructuralFeature("name");

			List<EObject> toBeDeletedObjects = new ArrayList<EObject>();

			// ----
			filter = FileFilterUtils.directoryFileFilter();
			fileList = javaProjectsDirectory.listFiles(filter);
			System.out.println("Processing " + fileList.length + " files(s)");
			for (File file : fileList) {
				long before = System.currentTimeMillis();
				
				System.gc();
				System.out.print("Exporting the XMI of " + file.getName());

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
				// System.out.println("Creating Java model ...");

				try {
					javaDiscoverer.setDeepAnalysis(false);
					javaDiscoverer.discoverElement(javaProject, new NullProgressMonitor());
				} catch (DiscoveryException e) {
					e.printStackTrace();
				}

				// get and save the model in xmi
				javaResource = javaDiscoverer.getTargetModel();
				try {
					output.reset();
					javaResource.save(output, null);
					javaResource.getContents().clear();
					javaResource.unload();
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

				// discover kdm elements
				// System.out.println("Creating KDM model ...");

				kdmDiscoverer.discoverElement(javaFile, new NullProgressMonitor());
				kdmResource = kdmDiscoverer.getTargetModel();

				// Clean unnecessary elements. Class units with Anonymous type
				// have to be deleted to prevents bugs of MoDisco.
				// It still cannot handle the conversion of Anonymous type of
				// KDM model to UML model.
				TreeIterator<EObject> iterator = kdmResource.getAllContents();
				EObject element = null;
				while (iterator.hasNext()) {
					element = iterator.next();
					if (element.eClass().equals(classUnitClass)) {
						String name = (String) element.eGet(nameFeature);
						if (name.equals("Anonymous type")) {
							toBeDeletedObjects.add(element);
						}
					}
				}
				for (EObject eObject : toBeDeletedObjects) {
					EcoreUtil.delete(eObject, true);
				}
				toBeDeletedObjects.clear();

				try {
					output.reset();
					kdmResource.save(output, null);
					kdmResource.getContents().clear();
					kdmResource.unload();
					kdmFile = project.getFile(project.getName() + "-kdm.xmi");
					if (kdmFile.exists()) {
						kdmFile.delete(true, null);
					}
					inputStream = new ByteArrayInputStream(output.toByteArray());
					kdmFile.create(inputStream, true, new NullProgressMonitor());
					inputStream.reset();
					inputStream.close();
					output.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// DISCOVER UML ELEMENTS
				// -------------------------------------------------------------------------
				// System.out.println("Creating UML model ...");
				umlDiscoverer.discoverElement(kdmFile, new NullProgressMonitor());
				umlResource = umlDiscoverer.getTargetModel();

				
				// nullify all ids and remove unnecessary elements
				TreeIterator<EObject> elements = umlResource.getAllContents();

				toBeDeletedObjects.clear();
				element = null;

				EPackage ePackage = umlResource.getContents().get(0).eClass().getEPackage();

				EClassifier modelClassifier = ePackage.getEClassifier("Model");
				EClass modelClass = (EClass) modelClassifier;
				EStructuralFeature feature = modelClass.getEStructuralFeature("name");

				EClassifier rtsClassifier = ePackage.getEClassifier("RedefinableTemplateSignature");
				EClass rtsClass = (EClass) rtsClassifier;

				EClassifier iterfaceRealizationClassifier = ePackage.getEClassifier("InterfaceRealization");
				EClass iterfaceRealizationClass = (EClass) iterfaceRealizationClassifier;

				EClassifier dependencyClassifier = ePackage.getEClassifier("Dependency");
				EClass dependencyClass = (EClass) dependencyClassifier;

				while (elements.hasNext()) {
					element = elements.next();
					if (element.eClass().equals(modelClass)) {
						String name = (String) element.eGet(feature);
						if (name.equals("source references")) {
							toBeDeletedObjects.add(element);
						}
					} 
					else if (element.eClass().equals(rtsClass)) {
						toBeDeletedObjects.add(element);
					} else if (element.eClass().equals(iterfaceRealizationClass)) {
						toBeDeletedObjects.add(element);
					} else if (element.eClass().equals(dependencyClass)) {
						toBeDeletedObjects.add(element);
					}
				}
				
				for (EObject item : umlResource.getContents()){
					if (!item.eClass().equals(modelClass)){
						toBeDeletedObjects.add(item);
					}
				}

				for (EObject eObject : toBeDeletedObjects) {
					EcoreUtil.delete(eObject, true);
				}
				
				toBeDeletedObjects.clear();
			
				try {
					output.reset();

					umlResource.save(output, null);
					umlResource.getContents().clear();
					umlResource.unload();
					
					ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
					umlResource.load(input,null);
					input.reset();
					input.close();
					output.reset();
					
					umlResource.save(output,null);
					umlResource.getContents().clear();
					umlResource.unload();
										
					filePath = targetXmiDirectory.getAbsolutePath() + File.separator + file.getName() + ".xmi";
					File fileXmi = new File(filePath);
					FileUtils.writeStringToFile(fileXmi, output.toString(), Charset.defaultCharset(), false);
					output.reset();
					// output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					project.close(null);
					project.delete(false, null);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				long after = System.currentTimeMillis();
				System.out.println(" ... Required time = " + (after - before)/1000.0);
			}
			output.close();
			System.out.println("Finished!");
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
