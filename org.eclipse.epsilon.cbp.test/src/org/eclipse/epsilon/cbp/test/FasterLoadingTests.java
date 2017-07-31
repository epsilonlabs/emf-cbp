package org.eclipse.epsilon.cbp.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.blog.BlogPackage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FasterLoadingTests extends LoadingPerformanceEquivalenceTests {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml" } });

	}

	public FasterLoadingTests(String extension) {
		super(extension);
	}

	@Test
	public void fasterLoadingTest() throws Exception {
		
		String eolCode = new String(Files.readAllBytes(Paths.get("data/blog.eol")));
		run(eolCode, true);
		
	}

	@Override
	public EPackage getEPackage() {
		return BlogPackage.eINSTANCE;
	}

}
