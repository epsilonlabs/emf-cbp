
package org.eclipse.epsilon.cbp.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.context.PersistenceManager;
import org.eclipse.epsilon.cbp.event.AddEObjectsToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.ResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.eclipse.epsilon.cbp.util.SimpleType;

import gnu.trove.map.TObjectIntMap;

public class CBPTextSerialiser 
{
	//format id
	private final String FORMAT_ID = "CBP_TEXT"; 
	
	//version number (wtf?)
	private final double VERSION = 1.0;
	
	//event list
    private final List<Event> eventList;
    
    //persistence manager
	private final PersistenceManager manager;
	
	//change log
	private final Changelog changelog; 
	
	//epackage element map
	private final ModelElementIDMap ePackageElementsNamesMap;
	
	//common simple type name
	private final TObjectIntMap<String> commonsimpleTypeNameMap;
	
	//tet simple type name
	private final TObjectIntMap<String> textSimpleTypeNameMap;
	
	public CBPTextSerialiser(PersistenceManager manager, Changelog aChangelog, ModelElementIDMap 
			ePackageElementsNamesMap)
	{
		this.manager =  manager;
		this.changelog = aChangelog;
		this.ePackageElementsNamesMap = ePackageElementsNamesMap;
		
		this.eventList = manager.getChangelog().getEventsList();
		
		this.commonsimpleTypeNameMap = manager.getCommonSimpleTypesMap();
		this.textSimpleTypeNameMap = manager.getTextSimpleTypesMap();
	}
	
	public void save(Map<?,?> options) //tbr
	{
		if(eventList.isEmpty()) //tbr
		{
			System.out.println("CBPTextSerialiser: no events found, returning!");
			return;
		}
			
		
		PrintWriter printWriter = null;
		//setup printwriter
	    try
        {
	    	BufferedWriter bw = new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(manager.getURI().path(), manager.isResume()), manager.STRING_ENCODING));
            printWriter = new PrintWriter(bw);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
		
		//if we're not in resume mode, serialise initial entry
		if(!manager.isResume())
			serialiseHeader(printWriter);
		
		for(Event e : eventList)
		{
			switch(e.getEventType())
			{
			case Event.ADD_EOBJ_TO_RESOURCE:
				handleAddToResourceEvent((AddEObjectsToResourceEvent)e, printWriter);
				break;
			case Event.SET_EATTRIBUTE:
				handleSetEAttributeEvent((EAttributeEvent)e, printWriter);
				break;
			case Event.ADD_TO_EATTRIBUTE:
				handleAddToEAttributeEvent((EAttributeEvent)e, printWriter);
				break;
			case Event.SET_EREFERENCE:
				handleSetEReferenceEvent((SetEReferenceEvent)e, printWriter);
				break;
			case Event.ADD_TO_EREFERENCE:
				handleAddToEReferenceEvent((AddToEReferenceEvent)e, printWriter);
				break;
			case Event.REMOVE_FROM_EATTRIBUTE:
				handleRemoveFromAttributeEvent((EAttributeEvent)e, printWriter);
				break;
			case Event.REMOVE_FROM_EREFERENCE:
				handleRemoveFromEReferenceEvent((RemoveFromEReferenceEvent)e, printWriter);
				break;
			case Event.REMOVE_EOBJ_FROM_RESOURCE:
				handleRemoveFromResourceEvent((RemoveFromResourceEvent)e, printWriter);
				break;
			}
		}
		
		changelog.clearEvents();
		
