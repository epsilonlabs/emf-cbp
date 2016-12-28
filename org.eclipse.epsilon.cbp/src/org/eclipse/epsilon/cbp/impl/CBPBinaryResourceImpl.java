package org.eclipse.epsilon.cbp.impl;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;

public class CBPBinaryResourceImpl extends CBPResource {
	// event adapter
	private final EventAdapter eventAdapter;

	public CBPBinaryResourceImpl(URI uri) {
		super(uri);

		eventAdapter = new EventAdapter(changelog);

		this.eAdapters().add(eventAdapter);
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
	}

	@Override
	public void load(Map<?, ?> options) throws IOException {
		eventAdapter.setEnabled(false);

		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eventAdapter.setEnabled(true);
	}

	@Override
	public AbstractCBPSerialiser getSerialiser() {
		return null;
	}

	@Override
	public AbstractCBPDeserialiser getDeserialiser() {
		// TODO Auto-generated method stub
		return null;
	}
}
