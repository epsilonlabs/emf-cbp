package org.eclipse.epsilon.cbp.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;
import org.eclipse.epsilon.cbp.io.CBPBinaryDeserializer;
import org.eclipse.epsilon.cbp.io.CBPBinarySerialiser;

public class CBPBinaryResourceImpl extends CBPResource {

	public CBPBinaryResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	public AbstractCBPSerialiser getSerialiser() {
		CBPBinarySerialiser serialiser = new CBPBinarySerialiser(this);
		return serialiser;
	}

	@Override
	public AbstractCBPDeserialiser getDeserialiser() {
		CBPBinaryDeserializer deserializer = new CBPBinaryDeserializer(this);
		return deserializer;
	}
	
}
