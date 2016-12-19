
package org.eclipse.epsilon.cbp.impl;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;
import org.eclipse.epsilon.cbp.io.CBPTextDeserialiser;
import org.eclipse.epsilon.cbp.io.CBPTextSerialiser;
import org.eclipse.epsilon.cbp.io.CBPVerboseTextDeserialiser;
import org.eclipse.epsilon.cbp.io.CBPVerboseTextSerialiser;

public class CBPTextResourceImpl extends CBPResource
{
	protected EventAdapter eventAdapter;
    
    public CBPTextResourceImpl(URI uri)
	{
		super(uri);
		
		eventAdapter = new EventAdapter(changelog);
		
		this.eAdapters().add(eventAdapter); 
	}
    
	@Override
	public void save(Map<?, ?> options) throws IOException
	{
		if (options.get("debug") != null) {
			debug = (boolean) options.get("debug");	
		}
		
		AbstractCBPSerialiser serialiser = getSerialiser();
		try {
			serialiser.serialise(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void load(Map<?, ?> options)throws IOException
	{
		eventAdapter.setEnabled(false);
		
		AbstractCBPDeserialiser deserialiser = getDeserialiser();
		try {
			deserialiser.deserialise(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		eventAdapter.setEnabled(true);
	}

	@Override
	public AbstractCBPSerialiser getSerialiser() {
		if (debug) {
			return new CBPVerboseTextSerialiser(this);
		}
		return new CBPTextSerialiser(this);
	}

	@Override
	public AbstractCBPDeserialiser getDeserialiser() {
		if (debug) {
			return new CBPVerboseTextDeserialiser(this);
		}
		return new CBPTextDeserialiser(this);
	}
	
}
