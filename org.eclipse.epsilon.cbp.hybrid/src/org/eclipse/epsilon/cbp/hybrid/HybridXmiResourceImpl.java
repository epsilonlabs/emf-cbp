package org.eclipse.epsilon.cbp.hybrid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public class HybridXmiResourceImpl extends HybridResource {

	public HybridXmiResourceImpl(Resource xmiResource, OutputStream cbpOutputStream) {
		super(xmiResource.getURI());
		this.stateBasedResource = xmiResource;
		this.uri = xmiResource.getURI();
		this.cbpOutputStream = cbpOutputStream;

		hybridChangeEventAdapter = new HybridXmiChangeEventAdapter(this);
		stateBasedResource.eSetDeliver(true);
		stateBasedResource.eAdapters().add(hybridChangeEventAdapter);
	}

	@Override
	public void doSave(OutputStream out, Map<?, ?> options) throws IOException {
		saveChangeBasedPersistence(cbpOutputStream, options);
		cbpOutputStream.flush();
		stateBasedResource.save(out, options);
		out.flush();
	}

	@Override
	public void doLoad(InputStream out, Map<?, ?> options) throws IOException {
		hybridChangeEventAdapter.setEnabled(false);
		stateBasedResource.load(options);
		hybridChangeEventAdapter.setEnabled(true);
	}
	
}
