
package org.eclipse.epsilon.cbp.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;
import org.eclipse.epsilon.cbp.io.CBPTextDeserialiser;
import org.eclipse.epsilon.cbp.io.CBPTextSerialiser;

public class CBPTextResourceImpl extends CBPResource {

	public CBPTextResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	public AbstractCBPSerialiser getSerialiser() {
		CBPTextSerialiser serialiser = new CBPTextSerialiser(this);
		serialiser.setDebug(verbose);
		return serialiser;
	}

	@Override
	public AbstractCBPDeserialiser getDeserialiser() {
		CBPTextDeserialiser deserialiser = new CBPTextDeserialiser(this);
		deserialiser.setDebug(verbose);
		return deserialiser;
	}

}
