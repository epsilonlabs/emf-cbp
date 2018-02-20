package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.epsilon.cbp.state2change.State2ChangeTool;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class SimpleTest {
	@Test
	public void testSimple() {
		System.out.println(State2ChangeTool.getTimeStamp());
		try {
			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();
			
			File cbpFile = new File("D:\\TEMP\\ECMFA\\cbp\\wikipedia.cbpxml");
			File ignoreList = new File("D:\\TEMP\\ECMFA\\cbp\\wikipedia.ignorelist");

			CBPXMLResourceImpl cbpResource = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
					.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
			cbpResource.loadIgnoreSet(new FileInputStream(ignoreList));
			cbpResource.load(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(State2ChangeTool.getTimeStamp());
		
		assertEquals(true, true);
	}
}

