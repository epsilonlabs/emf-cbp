
package org.eclipse.epsilon.cbp.driver;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.event.AddEObjectsToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.Changelog;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EReferenceEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.ResourceEvent;

public class CBPBinarySerializer 
{
	private final String classname = this.getClass().getSimpleName();
	private final String FORMAT_ID = "CBP_BIN"; 
	private final int FORMAT_VERSION = 1;
	
	private final List<Event> eventList;
    
    private final PersistenceManager manager;
    
    private final Changelog changelog; 
    
    private final EPackageElementsNamesMap ePackageElementsNamesMap;
    
    private final HashMap<String, Integer> commonSimpleTypeNameMap;
    
    public CBPBinarySerializer(PersistenceManager manager, Changelog changelog,EPackageElementsNamesMap 
    		ePackageElementsNamesMap)
    {
    	this.manager =  manager;
        this.changelog = changelog;
        this.ePackageElementsNamesMap = ePackageElementsNamesMap;
        
        this.eventList = manager.getChangelog().getEventsList();
        
        this.commonSimpleTypeNameMap = manager.getCommonSimpleTypesMap();
    }
    
    public void save(Map<?,?> options) throws IOException
    {
    	if(eventList.isEmpty())
    		return;
    	
    	 OutputStream  outputStream = new BufferedOutputStream
        		(new FileOutputStream(manager.getURI().path(), manager.isResume()));
    
        //if we're not in resume mode, serialise initial entry
        if(!manager.isResume())
            writeInitialRecord(outputStream);
        
        for(Event e : eventList)
        {
        	switch(e.getEventType())
        	{
        	case Event.ADD_EOBJ_TO_RESOURCE:
        		writeEObjectAdditionEvent((AddEObjectsToResourceEvent)e, outputStream);
        		break;
        	case Event.ADD_TO_EREFERENCE:
        		writeEObjectAdditionEvent((AddToEReferenceEvent)e,outputStream);
        		break;
        	case Event.REMOVE_EOBJ_FROM_RESOURCE:
        		writeEObjectRemovalEvent((RemoveFromResourceEvent)e,outputStream);
        		break;
        	case Event.REMOVE_FROM_EREFERENCE:
        		writeEObjectRemovalEvent((RemoveFromEReferenceEvent)e, outputStream);
        		break;
        	case Event.ADD_TO_EATTRIBUTE:
        	case Event.REMOVE_FROM_EATTRIBUTE:
        		writeEAttributeEvent((EAttributeEvent)e,outputStream);
        		break;
        	}
        }
        
        changelog.clearEvents();
        
      	outputStream.close();
      	
      	manager.setResume(true);
	
    }
    
    private void writeEAttributeEvent(EAttributeEvent e, OutputStream out) throws IOException
    {
    	EDataType type = e.getEAttribute().getEAttributeType();
    	
    	if(type instanceof EEnum)
    	{
    		writeComplexEAttributes(e,out);
    		return;
    	}
    	
    	switch(getTypeID(type))
    	{
    	case PersistenceManager.SIMPLE_TYPE_INT:
    		writePrimitiveEAttributes(e,out, PersistenceManager.SIMPLE_TYPE_INT);
    		return;
    	case PersistenceManager.SIMPLE_TYPE_SHORT:
    		writePrimitiveEAttributes(e,out, PersistenceManager.SIMPLE_TYPE_SHORT);
    		return;
    	case PersistenceManager.SIMPLE_TYPE_LONG:
    		writePrimitiveEAttributes(e,out,PersistenceManager.SIMPLE_TYPE_LONG);
    		return;
    	case PersistenceManager.SIMPLE_TYPE_FLOAT:
    		writePrimitiveEAttributes(e,out, PersistenceManager.SIMPLE_TYPE_FLOAT);
    		return;
    	case PersistenceManager.SIMPLE_TYPE_DOUBLE:
    		writePrimitiveEAttributes(e,out, PersistenceManager.SIMPLE_TYPE_DOUBLE);
    		return;
    	case PersistenceManager.SIMPLE_TYPE_BOOLEAN:
    		writePrimitiveEAttributes(e,out, PersistenceManager.SIMPLE_TYPE_BOOLEAN);
    		return;
    	case PersistenceManager.SIMPLE_TYPE_CHAR:
    		writePrimitiveEAttributes(e,out, PersistenceManager.SIMPLE_TYPE_CHAR);
    		return;
    	case PersistenceManager.COMPLEX_TYPE:
    		writeComplexEAttributes(e,out);
    		return;
    	}
    }
    
