package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.epsilon.cbp.state2change.State2ChangeTool;
import org.eclipse.epsilon.cbp.state2change.UmlXmiGenerator;
import org.eclipse.epsilon.cbp.state2change.Xml2XmiConverter;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.junit.Test;

public class Xml2XmiConverterTest {
	
	@Test
	public void testXml2Xmi() throws DiscoveryException{
		System.out.println(State2ChangeTool.getTimeStamp() +  ": Starting ...");
		System.out.println();
		
		File xmlDirectory = new File("D:/TEMP/WIKIPEDIA/xml/");
		File xmiDirectory = new File("D:/TEMP/WIKIPEDIA/xmi/");
		
		Xml2XmiConverter generator = new Xml2XmiConverter();
		generator.generateXmiFiles(xmlDirectory, xmiDirectory);
		
		assertNotEquals(xmiDirectory.listFiles().length, 0);
		
		System.out.println();
		System.out.println(State2ChangeTool.getTimeStamp() +  ": Finished!");
	}
	
	
	
}
