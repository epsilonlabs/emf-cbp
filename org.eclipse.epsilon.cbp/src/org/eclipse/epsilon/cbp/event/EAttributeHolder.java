package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;

public class EAttributeHolder {
	
	protected HashMap<EAttribute,List<Object>> eAttributeToObjectValuesMap = new HashMap<EAttribute,List<Object>>();
	
	public boolean removeObject(EAttribute attr, Object obj)
	{
		if(!eAttributeToObjectValuesMap.containsKey(attr)) //tbr
		{
			return false;
		}
		return eAttributeToObjectValuesMap.get(attr).remove(obj);
	}
	
	public void addObjects(EAttribute attr, List<Object> objList)
	{
		if(!eAttributeToObjectValuesMap.containsKey(attr))
		{
			eAttributeToObjectValuesMap.put(attr, new ArrayList<Object>());
		}
		
		if(!attr.isMany())
		{
			eAttributeToObjectValuesMap.get(attr).add(0,objList.get(0));
		}
		else
		{
			eAttributeToObjectValuesMap.get(attr).addAll(objList);
		}	
	}
	
	public Map<EAttribute,List<Object>> getEAttributeToObjectValuesMap()
	{
		return eAttributeToObjectValuesMap;
	}	
}
