package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
//import org.eclipse.epsilon.cbp.thrift.CBPThriftResourceFactory;
import org.eclipse.epsilon.cbp.util.CBPResourceConverter;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.junit.Test;

/**
 * Tests for the {@link CBPResourceConverter} class.
 */
public class CBPResourceConverterTests {

	@Test
	public void convertEcoreToXML() throws Exception {
		convertTest("model/blog.ecore", "target.cbpxml");
	}

	@Test
	public void convertEcoreToThrift() throws Exception {
		convertTest("model/blog.ecore", "target.cbpthrift");
	}

	protected void convertTest(final String sourceFilename, final String targetFilename) throws IOException {
		ResourceSet rs = createResourceSet();
		File fOriginal = new File(sourceFilename);
		Resource rOriginal = rs.createResource(URI.createFileURI(fOriginal.getAbsolutePath()));
		assertConvertedIsEqualTo(targetFilename, rOriginal,
				Collections.singletonMap(XMLResource.OPTION_ENCODING, "UTF-8"));
	}

	protected void assertConvertedIsEqualTo(String targetFilename, Resource rOriginal, Map<String, String> options)
			throws IOException {
		ResourceSet rs = createResourceSet();

		File fTarget = new File(targetFilename);
		if (fTarget.exists()) {
			fTarget.delete();
		}
		CBPResource rTarget = (CBPResource) rs.createResource(URI.createFileURI(fTarget.getAbsolutePath()));
		CBPResourceConverter converter = new CBPResourceConverter();
		converter.convert(rOriginal, rTarget, null);
		rTarget.save(null);

		rTarget.unload();
		rs.getResources().remove(rTarget);
		rTarget = (CBPResource) rs.createResource(URI.createFileURI(fTarget.getAbsolutePath()));
		rTarget.load(null);

		StringOutputStream sosOriginal = new StringOutputStream(), sosCopy = new StringOutputStream();
		Resource rXMI = new XMIResourceImpl();
		rXMI.getContents().addAll(rTarget.getContents());
		rOriginal.save(sosOriginal, options);
		rXMI.save(sosCopy, options);
		assertEquals(sosOriginal.toString(), sosCopy.toString());
	}

	protected ResourceSet createResourceSet() {
		ResourceSet rs = new ResourceSetImpl();
		rs.setPackageRegistry(EPackage.Registry.INSTANCE);

		final Map<String, Object> extensionToFactoryMap = rs.getResourceFactoryRegistry().getExtensionToFactoryMap();
		//extensionToFactoryMap.put("cbpthrift", new CBPThriftResourceFactory());
		extensionToFactoryMap.put("cbpxml", new CBPXMLResourceFactory());
		extensionToFactoryMap.put("*", new XMIResourceFactoryImpl());
		return rs;
	}
}
