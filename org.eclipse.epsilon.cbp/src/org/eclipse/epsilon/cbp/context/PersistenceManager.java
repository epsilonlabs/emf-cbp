package org.eclipse.epsilon.cbp.context; 


import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.impl.CBPBinaryResourceImpl;
import org.eclipse.epsilon.cbp.impl.CBPResource;
import org.eclipse.epsilon.cbp.impl.CBPTextResourceImpl;
import org.eclipse.epsilon.cbp.io.CBPBinaryDeserializer;
import org.eclipse.epsilon.cbp.io.CBPBinarySerialiser;
import org.eclipse.epsilon.cbp.io.CBPTextDeserialiser;
import org.eclipse.epsilon.cbp.io.CBPTextSerialiser;

public class PersistenceManager 
{
	protected CBPContext context;
	
	protected Resource resource = null;
	
	public PersistenceManager(CBPContext context, CBPResource resource)
	{
		this.context = context;
	}
	
	public void save(Map<?,?> options) throws IOException
	{
		
		if(options != null)
		{
		}
		
		if(resource instanceof CBPBinaryResourceImpl)
		{
			CBPBinarySerialiser serializer = 
					new CBPBinarySerialiser(context, resource);
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
					new CBPTextSerialiser(context, resource);
			serializer.serialise(options);
		}
	}
	
	public void load(Map<?,?> options) throws Exception
	{	
		if(resource instanceof CBPBinaryResourceImpl)
		{
//			CBPBinaryDeserializer deserializer = new CBPBinaryDeserializer(context, resource);
//			deserializer.load(options);
		}
		else if(resource instanceof CBPTextResourceImpl)
		{
			CBPTextDeserialiser textDeserializer = new CBPTextDeserialiser(context, resource);
			textDeserializer.deserialise(options);
		}
	}
}
