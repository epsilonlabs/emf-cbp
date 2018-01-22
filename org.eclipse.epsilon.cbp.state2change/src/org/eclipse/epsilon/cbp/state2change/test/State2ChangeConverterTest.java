package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.epsilon.cbp.state2change.State2ChangeConverter;
import org.junit.Test;


public class State2ChangeConverterTest {

	private File xmiDirectory = new File("./test.data/xmi/".replace("/", File.separator));
	private File diffDirectory = new File("./test.data/diff/".replace("/", File.separator));
	private File cbpFile = new File("./test.data/cbp/".replace("/", File.separator) + "javamodel.cbpxml");
	
	@Test
	public void generateTest() throws Exception{
		State2ChangeConverter state2ChangeConverter = new State2ChangeConverter(xmiDirectory);
		boolean result = state2ChangeConverter.generate(cbpFile, diffDirectory);
		assertEquals(true, result);		
	}
}
