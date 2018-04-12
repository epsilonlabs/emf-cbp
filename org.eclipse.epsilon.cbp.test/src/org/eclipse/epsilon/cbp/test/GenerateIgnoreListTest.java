package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.modelmbean.ModelMBeanInfoSupport;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class GenerateIgnoreListTest {

	@Test
	public void generateIgnoreList() {
		boolean isSuccess = false;
		try {
			System.out.println("Initialise ...");
			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();

			Map<Object, Object> saveOptions = (new XMIResourceImpl()).getDefaultSaveOptions();
			saveOptions.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

			File cbpFile = new File("D:\\TEMP\\ASE\\epsilon.1473.cbpxml");
//			File cbpFile = new File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.cbpxml");
			File cbpDummyFile = new File("D:\\TEMP\\ASE\\_temp.cbpxml");
			File ignoreListFile = new File("D:\\TEMP\\ASE\\epsilon.1473.ignoreset");
			FileOutputStream xmiFile1 = new FileOutputStream("D:\\TEMP\\ASE\\_temp1.xmi");
			FileOutputStream xmiFile2 = new FileOutputStream("D:\\TEMP\\ASE\\_temp2.xmi");

			if (ignoreListFile.exists()) {
				ignoreListFile.delete();
			}

			System.out.println("Generating ignore list of " + cbpFile.getName() + " ...");
			// CBP Resource 1
			CBPXMLResourceImpl cbpResource1 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

			cbpResource1.generateIgnoreListFile(cbpFile, cbpDummyFile, ignoreListFile);

			Resource xmiResource1 = (new XMIResourceFactoryImpl().createResource(URI.createURI("model.xmi")));
			xmiResource1.getContents().addAll(cbpResource1.getContents());

			// CBP Resource 2
			System.out.println("Loading Optimised CBP ...");
			FileInputStream ignoreFileInputStream = new FileInputStream(ignoreListFile);

			CBPXMLResourceImpl cbpResource2 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
			cbpResource2.loadIgnoreSet(ignoreFileInputStream);
			cbpResource2.load(null);

			Resource xmiResource2 = (new XMIResourceFactoryImpl().createResource(URI.createURI("model.xmi")));
			xmiResource2.getContents().addAll(cbpResource2.getContents());

			// Comparison
			System.out.println("Saving original XMI ...");
			StringOutputStream outputStream1 = new StringOutputStream();
			xmiResource1.save(outputStream1, saveOptions);
			xmiResource1.save(xmiFile1, saveOptions);

			System.out.println("Saving XMI from optimised CBP loading ...");
			StringOutputStream outputStream2 = new StringOutputStream();
			xmiResource2.save(outputStream2, saveOptions);
			xmiResource2.save(xmiFile2, saveOptions);

			System.out.println("Comparing XMIs ...");
			if (outputStream1.toString().equals(outputStream2.toString())) {
				System.out.println("SUCCESS!");
				isSuccess = true;
			} else {
				System.out.println("FAIL!");
				isSuccess = false;
			}

			// clearing
			cbpResource1.getModelHistory().clear();
			cbpResource1.clearIgnoreSet();
			cbpResource1.unload();

			cbpResource2.getModelHistory().clear();
			cbpResource2.clearIgnoreSet();
			cbpResource2.unload();

			xmiResource1.unload();
			xmiResource2.unload();

			System.out.println("Finished!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		assertEquals(isSuccess, true);

	}
	
	@Test
	public void loadCBP() {
		UMLPackage.eINSTANCE.eClass();
		
		try {
			Map<Object, Object> loadOptions = new HashMap<>();
			loadOptions.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD, true);
			loadOptions.put(CBPXMLResourceImpl.OPTION_KEEP_CHANGE_EVENTS_AFTER_LOAD, true);
			
			File cbpDummyFile = new File("D:\\TEMP\\ECMFA\\cbp\\_temp.cbpxml");
			File ignoreListFile = new File("D:\\TEMP\\ECMFA\\cbp\\_output.ignoreset");
			CBPXMLResourceImpl cbpResource2 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(cbpDummyFile.getAbsolutePath()));
			cbpResource2.loadIgnoreSet(new FileInputStream(ignoreListFile));
			cbpResource2.load(loadOptions);
			System.out.println("Size 2 : " + cbpResource2.getChangeEvents().size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
