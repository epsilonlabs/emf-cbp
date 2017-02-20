package org.eclipse.epsilon.cbp.test;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.test.blog.BlogPackage;
import org.junit.Test;

public class BlogEquivalenceTests extends XmiResourceEquivalenceTests {
	
	@Test
	public void addRatings() throws Exception {
		run("var p : new Post; p.ratings.add(1); p.ratings.add(2);");
	}
	
	@Test
	public void addRatingAtIndex() throws Exception {
		debug("var p : new Post; p.ratings.add(1); p.ratings.add(0, 2);");
	}
	
	@Test
	public void addRatingsAtIndex() throws Exception {
		run("var p : new Post; p.ratings.add(1); p.ratings.addAll(0, Sequence{2, 3});");
	}
	
	@Test
	public void setPostType() throws Exception {
		run("var p : new Post; p.type = PostType#Sticky;");		
	}
	
	@Test
	public void unsetPostType() throws Exception {
		run("var p : new Post; p.type = PostType#Sticky; p.type = null;");		
	}
	
	@Test
	public void removeRatings() throws Exception {
		run("var p : new Post; p.ratings.add(1); p.ratings.add(2); p.ratings.remove(1);");		
	}
	
	@Test
	public void addCommentFlag() throws Exception {
		run("var c : new Comment; c.flags.add(Flag#Offensive);");
	}

	@Test
	public void removeCommentFlag() throws Exception {
		run("var c : new Comment; c.flags.add(Flag#Offensive); c.flags.remove(Flag#Offensive);");
	}	
	
	@Override
	public EPackage getEPackage() {
		return BlogPackage.eINSTANCE;
	}

}
