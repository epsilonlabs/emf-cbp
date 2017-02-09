package org.eclipse.epsilon.cbp.resource;

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
		return new CBPBinarySerialiser(this);
	}

	@Override
	public AbstractCBPDeserialiser getDeserialiser() {
		return new CBPBinaryDeserializer(this);
	}
	
}
