package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.junit.Test;

public class CBPComparisonTest {

	@Test
	public void testGetComparisonList() {

		List<String> leftText = Arrays.asList("A", "B", "B", "A");
		List<String> rightText = Arrays.asList("A", "D", "A", "C");

		CBPComparison myers = new CBPComparison();
		List<String> result = myers.diff(leftText, rightText);
		
		// System.out.println(result);
		System.out.println("Done!");

		assertEquals(true, false);

	}
}
