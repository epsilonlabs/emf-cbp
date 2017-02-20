package org.eclipse.epsilon.cbp.test;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Test;

public class EcoreEquivalenceTests extends XmiResourceEquivalenceTests {
	
	/*
	@Test
	public void annotation() throws Exception {
		run("var a : new EAnnotation; a.details.put('foo', 'bar');", true);
	}*/
	
	@Test
	public void eOpposites() throws Exception {
		run("var r1 = new EReference; var r2 = new EReference; r1.eOpposite = r2; r2.eOpposite = r1;");
	}
	
	@Test
	public void createAndDelete() throws Exception {
		run("var c : new EClass; delete c;");
	}
	
	@Test
	public void moveEClassifiers() throws Exception {
		run("var p1 = new EPackage; var p2 = new EPackage; p1.name = 'p1'; p2.name = 'p2'; var c : new EClass; p1.eClassifiers.add(c); p2.eClassifiers.add(c);", true);
	}
	
	@Test
	public void createAndDeleteNested() throws Exception {
		run("var c : new EClass; var a : new EAttribute; c.eStructuralFeatures.add(a); delete c;");
	}
	
	@Test
	public void setEType() throws Exception {
		run("var c : new EClass; var r : new EReference; r.eType = c;");
	}
	
	@Test
	public void addToEClassifiers() throws Exception {
		run("var c : new EClass; var p: new EPackage; p.eClassifiers.add(c);");
	}
	 
	@Test
	public void addToEClassifiersAtIndex() throws Exception {
		run("var c1 = new EClass; c1.name = 'c1'; var c2 = new EClass; c2.name='c2'; var p: new EPackage; p.eClassifiers.add(c1); p.eClassifiers.add(0, c2);");
	}
	
	@Test
	public void addManyToEClassifiersAtIndex() throws Exception {
		run("var c1 = new EClass; c1.name = 'c1'; var c2 = new EClass; c2.name='c2'; var c3 = new EClass; c3.name='c3'; var p: new EPackage; p.eClassifiers.add(c1); p.eClassifiers.addAll(0, Sequence{c2, c3});");
	}
	
	@Test
	public void addToESuperTypes() throws Exception {
		run("var c : new EClass; var s : new EClass; c.eSuperTypes.add(s);");
	}
	
	@Test
	public void unsetEType() throws Exception {
		run("var c : new EClass; var a: new EReference; a.eType=c; a.eUnset(a.eClass().getEStructuralFeature('eType'));");
	}
	
	@Test
	public void unsetESuperTypes() throws Exception {
		run("var c1 : new EClass; var c2: new EClass; c1.eSuperTypes.add(c2); c1.eUnset(c1.eClass().getEStructuralFeature('eSuperTypes'));");
	}
	
	@Test
	public void nullifyEType() throws Exception {
		run("var c : new EClass; var a: new EReference; a.eType=c; a.eType = null;");
	}
	
	@Override
	public EPackage getEPackage() {
		return EcorePackage.eINSTANCE;
	}

}
