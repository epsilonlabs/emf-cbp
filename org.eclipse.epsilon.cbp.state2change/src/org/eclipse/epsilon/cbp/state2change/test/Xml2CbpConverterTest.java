package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertNotEquals;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.epsilon.cbp.state2change.State2ChangeTool;
import org.eclipse.epsilon.cbp.state2change.Xmi2CbpConverter;
import org.junit.Test;

public class Xml2CbpConverterTest {

    @Test
    public void testXmi2Cbp() throws Exception {
	Logger.getRootLogger().setLevel(Level.OFF);

	System.out.println(State2ChangeTool.getTimeStamp() + ": Starting Xmi 2 Cbp Test ...");
	System.out.println();

	File cbpDirectory = new File("D:/TEMP/WIKIPEDIA/cbp/");
	File xmiDirectory = new File("D:/TEMP/WIKIPEDIA/xmi/");

	Xmi2CbpConverter converter = new Xmi2CbpConverter();
	converter.convertXmiToCbp(xmiDirectory, cbpDirectory);

	assertNotEquals(cbpDirectory.listFiles().length, 0);

	System.out.println();
	System.out.println(State2ChangeTool.getTimeStamp() + ": Finished!");
    }
}
