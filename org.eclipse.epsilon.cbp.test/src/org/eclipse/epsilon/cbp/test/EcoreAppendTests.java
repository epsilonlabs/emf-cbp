package org.eclipse.epsilon.cbp.test;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Test;

public class EcoreAppendTests extends AppendTests {
	
	@Test
	public void testCreates() throws Exception {
		run("var c1 = new EClass;", "var c2 = new EClass;");
	}
	
	@Test
	public void testCreatesAndDelete() throws Exception {
		run("var c1 = new EClass;", "delete EClass.all;", "var c2 = new EClass;");
	}
	
	@Override
	public EPackage getEPackage() {
		return EcorePackage.eINSTANCE;
	}
	
}
