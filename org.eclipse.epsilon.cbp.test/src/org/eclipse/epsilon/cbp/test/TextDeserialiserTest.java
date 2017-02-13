package org.eclipse.epsilon.cbp.test;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.resource.CBPTextResourceImpl;

public class TextDeserialiserTest extends AbstractDeserialiserTest{

	@Override
	protected Resource getResource() {
		return new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));
	}
	
}
