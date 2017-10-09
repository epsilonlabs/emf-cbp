package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.eclipse.epsilon.cbp.test.node.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConferencePerformanceTestsExperiment extends NonParalelLoadingPerformanceTests {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml" } });

	}

	public ConferencePerformanceTestsExperiment(String extension) {
		super(extension);
	}

	@Test
	public void testRandomModelPerformance() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
		System.out.println("Start: " + sdf.format(new Date()) + "\n");

		System.out.println("SavXMI\tSavCBP\tLoaXMI\tLoOCBP\tLoaCBP\tNuNodes\tNLOCBP\tNLCBP"
				+ "\tTAtSU\tTReSU\tTAtARM\tTReARM\tTiDel");

		List<String> eolCodeList = new ArrayList<String>();
		run(eolCodeList, true);
		System.out.println("\nEnd: " + sdf.format(new Date()) + "\n");
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
