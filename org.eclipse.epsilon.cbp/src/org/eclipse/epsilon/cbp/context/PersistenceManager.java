package org.eclipse.epsilon.cbp.context; 


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.impl.CBPBinaryResourceImpl;
import org.eclipse.epsilon.cbp.impl.CBPResource;
import org.eclipse.epsilon.cbp.impl.CBPTextResourceImpl;
import org.eclipse.epsilon.cbp.io.CBPBinaryDeserializer;
import org.eclipse.epsilon.cbp.io.CBPBinarySerialiser;
import org.eclipse.epsilon.cbp.io.CBPTextDeserialiser;
import org.eclipse.epsilon.cbp.io.CBPTextSerialiser;
import org.eclipse.epsilon.cbp.resource.to.events.ResourceContentsToEventsConverter;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.SimpleType;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class PersistenceManager 
{
	//delimiter
	public static final String DELIMITER = ",";
	//escaped char
	public static final String ESCAPE_CHAR ="+";
	//UTF-8 string encoding
	public final Charset STRING_ENCODING = StandardCharsets.UTF_8;
	
	//Null string I dont know what this is
	public final String NULL_STRING = "pFgrW";
	
	protected CBPContext context;
	
	/*
	 * Only remove redundant changes made during the current session.
	 */
	String OPTION_OPTIMISE_SESSION = "OPTIMISE_SESSION";
	
	/*
	 * Remove redundant changes from the entire model.
	 */
	String OPTION_OPTIMISE_MODEL ="OPTIMISE_MODEL";
	
	private final CBPResource resource;
	
	private EList<EObject> contents;
	
	private boolean resume = false;
	
	public PersistenceManager(CBPContext context, CBPResource resource)
	{
		this.context = context;
		this.resource = resource;
		populateAllContents();
	}
	
	protected void populateAllContents()
	{
		contents = resource.getContents();
	}
	
	public void setResume(boolean b)
	{
		resume = b;
	}
	
	public boolean isResume()
	{
		return resume;
	}
	
	public boolean addEObjectToContents(EObject object)
	{
		return contents.add(object);
	}
	
	public boolean removeEObjectFromContents(EObject obj)
	{
		return contents.remove(obj);
	}
	
	public URI getURI()
	{
		return resource.getURI();
	}

	public void save(Map<?,?> options) throws IOException
	{
		
		if(options != null)
		{
			if(options.containsKey(OPTION_OPTIMISE_MODEL))
			{
				if((boolean)options.get(OPTION_OPTIMISE_MODEL ) == true)
				{
					ResourceContentsToEventsConverter rc = 
							new ResourceContentsToEventsConverter(context.getChangelog(),resource);
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
			CBPBinarySerialiser serializer = 
					new CBPBinarySerialiser(this,context.getChangelog(), context.getePackageElementsNamesMap());
			try {
				serializer.serialise(options);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(resource instanceof CBPTextResourceImpl)
		{
			CBPTextSerialiser serializer = 
					new CBPTextSerialiser(this, changelog,ePackageElementsNamesMap);
			serializer.serialise(options);
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
			CBPTextDeserialiser textDeserializer = new CBPTextDeserialiser(this,changelog,ePackageElementsNamesMap);
			textDeserializer.deserialise(options);
		}
	}
}
