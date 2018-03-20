package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.stream.XMLStreamException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.history.ModelHistory;
import org.eclipse.epsilon.cbp.history.ObjectHistory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.epsilon.cbp.state2change.State2ChangeTool;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class SimpleTest {
	@Test
	public void testSimple() throws JAXBException, XMLStreamException {
		System.out.println(State2ChangeTool.getTimeStamp());
		try {
			UMLPackage.eINSTANCE.eClass();
			MoDiscoXMLPackage.eINSTANCE.eClass();

			File cbpFile = new File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.cbpxml");
			File ignoreList = new File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.ignoreset");
			File modelHistoryFile = new File("D:\\TEMP\\ECMFA\\cbp\\BPMN2.history");

			Map<Object, Object> options = new HashMap<>();
			options.put(CBPXMLResourceImpl.OPTION_GENERATE_MODEL_HISTORY, true);
			
//			for (int i = 0; i < 5; i++) {
				
				CBPXMLResourceImpl cbpResource = (CBPXMLResourceImpl) (new CBPXMLResourceFactory())
						.createResource(URI.createFileURI(cbpFile.getAbsolutePath()));

				long start = System.nanoTime();
				cbpResource.loadIgnoreSet(new FileInputStream(ignoreList));
				cbpResource.load(options);
//				cbpResource.load(null);
				long end = System.nanoTime();
				double delta = (end - start) / 1000000000.0;
				System.out.printf("%f" + System.lineSeparator(), delta);

//				Object object = cbpResource.getModelHistory();
//				cbpResource.getModelHistory().printStructure();
				
				start = System.nanoTime();
				cbpResource.getModelHistory().exportModelHistory(modelHistoryFile);
				end = System.nanoTime();
				delta = (end - start) / 1000000000.0;
				System.out.printf("%f" + System.lineSeparator(), delta);
				
				cbpResource.getModelHistory().clear();
				start = System.nanoTime();
				cbpResource.getModelHistory().importModelHistory(modelHistoryFile);
				end = System.nanoTime();
				delta = (end - start) / 1000000000.0;
				System.out.printf("%f" + System.lineSeparator(), delta);
				
				cbpResource.unload();
//			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(State2ChangeTool.getTimeStamp());

		assertEquals(true, true);
	}
}
