package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.eclipse.epsilon.cbp.test.conference.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConferenceMemoryTests extends MemoryPerformanceTests {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml" } });

	}

	public ConferenceMemoryTests(String extension) {
		super(extension);
	}

	@Test
	public void testRandomModelPerformance() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		appendLineToOutputText("Start: " + sdf.format(new Date()) + "\n");

		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tNuNodes\tOptimiCBP\tNonOpCBP\tXMISize");
		// for (int i = 3200; i <= 3200; i += 200) {
		for (int i = 0; i <= 20000; i += 100) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");

			ConferenceModelGenerator.initialise();
			for (int j = 0; j < i; j++) {
				eolCode.append(ConferenceModelGenerator.createObjects());
			}
			ConferenceModelGenerator.setNumberOfOperation(i);
			for (String line : ConferenceModelGenerator.generateCompleteCode()) {
				eolCode.append(line);
			}
			run(eolCode.toString(), true);
			// appendLineToOutputText(eolCode);
		}

		appendLineToOutputText("\nEnd: " + sdf.format(new Date()) + "\n");
		saveOutputText();
		saveErrorMessages();
		assertEquals(true, true);
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
