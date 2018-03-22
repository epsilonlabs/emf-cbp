package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.eclipse.epsilon.cbp.hybrid.CBP2XMIResource;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class CBP2XMITest {

	@Test
	public void testGeneateAllSessionsXMIs() {
		try {
			UMLPackage.eINSTANCE.eClass();
			
			File cbpFile = new File("D:\\TEMP\\XMI_GENERATOR\\BPMN2.cbpxml");
			File targetDir = new File("D:\\TEMP\\XMI_GENERATOR\\BPMN2\\");
//			File cbpFile = new File("D:\\TEMP\\XMI_GENERATOR\\input.cbpxml");
//			File targetDir = new File("D:\\TEMP\\XMI_GENERATOR\\");
					
			CBP2XMIResource converter = new CBP2XMIResource(cbpFile, targetDir, false);
			converter.generateAllSessionsXMIs();
			converter.unload();
		}catch(Exception e) {
			e.printStackTrace();
		}	
		assertEquals(true, true);
	}
	
	@Test
	public void testGenerateXMI() {
		try {
			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();
			
			File cbpFile = new File("D:\\TEMP\\XMI_GENERATOR\\wikipedia.003100.ISO.cbpxml");
			File targetDir = new File("D:\\TEMP\\XMI_GENERATOR\\");
//			File cbpFile = new File("D:\\TEMP\\XMI_GENERATOR\\input.cbpxml");
//			File targetDir = new File("D:\\TEMP\\XMI_GENERATOR\\");
					
			CBP2XMIResource converter = new CBP2XMIResource(cbpFile, targetDir, false);
			converter.generateXMI();
			converter.unload();
		}catch(Exception e) {
			e.printStackTrace();
		}	
		assertEquals(true, true);
	}
}
