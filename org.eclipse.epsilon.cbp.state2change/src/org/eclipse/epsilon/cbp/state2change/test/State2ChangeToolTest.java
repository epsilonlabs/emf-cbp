package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.*;

import java.io.File;

import org.eclipse.epsilon.cbp.state2change.State2ChangeTool;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.junit.Test;

public class State2ChangeToolTest {

	@Test
	public void testGenerateCbpSessionString() throws DiscoveryException {

		File xmiDirectory = new File("D:\\TEMP\\BPMN2\\xmi-backup");
		State2ChangeTool.generateCbpSessionString(xmiDirectory);
		assertEquals(xmiDirectory.listFiles().length > 0, true);
	}

}