    private void writePrimitiveEAttributes(EAttributeEvent e, OutputStream out, int primitiveType) throws IOException
    {
    	EObject focusObject = e.getFocusObject();
    	
    	EAttribute eAttribute = e.getEAttribute();
    	
    	List<Object> eAttributeValuesList = e.getEAttributeValuesList();
    	
    	int serializationType = PersistenceManager.SET_EOBJECT_PRIMITIVE_EATTRIBUTE_VALUES;
    	
    	if(e.getEventType() == Event.REMOVE_FROM_EATTRIBUTE)
    		serializationType = PersistenceManager.UNSET_EOBJECT_PRIMITIVE_EATTRIBUTE_VALUES;
    	
        writePrimitive(out,serializationType);
        writePrimitive(out,changelog.getObjectId(focusObject));
        writePrimitive(out,ePackageElementsNamesMap.getID(eAttribute.getName()));
        writePrimitive(out,eAttributeValuesList.size());
        
        int nullCounter = 0;
        
        for(Object obj : eAttributeValuesList)
        {
        	if(obj == null)
        	{
        		nullCounter++;
        		continue;
        	}
        	
        	writePrimitive(out,primitiveType,obj);
        }
        
        if(nullCounter > 0) // for obj with null values, serialise as complex types
        {
        	String[] nullsArray = new String[nullCounter];
        	
        	Arrays.fill(nullsArray,manager.NULL_STRING);
        	
        	List<Object> nullList = new ArrayList<Object>(Arrays.asList(nullsArray));
        	
        	int complexSerializationType = PersistenceManager.SET_EOBJECT_COMPLEX_EATTRIBUTE_VALUES;
        	
        	if(serializationType== PersistenceManager.UNSET_EOBJECT_PRIMITIVE_EATTRIBUTE_VALUES)
        		complexSerializationType = PersistenceManager.UNSET_EOBJECT_COMPLEX_EATTRIBUTE_VALUES;
        	
        	writeComplexEAttributes(focusObject,eAttribute,nullList,complexSerializationType,out);
        }
    }
    
    private void writeComplexEAttributes(EObject focusObject, EAttribute eAttribute,List<Object> eAttributeValuesList,int serializationType,
    		OutputStream out) throws IOException
    {
    	
    	EDataType eDataType = eAttribute.getEAttributeType();
  
    	writePrimitive(out,serializationType);
    	writePrimitive(out,changelog.getObjectId(focusObject));
    	writePrimitive(out,ePackageElementsNamesMap.getID(eAttribute.getName()));
    	writePrimitive(out,eAttributeValuesList.size());
    	
    	if(eDataType.getName().equals("EString"))
    	{
    		for(Object obj : eAttributeValuesList)
    		{
    			if(obj == null)
    				obj = manager.NULL_STRING;
    			
    			writeString(out,(String)obj);
    		}
    	}
    	else
    	{
    		for(Object obj : eAttributeValuesList)
    		{
    			String valueString = EcoreUtil.convertToString(eDataType, obj);
    			
    			if(valueString == null)
    				valueString = manager.NULL_STRING;
    			
    			writeString(out,valueString);
    		}
    	}
    }
    
    private void writeComplexEAttributes(EAttributeEvent e,OutputStream out) throws IOException
    {
    	int serializationType = PersistenceManager.SET_EOBJECT_COMPLEX_EATTRIBUTE_VALUES;
    	
    	if(e.getEventType() == Event.REMOVE_FROM_EATTRIBUTE)
    		serializationType = PersistenceManager.UNSET_EOBJECT_COMPLEX_EATTRIBUTE_VALUES;
    	
    	writeComplexEAttributes(e.getFocusObject(),e.getEAttribute(),e.getEAttributeValuesList(),serializationType,out);
    }
    
    private int getTypeID(EDataType type)
    {
    	if(commonSimpleTypeNameMap.containsKey(type.getName()))
    	{
    		return commonSimpleTypeNameMap.get(type.getName());
    	}
    	return PersistenceManager.COMPLEX_TYPE;
    }
    
    private void writeEObjectAdditionEvent(AddToEReferenceEvent e, OutputStream out) throws IOException
    {
        writeEObjectAdditionEvent(e.getEObjectList(),e.getFocusObject(),e.getEReference(),false,out);
    }
    
