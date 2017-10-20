package org.eclipse.epsilon.cbp.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.eclipse.epsilon.cbp.test.conference.Node;
import org.eclipse.epsilon.cbp.test.node.NodePackage;
//import org.eclipse.epsilon.cbp.thrift.CBPThriftResourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConferenceAppendTests extends AppendPerformanceTests {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml", new CBPXMLResourceFactory() } });
	}

	public ConferenceAppendTests(String extension, Resource.Factory factory) {
		super(extension, factory);
	}

	@Test
	public void testAppendRandomModel() throws Exception {
		List<String> list = new ArrayList<>();

		ConferenceModelGenerator.initialise();
		ConferenceModelGenerator.setNumberOfOperation(100000);
		list = ConferenceModelGenerator.generateCompleteCode();
		run(list.toArray(new String[list.size()]));
		// debug(list.toArray(new String[list.size()]));
	}

	@Override
	public EPackage getEPackage() {
		return ConferencePackage.eINSTANCE;
	}

	@Override
	public Class<?> getNodeClass() {
		return Node.class;
	}

}
