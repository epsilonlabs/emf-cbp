
package org.eclipse.epsilon.cbp.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;
import org.eclipse.epsilon.cbp.io.CBPTextDeserialiser;
import org.eclipse.epsilon.cbp.io.CBPTextSerialiser;

public class CBPTextResourceImpl extends CBPResource {

	protected EventAdapter eventAdapter;

	public CBPTextResourceImpl(URI uri) {
		super(uri);

		eventAdapter = new EventAdapter(changelog);

		this.eAdapters().add(eventAdapter);

		verbose = true;
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		if (options != null && options.get("verbose") != null) {
			verbose = (boolean) options.get("verbose");
		}

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
		} else {
			if (options.get("verbose") != null) {
				verbose = (boolean) options.get("verbose");
			}

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

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		boolean defaultLoading = false;
		if (options.get("DEFAULT_LOADING") != null) {
			defaultLoading = (boolean) options.get("DEFAULT_LOADING");
		}
		if (defaultLoading) {
			super.load(options);
		} else {
			if (options.get("verbose") != null) {
				verbose = (boolean) options.get("verbose");
			}

			// We do not want changes during loading to be logged
			eventAdapter.setEnabled(false);
			CBPTextDeserialiser deserialiser = (CBPTextDeserialiser) getDeserialiser();
			deserialiser.setInputStream(inputStream);
			
			try {
				deserialiser.deserialise(options);
				eventAdapter.setEnabled(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
