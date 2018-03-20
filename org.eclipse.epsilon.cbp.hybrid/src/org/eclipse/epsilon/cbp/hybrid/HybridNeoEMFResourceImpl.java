package org.eclipse.epsilon.cbp.hybrid;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.impl.EClassifierImpl;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl.EStoreEList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;

import fr.inria.atlanmod.neoemf.core.DefaultPersistentEObject;
import fr.inria.atlanmod.neoemf.core.PersistentEObject;
import fr.inria.atlanmod.neoemf.data.blueprints.neo4j.option.BlueprintsNeo4jOptionsBuilder;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;

public class HybridNeoEMFResourceImpl extends HybridResource implements PersistentResource {

	private DefaultPersistentEObject rootObject;
	private static final ResourceContentsEStructuralFeature ROOT_CONTENTS_ESTRUCTURALFEATURE = new ResourceContentsEStructuralFeature();

	public HybridNeoEMFResourceImpl(PersistentResource persistentResource, OutputStream outputStream)
			throws IOException {
		super(persistentResource.getURI());
		this.stateBasedResource = persistentResource;
		this.uri = persistentResource.getURI();
		this.cbpOutputStream = outputStream;

		hybridChangeEventAdapter = new HybridNeoEMFChangeEventAdapter(this);

		rootObject = (DefaultPersistentEObject) ((EStoreEList<?>) this.stateBasedResource.getContents()).getEObject();

		stateBasedResource.eSetDeliver(true);
		stateBasedResource.eAdapters().add(hybridChangeEventAdapter);

		rootObject.eSetDeliver(true);
		rootObject.eAdapters().add(hybridChangeEventAdapter);
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		stateBasedResource.save(options);
		saveChangeBasedPersistence(cbpOutputStream, options);
		cbpOutputStream.flush();
	}

	@Override
	public void load(Map<?, ?> options) throws IOException {
		hybridChangeEventAdapter.setEnabled(false);
		stateBasedResource.load(options);
		stateBasedResource.eSetDeliver(true);

		eObjectToIdMap.clear();
		TreeIterator<EObject> iterator = stateBasedResource.getAllContents();
		while (iterator.hasNext()) {
			EObject eObject = iterator.next();
			register(eObject);
		}
		if (stateBasedResource.eAdapters().contains(hybridChangeEventAdapter)) {
			stateBasedResource.eAdapters().remove(hybridChangeEventAdapter);
		}
		stateBasedResource.eAdapters().add(hybridChangeEventAdapter);
		hybridChangeEventAdapter.setEnabled(true);
	}

	@Override
	public EList<EObject> getContents() {
		// return this.persistentResource.getContents();
		EList<EObject> list = new HybridResourceContentsEStoreEList(rootObject, ROOT_CONTENTS_ESTRUCTURALFEATURE,
				((PersistentResource) this.stateBasedResource).eStore());
		return list;
	}

	@Override
	public void close() {
		((PersistentResource) this.stateBasedResource).close();
	}

	@Override
	public EStore eStore() {
		return ((PersistentResource) this.stateBasedResource).eStore();
	}

	@Override
	public EList<EObject> getAllInstances(EClass arg0) {
		return ((PersistentResource) this.stateBasedResource).getAllInstances(arg0);
	}

	@Override
	public EList<EObject> getAllInstances(EClass arg0, boolean arg1) {
		return ((PersistentResource) this.stateBasedResource).getAllInstances(arg0, arg1);
	}

	/**
	 * Fake {@link EStructuralFeature} that represents the
	 * {@link Resource#getContents()} feature.
	 */
	private static class ResourceContentsEStructuralFeature extends EReferenceImpl {

		/**
		 * ???
		 */
		private static final String CONTENTS = "eContents";

		/**
		 * Constructs a new {@code ResourceContentsEStructuralFeature}.
		 */
		public ResourceContentsEStructuralFeature() {
			setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
			setLowerBound(0);
			setName(CONTENTS);
			setEType(new EClassifierImpl() {
			});
			setFeatureID(RESOURCE__CONTENTS);
		}
	}

	private class HybridResourceContentsEStoreEList extends EStoreEObjectImpl.EStoreEList {

		public HybridResourceContentsEStoreEList(InternalEObject owner, EStructuralFeature eStructuralFeature,
				EStore store) {
			super(owner, eStructuralFeature, store);

			HybridNeoEMFResourceImpl.HybridResourceContentsEStoreEList.this.eStructuralFeature = eStructuralFeature;
			HybridNeoEMFResourceImpl.HybridResourceContentsEStoreEList.this.store = store;
		}

		@Override
		public boolean add(Object object) {
			EObject eObject = (EObject) object;
			eObject.eSetDeliver(true);
			// eObject.eAdapters().add(hybridChangeEventAdapter);

			return stateBasedResource.getContents().add(eObject);
		}

		@Override
		public boolean addAll(Collection objects) {
			for (Object object : objects) {
				EObject eObject = (EObject) object;
				eObject.eSetDeliver(true);
				eObject.eAdapters().add(hybridChangeEventAdapter);
			}
			return stateBasedResource.getContents().addAll(objects);
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}

		@Override
		public int size() {
			return stateBasedResource.getContents().size();
		}

		@Override
		public Iterator<EObject> iterator() {
			// TODO Auto-generated method stub
			return stateBasedResource.getContents().iterator();
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			stateBasedResource.getContents().clear();
		}

		@Override
		public EObject get(int index) {
			// TODO Auto-generated method stub
			return stateBasedResource.getContents().get(index);
		}
	}
}
