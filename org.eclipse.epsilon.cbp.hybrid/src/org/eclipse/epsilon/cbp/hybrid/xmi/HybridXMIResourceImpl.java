package org.eclipse.epsilon.cbp.hybrid.xmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;

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
		hasJustBeenLoaded = true;
		hybridChangeEventAdapter.setEnabled(true);

		//get the maximum id number to set the id counter if the idType is numeric
		if (idType == HybridResource.IdType.NUMERIC) {
			TreeIterator<EObject> iterator = stateBasedResource.getAllContents();
			while (iterator.hasNext()) {
				EObject obj = iterator.next();
				int id = Integer.valueOf(((XMIResource) stateBasedResource).getID(obj));
				if (id > getIdCounter())
					setIdCounter(id);
			}
		}
	}

	@Override
	public EList<EObject> getContents() {
		if (hasJustBeenLoaded == true) {
			hasJustBeenLoaded = false;
			stateBasedResource.eAdapters().remove(hybridChangeEventAdapter);
			stateBasedResource.eAdapters().add(hybridChangeEventAdapter);
		}
		return stateBasedResource.getContents();
	}

	@Override
	public TreeIterator<EObject> getAllContents() {
		if (hasJustBeenLoaded == true) {
			hasJustBeenLoaded = false;
			stateBasedResource.eAdapters().remove(hybridChangeEventAdapter);
			stateBasedResource.eAdapters().add(hybridChangeEventAdapter);
		}
		return stateBasedResource.getAllContents();
	}

	@Override
	public void adopt(EObject eObject, String id) {
		super.adopt(eObject, id);
		((XMIResource) stateBasedResource).setID(eObject, id);
	}

	@Override
	protected EObject getEObjectByID(String id) {
		return super.getEObjectByID(id);
	}

	@Override
	public EObject getEObject(String uriFragment) {
		return super.getEObject(uriFragment);
	}

}
