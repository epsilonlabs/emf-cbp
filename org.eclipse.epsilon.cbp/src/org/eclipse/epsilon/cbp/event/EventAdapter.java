package org.eclipse.epsilon.cbp.event;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.epsilon.cbp.util.Changelog;

public class EventAdapter extends EContentAdapter
{
	//change log
	protected final Changelog changelog;
	
	//flag adapter_enabled
	private boolean adapter_enabled = true;
	
	//constructor
	public EventAdapter(Changelog aChangelog)
	{
		super(); 
		this.changelog = aChangelog;
	}
	
	public void showLog()
	{
		changelog.printLog();
	}
	
	@Override
	public void notifyChanged(Notification n)
	{
		super.notifyChanged(n);
		
		//if n is touch and no adapter enabled, return
		if(n.isTouch() || !adapter_enabled)
		{
			return;
		}
		
		//switch by event type
		switch(n.getEventType())
		{
			//if event is ADD
			case Notification.ADD:
			{
				//if new value is EObject
				if(n.getNewValue() instanceof EObject)
				{
					//if Notifier is resource
					if(n.getNotifier() instanceof Resource)
					{
						//create add to resource event
					   changelog.addEvent(new AddEObjectsToResourceEvent(n));
					}
					//if notifier is eobject
					else if(n.getNotifier() instanceof EObject)
					{
						//create add to reference event
						changelog.addEvent(new AddToEReferenceEvent(n)); 
					} 
				}
				//else if new value is eattribute
				else if(n.getFeature() instanceof EAttribute)
				{
					//create add to attribute event
					changelog.addEvent(new AddToEAttributeEvent(n));
				}
				break;
			}
			//if event is SET
			case Notification.SET:
			{
				//if new valueis EObject
				if(n.getNewValue() instanceof EObject)
				{
					//if notifier is resource
					if(n.getNotifier() instanceof Resource)
					{
						//create add to resource event
					   changelog.addEvent(new AddEObjectsToResourceEvent(n));
					}
					//if notifier is eboject
					else if(n.getNotifier() instanceof EObject)
					{
						//create set ereference event
						changelog.addEvent(new SetEReferenceEvent(n)); 
					} 
				}
				//if feature is eattribte
				else if(n.getFeature() instanceof EAttribute)
				{
					//create add to eattribute event
					changelog.addEvent(new SetEAttributeEvent(n));
				}
				//if new value is null
				else if(n.getNewValue() == null)
				{
					//if notifier is resource
					if(n.getNotifier() instanceof Resource)
					{
						//create remove from resource event
					   changelog.addEvent(new RemoveFromResourceEvent(n));
					}
					//if notifier is eobject
					else if(n.getNotifier() instanceof EObject)
					{
						//create remove from ereference event
						changelog.addEvent(new RemoveFromEReferenceEvent(n)); 
					} 
				}
				break;
			}
			
			//if event is add many
			case Notification.ADD_MANY:
			{
				@SuppressWarnings("unchecked")
				List<Object> list =  (List<Object>) n.getNewValue();
				if(list.get(0) instanceof EObject)
				{
					//if notifier is resource
					if(n.getNotifier() instanceof Resource)
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
					if(n.getNotifier() instanceof Resource)
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
					if(n.getNotifier() instanceof Resource)
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
				System.out.println("EventAdapter: Unhandled notification!" +n.toString());
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
    
    public Changelog getChangelog() {
		return changelog;
	}
}
