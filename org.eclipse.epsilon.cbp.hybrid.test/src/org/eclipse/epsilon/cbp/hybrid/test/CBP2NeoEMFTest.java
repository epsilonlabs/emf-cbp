package org.eclipse.epsilon.cbp.hybrid.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.eclipse.epsilon.cbp.hybrid.neoemf.CBP2NeoEMFResource;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;

import org.junit.Test;


public class CBP2NeoEMFTest {

	@Test
	public void testGeneateAllSessionsXMIs() {
		try {
			UML.UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();

			File cbpFile = new File("D:\\TEMP\\ASE\\epsilon.940.cbpxml");
			File targetDir = new File("D:\\TEMP\\NEOEMF_GENERATOR\\EPSILON-NeoEMF\\");
//			File cbpFile = new File("D:\\TEMP\\ASE\\wikipedia.10187.cbpxml");
//			File targetDir = new File("D:\\TEMP\\NEOEMF_GENERATOR\\WIKIPEDIA-NeoEMF\\");
//			File cbpFile = new File("D:\\TEMP\\NEOEMF_GENERATOR\\BPMN2.cbpxml");
//			File targetDir = new File("D:\\TEMP\\NEOEMF_GENERATOR\\BPMN2-NeoEMF\\");
			if (!targetDir.exists())
				targetDir.mkdir();

			CBP2NeoEMFResource converter = new CBP2NeoEMFResource(cbpFile, targetDir, false);
			converter.generateAllSessionsXMIs();
			converter.unload();

		} catch (Exception e) {
			e.printStackTrace();
		}

		assertEquals(true, true);
	}

}
