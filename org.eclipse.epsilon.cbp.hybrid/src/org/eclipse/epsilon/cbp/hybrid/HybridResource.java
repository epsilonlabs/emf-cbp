package org.eclipse.epsilon.cbp.hybrid;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
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
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EObjectValuesEvent;
import org.eclipse.epsilon.cbp.event.EStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.event.FromPositionEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.StartNewSessionEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.inria.atlanmod.neoemf.core.DefaultPersistentEObject;
import fr.inria.atlanmod.neoemf.core.PersistentEObject;
import fr.inria.atlanmod.neoemf.resource.PersistentResource;

public class HybridResource extends ResourceImpl implements PersistentResource {

	protected int persistedEvents = 0;

	private PersistentResource neoPersistentResource;
	private NeoChangeEventAdapter neoChangeEventAdapter;
	private EStoreEList eStoreEList;
	private DefaultPersistentEObject rootObject;
	private OutputStream outputStream;
	private static final ResourceContentsEStructuralFeature ROOT_CONTENTS_ESTRUCTURALFEATURE = new ResourceContentsEStructuralFeature();

	public PersistentResource getPersistentResource() {
		return neoPersistentResource;
	}

	public HybridResource(PersistentResource persistentResource, OutputStream outputStream) {
		super(persistentResource.getURI());
		this.neoPersistentResource = persistentResource;
		this.uri = persistentResource.getURI();
		this.outputStream = outputStream;

		neoChangeEventAdapter = new NeoChangeEventAdapter(neoPersistentResource);

		eStoreEList = (EStoreEList) this.neoPersistentResource.getContents();
		rootObject = (DefaultPersistentEObject) eStoreEList.getEObject();

		neoPersistentResource.eSetDeliver(true);
		neoPersistentResource.eAdapters().add(neoChangeEventAdapter);

		rootObject.eSetDeliver(true);
		rootObject.eAdapters().add(neoChangeEventAdapter);
	}

	public HybridResource(URI uri) {
		super(uri);
	}
	
	public HybridResource(URI uri, PersistentResource persistenceResource) {
		super(uri);
		this.neoPersistentResource = persistenceResource;
	}

	@Override
	public String getURIFragment(EObject eObject) {
		String id = null;
		if (eObject instanceof PersistentEObject) {
			id = ((PersistentEObject) eObject).id().toString();
		}else {
			id = neoPersistentResource.getURIFragment(eObject);
		}
		return id; 
	}
	
	@Override
	public void save(Map<?, ?> options) throws IOException {
		neoPersistentResource.save(options);
		saveChangeBasedPersistence(outputStream, options);
	}

	@Override
	public void load(Map<?, ?> options) throws IOException {
		neoPersistentResource.load(options);
		neoPersistentResource.eSetDeliver(true);
		neoPersistentResource.eAdapters().add(neoChangeEventAdapter);
	}

	@Override
	public EList<EObject> getContents() {
		// return this.persistentResource.getContents();
		EList<EObject> list = new HybridResourceContentsEStoreEList(rootObject, ROOT_CONTENTS_ESTRUCTURALFEATURE,
				this.neoPersistentResource.eStore());
		return list;
	}

	@Override
	public void close() {
		this.neoPersistentResource.close();

	}

	@Override
	public EStore eStore() {
		return this.neoPersistentResource.eStore();
	}

	@Override
	public EList<EObject> getAllInstances(EClass arg0) {
		return this.neoPersistentResource.getAllInstances(arg0);
	}

	@Override
	public EList<EObject> getAllInstances(EClass arg0, boolean arg1) {
		return this.neoPersistentResource.getAllInstances(arg0, arg1);
	}

	@Override
	protected void doUnload() {
		neoPersistentResource.unload();
	}

	@Override
	public TreeIterator<EObject> getAllContents() {
		// TODO Auto-generated method stub
		return neoPersistentResource.getAllContents();
	}

