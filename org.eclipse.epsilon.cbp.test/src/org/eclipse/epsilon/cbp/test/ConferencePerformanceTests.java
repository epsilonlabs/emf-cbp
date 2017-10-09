package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.eclipse.epsilon.cbp.test.conference.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConferencePerformanceTests extends LoadingPerformanceTests {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml" } });

	}

	public ConferencePerformanceTests(String extension) {
		super(extension);
	}

	@Test
	public void testRandomModelPerformance() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		appendLineToOutputText("Start: " + sdf.format(new Date()) + "\n");

		List<String> codeList = new ArrayList<>();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP"
				+ "\tTAtSU\tTReSU\tTAtARM\tTReARM\tTiDel");
		for (int i = 0; i <= 10000; i += 500) {
			int first = (i == 0) ? 1 : i;
			appendToOutputText(String.valueOf(first) + "\t");

			codeList.clear();
			ConferenceModelGenerator.initialise();
			for (int j = 0; j < i-1; j++) {
				codeList.add(ConferenceModelGenerator.createObjects());
			}
			ConferenceModelGenerator.setNumberOfOperation(i*1);
			codeList.addAll(ConferenceModelGenerator.generateCompleteCode());
			//String eolCode = String.join("\n", codeList);

			run("", true, codeList);

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

	public String randomString(int length) {
		String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		String result = "";
		for (int i = 0; i < length; i++) {
			int index = ThreadLocalRandom.current().nextInt(alphabets.length());
			result = result + alphabets.charAt(index);
		}
		return result;
	}

}
