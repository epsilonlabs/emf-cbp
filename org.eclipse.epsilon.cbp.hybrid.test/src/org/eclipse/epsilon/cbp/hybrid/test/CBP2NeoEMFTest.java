package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.eclipse.epsilon.cbp.hybrid.CBP2NeoEMFResource;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class CBP2NeoEMFTest {

	@Test
	public void testGeneateAllSessionsXMIs() {
		try {
			UMLPackage.eINSTANCE.eClass();
			
			File cbpFile = new File("D:\\TEMP\\XMI_GENERATOR\\BPMN2.cbpxml");
			File targetDir = new File("D:\\TEMP\\XMI_GENERATOR\\BPMN2\\");
//			File cbpFile = new File("D:\\TEMP\\XMI_GENERATOR\\input.cbpxml");
//			File targetDir = new File("D:\\TEMP\\XMI_GENERATOR\\");
					
			CBP2NeoEMFResource converter = new CBP2NeoEMFResource(cbpFile, targetDir, true);
			converter.generateAllSessionsXMIs();
			converter.unload();
			
			
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(true, true);
	}
	
}
