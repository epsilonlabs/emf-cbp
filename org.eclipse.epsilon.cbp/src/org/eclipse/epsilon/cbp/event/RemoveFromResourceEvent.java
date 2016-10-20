package org.eclipse.epsilon.cbp.event;

import org.eclipse.emf.common.notify.Notification;

public class RemoveFromResourceEvent extends ResourceEvent 
{
	public RemoveFromResourceEvent(Notification n)
	{
		super(Event.REMOVE_EOBJ_FROM_RESOURCE,n);
	}
	
	public RemoveFromResourceEvent(Object removedEObjects)
	{
		super(Event.REMOVE_EOBJ_FROM_RESOURCE,removedEObjects);
	}
}
