package org.eclipse.epsilon.cbp.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Maps the names of EClasses and EStructuralFeatures (within an EPackage) to 
 * unique numerical ids.
 */

public class ModelElementIDMap
{
	private BiMap<String, Integer> namesMap = HashBiMap.create();
	
	int counter = 0;
	
	public void addName(String name)
	{
		if(namesMap.get(name) == null)
		{
			namesMap.put(name, counter);
			counter = counter + 1;
		}
	}
	
	public int getID(String name)
	{
		return namesMap.get(name);
	}
	
	public String getName(int id)
	{
		return namesMap.inverse().get(id);
	}
}
