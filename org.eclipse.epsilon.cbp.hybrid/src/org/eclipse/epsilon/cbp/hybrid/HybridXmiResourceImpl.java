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
		// TreeIterator<EObject> iterator = stateBasedResource.getAllContents();
		// while (iterator.hasNext()) {
		// EObject eObject = iterator.next();
		// String id = eObjectToIdMap.get(eObject);
		// ((XMIResourceImpl) stateBasedResource).setID(eObject, id);
		// }
		saveChangeBasedPersistence(cbpOutputStream, options);
		cbpOutputStream.flush();
		stateBasedResource.save(out, options);
		out.flush();
	}

	@Override
	public void doLoad(InputStream out, Map<?, ?> options) throws IOException {
		hybridChangeEventAdapter.setEnabled(false);
		stateBasedResource.load(options);
		// stateBasedResource.eSetDeliver(true);

		eObjectToIdMap.clear();
		TreeIterator<EObject> iterator = stateBasedResource.getAllContents();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			// String id = getURIFragment(eObject);
			// String id = ((XMIResourceImpl)
			// stateBasedResource).getID(eObject);
			try {
				register(eObject);
			} catch (Exception w) {
				w.printStackTrace();
			}
		}

		// stateBasedResource.eAdapters().remove(hybridChangeEventAdapter);
		// stateBasedResource.eAdapters().add(hybridChangeEventAdapter);
		hybridChangeEventAdapter.setEnabled(true);
	}
	
}