		printWriter.close();
		manager.setResume(true);
	}
	
	
	/*
	 * event has the format of:
	 * 0 [(MetaElementTypeID objectID)* (,)*]
	 */
	private void handleAddToResourceEvent(AddEObjectsToResourceEvent e, PrintWriter out)
	{
		ArrayList<Integer> eObjectsToCreateList = new ArrayList<Integer>();
    	
    	for(EObject obj : e.getEObjectList())
    	{
    		//if obj is not added already
    		if(changelog.addObjectToMap(obj))
    		{
    			//add type to object-to-create-list
    			eObjectsToCreateList.add(ePackageElementsNamesMap.getID(obj.eClass().getName())); 
    			
    			//add id to object-to-create-list
    			eObjectsToCreateList.add(changelog.getObjectId(obj));
    		}
    		else {
    			//should not happen
				System.err.println("redundant creation");
			}
    	}
    	
    	//delimiter
    	String delimiter= "";
    	
    	//if create list is not empty
		if(!eObjectsToCreateList.isEmpty())
		{
			out.print(SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE+" [");

	        int index = 0;
    		for(int i = 0; i < (eObjectsToCreateList.size() / 2); i++)
    		{
    			//add type-id pair
    			out.print(delimiter+eObjectsToCreateList.get(index)+" "+eObjectsToCreateList.get(index+1));
    			
    			//set delimiter
    			delimiter = PersistenceManager.DELIMITER;
    			
    			//increase index by 2
    			index = index + 2;
    		}
    		
    		out.print("]");
		}
		
		out.println();
	}

	/*
	 * event format:
	 * 3/4 objectID EAttributeID [value*]
	 */
	private void handleSetEAttributeEvent(EAttributeEvent e, PrintWriter out)
	{
		//get forcus object
		EObject focusObject = e.getFocusObject();
		
		//get attr
		EAttribute eAttribute = e.getEAttribute();
		
		//get data type
		EDataType eDataType = eAttribute.getEAttributeType();
		
		//get serialisation type flag
		int serializationType = SerialisationEventType.SET_EATTRIBUTE_PRIMITIVE;
		
		String newValue;
		String delimiter ="";

		if (getTypeID(eDataType) != SimpleType.COMPLEX_TYPE) {
			out.print((serializationType+" "+changelog.getObjectId(focusObject)+" "+
					ePackageElementsNamesMap.getID(eAttribute.getName())+" ["));
			
			for(Object obj: e.getEAttributeValuesList())
			{
				if(obj != null)
				{
					newValue = String.valueOf(obj);
					newValue = newValue.replace(PersistenceManager.DELIMITER, 
							PersistenceManager.ESCAPE_CHAR+PersistenceManager.DELIMITER); //escape delimiter
					out.print(delimiter+newValue);	
				}
//				else
//				{
//					newValue = manager.NULL_STRING;
//				}
				
				delimiter = PersistenceManager.DELIMITER;
			}
			out.print("]");
		}
		else //all other datatypes
		{
			serializationType = SerialisationEventType.SET_EATTRIBUTE_COMPLEX;
			
			out.print((serializationType+" "+changelog.getObjectId(focusObject)+" "+
					ePackageElementsNamesMap.getID(eAttribute.getName())+" ["));
			
			for(Object obj: e.getEAttributeValuesList())
			{
				newValue = (EcoreUtil.convertToString(eDataType, obj));
				
				if(newValue!= null)
				{
					newValue = newValue.replace(PersistenceManager.DELIMITER, 
							PersistenceManager.ESCAPE_CHAR+PersistenceManager.DELIMITER); //escape delimiter
					out.print(delimiter+newValue);
				}
//				else
//				{
//					newValue = manager.NULL_STRING;
//				}
//				
//				out.print(delimiter+newValue);	
				delimiter = PersistenceManager.DELIMITER;
			}
			out.print("]");
		}
		out.println();
	}


	/*
	 * event format:
	 * 5/6 objectID EAttributeID [value*]
	 */
	private void handleAddToEAttributeEvent(EAttributeEvent e, PrintWriter out) 
	{
		//get forcus object
		EObject focusObject = e.getFocusObject();
		
		//get attr
		EAttribute eAttribute = e.getEAttribute();
		
		//get data type
		EDataType eDataType = eAttribute.getEAttributeType();
		
		//get serialisation type flag
		int serializationType = SerialisationEventType.ADD_TO_EATTRIBUTE_PRIMITIVE;
		
		String newValue ;
		String delimiter ="";

		if (getTypeID(eDataType) != SimpleType.COMPLEX_TYPE) {
			out.print((serializationType+" "+changelog.getObjectId(focusObject)+" "+
					ePackageElementsNamesMap.getID(eAttribute.getName())+" ["));
			
			for(Object obj: e.getEAttributeValuesList())
			{
				if(obj != null)
				{
					newValue = String.valueOf(obj);
					newValue = newValue.replace(PersistenceManager.DELIMITER, 
							PersistenceManager.ESCAPE_CHAR+PersistenceManager.DELIMITER); //escape delimiter
					out.print(delimiter+newValue);
				}
//				else
//				{
//					newValue = manager.NULL_STRING;
//				}
				
//				out.print(delimiter+newValue);	
				delimiter = PersistenceManager.DELIMITER;
			}
			out.print("]");
		}
		else //all other datatypes
		{
			serializationType = SerialisationEventType.ADD_TO_EATTRIBUTE_COMPLEX;
			
			out.print((serializationType+" "+changelog.getObjectId(focusObject)+" "+
					ePackageElementsNamesMap.getID(eAttribute.getName())+" ["));
			
			for(Object obj: e.getEAttributeValuesList())
			{
				newValue = (EcoreUtil.convertToString(eDataType, obj));
				
				if(newValue!= null)
				{
					newValue = newValue.replace(PersistenceManager.DELIMITER, 
							PersistenceManager.ESCAPE_CHAR+PersistenceManager.DELIMITER); //escape delimiter
					out.print(delimiter+newValue);
				}
//				else
//				{
//					newValue = manager.NULL_STRING;
//				}
//				
//				out.print(delimiter+newValue);	
				delimiter = PersistenceManager.DELIMITER;
			}
			out.print("]");
		}
		out.println();
	}
	
	/*
	 * event format:
	 * 10 objectID EReferenceID [(ECLass ID, EObject)*(,)*]
	 * 12/9 objectID EReferenceID [EObjectID]
	 */
	private void handleSetEReferenceEvent(SetEReferenceEvent e, PrintWriter out)
	{
		boolean created = false;
		EObject focusObject = e.getFocusObject();
		EReference eReference = e.getEReference();
		
		ArrayList<Integer> eObjectsToAddList = new ArrayList<Integer>();
		ArrayList<Integer> eObjectsToCreateList = new ArrayList<Integer>();
    	
    	for(EObject obj : e.getEObjectList())
    	{
    		//if obj is not added already
    		if(changelog.addObjectToMap(obj))
    		{
    			//add type to object-to-create-list
    			eObjectsToCreateList.add(ePackageElementsNamesMap.getID(obj.eClass().getName())); 
    			
    			//add id to object-to-create-list
    			eObjectsToCreateList.add(changelog.getObjectId(obj));
    			
    		}
    		else
    		{
    			//add id to object-to-add list
    			eObjectsToAddList.add(changelog.getObjectId(obj));
    		}
    	}
    	
    	//delimiter
    	String delimiter= "";
    	
    	//if create list is not empty
		if(!eObjectsToCreateList.isEmpty())
		{
			created = true;
			out.print(SerialisationEventType.CREATE_AND_SET_EREFERENCE+" "+
					changelog.getObjectId(focusObject)+" "+
					ePackageElementsNamesMap.getID(eReference.getName())+" [");
			
					
	        int index = 0;
    		for(int i = 0; i < (eObjectsToCreateList.size() / 2); i++)
    		{
    			//add type-id pair
    			out.print(delimiter+eObjectsToCreateList.get(index)+" "+eObjectsToCreateList.get(index+1));
    			
    			//set delimiter
    			delimiter = PersistenceManager.DELIMITER;
    			
    			//increase index by 2
    			index = index + 2;
    		}
    		
    		out.print("]");
		}
		
		//if add list is not empty
		if(!eObjectsToAddList.isEmpty())
		{

			if (created) {
				out.print(SerialisationEventType.ADD_TO_EREFERENCE+" "+
						changelog.getObjectId(focusObject)+" "+
						ePackageElementsNamesMap.getID(eReference.getName())+
						" [");
			}
			else {
				out.print(SerialisationEventType.SET_EREFERENCE+" "+
						changelog.getObjectId(focusObject)+" "+
						ePackageElementsNamesMap.getID(eReference.getName())+
						" [");
			}
			
					
			delimiter="";
			for(Iterator<Integer> it = eObjectsToAddList.iterator(); it.hasNext();)
			{
				out.print(delimiter+it.next());
				delimiter = PersistenceManager.DELIMITER;
			}
			
			out.print("]");	
		}
		out.println();
	}

	/*
	 * event format:
	 * 10 objectID EReferenceID [(ECLass ID, EObject ID)* ,*]
	 * 12 objectID EReferenceID [EObjectID*]
	 */
	private void handleAddToEReferenceEvent(AddToEReferenceEvent e, PrintWriter out)
	{
		EObject focusObject = e.getFocusObject();
		EReference eReference = e.getEReference();
		ArrayList<Integer> eObjectsToAddList = new ArrayList<Integer>();
		ArrayList<Integer> eObjectsToCreateList = new ArrayList<Integer>();
    	
    	for(EObject obj : e.getEObjectList())
    	{
    		//if obj is not added already
    		if(changelog.addObjectToMap(obj))
    		{
    			//add type to object-to-create-list
    			eObjectsToCreateList.add(ePackageElementsNamesMap.getID(obj.eClass().getName())); 
    			
    			//add id to object-to-create-list
    			eObjectsToCreateList.add(changelog.getObjectId(obj));
    			
    		}
    		else
    		{
    			//add id to object-to-add list
    			eObjectsToAddList.add(changelog.getObjectId(obj));
    		}
    	}
    	
    	//delimiter
    	String delimiter= "";
    	
    	//if create list is not empty
		if(!eObjectsToCreateList.isEmpty())
		{

			out.print(SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE+" "+
					changelog.getObjectId(focusObject)+" "+
					ePackageElementsNamesMap.getID(eReference.getName())+" [");
			
	        int index = 0;
    		for(int i = 0; i < (eObjectsToCreateList.size() / 2); i++)
    		{
    			//add type-id pair
    			out.print(delimiter+eObjectsToCreateList.get(index)+" "+eObjectsToCreateList.get(index+1));
    			
    			//set delimiter
    			delimiter = PersistenceManager.DELIMITER;
    			
    			//increase index by 2
    			index = index + 2;
    		}
    		
    		out.print("]");
		}
		
		//if add list is not empty
		if(!eObjectsToAddList.isEmpty())
		{
			out.print(SerialisationEventType.ADD_TO_EREFERENCE+" "+
					changelog.getObjectId(focusObject)+" "+
					ePackageElementsNamesMap.getID(eReference.getName())+
					" [");
			
			delimiter="";
			for(Iterator<Integer> it = eObjectsToAddList.iterator(); it.hasNext();)
			{
				out.print(delimiter+it.next());
				delimiter = PersistenceManager.DELIMITER;
			}
			out.print("]");	
		}
		out.println();
	}

	/*
	 * event type:
	 * 7/8 objectID EAttributeID [value*]
	 */
	private void handleRemoveFromAttributeEvent(EAttributeEvent e, PrintWriter out)
	{

		//get forcus object
		EObject focusObject = e.getFocusObject();
		
		//get attr
		EAttribute eAttribute = e.getEAttribute();
		
		//get data type
		EDataType eDataType = eAttribute.getEAttributeType();
		
		//get serialisation type flag
		int serializationType = SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE;
		
		String newValue ;
		String delimiter ="";
		
		if(getTypeID(eDataType) != SimpleType.COMPLEX_TYPE )
		{
			out.print((serializationType+" "+changelog.getObjectId(focusObject)+" "+
					ePackageElementsNamesMap.getID(eAttribute.getName())+" ["));
			

			for(Object obj: e.getEAttributeValuesList())
			{
				if(obj != null)
				{
					newValue = String.valueOf(obj);
					newValue = newValue.replace(PersistenceManager.DELIMITER, 
							PersistenceManager.ESCAPE_CHAR+PersistenceManager.DELIMITER); //escape delimiter
					out.print(delimiter+newValue);	
				}
				delimiter = PersistenceManager.DELIMITER;
			}
			out.print("]");
		
		}
		else //all other datatypes
		{
			serializationType = SerialisationEventType.REMOVE_FROM_EATTRIBUTE_COMPLEX;
			out.print((serializationType+" "+changelog.getObjectId(focusObject)+" "+
					ePackageElementsNamesMap.getID(eAttribute.getName())+" ["));

			for(Object obj: e.getEAttributeValuesList())
			{
				newValue = (EcoreUtil.convertToString(eDataType, obj));
				
				if(newValue!= null)
				{
					newValue = newValue.replace(PersistenceManager.DELIMITER, 
							PersistenceManager.ESCAPE_CHAR+PersistenceManager.DELIMITER); //escape delimiter
					out.print(delimiter+newValue);
				}
//				else
//				{
//					newValue = manager.NULL_STRING;
//				}
				
//				out.print(delimiter+newValue);	
				delimiter = PersistenceManager.DELIMITER;
			}
			out.print("]");
		}
		out.println();
	}
	
	/*
	 * event type:
	 * 13 objectID EReferenceID [EObjectID*]
	 */
	private void handleRemoveFromEReferenceEvent(RemoveFromEReferenceEvent e, PrintWriter out)
	{
		EObject focusObject = e.getFocusObject();
		EReference eReference = e.getEReference();
		List<EObject> removedEObjectsList = e.getEObjectList();
		
		out.print(SerialisationEventType.REMOVE_FROM_EREFERENCE+" "+
				changelog.getObjectId(focusObject)+" "+
                (ePackageElementsNamesMap.getID(eReference.getName())+" ["));
			
		String delimiter = "";
		
		for(EObject obj : removedEObjectsList)
		{
			out.print(delimiter + changelog.getObjectId(obj));
			delimiter = PersistenceManager.DELIMITER;
		}
		out.print("]");
		out.println();
	}

	/*
	 * event type:
	 * 2 [EObjectID*]
	 */
	private void handleRemoveFromResourceEvent(RemoveFromResourceEvent e, PrintWriter out)
	{
		List<EObject> removedEObjectsList = e.getEObjectList();
		out.print(SerialisationEventType.REMOVE_FROM_RESOURCE+" [");
		
		String delimiter = "";
		
		for(EObject obj : removedEObjectsList)
		{
			out.print(delimiter + changelog.getObjectId(obj));
			delimiter = PersistenceManager.DELIMITER;
		}
		out.print("]");
		out.println();
	}

	private void serialiseHeader(PrintWriter out) 
	{
		//obj
		EObject obj = null;
		
		//get first event
		Event e = eventList.get(0);
		
		if(e instanceof ResourceEvent)
		{
			obj = ((ResourceEvent)e).getEObjectList().get(0);
		}
		else //throw tantrum
		{
			try 
			{
				System.out.println("CBPTextSerialiser: "+e.getEventType());
				throw new Exception("Error! first item in events list is not a ResourceEvent.");
			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
				System.exit(0);
			}
		}
		
		if(obj == null) //TBR
		{
			System.out.println("CBPTextSerialiser: "+e.getEventType());
			System.exit(0);
		}
		
		out.println(FORMAT_ID+" "+VERSION);
		out.println("NAMESPACE_URI "+obj.eClass().getEPackage().getNsURI());
	}
	
	
	private int getTypeID(EDataType type)
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
