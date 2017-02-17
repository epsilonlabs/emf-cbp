package org.eclipse.epsilon.cbp.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;
import org.eclipse.epsilon.cbp.io.CBPXMLSerialiser;

public class CBPXMLResourceImpl extends CBPResource {

	public CBPXMLResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	public AbstractCBPSerialiser getSerialiser() {
		return new CBPXMLSerialiser(this);
	}

	@Override
	public AbstractCBPDeserialiser getDeserialiser() {
		return null;
	}

}
