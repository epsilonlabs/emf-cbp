package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.eclipse.epsilon.cbp.hybrid.CBP2XMIResource;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.junit.Test;

public class CBP2XMITest {

	@Test
	public void testGeneateAllSessionsXMIs() {
		try {
			org.eclipse.uml2.uml.UMLPackage.eINSTANCE.eClass();
//			UML.UMLPackage.eINSTANCE.eClass();
			
//			File cbpFile = new File("D:\\TEMP\\ASE\\epsilon.1473.cbpxml");
//			File targetDir = new File("D:\\TEMP\\ASE\\epsilon_xmi");
			File cbpFile = new File("D:\\TEMP\\ASE\\bpmn2.192.cbpxml");
			File targetDir = new File("D:\\TEMP\\ASE\\bpmn2_xmi");
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
//			org.eclipse.uml2.uml.UMLPackage.eINSTANCE.eClass();
			UML.UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();
			
			File cbpFile = new File("D:\\TEMP\\ASE\\epsilon.1473.cbpxml");
			File targetDir = new File("D:\\TEMP\\ASE\\");
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
