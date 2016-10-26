package org.eclipse.epsilon.cbp.tests;

import java.util.HashMap;

public class EObjectToIDTests {

	public static void main(String[] args) {
		HashMap<Object, Integer> map = new HashMap<Object, Integer>();
		
		
		String a = "Hello";
		int i = 0;
		map.put(a, i);
		
		
		System.out.println(map.get(a));
		
		Integer x = map.get(a);
		x++;
		map.put(a, x);
		
		System.out.println(map.get(a));
		
	}
}
