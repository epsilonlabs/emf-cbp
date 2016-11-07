package org.eclipse.epsilon.cbp.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.event.Event;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class Changelog 
{
	//event list
	private List<Event> eventList;
	
	//eobject to id map
	private TObjectIntMap<EObject> eObjToIDMap = new TObjectIntHashMap<EObject>();
	
	//class name
	private String classname = this.getClass().getSimpleName();
	
	//current id, increases when an object is encountered
	private static int current_id = 0; 
	
	public Changelog()
	{
		eventList = new ArrayList<Event>();
	}
	
	//clear to reset everything
	public void clear()
	{
		eventList.clear();
		eObjToIDMap.clear();
		current_id = 0;
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
			System.out.println(classname+" search returned false");
			System.exit(0);
		}
		return eObjToIDMap.get(obj);
	}

	public void addEvent(Event e)
	{
		eventList.add(e);
	}
	
	public void removeEvent(Event e)
	{
		eventList.remove(e);
	}
	
	public void clearEvents()
	{
		eventList.clear();
	}
	
	public List<Event> getEventsList()
	{
		return eventList;
	}
	
	public void debug()
	{
		System.out.println(classname+" DEBUG!");
		for(Event e : eventList)
		{
			System.out.println(e.getEventType() + " " + e.getClass().getSimpleName());
		}
	}
}
