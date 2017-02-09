
package org.eclipse.epsilon.cbp.resource;

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
		return new CBPTextSerialiser(this);
	}

	@Override
	public AbstractCBPDeserialiser getDeserialiser() {
		return new CBPTextDeserialiser(this);
	}

}
