package org.eclipse.epsilon.cbp.io;

import java.io.Closeable;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.epsilon.cbp.context.PersistenceManager;
import org.eclipse.epsilon.cbp.event.AddEObjectsToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.SimpleType;

import gnu.trove.map.TObjectIntMap;

public abstract class AbstractCBPSerialiser {

	//event list
    protected List<Event> eventList;
    
    //persistence manager
	protected PersistenceManager manager;
	
	//change log
	protected Changelog changelog; 
	
	//epackage element map
	protected ModelElementIDMap ePackageElementsNamesMap;
	
	//common simple type name
	protected TObjectIntMap<String> commonsimpleTypeNameMap;
	
	//tet simple type name
	protected TObjectIntMap<String> textSimpleTypeNameMap;

	
	public abstract void serialise(Map<?,?> options) throws Exception;
	
	public abstract String getFormatID();
	public abstract double getVersion();
	
	protected abstract void serialiseHeader(Closeable out); 

	
	protected abstract void handleAddToResourceEvent(AddEObjectsToResourceEvent e, Closeable out);
	protected abstract void handleRemoveFromResourceEvent(RemoveFromResourceEvent e, Closeable out);

	protected abstract void handleSetEAttributeEvent(EAttributeEvent e, Closeable out);
	protected abstract void handleAddToEAttributeEvent(EAttributeEvent e, Closeable out);
	
	protected abstract void handleSetEReferenceEvent(SetEReferenceEvent e, Closeable out);
	protected abstract void handleAddToEReferenceEvent(AddToEReferenceEvent e, Closeable out);
	
	protected abstract void handleRemoveFromAttributeEvent(EAttributeEvent e, Closeable out);
	protected abstract void handleRemoveFromEReferenceEvent(RemoveFromEReferenceEvent e, Closeable out);
	
	protected int getTypeID(EDataType type)
	{
		if(commonsimpleTypeNameMap.containsKey(type.getName()))
    	{
			return commonsimpleTypeNameMap.get(type.getName());
    	}
		else if(textSimpleTypeNameMap.containsKey(type.getName()))
		{
			return textSimpleTypeNameMap.get(type.getName());
		}
    	
    	return SimpleType.COMPLEX_TYPE;
    }
}
