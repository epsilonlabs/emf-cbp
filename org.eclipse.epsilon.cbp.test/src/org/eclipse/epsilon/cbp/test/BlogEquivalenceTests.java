package org.eclipse.epsilon.cbp.test;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.blog.BlogPackage;
import org.junit.Test;

public class BlogEquivalenceTests extends XmiResourceEquivalenceTests {

	@Test
	public void addRatings() throws Exception {
		run("var p : new Post; p.ratings.add(1); p.ratings.add(2);", true);
	}
	
	@Test
	public void removeRatings() throws Exception {
		run("var p : new Post; p.ratings.add(1); p.ratings.add(2); p.ratings.remove(1);", true);		
	}
	
	@Override
	public EPackage getEPackage() {
		return BlogPackage.eINSTANCE;
	}

}
