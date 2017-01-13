package org.eclipse.epsilon.cbp.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class XMItoCBPConverter {

	protected String sourceURI = "";
	protected String targetURI = "";
	protected Map<String, Object> options;
	protected Resource sourceResource;
	
	public XMItoCBPConverter(Map<String, Object> options)
	{
		this.options = options;
		sourceURI = (String) options.get("SOURCE_URI");
		targetURI = (String) options.get("TARGET_URI");
	}
	
	public void loadSource() throws IOException
	{
		ResourceSet resourceSet = new ResourceSetImpl();

		ResourceSet ecoreResourceSet = new ResourceSetImpl();
		ecoreResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		@SuppressWarnings("unchecked")
		ArrayList<String> metamodelPaths = (ArrayList<String>) options.get("METAMODELPATHS");
		for(String str: metamodelPaths)
		{
			Resource ecoreResource = ecoreResourceSet
					.createResource(URI.createFileURI(new File(str).getAbsolutePath()));
			ecoreResource.load(null);
			for (EObject o : ecoreResource.getContents()) {
				EPackage ePackage = (EPackage) o;
				resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			}
		}

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		sourceResource = resourceSet.createResource(URI.createFileURI(new File(sourceURI).getAbsolutePath()));
		sourceResource.load(null);
	}
	
	public Resource getSourceResource() {
		return sourceResource;
	}
	
	public static void main(String[] args) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("SOURCE_URI", "model/set0.xmi");
		ArrayList<String> metamodelPaths = new ArrayList<String>();
		metamodelPaths.add("model/JDTAST.ecore");
		options.put("METAMODELPATHS", metamodelPaths);

		XMItoCBPConverter converter = new XMItoCBPConverter(options);
		try {
			converter.loadSource();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Iterator<EObject> iter = converter.getSourceResource().getAllContents();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
	
}
