package org.eclipse.epsilon.cbp.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConferenceEquivalenceTests extends LoadingEquivalenceTests {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml" } });

	}

	public ConferenceEquivalenceTests(String extension) {
		super(extension);
	}

	// @Test
	// public void unsetAttributeTest() throws Exception {
	// StringBuilder eolCode = new StringBuilder();
	// eolCode.append("var p = new Person;\n");
	// eolCode.append("p.fullName = \"Old Name!\";\n");
	// eolCode.append("p.fullName = null;\n");
	// eolCode.append("p.fullName = \"New Name!\";\n");
	// eolCode.append("p.fullName = null;\n");
	// System.out.println(eolCode);
	// run(eolCode.toString(), true);
	//
	// }
	//
	// @Test
	// public void setAttributeTest() throws Exception {
	// StringBuilder eolCode = new StringBuilder();
	// eolCode.append("var p = new Person;\n");
	// eolCode.append("p.fullName = \"Old Name!\";\n");
	// eolCode.append("p.fullName = null;\n");
	// eolCode.append("p.fullName = \"New Name!\";\n");
	// System.out.println(eolCode);
	// run(eolCode.toString(), true);
	// }
	//
	// @Test
	// public void testFromFile() throws Exception {
	// String eolCode = new
	// String(Files.readAllBytes(Paths.get("data/conference.eol")));
	// run(eolCode, true);
	//
	// }

	@Test
	public void testRandomModel() throws Exception {
		
		List<String> codeList = ConferenceModelGenerator.generateEolCode();
		String eolCode = String.join("\n", codeList);
		System.out.println(eolCode);
		System.out.println("Running...");
		run(eolCode, true);
	}

	@Override
	public EPackage getEPackage() {
		return ConferencePackage.eINSTANCE;
	}
}
