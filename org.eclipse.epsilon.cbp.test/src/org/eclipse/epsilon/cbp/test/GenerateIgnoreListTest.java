package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class GenerateIgnoreListTest {

	@Test
	public void generateIgnoreList() {
		boolean isSuccess = false;
		try {
			UMLPackage.eINSTANCE.eClass();
			
			File cbpFile = new File("D:\\TEMP\\ECMFA\\BPMN2.cbpxml");
			File cbpDummyFile = new File("D:\\TEMP\\ECMFA\\can-be-deleted.cbpxml");
			File ignoreListFile = new File("D:\\TEMP\\ECMFA\\BPMN2.ignorelist");
			if (ignoreListFile.exists()) {
				ignoreListFile.delete();
			}
			System.out.println("Generating ignore list of " + cbpFile.getName() + " ...");
			

			//CBP Resource 1
			CBPXMLResourceImpl cbpResource1 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
			
			cbpResource1.generateIgnoreListFile(cbpDummyFile, ignoreListFile);
			
//			Map<Object, Object> options1 = new HashMap<>();
//			options1.put(CBPXMLResourceImpl.OPTION_OPTIMISE_LOAD, false);
//			options1.put(CBPXMLResourceImpl.OPTION_KEEP_CHANGE_EVENTS_AFTER_LOAD, true);
//			cbpResource1.load(options1);
//			
//			FileOutputStream cbpDummyOutputStream = new FileOutputStream(cbpDummyFile, false);
//			cbpResource1.save(cbpDummyOutputStream, null);
//			FileOutputStream ignoreFileOutputStream = new FileOutputStream(ignoreListFile, false);
//			cbpResource1.saveIgnoreSet(ignoreFileOutputStream);
			
			Resource xmiResource1 = (new XMIResourceFactoryImpl().createResource(URI.createURI("model.xmi")));
			xmiResource1.getContents().addAll(cbpResource1.getContents());
			
			//CBP Resource 2
			FileInputStream ignoreFileInputStream = new FileInputStream(ignoreListFile);
			
			CBPXMLResourceImpl cbpResource2 = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
			cbpResource2.loadIgnoreSet(ignoreFileInputStream);
			cbpResource2.load(null);
			
			Resource xmiResource2 = (new XMIResourceFactoryImpl().createResource(URI.createURI("model.xmi")));
			xmiResource2.getContents().addAll(cbpResource2.getContents());
			
			//Comparison
			StringOutputStream outputStream1 = new StringOutputStream();
			xmiResource1.save(outputStream1, null);
			
			StringOutputStream outputStream2 = new StringOutputStream();
			xmiResource1.save(outputStream2, null);
			
			if (outputStream1.toString().equals(outputStream2.toString())) {
				System.out.println("SUCCESS!");
				isSuccess = true;
			}else {
				System.out.println("FAIL!");
				isSuccess = false;
			}
			
			//clearing
			cbpResource1.getModelHistory().clear();
			cbpResource1.clearIgnoreSet();
			cbpResource1.unload();
			
			cbpResource2.getModelHistory().clear();
			cbpResource2.clearIgnoreSet();
			cbpResource2.unload();
			
			xmiResource1.unload();
			xmiResource2.unload();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		assertEquals(isSuccess, true);

	}
}
