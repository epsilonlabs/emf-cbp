package org.eclipse.epsilon.cbp.event;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.epsilon.cbp.impl.CBPResource;
import org.eclipse.epsilon.cbp.util.Changelog;

public class EventAdapter extends EContentAdapter
{
	//change log
	private final Changelog changelog;
	
	//class name (I dont know why it is needed)
	private final String classname = this.getClass().getSimpleName();
	
	//flag adapter_enabled
	private boolean adapter_enabled = true;
	
	
	//constructor
	public EventAdapter(Changelog aChangelog)
	{
		super(); 
		this.changelog = aChangelog;
	}
	
	@Override
	public void notifyChanged(Notification n)
	{
		super.notifyChanged(n);
		
		if(n.isTouch() || !adapter_enabled)
		{
			return;
		}
		
		switch(n.getEventType())
		{
			case Notification.ADD:
			{
				if(n.getNewValue() instanceof EObject)
				{
					if(n.getNotifier() instanceof CBPResource)
					{
					   changelog.addEvent(new AddEObjectsToResourceEvent(n));
					}
					else if(n.getNotifier() instanceof EObject)
					{
						changelog.addEvent(new AddToEReferenceEvent(n)); 
					} 
				}
				else if(n.getFeature() instanceof EAttribute)
				{
					changelog.addEvent(new AddToEAttributeEvent(n));
				}
				break;
			}
			case Notification.SET:
			{
				if(n.getNewValue() instanceof EObject)
				{
					if(n.getNotifier() instanceof CBPResource)
					{
					   changelog.addEvent(new AddEObjectsToResourceEvent(n));
					}
					else if(n.getNotifier() instanceof EObject)
					{
						changelog.addEvent(new AddToEReferenceEvent(n)); 
					} 
				}
				else if(n.getFeature() instanceof EAttribute)
				{
					changelog.addEvent(new AddToEAttributeEvent(n));
				}
				else if(n.getNewValue() == null)
				{
					if(n.getNotifier() instanceof CBPResource)
					{
					   changelog.addEvent(new RemoveFromResourceEvent(n));
					}
					else if(n.getNotifier() instanceof EObject)
					{
						changelog.addEvent(new RemoveFromEReferenceEvent(n)); 
					} 
				}
				break;
			}
			case Notification.ADD_MANY:
			{
				@SuppressWarnings("unchecked")
				List<Object> list =  (List<Object>) n.getNewValue();
				if(list.get(0) instanceof EObject)
				{
					if(n.getNotifier() instanceof CBPResource)
					{
					   changelog.addEvent(new AddEObjectsToResourceEvent(n));
					}
					else if(n.getNotifier() instanceof EObject)
					{
						changelog.addEvent(new AddToEReferenceEvent(n)); 
					} 
				}
				else if(n.getFeature() instanceof EAttribute)
				{
					changelog.addEvent(new AddToEAttributeEvent(n));
				}
				break;
			}
			case Notification.REMOVE:
			{
				if(n.getOldValue() instanceof EObject)
				{
					if(n.getNotifier() instanceof CBPResource)
					{
					   changelog.addEvent(new RemoveFromResourceEvent(n));
					}
					else if(n.getNotifier() instanceof EObject)
					{
						changelog.addEvent(new RemoveFromEReferenceEvent(n)); 
					} 
				}
				else if(n.getFeature() instanceof EAttribute)
				{
					changelog.addEvent(new RemoveFromEAttributeEvent(n));
				}
				break;
			}
			case Notification.REMOVE_MANY:
			{
				@SuppressWarnings("unchecked")
				List<Object> list =  (List<Object>) n.getOldValue();
				if(list.get(0) instanceof EObject)
				{
					if(n.getNotifier() instanceof CBPResource)
					{
					   changelog.addEvent(new RemoveFromResourceEvent(n));
					}
					else if(n.getNotifier() instanceof EObject)
					{
						changelog.addEvent(new RemoveFromEReferenceEvent(n)); 
					} 
				}
				else if(n.getFeature() instanceof EAttribute)
				{
					changelog.addEvent(new RemoveFromEAttributeEvent(n));
				}
				break;
			}
			default:
			{
				System.out.println(classname+"Unhandled notification!" +n.toString());
				System.exit(0);
				break;
			}
		}
	}
	
	public void setEnabled(boolean bool)
	{
		adapter_enabled = bool;
	}
	
	
	/* The following code (which allows subclass EContentAdapter to receive notifications across non containment 
	 * references was copied (almost, see setTarget) verbatim from : http://wiki.eclipse.org/EMF/Recipes#Recipe:_Subclass_EContentAdapter
	 * _to_receive_notifications_across_non-containment_references*/
	
	
	/**
     * By default, all cross document references are followed. Usually this is
     * not a great idea so this class can be subclassed to customize.
     * 
     * @param feature
     *      a cross document reference
     * @return whether the adapter should follow it
     */
    protected boolean shouldAdapt(EStructuralFeature feature)
    {
        return true;
    }
    
    @Override
    protected void setTarget(EObject target)
    {
        if(target.eAdapters().contains(this)) //fixes stack overflow on opposite ref
        	return;
        
        super.setTarget(target);
        for (EContentsEList.FeatureIterator<EObject> featureIterator = (EContentsEList.FeatureIterator<EObject>) target.eCrossReferences()
                                                                                                                       .iterator(); featureIterator.hasNext();)
        {
            Notifier notifier = featureIterator.next();
            EStructuralFeature feature = featureIterator.feature();
            if (shouldAdapt(feature))
            {
                addAdapter(notifier);
            }
        }
    }
	
    @Override
    protected void unsetTarget(EObject target)
    {
        super.unsetTarget(target);
        for (EContentsEList.FeatureIterator<EObject> featureIterator = (EContentsEList.FeatureIterator<EObject>) target.eCrossReferences()
                                                                                                                       .iterator(); featureIterator.hasNext();)
        {
            Notifier notifier = featureIterator.next();
            EStructuralFeature feature = featureIterator.feature();
            if (shouldAdapt(feature))
            {
                removeAdapter(notifier);
            }
        }
    }
    
    @Override
    protected void selfAdapt(Notification notification)
    {
        super.selfAdapt(notification);
        if (notification.getNotifier() instanceof EObject)
        {
            Object feature = notification.getFeature();
            if (feature instanceof EReference)
            {
                EReference eReference = (EReference) feature;
                if (!eReference.isContainment() && shouldAdapt(eReference))
                {
                    handleContainment(notification);
                }
            }
        }
    }
}