	private void saveChangeBasedPersistence(OutputStream outputStream, Map<?, ?> options) throws IOException {

		boolean optimised = true;
		if (options != null && options.containsKey("optimise")) {
			optimised = (Boolean) options.get("optimise");
		}

		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			//// alfa
			int eventNumber = persistedEvents;

			for (ChangeEvent<?> event : neoChangeEventAdapter.getChangeEvents().subList(0,
					neoChangeEventAdapter.getChangeEvents().size())) {

				Document document = documentBuilder.newDocument();
				Element e = null;

				if (event instanceof StartNewSessionEvent) {
					StartNewSessionEvent s = ((StartNewSessionEvent) event);
					e = document.createElement("session");
					e.setAttribute("id", s.getSessionId());
					e.setAttribute("time", s.getTime());
				} else if (event instanceof RegisterEPackageEvent) {
					RegisterEPackageEvent r = ((RegisterEPackageEvent) event);
					e = document.createElement("register");
					e.setAttribute("epackage", r.getEPackage().getNsURI());
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(r.getEPackage(), event,
					// eventNumber);
				} else if (event instanceof CreateEObjectEvent) {
					e = document.createElement("create");
					e.setAttribute("epackage", ((CreateEObjectEvent) event).getEClass().getEPackage().getNsURI());
					e.setAttribute("eclass", ((CreateEObjectEvent) event).getEClass().getName());
					e.setAttribute("id", ((CreateEObjectEvent) event).getId());
					EObject eObject = ((CreateEObjectEvent) event).getValue();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber);
				} else if (event instanceof DeleteEObjectEvent) {
					e = document.createElement("delete");
					e.setAttribute("epackage", ((DeleteEObjectEvent) event).getEClass().getEPackage().getNsURI());
					e.setAttribute("eclass", ((DeleteEObjectEvent) event).getEClass().getName());
					EObject eObject = ((DeleteEObjectEvent) event).getValue();
					e.setAttribute("id", getURIFragment(eObject));
					System.out.println();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber);
				} else if (event instanceof AddToResourceEvent) {
					e = document.createElement("add-to-resource");
					EObject eObject = ((AddToResourceEvent) event).getValue();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber);
				} else if (event instanceof RemoveFromResourceEvent) {
					e = document.createElement("remove-from-resource");
					EObject eObject = ((RemoveFromResourceEvent) event).getValue();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber);
				} else if (event instanceof AddToEReferenceEvent) {
					e = document.createElement("add-to-ereference");
					EObject eObject = ((AddToEReferenceEvent) event).getTarget();
					EObject value = ((AddToEReferenceEvent) event).getValue();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber, value);
				} else if (event instanceof RemoveFromEReferenceEvent) {
					e = document.createElement("remove-from-ereference");
					EObject eObject = ((RemoveFromEReferenceEvent) event).getTarget();
					EObject value = ((RemoveFromEReferenceEvent) event).getValue();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber, value);
				} else if (event instanceof SetEAttributeEvent) {
					e = document.createElement("set-eattribute");
					EObject eObject = ((SetEAttributeEvent) event).getTarget();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber);
				} else if (event instanceof SetEReferenceEvent) {
					e = document.createElement("set-ereference");
					EObject eObject = ((SetEReferenceEvent) event).getTarget();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber);
				} else if (event instanceof UnsetEReferenceEvent) {
					e = document.createElement("unset-ereference");
					EObject eObject = ((UnsetEReferenceEvent) event).getTarget();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber);
				} else if (event instanceof UnsetEAttributeEvent) {
					e = document.createElement("unset-eattribute");
					EObject eObject = ((UnsetEAttributeEvent) event).getTarget();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber);
				} else if (event instanceof AddToEAttributeEvent) {
					e = document.createElement("add-to-eattribute");
					EObject eObject = ((AddToEAttributeEvent) event).getTarget();
					Object value = ((AddToEAttributeEvent) event).getValue();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber, value);
				} else if (event instanceof RemoveFromEAttributeEvent) {
					e = document.createElement("remove-from-eattribute");
					EObject eObject = ((RemoveFromEAttributeEvent) event).getTarget();
					Object value = ((RemoveFromEAttributeEvent) event).getValue();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber, value);
				} else if (event instanceof MoveWithinEReferenceEvent) {
					e = document.createElement("move-in-ereference");
					EObject eObject = ((MoveWithinEReferenceEvent) event).getTarget();
					Object values = ((MoveWithinEReferenceEvent) event).getValues();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber, values);
				} else if (event instanceof MoveWithinEAttributeEvent) {
					e = document.createElement("move-in-eattribute");
					EObject eObject = ((MoveWithinEAttributeEvent) event).getTarget();
					Object values = ((MoveWithinEAttributeEvent) event).getValues();
					// if (optimised == true)
					// modelHistory.addObjectHistoryLine(eObject, event,
					// eventNumber, values);
				} else {
					throw new RuntimeException("Unexpected event:" + event);
				}

				if (event instanceof EStructuralFeatureEvent<?>) {
					e.setAttribute("name", ((EStructuralFeatureEvent<?>) event).getEStructuralFeature().getName());
					e.setAttribute("target", getURIFragment(((EStructuralFeatureEvent<?>) event).getTarget()));
				}

				if (event instanceof AddToEReferenceEvent || event instanceof AddToEAttributeEvent
						|| event instanceof AddToResourceEvent) {
					e.setAttribute("position", event.getPosition() + "");
				}
				if (event instanceof FromPositionEvent) {
					e.setAttribute("from", ((FromPositionEvent) event).getFromPosition() + "");
					e.setAttribute("to", event.getPosition() + "");
				}

				if (event instanceof EObjectValuesEvent) {
					for (EObject eObject : ((EObjectValuesEvent) event).getValues()) {
						Element o = document.createElement("value");
						o.setAttribute("eobject", getURIFragment(eObject));
						e.appendChild(o);
					}
				} else if (event instanceof EAttributeEvent) {
					for (Object object : ((EAttributeEvent) event).getValues()) {
						Element o = document.createElement("value");
						o.setAttribute("literal", object + "");
						e.appendChild(o);
					}
				}

				if (e != null)
					document.appendChild(e);

				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(outputStream);
				transformer.transform(source, result);
				outputStream.write(System.getProperty("line.separator").getBytes());

				eventNumber += 1;
			}
			documentBuilder.reset();
			// persistedEvents = getChangeEvents().size();
			persistedEvents = eventNumber;
			neoChangeEventAdapter.getChangeEvents().clear();
		} catch (

		Exception ex) {
			ex.printStackTrace();
			throw new IOException(ex);
		}
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
		}

		@Override
		public boolean add(Object object) {
			EObject eObject = (EObject) object;
			eObject.eSetDeliver(true);
			eObject.eAdapters().add(neoChangeEventAdapter);

			return neoPersistentResource.getContents().add(eObject);
		}

		@Override
		public int size() {
			return neoPersistentResource.getContents().size();
		}

		@Override
		public Iterator<EObject> iterator() {
			// TODO Auto-generated method stub
			return neoPersistentResource.getContents().iterator();
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			neoPersistentResource.getContents().clear();
		}

		@Override
		public EObject get(int index) {
			// TODO Auto-generated method stub
			return neoPersistentResource.getContents().get(index);
		}
	}
}
