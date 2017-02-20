package org.eclipse.epsilon.cbp.test;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Test;

public class EcorePerformanceTests extends PerformanceTests {
	
	@Test
	public void fiveClasses() throws Exception {
		for (int i=0;i<10;i++)
		run("for (i in 1.to(5000)) { var c : new EClass; var r : new EReference; c.eStructuralFeatures.add(r); r.eType = c;}", "for (i in 1.to(10)) { new EClass; }", "for (i in 1.to(10)) { new EClass; }");
	}
	
	@Override
	public EPackage getEPackage() {
		return EcorePackage.eINSTANCE;
	}
	
	
}
