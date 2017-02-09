package org.eclipse.epsilon.cbp.test.equivalence;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Test;

public class EcoreEquivalenceTests extends XmiResourceEquivalenceTests {
	
	@Test
	public void createAndDelete() throws Exception {
		run ("var c : new EClass; delete c;");
	}
	
	@Test
	public void createAndDeleteNested() throws Exception {
		run ("var c : new EClass; var a : new EAttribute; c.eStructuralFeatures.add(a); delete c;");
	}
	
	@Test
	public void testEClassAndEReference() throws Exception {
		run("var c : new EClass; var r : new EReference; r.name = 'ref'; c.name = 'c'; r.eType = c;");
	}
	
	@Test
	public void testEPackageAndEClass() throws Exception {
		run("var c : new EClass; var p: new EPackage; p.eClassifiers.add(c);");
	}
	
	@Test
	public void testSuperclass() throws Exception {
		run("var c : new EClass; var s : new EClass; c.eSuperTypes.add(s);");
	}
	
	@Override
	public EPackage getEPackage() {
		return EcorePackage.eINSTANCE;
	}

}
