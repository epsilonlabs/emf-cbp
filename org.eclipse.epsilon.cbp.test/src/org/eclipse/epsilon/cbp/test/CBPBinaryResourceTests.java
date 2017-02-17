package org.eclipse.epsilon.cbp.test;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.resource.CBPBinaryResourceImpl;

public class CBPBinaryResourceTests extends AbstractCBPResourceTests {

	@Override
	protected Resource getResource() {
		return new CBPBinaryResourceImpl(
				URI.createFileURI(new File("model/test.cbpbin").getAbsolutePath()));
	}
	

}
