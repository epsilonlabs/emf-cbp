package org.eclipse.epsilon.cbp.hybrid.neoemf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;

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
import org.eclipse.epsilon.cbp.hybrid.HybridResource;

import fr.inria.atlanmod.neoemf.core.DefaultPersistentEObject;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;

public class HybridNeoEMFResourceImpl extends HybridResource implements PersistentResource {

	protected DefaultPersistentEObject rootObject;
	protected static final ResourceContentsEStructuralFeature ROOT_CONTENTS_ESTRUCTURALFEATURE = new ResourceContentsEStructuralFeature();
	protected boolean hasJustBeenLoaded = false;

	public HybridNeoEMFResourceImpl() {
	}

	public HybridNeoEMFResourceImpl(PersistentResource persistentResource, OutputStream outputStream)
			throws IOException {
		super(persistentResource.getURI());
		this.stateBasedResource = persistentResource;
		this.uri = persistentResource.getURI();
		this.cbpOutputStream = outputStream;

		hybridChangeEventAdapter = new HybridNeoEMFChangeEventAdapter(this);

		// File file = new File(persistentResource.getURI().devicePath());
		// if (file.exists()) {
		// stateBasedResource.load(Collections.emptyMap());
		// } else {
		// stateBasedResource.save(BlueprintsNeo4jOptionsBuilder.newBuilder().autocommit().asMap());
		// stateBasedResource.load(Collections.emptyMap());
		// }

		rootObject = (DefaultPersistentEObject) ((EStoreEList<?>) this.stateBasedResource.getContents()).getEObject();

		stateBasedResource.eSetDeliver(true);
		stateBasedResource.eAdapters().add(hybridChangeEventAdapter);

		rootObject.eSetDeliver(true);
		rootObject.eAdapters().add(hybridChangeEventAdapter);

		// stateBasedResource.unload();

	}

	public void loadFromCBPToNeoEMF(File cbpFile, Map<String, Object> neoSaveOptions,
			Map<String, Object> neoLoadOptions) throws FactoryConfigurationError, IOException {
		hybridChangeEventAdapter.setEnabled(false);
		deletedEObjectToIdMap.clear();
		eObjectToIdMap.clear();
		getChangeEvents().clear();
		persistedEvents = 0;

		FileReader fileReader = new FileReader(cbpFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int lineCount = 0;
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			if (lineCount % 1000 == 0) {
				System.out.println(lineCount);
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(line.getBytes());
			loadAndReplayEvents(bais);
			save(neoSaveOptions);
			// unload();
			// close();
			internalLoadFromCBPToNeoEMF(neoLoadOptions);
			bais.close();
			lineCount += 1;
		}
		bufferedReader.close();
		hybridChangeEventAdapter.setEnabled(true);
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		stateBasedResource.save(options);
		saveChangeBasedPersistence(cbpOutputStream, options);
		cbpOutputStream.flush();
	}

	private void internalLoadFromCBPToNeoEMF(Map<?, ?> options) throws IOException {
		hybridChangeEventAdapter.setEnabled(false);
		stateBasedResource.load(options);
		stateBasedResource.eSetDeliver(true);

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
	public void load(Map<?, ?> options) throws IOException {
		hybridChangeEventAdapter.setEnabled(false);
		stateBasedResource.load(options);
		hasJustBeenLoaded = true;
		hybridChangeEventAdapter.setEnabled(true);
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
	public EList<EObject> getContents() {
		if (hasJustBeenLoaded == true) {
			hasJustBeenLoaded = false;
			stateBasedResource.eAdapters().remove(hybridChangeEventAdapter);
			stateBasedResource.eAdapters().add(hybridChangeEventAdapter);
		}

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

			// stateBasedResource.eSetDeliver(true);
			// stateBasedResource.eAdapters().add(hybridChangeEventAdapter);
			// rootObject.eSetDeliver(true);
			// rootObject.eAdapters().add(hybridChangeEventAdapter);

			hybridChangeEventAdapter.setEnabled(true);
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

//	@Override
//	public boolean isDistributed() {
//		// TODO Auto-generated method stub
//		return false;
//	}
}
