package org.eclipse.epsilon.cbp.test.workbench;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPTextResourceFactory;

public class GrabatsModel_Load {

	public static void main(String[] args) {

		ResourceSet cbpResourceSet = new ResourceSetImpl();

		ResourceSet ecoreCBPResourceSet = new ResourceSetImpl();
		ecoreCBPResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("cbptxt", new CBPTextResourceFactory());
		Resource ecoreResource = ecoreCBPResourceSet
				.createResource(URI.createFileURI(new File("model/JDTAST.ecore").getAbsolutePath()));
		try {
			ecoreResource.load(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (EObject o : ecoreResource.getContents()) {
			EPackage ePackage = (EPackage) o;
			cbpResourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
		}

		cbpResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("cbp", new ResourceFactoryImpl());
		CBPResource resource = (CBPResource) cbpResourceSet.createResource(URI.createFileURI(new File("model/set0.xmi").getAbsolutePath()));
		
		try {
			Map<String, Object> options = new HashMap<String, Object>();
			File f = new File("model/set0.txt");
			options.put("path", f.getAbsolutePath());
			options.put("DEFAULT_LOADING", true);

			resource.load(options);
			resource.getChangelog().printLog();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
