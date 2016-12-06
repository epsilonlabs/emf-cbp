package org.eclipse.epsilon.cbp.context;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.SimpleType;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class CBPContext {

	protected Changelog changelog;
	
	protected TObjectIntMap<String> textSimpleTypesMap = new TObjectIntHashMap<String>(2);
	
	protected TObjectIntMap<String> commonSimpleTypesMap = new TObjectIntHashMap<String>(13);

	private boolean resume = false;
	
	//eobject to id map
	protected TObjectIntMap<EObject> eObjToIDMap = new TObjectIntHashMap<EObject>();

	//current id, increases when an object is encountered
	protected static int current_id = 0; 

	public CBPContext()
	{
		populatecommonSimpleTypesMap();
		populateTextSimpleTypesMap();
	}
	
	public boolean isResume()
	{
		return resume;
	}
	
	public void setResume(boolean resume) {
		this.resume = resume;
	}
	
	public void setChangelog(Changelog changelog) {
		this.changelog = changelog;
	}
	
	private void populateTextSimpleTypesMap()
	{
    	textSimpleTypesMap.put("EString", SimpleType.TEXT_SIMPLE_TYPE_ESTRING);
    	textSimpleTypesMap.put("EStringObject", SimpleType.TEXT_SIMPLE_TYPE_ESTRING);
	}
	
	private void populatecommonSimpleTypesMap()
	{
		commonSimpleTypesMap.put("EInt", SimpleType.SIMPLE_TYPE_INT);
		commonSimpleTypesMap.put("EIntegerObject", SimpleType.SIMPLE_TYPE_INT);
		commonSimpleTypesMap.put("EBoolean", SimpleType.SIMPLE_TYPE_BOOLEAN);
		commonSimpleTypesMap.put("EBooleanObject", SimpleType.SIMPLE_TYPE_BOOLEAN);
		commonSimpleTypesMap.put("EFloat", SimpleType.SIMPLE_TYPE_FLOAT);
		commonSimpleTypesMap.put("EFloatObject", SimpleType.SIMPLE_TYPE_FLOAT);
		commonSimpleTypesMap.put("EDouble", SimpleType.SIMPLE_TYPE_DOUBLE);
		commonSimpleTypesMap.put("EDoubleObject", SimpleType.SIMPLE_TYPE_DOUBLE);
		commonSimpleTypesMap.put("EShort", SimpleType.SIMPLE_TYPE_SHORT);
		commonSimpleTypesMap.put("EShortObject", SimpleType.SIMPLE_TYPE_SHORT);
		commonSimpleTypesMap.put("ELong", SimpleType.SIMPLE_TYPE_LONG);
		commonSimpleTypesMap.put("ELongObject", SimpleType.SIMPLE_TYPE_LONG);
		commonSimpleTypesMap.put("EChar", SimpleType.SIMPLE_TYPE_CHAR);
	}
	
	public TObjectIntMap<String> getCommonSimpleTypesMap()
	{
		return commonSimpleTypesMap;
	}
	
	public TObjectIntMap<String> getTextSimpleTypesMap()
	{
		return textSimpleTypesMap;
	}

	
	//add object to map, return false if object exists
	public boolean addObjectToMap(EObject obj)
	{
		//if obj is not in the map
		if(!eObjToIDMap.containsKey(obj))
		{
			//put current id 
			eObjToIDMap.put(obj, current_id);
			
			//increase current id
			current_id = current_id +1;
			return true;
		}
		return false;
	}
	
	//add object to map with a specific id
	public boolean addObjectToMap(EObject obj, int id)
	{
		//if obj is not in the map
		if(!eObjToIDMap.containsKey(obj))
		{
			//put id
			eObjToIDMap.put(obj, id);
			
			//if current id is less than id, set current id
			if(id >= current_id)
			{
				current_id = id + 1;
			}
			return true;
		}
		return false;
	}
	
	//get the object id based on obj
	public int getObjectId(EObject obj)
	{
		if(!eObjToIDMap.containsKey(obj))
		{
			System.out.println("ChangeLog: search returned false");
			System.exit(0);
		}
		return eObjToIDMap.get(obj);
	}
	
	public Changelog getChangelog() {
		return changelog;
	}
}
