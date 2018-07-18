package org.eclipse.epsilon.cbp.hybrid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
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
import org.eclipse.epsilon.cbp.event.ResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.StartNewSessionEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;
import org.eclipse.epsilon.hybrid.event.xmi.CreateEObjectEvent;
import org.eclipse.epsilon.hybrid.event.xmi.DeleteEObjectEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public abstract class HybridResource extends ResourceImpl {

	protected int sessionCount = 1;

	protected int persistedEvents = 0;
	protected int idCounter = 0;

	protected Resource stateBasedResource;
	protected OutputStream cbpOutputStream;

	protected HybridChangeEventAdapter hybridChangeEventAdapter;
	protected BiMap<EObject, String> eObjectToIdMap;
	protected Map<EObject, String> deletedEObjectToIdMap;

	protected boolean hasJustBeenLoaded = false;

	protected IdType idType = IdType.NUMERIC;

	public enum IdType {
		NUMERIC, UUID, FRAGMENT
	}

	public HybridResource() {
		this(null);
	}

	public HybridResource(URI uri) {
		super(uri);
		this.eObjectToIdMap = HashBiMap.create();
		this.deletedEObjectToIdMap = new HashMap<>();
	}

	public Resource getStateBasedResource() {
		return stateBasedResource;
	}

	public BiMap<EObject, String> getEObjectToIdMap() {
		return eObjectToIdMap;
	}

	public void loadFromCBP(InputStream inputStream) throws FactoryConfigurationError, IOException {
		hybridChangeEventAdapter.setEnabled(false);
		deletedEObjectToIdMap.clear();
		eObjectToIdMap.clear();
		getChangeEvents().clear();
		persistedEvents = 0;
		replayEvents(inputStream);
		// TreeIterator<EObject> iterator = this.getAllContents();
		// while (iterator.hasNext()) {
		// EObject eObject = iterator.next();
		// String id = eObjectToIdMap.get(eObject);
		// ((XMIResourceImpl) stateBasedResource).setID(eObject, id);
		// }
		hybridChangeEventAdapter.setEnabled(true);
	}

	public void loadAndReplayEvents(InputStream inputStream) throws IOException {
		hybridChangeEventAdapter.setEnabled(false);
		this.replayEvents(inputStream);
		hybridChangeEventAdapter.setEnabled(true);
	}

	private void replayEvents(InputStream inputStream) throws FactoryConfigurationError, IOException {
		String errorMessage = null;
		int eventNumber = persistedEvents;
		try {
			InputStream stream = new ByteArrayInputStream(new byte[0]);

			ByteArrayInputStream header = new ByteArrayInputStream(
					"<?xml version='1.0' encoding='ISO-8859-1' ?>".getBytes());
			ByteArrayInputStream begin = new ByteArrayInputStream("<m>".getBytes());
			ByteArrayInputStream end = new ByteArrayInputStream("</m>".getBytes());
			stream = new SequenceInputStream(stream, header);
			stream = new SequenceInputStream(stream, begin);
			stream = new SequenceInputStream(stream, inputStream);
			stream = new SequenceInputStream(stream, end);
			
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(stream);

			ChangeEvent<?> event = null;

			while (xmlReader.hasNext()) {
				XMLEvent xmlEvent = xmlReader.nextEvent();
				if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
					StartElement e = xmlEvent.asStartElement();
					String name = e.getName().getLocalPart();

					if (name.equals("m")) {
						continue;
					}

					if (!name.equals("value")) {

						errorMessage = name;
						switch (name) {
						case "session": {
							sessionCount += 1;
							String sessionId = e.getAttributeByName(new QName("id")).getValue();
							String time = e.getAttributeByName(new QName("time")).getValue();
							event = new StartNewSessionEvent(sessionId, time);
						}
							break;
						case "register": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							errorMessage = errorMessage + ", package: " + packageName;
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							event = new RegisterEPackageEvent(ePackage, hybridChangeEventAdapter);
						}
							break;
						case "create": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							String className = e.getAttributeByName(new QName("eclass")).getValue();
							String id = e.getAttributeByName(new QName("id")).getValue();
							errorMessage = errorMessage + ", package: " + packageName + ", class: " + className
									+ ", id: " + id;
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							event = new CreateEObjectEvent(eClass, this, id);

						}
							break;
						case "add-to-resource":
							event = new AddToResourceEvent();
							break;
						case "remove-from-resource":
							event = new RemoveFromResourceEvent();
							break;
						case "add-to-ereference":
							event = new AddToEReferenceEvent();
							break;
						case "remove-from-ereference":
							event = new RemoveFromEReferenceEvent();
							break;
						case "set-eattribute":
							event = new SetEAttributeEvent();
							break;
						case "set-ereference":
							event = new SetEReferenceEvent();
							break;
						case "unset-eattribute":
							event = new UnsetEAttributeEvent();
							break;
						case "unset-ereference":
							event = new UnsetEReferenceEvent();
							break;
						case "add-to-eattribute":
							event = new AddToEAttributeEvent();
							break;
						case "remove-from-eattribute":
							event = new RemoveFromEAttributeEvent();
							break;
						case "move-in-eattribute":
							event = new MoveWithinEAttributeEvent();
							break;
						case "move-in-ereference":
							event = new MoveWithinEReferenceEvent();
							break;
						case "delete": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							String className = e.getAttributeByName(new QName("eclass")).getValue();
							String id = e.getAttributeByName(new QName("id")).getValue();
							errorMessage = errorMessage + ", package: " + packageName + ", class: " + className
									+ ", id: " + id;
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							event = new DeleteEObjectEvent(eClass, this, id);

						}
							break;
						}

						if (event instanceof EStructuralFeatureEvent<?>) {
							String sTarget = e.getAttributeByName(new QName("target")).getValue();
							String sName = e.getAttributeByName(new QName("name")).getValue();
							errorMessage = errorMessage + ", target: " + sTarget + ", name: " + sName;
							EObject target = getEObject(sTarget);
							EStructuralFeature eStructuralFeature = target.eClass().getEStructuralFeature(sName);
							((EStructuralFeatureEvent<?>) event).setEStructuralFeature(eStructuralFeature);
							((EStructuralFeatureEvent<?>) event).setTarget(target);
						} else if (event instanceof ResourceEvent) {
							((ResourceEvent) event).setResource(this);
						}

						if (event instanceof AddToEAttributeEvent || event instanceof AddToEReferenceEvent
								|| event instanceof AddToResourceEvent) {
							String sPosition = e.getAttributeByName(new QName("position")).getValue();
							errorMessage = errorMessage + ", target: " + sPosition;
							event.setPosition(Integer.parseInt(sPosition));
						}
						if (event instanceof FromPositionEvent) {
							String sTo = e.getAttributeByName(new QName("to")).getValue();
							errorMessage = errorMessage + ", to: " + sTo;
							String sFrom = e.getAttributeByName(new QName("from")).getValue();
							errorMessage = errorMessage + ", from: " + sFrom;
							event.setPosition(Integer.parseInt(sTo));
							((FromPositionEvent) event).setFromPosition(Integer.parseInt(sFrom));
						}

					} else if (name.equals("value")) {

						if (event instanceof EObjectValuesEvent) {
							EObjectValuesEvent valuesEvent = (EObjectValuesEvent) event;
							String seobject = e.getAttributeByName(new QName("eobject")).getValue();
							errorMessage = errorMessage + ", value: " + seobject;
							EObject eob = resolveXRef(seobject);
							valuesEvent.getValues().add(eob);
						} else if (event instanceof EAttributeEvent) {
							EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
							String sliteral = e.getAttributeByName(new QName("literal")).getValue();
							errorMessage = errorMessage + ", value: " + sliteral;
							EDataType eDataType = ((EDataType) eAttributeEvent.getEStructuralFeature().getEType());
							Object value = eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType,
									sliteral);
							eAttributeEvent.getValues().add(value);
						}

					}
				}
				if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
					EndElement ee = xmlEvent.asEndElement();
					String name = ee.getName().getLocalPart();
					if (event != null && !name.equals("value") && !name.equals("m")) {

						// if (eventNumber % 1000 == 0)
						// System.out.println(eventNumber);

						event.replay();
						errorMessage = "";
						eventNumber += 1;
					}
				}
			}
			begin.close();
			end.close();
			inputStream.close();
			stream.close();
			xmlReader.close();

			persistedEvents = eventNumber;

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: Event Number " + eventNumber + " : " + errorMessage);
			throw new IOException(
					"Error: Event Number " + eventNumber + " : " + errorMessage + "\n" + ex.toString() + "\n");
		}
	}

	public void saveChangeBasedPersistence(OutputStream outputStream, Map<?, ?> options) throws IOException {

		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			//// alfa
			int eventNumber = persistedEvents;

			for (ChangeEvent<?> event : hybridChangeEventAdapter.getChangeEvents().subList(0,
					hybridChangeEventAdapter.getChangeEvents().size())) {

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
				} else if (event instanceof CreateEObjectEvent) {
					e = document.createElement("create");
					e.setAttribute("epackage", ((CreateEObjectEvent) event).getEClass().getEPackage().getNsURI());
					e.setAttribute("eclass", ((CreateEObjectEvent) event).getEClass().getName());
					e.setAttribute("id", ((CreateEObjectEvent) event).getId());
				} else if (event instanceof DeleteEObjectEvent) {
					e = document.createElement("delete");
					e.setAttribute("epackage", ((DeleteEObjectEvent) event).getEClass().getEPackage().getNsURI());
					e.setAttribute("eclass", ((DeleteEObjectEvent) event).getEClass().getName());
					e.setAttribute("id", ((DeleteEObjectEvent) event).getId());
					// EObject eObject = ((DeleteEObjectEvent)
					// event).getValue();
					// e.setAttribute("id", getURIFragment(eObject));
				} else if (event instanceof AddToResourceEvent) {
					e = document.createElement("add-to-resource");
				} else if (event instanceof RemoveFromResourceEvent) {
					e = document.createElement("remove-from-resource");
				} else if (event instanceof AddToEReferenceEvent) {
					e = document.createElement("add-to-ereference");
				} else if (event instanceof RemoveFromEReferenceEvent) {
					e = document.createElement("remove-from-ereference");
				} else if (event instanceof SetEAttributeEvent) {
					e = document.createElement("set-eattribute");
				} else if (event instanceof SetEReferenceEvent) {
					e = document.createElement("set-ereference");
				} else if (event instanceof UnsetEReferenceEvent) {
					e = document.createElement("unset-ereference");
				} else if (event instanceof UnsetEAttributeEvent) {
					e = document.createElement("unset-eattribute");
				} else if (event instanceof AddToEAttributeEvent) {
					e = document.createElement("add-to-eattribute");
				} else if (event instanceof RemoveFromEAttributeEvent) {
					e = document.createElement("remove-from-eattribute");
				} else if (event instanceof MoveWithinEReferenceEvent) {
					e = document.createElement("move-in-ereference");
				} else if (event instanceof MoveWithinEAttributeEvent) {
					e = document.createElement("move-in-eattribute");
				} else {
					throw new RuntimeException("Unexpected event:" + event);
				}

				if (event instanceof EStructuralFeatureEvent<?>) {
					if (((EStructuralFeatureEvent<?>) event).getEStructuralFeature() != null) {
						e.setAttribute("name", ((EStructuralFeatureEvent<?>) event).getEStructuralFeature().getName());
					} else {
						e.setAttribute("name", null);
					}
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
				outputStream.flush();

				eventNumber += 1;
			}
			documentBuilder.reset();
			persistedEvents = eventNumber;
			hybridChangeEventAdapter.getChangeEvents().clear();
		} catch (

		Exception ex) {
			ex.printStackTrace();
			throw new IOException(ex);
		}
	}

	protected EObject resolveXRef(final String sEObjectURI) {
		EObject eob = getEObject(sEObjectURI);
		if (eob == null) {
			URI uri = URI.createURI(sEObjectURI);

			String nsURI = uri.trimFragment().toString();
			EPackage pkg = (EPackage) getResourceSet().getPackageRegistry().get(nsURI);
			if (pkg != null) {
				eob = pkg.eResource().getEObject(uri.fragment());
			}
		}
		return eob;
	}

	@Override
	protected void doUnload() {
		super.doUnload();
		stateBasedResource.unload();
		eObjectToIdMap.clear();
		deletedEObjectToIdMap.clear();
		hybridChangeEventAdapter.getChangeEvents().clear();
		hasJustBeenLoaded = false;
		try {
			cbpOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getURIFragment(EObject eObject) {
		String uriFragment = null;
		if (eObjectToIdMap == null) {
			uriFragment = null;
		} else {
			uriFragment = eObjectToIdMap.get(eObject);
		}
		if (uriFragment == null) {
			uriFragment = deletedEObjectToIdMap.get(eObject);
		}
		if (uriFragment == null) {
			uriFragment = stateBasedResource.getURIFragment(eObject);
		}
		return uriFragment;
	}

	public String register(EObject eObject) {
		String id = null;
		if (idType == IdType.NUMERIC) {
			while (eObjectToIdMap.containsValue(String.valueOf(idCounter))) {
				idCounter += 1;
			}
			id = String.valueOf(idCounter);
			idCounter = idCounter + 1;
		} else if (idType == IdType.UUID) {
			id = EcoreUtil.generateUUID();
		} else if (idType == IdType.FRAGMENT) {
			id = getURIFragment(eObject);
		}
		adopt(eObject, id);
		return id;
	}

	public String register(EObject eObject, String id) {
		adopt(eObject, id);
		return id;
	}

	public void unregister(String objectId) {
		// if (eObjectToIdMap.containsValue(objectId)) {
		// EObject eObject = eObjectToIdMap.inverse().get(objectId);
		// deletedEObjctToIdMap.put(eObject, objectId);
		// }
		eObjectToIdMap.inverse().remove(objectId);
	}

	public void unregister(EObject eObject) {
		// if (eObjectToIdMap.containsKey(eObject)) {
		// String id = eObjectToIdMap.get(eObject);
		// deletedEObjctToIdMap.put(eObject, id);
		// }
		eObjectToIdMap.remove(eObject);
	}

	public boolean isRegistered(String objectId) {
		return eObjectToIdMap.containsValue(objectId);
	}

	public boolean isRegistered(EObject eObject) {
		return eObjectToIdMap.containsKey(eObject);
	}

	public void adopt(EObject eObject, String id) {
		if (eObjectToIdMap.containsValue(id)) {
			EObject oldEObject = eObjectToIdMap.inverse().get(id);
			deletedEObjectToIdMap.put(oldEObject, id);
			eObjectToIdMap.inverse().remove(id);
		}
		eObjectToIdMap.put(eObject, id);
	}

	@Override
	public URI getURI() {
		return stateBasedResource.getURI();
	}

	@Override
	public TreeIterator<EObject> getAllContents() {
		return stateBasedResource.getAllContents();
	}

	public void startNewSession(String id) {
		hybridChangeEventAdapter.handleStartNewSession(id);
	}

	public List<ChangeEvent<?>> getChangeEvents() {
		return hybridChangeEventAdapter.getChangeEvents();
	}

	@Override
	public EList<EObject> getContents() {
		EList<EObject> list = stateBasedResource.getContents();
		return list;
	}

	@Override
	public EObject getEObject(String uriFragment) {
		return eObjectToIdMap.inverse().get(uriFragment);
	}

	public String getEObjectId(EObject eObject) {
		if (eObjectToIdMap.containsKey(eObject)) {
			return eObjectToIdMap.get(eObject);
		}
		return null;
	}

	public OutputStream getCBPOutputStream() {
		return cbpOutputStream;
	}

	public void deleteElement(EObject targetEObject) {
		this.startCompositeEvent();

		recursiveDeleteEvent(targetEObject);
		removeFromExternalRefferences(targetEObject);
		unsetAllReferences(targetEObject);
		unsetAllAttributes(targetEObject);
		EcoreUtil.remove(targetEObject);

		this.endCompositeEvent();
	}

	private void recursiveDeleteEvent(EObject targetEObject) {
		for (EReference eRef : targetEObject.eClass().getEAllReferences()) {
			if (eRef.isChangeable() && targetEObject.eIsSet(eRef) && !eRef.isDerived() && eRef.isContainment()) {
				if (eRef.isMany()) {
					List<EObject> values = (List<EObject>) targetEObject.eGet(eRef);
					while (values.size() > 0) {
						EObject value = values.get(values.size() - 1);
						recursiveDeleteEvent(value);
						removeFromExternalRefferences(value);
						unsetAllReferences(value);
						unsetAllAttributes(value);
						values.remove(value);
					}
				} else {
					EObject value = (EObject) targetEObject.eGet(eRef);
					if (value != null) {
						recursiveDeleteEvent(value);
						removeFromExternalRefferences(value);
						unsetAllReferences(value);
						unsetAllAttributes(value);
						targetEObject.eUnset(eRef);
					}
				}
			}
		}
	}

	private void unsetAllReferences(EObject targetEObject) {
		for (EReference eRef : targetEObject.eClass().getEAllReferences()) {
			if (eRef.isChangeable() && targetEObject.eIsSet(eRef) && !eRef.isDerived()
					&& eRef.isContainment() == false) {
				if (eRef.isMany()) {
					List<EObject> values = (List<EObject>) targetEObject.eGet(eRef);
					while (values.size() > 0) {
						EObject value = values.get(values.size() - 1);
						values.remove(value);
					}
				} else {
					EObject value = (EObject) targetEObject.eGet(eRef);
					if (value != null) {
						targetEObject.eUnset(eRef);
					}
				}
			}
		}
	}

	private void unsetAllAttributes(EObject targetEObject) {
		for (EAttribute eAttr : targetEObject.eClass().getEAllAttributes()) {
			if (eAttr.isChangeable() && targetEObject.eIsSet(eAttr) && !eAttr.isDerived()) {
				if (eAttr.isMany()) {
					EList<?> valueList = (EList<?>) targetEObject.eGet(eAttr);
					while (valueList.size() > 0) {
						valueList.remove(valueList.size() - 1);
					}
				} else {
					Object value = targetEObject.eGet(eAttr);
					targetEObject.eUnset(eAttr);
				}
			}
		}
	}

	private void removeFromExternalRefferences(EObject refferedEObject) {
		Iterator<EObject> iterator = this.getAllContents();
		while (iterator.hasNext()) {
			EObject refferingEObject = iterator.next();
			for (EReference eRef : refferingEObject.eClass().getEAllReferences()) {
				if (eRef.isContainment() == false) {
					if (eRef.isMany()) {
						List<EObject> valueList = (List<EObject>) refferingEObject.eGet(eRef);
						valueList.remove(refferedEObject);
					} else {
						EObject value = (EObject) refferingEObject.eGet(eRef);
						if (value != null && value.equals(refferedEObject)) {
							refferingEObject.eUnset(eRef);
						}
					}
				}
			}
		}
	}

	public void startCompositeEvent() {
		getHybridChangeEventAdapter().startCompositeOperation();
	}

	public void endCompositeEvent() {
		getHybridChangeEventAdapter().endCompositeOperation();
	}

	public HybridChangeEventAdapter getHybridChangeEventAdapter() {
		return hybridChangeEventAdapter;
	}

	public void setHybridChangeEventAdapter(HybridChangeEventAdapter hybridChangeEventAdapter) {
		this.hybridChangeEventAdapter = hybridChangeEventAdapter;
	}

	public void closeCBPOutputStream() throws IOException {
		cbpOutputStream.close();
	}

	public void openCBPOutputStream(OutputStream cbpOutputStream) {
		this.cbpOutputStream = cbpOutputStream;
	}
}