    private void writeEObjectAdditionEvent(AddEObjectsToResourceEvent e, OutputStream out) throws IOException
    {
        writeEObjectAdditionEvent(e.getEObjectList(),null,null,true,out);
    }
    
    private void writeEObjectAdditionEvent(List<EObject> eObjectsList,EObject focusObject, 
            EReference eReference,boolean isAddToResource, OutputStream out) throws IOException
    {
    	ArrayList<Integer> eObjectsToAddList = new ArrayList<Integer>();
    	ArrayList<Integer> eObjectsToCreateList = new ArrayList<Integer>();
    	
    	for(EObject obj : eObjectsList)
    	{
    		if(changelog.addObjectToMap(obj))
    		{
    			eObjectsToCreateList.add(ePackageElementsNamesMap.getID(obj.eClass().getName()));
    			eObjectsToCreateList.add(changelog.getObjectId(obj));
    		}
    		else
    		{
    			eObjectsToAddList.add(changelog.getObjectId(obj));
    		}
    	}
    		
    	if(isAddToResource) 
    	{
    		if(!eObjectsToCreateList.isEmpty()) //CREATE_AND_ADD_TO_RESOURCE 
    		{
    			writePrimitive(out,PersistenceManager.CREATE_AND_ADD_EOBJECTS_TO_RESOURCE);
    			writePrimitive(out,eObjectsToCreateList.size());
    			
    			for(Iterator<Integer> it = eObjectsToCreateList.iterator(); it.hasNext();)
    			{
    				writePrimitive(out,it.next());
    			}
    			eObjectsToCreateList.clear();
    		}
    		if(!eObjectsToAddList.isEmpty()) //ADD TO RESOURCE
    		{
    			writePrimitive(out, PersistenceManager.ADD_EOBJECTS_TO_RESOURCE);
    			writePrimitive(out,eObjectsToAddList.size());
    			
    			for(Iterator<Integer> it = eObjectsToAddList.iterator(); it.hasNext();)
    			{
    				writePrimitive(out,it.next());
    			}
    			eObjectsToAddList.clear();
    		}
    	}
    	else 
    	{
    		if(!eObjectsToCreateList.isEmpty())//CREATE_AND_SET_REF_VALUE
    		{
    			writePrimitive(out,PersistenceManager.CREATE_EOBJECTS_AND_SET_EREFERENCE_VALUES);
    			writePrimitive(out,changelog.getObjectId(focusObject));
    			writePrimitive(out,ePackageElementsNamesMap.getID(eReference.getName()));
    			writePrimitive(out,eObjectsToCreateList.size());
    			
    			for(Iterator<Integer> it = eObjectsToCreateList.iterator(); it.hasNext();)
    			{
    				writePrimitive(out,it.next());
    			}
    		}
    		if(!eObjectsToAddList.isEmpty()) //SET_REFERENCE_VALUE
    		{
    			writePrimitive(out,PersistenceManager.SET_EOBJECT_EREFERENCE_VALUES);
    			writePrimitive(out,changelog.getObjectId(focusObject));
    			writePrimitive(out,ePackageElementsNamesMap.getID(eReference.getName()));
    			writePrimitive(out,eObjectsToAddList.size());
    			
    			for(Iterator<Integer> it = eObjectsToAddList.iterator(); it.hasNext();)
    			{
    				writePrimitive(out,it.next());
    			}
    		}
    	}
    }

    private void writeEObjectRemovalEvent(RemoveFromEReferenceEvent e, OutputStream out) throws IOException
    {
        writeEObjectRemovalEvent(e.getEObjectList(),e.getFocusObject(),e.getEReference(),false,out);
    }
    
    private void writeEObjectRemovalEvent(RemoveFromResourceEvent e, OutputStream out) throws IOException
    {
        writeEObjectRemovalEvent(e.getEObjectList(),null,null,true,out);
    }
    
