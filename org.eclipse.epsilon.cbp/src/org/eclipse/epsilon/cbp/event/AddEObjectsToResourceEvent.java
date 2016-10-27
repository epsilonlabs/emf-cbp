package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;

public class AddEObjectsToResourceEvent extends ResourceEvent
{
	public AddEObjectsToResourceEvent(Notification n)
	{
		super(Event.ADD_EOBJ_TO_RESOURCE,n);
	}
	
	public AddEObjectsToResourceEvent(Object addedEObjects)
	{
		super(Event.ADD_EOBJ_TO_RESOURCE, addedEObjects);
	}
}
