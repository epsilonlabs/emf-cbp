package org.eclipse.epsilon.cbp.hybrid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public class HybridXMIResourceImpl extends HybridResource {

	public HybridXMIResourceImpl(Resource xmiResource, OutputStream cbpOutputStream) {
		super(xmiResource.getURI());
		this.stateBasedResource = xmiResource;
		this.uri = xmiResource.getURI();
		this.cbpOutputStream = cbpOutputStream;

		hybridChangeEventAdapter = new HybridXMIChangeEventAdapter(this);
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
//		if (stateBasedResource.eAdapters().contains(hybridChangeEventAdapter)) {
//			stateBasedResource.eAdapters().remove(hybridChangeEventAdapter);
//		}
		hybridChangeEventAdapter.setEnabled(true);
	}
	
	
}
