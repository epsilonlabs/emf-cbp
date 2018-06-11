package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.epsilon.cbp.state2change.State2ChangeConverter;
import org.junit.Test;


public class State2ChangeConverterTest {

	private File xmiDirectory = new File("D:\\TEMP\\COMPARISON\\xmi-without-id\\");
	private File diffDirectory = new File("D:\\TEMP\\COMPARISON\\diff\\");
	private File cbpFile = new File("D:\\TEMP\\COMPARISON\\target-cbpxml\\BPMN2-id.cbpxml");
	
	@Test
	public void generateTest() throws Exception{
		State2ChangeConverter state2ChangeConverter = new State2ChangeConverter(xmiDirectory);
		boolean result = state2ChangeConverter.generateFromMultipleFiles(cbpFile, diffDirectory);
		assertEquals(true, result);		
	}
}
