package org.eclipse.epsilon.cbp.resource;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;
import org.eclipse.epsilon.cbp.io.CBPBinaryDeserializer;
import org.eclipse.epsilon.cbp.io.CBPBinarySerialiser;

public class CBPBinaryResourceImpl extends CBPResource {
	
	private final EventAdapter eventAdapter;

	public CBPBinaryResourceImpl(URI uri) {
		super(uri);

		eventAdapter = new EventAdapter(changelog);

		this.eAdapters().add(eventAdapter);
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		AbstractCBPSerialiser serialiser = getSerialiser();
		try {
			serialiser.serialise(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load(Map<?, ?> options) throws IOException {
		boolean defaultLoading = false;
		if (options.get("DEFAULT_LOADING") != null) {
			defaultLoading = (boolean) options.get("DEFAULT_LOADING");
		}
		if (defaultLoading) {
			super.load(options);
		}
		else {
			// We do not want changes during loading to be logged
			eventAdapter.setEnabled(false);
			AbstractCBPDeserialiser deserialiser = getDeserialiser();
			try {
				deserialiser.deserialise(options);
				eventAdapter.setEnabled(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
