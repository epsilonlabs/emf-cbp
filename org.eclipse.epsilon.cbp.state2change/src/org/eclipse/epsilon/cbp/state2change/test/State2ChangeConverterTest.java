package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.epsilon.cbp.state2change.State2ChangeConverter;
import org.junit.Test;


public class State2ChangeConverterTest {

	private File xmiDirectory = new File("./test.data/xmi/".replace("/", File.separator));
	private File cbpFile = new File("./test.data/cbp/".replace("/", File.separator) + "javamodel.cbpxml");
	
	@Test
	public void generateTest() throws Exception{
		FileOutputStream fop = new FileOutputStream(cbpFile);	
		State2ChangeConverter state2ChangeConverter = new State2ChangeConverter(xmiDirectory);
		String cbpText = state2ChangeConverter.generate(fop);
//		System.out.println(cbpText);
		fop.flush();
		fop.close();
		assertNotEquals(cbpText.length(), 0);		
	}
}
