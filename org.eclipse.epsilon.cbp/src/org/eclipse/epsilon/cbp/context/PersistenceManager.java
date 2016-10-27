
//todo: specify encoding ?
//http://www.javapractices.com/topic/TopicAction.do?Id=31
package org.eclipse.epsilon.cbp.context;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.impl.CBPBinaryResourceImpl;
import org.eclipse.epsilon.cbp.impl.CBPResource;
import org.eclipse.epsilon.cbp.impl.CBPTextResourceImpl;
import org.eclipse.epsilon.cbp.io.CBPBinaryDeserializer;
import org.eclipse.epsilon.cbp.io.CBPBinarySerializer;
import org.eclipse.epsilon.cbp.io.CBPTextDeserializer;
import org.eclipse.epsilon.cbp.io.CBPTextSerializer;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.ResourceContentsToEventsConverter;
import org.eclipse.epsilon.cbp.util.SimpleType;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class PersistenceManager 
{
	public static final String DELIMITER = ","; 
	public static final String ESCAPE_CHAR ="+"; 
	public final Charset STRING_ENCODING = StandardCharsets.UTF_8;
	public final String NULL_STRING = "pFgrW";
	
	//if you have redundancy it means you are doing it wrong
	
	/*
	 * Only remove redundant changes made during the current session.
	 */
	String OPTION_OPTIMISE_SESSION = "OPTIMISE_SESSION";
	
	/*
	 * Remove redundant changes from the entire model.
	 */
	String OPTION_OPTIMISE_MODEL ="OPTIMISE_MODEL";
	
	private final Changelog changelog; 

	private final CBPResource resource;
	
    private final ModelElementIDMap ePackageElementsNamesMap;
	
	private boolean resume = false;
	
	private final TObjectIntMap<String> textSimpleTypesMap = new TObjectIntHashMap<String>(2);
	
	private final TObjectIntMap<String> commonSimpleTypesMap = new TObjectIntHashMap<String>(13);
	
	public PersistenceManager(Changelog changelog, CBPResource resource, 
			ModelElementIDMap ePackageElementsNamesMap)
	{
		this.changelog = changelog;
		this.resource = resource;
		this.ePackageElementsNamesMap = ePackageElementsNamesMap;
		
		populatecommonSimpleTypesMap();
		populateTextSimpleTypesMap();
	}
	
	public void setResume(boolean b)
	{
		resume = b;
	}
	
	private void populateTextSimpleTypesMap()
	{
    	textSimpleTypesMap.put("EString", SimpleType.TEXT_SIMPLE_TYPE_ESTRING);
    	textSimpleTypesMap.put("EStringObject", SimpleType.TEXT_SIMPLE_TYPE_ESTRING);
	}
	
	private void populatecommonSimpleTypesMap()
	{
		commonSimpleTypesMap.put("EInt", SimpleType.SIMPLE_TYPE_INT);
		commonSimpleTypesMap.put("EIntegerObject", SimpleType.SIMPLE_TYPE_INT);
		commonSimpleTypesMap.put("EBoolean", SimpleType.SIMPLE_TYPE_BOOLEAN);
		commonSimpleTypesMap.put("EBooleanObject", SimpleType.SIMPLE_TYPE_BOOLEAN);
		commonSimpleTypesMap.put("EFloat", SimpleType.SIMPLE_TYPE_FLOAT);
		commonSimpleTypesMap.put("EFloatObject", SimpleType.SIMPLE_TYPE_FLOAT);
		commonSimpleTypesMap.put("EDouble", SimpleType.SIMPLE_TYPE_DOUBLE);
		commonSimpleTypesMap.put("EDoubleObject", SimpleType.SIMPLE_TYPE_DOUBLE);
		commonSimpleTypesMap.put("EShort", SimpleType.SIMPLE_TYPE_SHORT);
		commonSimpleTypesMap.put("EShortObject", SimpleType.SIMPLE_TYPE_SHORT);
		commonSimpleTypesMap.put("ELong", SimpleType.SIMPLE_TYPE_LONG);
		commonSimpleTypesMap.put("ELongObject", SimpleType.SIMPLE_TYPE_LONG);
		commonSimpleTypesMap.put("EChar", SimpleType.SIMPLE_TYPE_CHAR);
	}
	
	public TObjectIntMap<String> getCommonSimpleTypesMap()
	{
		return commonSimpleTypesMap;
	}
	
	public TObjectIntMap<String> getTextSimpleTypesMap()
	{
		return textSimpleTypesMap;
	}
	
	public boolean isResume()
	{
		return resume;
	}
	
//	public boolean addEObjectsToContents(List<EObject> objects)
//	{
//		return resource.getContents().addAll(objects);
//	}
//	
//	public boolean removeEObjectsFromContents(List<EObject> objects)
//	{
//		return resource.getContents().removeAll(objects);
//	}
	
	public boolean addEObjectToContents(EObject object)
	{
		return resource.getContents().add(object);
	}
	
	public boolean removeEObjectFromContents(EObject obj)
	{
		return resource.getContents().remove(obj);
	}
	
//	public Resource getResource()
//	{
//		return this.resource;
//	}
	
	public URI getURI()
	{
		return resource.getURI();
	}

	public void save(Map<?,?> options)
	{
		
		if(options != null)
		{
			if(options.containsKey(OPTION_OPTIMISE_MODEL))
			{
				if((boolean)options.get(OPTION_OPTIMISE_MODEL ) == true)
				{
					ResourceContentsToEventsConverter rc = 
							new ResourceContentsToEventsConverter(changelog,resource);
					rc.convert();
					resume = false;
				}	
			}
			else if(options.containsKey(OPTION_OPTIMISE_SESSION))
			{
				if((boolean)options.get(OPTION_OPTIMISE_SESSION) == true)
				{
				}
			}
		}
		
		if(resource instanceof CBPBinaryResourceImpl)
		{
			CBPBinarySerializer serializer = 
					new CBPBinarySerializer(this,changelog, ePackageElementsNamesMap);
			try {
				serializer.save(options);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(resource instanceof CBPTextResourceImpl)
		{
			CBPTextSerializer serializer = 
					new CBPTextSerializer(this, changelog,ePackageElementsNamesMap);
			serializer.save(options);
		}
	}
	
	public void load(Map<?,?> options) throws Exception
	{	
		if(resource instanceof CBPBinaryResourceImpl)
		{
			CBPBinaryDeserializer deserializer = new CBPBinaryDeserializer(this,changelog,ePackageElementsNamesMap);
			deserializer.load(options);
		}
		else if(resource instanceof CBPTextResourceImpl)
		{
			CBPTextDeserializer textDeserializer = new CBPTextDeserializer(this,changelog,ePackageElementsNamesMap);
			textDeserializer.load(options);
		}
	}
	
	public Changelog getChangelog()
	{
		return this.changelog;
	}
	
}
