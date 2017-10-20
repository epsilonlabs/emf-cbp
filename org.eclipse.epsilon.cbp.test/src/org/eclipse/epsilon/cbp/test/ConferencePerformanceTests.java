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
				+ "\tTAtSU\tTReSU\tTAtARM\tTReARM\tTiDel"

				+ "\tSetAtt\tUnsAtt\tAddAtt\tRemAtt\tMovAtt" + "\tSetRef\tUnsRef\tAddRef\tRemRef\tMovRef"
				+ "\tDelete\tAddRes\tRemRes\tPackage\tSession\tCreate"

				+ "\tiSetAtt\tiUnsAtt\tiAddAtt\tiRemAtt\tiMovAtt" + "\tiSetRef\tiUnsRef\tiAddRef\tiRemRef\tiMovRef"
				+ "\tiDelete\tiAddRes\tiRemRes\tiPackg\tiSessio\tiCreate"

				+ "\taSetAtt\taUnsAtt\taAddAtt\taRemAtt\taMovAtt" + "\taSetRef\taUnsRef\taAddRef\taRemRef\taMovRef"
				+ "\taDelete\taAddRes\taRemRes\taPackg\taSessio\taCreate");
		for (int i = 0; i <= 1000000; i += 500) {
			int first = (i == 0) ? 1 : i;
			appendToOutputText(String.valueOf(first) + "\t");

			codeList.clear();
			ConferenceModelGenerator.initialise();
			for (int j = 0; j < i - 1; j++) {
				codeList.add(ConferenceModelGenerator.createObjects());
			}
			ConferenceModelGenerator.setNumberOfOperation(i * 1);
			codeList.addAll(ConferenceModelGenerator.generateCompleteCode());
			// String eolCode = String.join("\n", codeList);

			run("", true, codeList);

			// appendLineToOutputText(eolCode);
		}

		appendLineToOutputText("\nEnd: " + sdf.format(new Date()) + "\n");
		saveOutputText();
		saveErrorMessages();
		assertEquals(true, true);
	}

	@Test
	public void testRemoveResourceAddToOneRootPerformance() throws Exception {
		StringBuilder eolCode = new StringBuilder();
		appendLineToOutputText("No\tSavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP"
				+ "\tTAtSU\tTReSU\tTAtARM\tTReARM\tTiDel"

				+ "\tSetAtt\tUnsAtt\tAddAtt\tRemAtt\tMovAtt" + "\tSetRef\tUnsRef\tAddRef\tRemRef\tMovRef"
				+ "\tDelete\tAddRes\tRemRes\tPackage\tSession\tCreate"

				+ "\tiSetAtt\tiUnsAtt\tiAddAtt\tiRemAtt\tiMovAtt" + "\tiSetRef\tiUnsRef\tiAddRef\tiRemRef\tiMovRef"
				+ "\tiDelete\tiAddRes\tiRemRes\tiPackg\tiSessio\tiCreate"

				+ "\taSetAtt\taUnsAtt\taAddAtt\taRemAtt\taMovAtt" + "\taSetRef\taUnsRef\taAddRef\taRemRef\taMovRef"
				+ "\taDelete\taAddRes\taRemRes\taPackg\taSessio\taCreate");
		for (int i = 0; i <= 100000; i += 500) {
			eolCode.setLength(0);
			appendToOutputText(String.valueOf(i) + "\t");
			StringBuilder code = new StringBuilder();
			code.append("var conf = new Conference;\n");
			code.append("var day1 = new Day;\n");
			code.append("var day2 = new Day;\n");
			code.append("conf.days.add(day1);\n");
			code.append("conf.days.add(day2);\n");
			code.append("for(i in Sequence{1..1000}){\n");
			code.append("    var track = new Track;\n");
			code.append("    day1.slots.add(track);\n");
			code.append("}\n");
			code.append("for(i in Sequence{1..%1$d}){\n");
			code.append("    delete day1.slots.last();\n");
			code.append("    day1.slots.add(new Track);\n");
			code.append("}\n");
//			code.append("var conf = new Conference;\n");
//			code.append("var day1 = new Day;\n");
//			code.append("var day2 = new Day;\n");
//			code.append("conf.days.add(day1);\n");
//			code.append("conf.days.add(day2);\n");
//			code.append("for(i in Sequence{1..%1$d}){\n");
//			code.append("    var track = new Track;\n");
//			code.append("    day1.slots.add(track);\n");
//			code.append("}\n");
//			code.append("for(i in Sequence{1..%1$d}){\n");
//			code.append("    day2.slots.add(day1.slots.last());\n");
//			code.append("}\n");
			eolCode.append(String.format(code.toString(), i));
			// System.out.println(eolCode);
			run(eolCode.toString(), true);
		}

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
