package org.eclipse.epsilon.cbp.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.blog.BlogPackage;
import org.eclipse.epsilon.cbp.test.employee.EmployeePackage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FasterLoadingTests extends XmiResourceEquivalenceTests {

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { { "cbpxml" } });

	}

	public FasterLoadingTests(String extension) {
		super(extension);
	}

	@Test
	public void fasterLoadingTest() throws Exception {
		
		String eolCode = new String(Files.readAllBytes(Paths.get("data/reset_attribute.eol")));
		run(eolCode, true);
		
	}
	
	@Test
	public void employeeTest() throws Exception {
		String eolCode = new String(Files.readAllBytes(Paths.get("data/employee.eol")));
		run(eolCode, true);
		
	}
	
	@Test
	public void justTest() throws Exception {
		
		String eolCode = new String("var blog = new Blog; var post1 = new Post; var post2 = new Post;"
				+ "blog.posts.add(post1); blog.posts.add(post2); blog.posts.remove(post1); delete post2;");
		run(eolCode, true);
		
	}

	@Override
	public EPackage getEPackage() {
		//return BlogPackage.eINSTANCE;
		return EmployeePackage.eINSTANCE;
	}

}
