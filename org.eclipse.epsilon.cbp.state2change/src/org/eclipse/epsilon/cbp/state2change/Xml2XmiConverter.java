package org.eclipse.epsilon.cbp.state2change;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.modisco.xml.discoverer.XMLModelDiscoverer;

public class Xml2XmiConverter implements IApplication {

	File xmlDirectory = new File("D:/TEMP/WIKIPEDIA/xml/");
	File xmiDirectory = new File("D:/TEMP/WIKIPEDIA/xmi/");

	@Override
	public Object start(IApplicationContext context) {

		return IApplication.EXIT_OK;
	}

	public void generateXmiFiles(File xmlDirectory, File xmiDirectory) throws DiscoveryException {
		this.xmlDirectory = xmlDirectory;
		this.xmiDirectory = xmiDirectory;

		if (xmlDirectory.exists() == false)
			xmlDirectory.mkdir();
		if (xmiDirectory.exists() == false)
			xmiDirectory.mkdir();

		File[] xmlFiles = xmlDirectory.listFiles();
		for (File xmlFile : xmlFiles) {
			System.out.print(State2ChangeTool.getTimeStamp() + ": Converting " + xmlFile.getName() + " to xmi ... ");

			Map<Object, Object> saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			XMLModelDiscoverer discoverer = new XMLModelDiscoverer();
			discoverer.setIgnoreWhitespace(false);
			discoverer.setLightweightModel(false);
			discoverer.discoverElement(xmlFile, new NullProgressMonitor());
			Resource xmlResource = discoverer.getTargetModel();

			String xmiFileName = xmlFile.getName();
			xmiFileName = xmiFileName.substring(0, xmiFileName.indexOf(".xml"));
			xmiFileName = xmiFileName + ".xmi";
			
			String xmiFilePath = xmiDirectory.getAbsolutePath() + File.separator + xmiFileName;

			EPackage ePackage = xmlResource.getContents().get(0).eClass().getEPackage();
			
			Resource xmiResource = (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiFilePath));
			xmiResource.getContents().addAll(xmlResource.getContents());
			xmlResource.getContents().clear();
			xmlResource.unload();

			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				output.reset();
				xmiResource.save(output, saveOptions);
									xmiResource.getContents().clear();
				xmiResource.unload();

				ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
				xmiResource.load(input, null);
				input.reset();
				input.close();
				output.reset();

				xmiResource.save(saveOptions);
				xmiResource.getContents().clear();
				xmiResource.unload();

				System.out.println("done");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		// nothing to do
	}
	
	
	public class Task extends Thread {
		
		
		
	}
}
