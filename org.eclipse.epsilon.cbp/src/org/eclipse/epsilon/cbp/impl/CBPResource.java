package org.eclipse.epsilon.cbp.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;
import org.eclipse.epsilon.cbp.util.Changelog;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public abstract class CBPResource extends ResourceImpl
{

	protected Changelog changelog = new Changelog();
	

	private boolean resume = false;
	
	//eobject to id map
	protected TObjectIntMap<EObject> eObjToIDMap = new TObjectIntHashMap<EObject>();

	//current id, increases when an object is encountered
	protected static int current_id = 0; 

	public CBPResource(URI uri)
	{
		super(uri);
	}
	
	public CBPResource()
	{
	}
	
	public abstract AbstractCBPSerialiser getSerialiser();
	public abstract AbstractCBPDeserialiser getDeserialiser();
	
	
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
