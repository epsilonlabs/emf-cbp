package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

public abstract class Event {
	
	private int eventType;
	
	public static final int ADD_EOBJ_TO_RESOURCE = 0;
	public static final int ADD_TO_EREFERENCE = 1;
	public static final int ADD_TO_EATTRIBUTE = 2;
	public static final int REMOVE_EOBJ_FROM_RESOURCE = 3;
	public static final int REMOVE_FROM_EREFERENCE = 4;
	public static final int REMOVE_FROM_EATTRIBUTE = 5;

	protected List<EObject> eObjectList = new ArrayList<EObject>();

	public Event(int eventType)
	{
		this.eventType = eventType;
	}
	
	public int getEventType()
	{
		return eventType;
	}
	
	public List<EObject> getEObjectList() {
		return eObjectList;
	}
}
