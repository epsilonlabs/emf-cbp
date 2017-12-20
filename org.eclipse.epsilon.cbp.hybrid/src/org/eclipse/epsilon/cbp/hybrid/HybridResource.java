package org.eclipse.epsilon.cbp.hybrid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.resource.CBPResource;

import fr.inria.atlanmod.neoemf.resource.PersistentResource;

public class HybridResource extends ResourceImpl implements PersistentResource {
	
	private PersistentResource persistentResource;
	private CBPResource cbpResource;
	
	public PersistentResource getPersistentResource() {
		return persistentResource;
	}

	public CBPResource getCbpResource() {
		return cbpResource;
	}

	public HybridResource(PersistentResource persistentResource, CBPResource cbpResource) {
		super(persistentResource.getURI());
		this.persistentResource = persistentResource;
		this.cbpResource = cbpResource;
		this.uri = persistentResource.getURI();
	}
	
	public HybridResource(URI uri) {
		super(uri);
	}
	
	public HybridResource(URI uri, PersistentResource persistenceResource) {
		super(uri);
		this.persistentResource = persistenceResource;
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		cbpResource.save(options);
		persistentResource.save(options);
	}

	@Override
	public void load(Map<?, ?> options) throws IOException {
		cbpResource.load(options);
		persistentResource.load(options);
	}

	@Override
	public EList<EObject> getContents() {
		// TODO Auto-generated method stub
		return this.persistentResource.getContents();
	}

	@Override
	public void close() {
		this.persistentResource.close();
		
	}

	@Override
	public EStore eStore() {
		return this.persistentResource.eStore();
	}

	@Override
	public EList<EObject> getAllInstances(EClass arg0) {
		return this.persistentResource.getAllInstances(arg0);
	}

	@Override
	public EList<EObject> getAllInstances(EClass arg0, boolean arg1) {
		return this.persistentResource.getAllInstances(arg0, arg1);
	}

	@Override
	protected void doUnload() {
		cbpResource.unload();
		persistentResource.unload();
	}	
}