    private void writeEObjectRemovalEvent(List<EObject> eObjectsList,EObject focusObject, 
            EReference eReference,boolean isRemoveFromResource, OutputStream out) throws IOException
    {
    	//List<EObject> removed_obj_list = e.getEObjectList();
    	
    	if(isRemoveFromResource)
    	{
    		writePrimitive(out, PersistenceManager.REMOVE_EOBJECTS_FROM_RESOURCE);
    		writePrimitive(out,eObjectsList.size());
    		
    		for(EObject obj : eObjectsList)
    		{
    			writePrimitive(out,changelog.getObjectId(obj));
    		}
    	}
    	else 
    	{
    		
    		
    		writePrimitive(out,PersistenceManager.UNSET_EOBJECT_EREFERENCE_VALUES);
    		writePrimitive(out,changelog.getObjectId(focusObject));
    		writePrimitive(out,ePackageElementsNamesMap.getID(eReference.getName()));
    		writePrimitive(out,eObjectsList.size());
    		
    		for(EObject obj: eObjectsList)
    		{
    			writePrimitive(out,changelog.getObjectId(obj));
    		}
    	}
    }
	private void writeInitialRecord(OutputStream out) throws IOException 
	{
		EObject obj = null;
		Event e = eventList.get(0);
		
		if(e instanceof EReferenceEvent)
		{
			obj = ((EReferenceEvent)e).getEObjectList().get(0);
		}
		else if(e instanceof ResourceEvent)
		{
			obj = ((ResourceEvent)e).getEObjectList().get(0);
		}
		else //throw tantrum
		{
			try 
			{
				System.out.println(classname+" "+e.getEventType());
				throw new Exception("Error! first item in events list was not a ResourceEvent or an EReference event.");
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
				System.exit(0);
			}
		}
		
		if(obj == null) //TBR
		{
			System.out.println(classname+" "+e.getEventType());
			System.exit(0);
		}
		
		out.write(FORMAT_ID.getBytes(manager.STRING_ENCODING)); //FORMAT ID
		writePrimitive(out, FORMAT_VERSION); //FORMAT VERSION
		writeString(out,obj.eClass().getEPackage().getNsURI()); //NS URI
	}
	
	
	private void writeString(OutputStream out, String str) throws IOException
	{
		byte[] bytes = str.getBytes(manager.STRING_ENCODING);
	
		writePrimitive(out,bytes.length);
		
		out.write(bytes);
	}
	
	private void writePrimitive(OutputStream out, int primitiveType, Object obj ) throws IOException
	{
		switch(primitiveType)
		{
		case PersistenceManager.SIMPLE_TYPE_INT:
			writePrimitive(out,(int)obj);
			return;
		case PersistenceManager.SIMPLE_TYPE_BOOLEAN:
			writePrimitive(out,(boolean)obj);
			return;
		case PersistenceManager.SIMPLE_TYPE_BYTE:
			writePrimitive(out,(byte)obj);
			return;
		case PersistenceManager.SIMPLE_TYPE_CHAR:
			writePrimitive(out,(char)obj);
			return;
		case PersistenceManager.SIMPLE_TYPE_DOUBLE:
			writePrimitive(out,(double)obj);
			return;
		case PersistenceManager.SIMPLE_TYPE_FLOAT:
			writePrimitive(out,(short)obj);
			return;
		case PersistenceManager.SIMPLE_TYPE_LONG:
			writePrimitive(out,(long)obj);
			return;
		case PersistenceManager.SIMPLE_TYPE_SHORT:
			writePrimitive(out,(short)obj);
			return;
		}
		
	}
	
	private void writePrimitive(OutputStream out, int i) throws IOException
	{
		byte[] bytes = ByteBuffer.allocate(manager.INTEGER_SIZE).putInt(i).array();
		out.write(bytes);
	}
	
	private void writePrimitive(OutputStream out, short s) throws IOException
	{
		byte[] bytes = ByteBuffer.allocate(manager.SHORT_SIZE).putShort(s).array();
		out.write(bytes);
	}
	
	private void writePrimitive(OutputStream out, byte b) throws IOException
	{
		byte[] bytes = ByteBuffer.allocate(manager.BYTE_SIZE).put(b).array();
		out.write(bytes);
	}
	
	private void writePrimitive(OutputStream out, char c) throws IOException
	{
		byte[] bytes = ByteBuffer.allocate(manager.CHAR_SIZE).putChar(c).array();
		out.write(bytes);
	}
	
	private void writePrimitive(OutputStream out, double d) throws IOException
	{
		byte[] bytes = ByteBuffer.allocate(manager.DOUBLE_SIZE).putDouble(d).array();
		out.write(bytes);
	}
	
	private void writePrimitive(OutputStream out, long l) throws IOException
	{
		byte[] bytes = ByteBuffer.allocate(manager.LONG_SIZE).putLong(l).array();
		out.write(bytes);
	}
	
	private void writePrimitive(OutputStream out, boolean b) throws IOException
	{
		if(b)
			writePrimitive(out,1);
		else
			writePrimitive(out,0);
	}
	
}
