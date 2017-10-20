package org.eclipse.epsilon.cbp.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
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
import org.eclipse.epsilon.cbp.event.ResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.StartNewSessionEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CBPXMLResourceImpl extends CBPResource {

	protected int setAttCount = 0;
	protected int unsetAttCount = 0;
	protected int addAttCount = 0;
	protected int removeAttCount = 0;
	protected int moveAttCount = 0;
	protected int setRefCount = 0;
	protected int unsetRefCount = 0;
	protected int addRefCount = 0;
	protected int removeRefCount = 0;
	protected int moveRefCount = 0;
	protected int deleteCount = 0;
	protected int addResCount = 0;
	protected int removeResCount = 0;
	protected int packageCount = 0;
	protected int sessionCount = 0;
	protected int createCount = 0;

	protected double setAttAvg = 0;
	protected double unsetAttAvg = 0;
	protected double addAttAvg = 0;
	protected double removeAttAvg = 0;
	protected double moveAttAvg = 0;
	protected double setRefAvg = 0;
	protected double unsetRefAvg = 0;
	protected double addRefAvg = 0;
	protected double removeRefAvg = 0;
	protected double moveRefAvg = 0;
	protected double deleteAvg = 0;
	protected double addResAvg = 0;
	protected double removeResAvg = 0;
	protected double packageAvg = 0;
	protected double sessionAvg = 0;
	protected double createAvg = 0;

	protected long setAttSum = 0;
	protected long unsetAttSum = 0;
	protected long addAttSum = 0;
	protected long removeAttSum = 0;
	protected long moveAttSum = 0;
	protected long setRefSum = 0;
	protected long unsetRefSum = 0;
	protected long addRefSum = 0;
	protected long removeRefSum = 0;
	protected long moveRefSum = 0;
	protected long deleteSum = 0;
	protected long addResSum = 0;
	protected long removeResSum = 0;
	protected long packageSum = 0;
	protected long sessionSum = 0;
	protected long createSum = 0;

	boolean newLine = true;

	public static void main(String[] args) throws Exception {
		EPackage.Registry.INSTANCE.put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		CBPResource resource = new CBPXMLResourceImpl();

		EClass c1 = EcoreFactory.eINSTANCE.createEClass();
		EClass c2 = EcoreFactory.eINSTANCE.createEClass();
		resource.getContents().add(c1);
		resource.getContents().add(c2);
		c1.getESuperTypes().add(c2);
		EClass c3 = EcoreFactory.eINSTANCE.createEClass();
		resource.getContents().add(c3);
		c2.getESuperTypes().add(c3);

		StringOutputStream sos = new StringOutputStream();
		resource.save(sos, null);
		System.out.println(sos.toString());

		resource = new CBPXMLResourceImpl();
		resource.load(sos.getInputStream(), null);
		// resource.save(System.out, null);
	}

	protected int persistedEvents = 0;
	private long beforeEvent;

	public CBPXMLResourceImpl() {
		super();
	}

	public CBPXMLResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	public void doSave(OutputStream out, Map<?, ?> options) throws IOException {

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

			// Ignore the first #eventsAfterMostRecentLoad events
//			for (ChangeEvent<?> event : getChangeEvents().subList(persistedEvents, getChangeEvents().size())) {
			for (ChangeEvent<?> event : getChangeEvents().subList(0, getChangeEvents().size())) {

				Document document = documentBuilder.newDocument();
				Element e = null;

				if (event instanceof StartNewSessionEvent) {
					StartNewSessionEvent s = ((StartNewSessionEvent) event);
					e = document.createElement("session");
					e.setAttribute("id", s.getSessionId());
				} else if (event instanceof RegisterEPackageEvent) {
					RegisterEPackageEvent r = ((RegisterEPackageEvent) event);
					e = document.createElement("register");
					e.setAttribute("epackage", r.getEPackage().getNsURI());
					if (optimised == true)
						modelHistory.addObjectHistoryLine(r.getEPackage(), event, eventNumber);
				} else if (event instanceof CreateEObjectEvent) {
					e = document.createElement("create");
					e.setAttribute("epackage", ((CreateEObjectEvent) event).getEClass().getEPackage().getNsURI());
					e.setAttribute("eclass", ((CreateEObjectEvent) event).getEClass().getName());
					e.setAttribute("id", ((CreateEObjectEvent) event).getId());
					EObject eObject = ((CreateEObjectEvent) event).getValue();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
				} else if (event instanceof DeleteEObjectEvent) {
					e = document.createElement("delete");
					e.setAttribute("epackage", ((DeleteEObjectEvent) event).getEClass().getEPackage().getNsURI());
					e.setAttribute("eclass", ((DeleteEObjectEvent) event).getEClass().getName());
					e.setAttribute("id", ((DeleteEObjectEvent) event).getId());
					EObject eObject = ((DeleteEObjectEvent) event).getValue();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
				} else if (event instanceof AddToResourceEvent) {
					e = document.createElement("add-to-resource");
					EObject eObject = ((AddToResourceEvent) event).getValue();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
				} else if (event instanceof RemoveFromResourceEvent) {
					e = document.createElement("remove-from-resource");
					EObject eObject = ((RemoveFromResourceEvent) event).getValue();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
				} else if (event instanceof AddToEReferenceEvent) {
					e = document.createElement("add-to-ereference");
					EObject eObject = ((AddToEReferenceEvent) event).getTarget();
					EObject value = ((AddToEReferenceEvent) event).getValue();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber, value);
				} else if (event instanceof RemoveFromEReferenceEvent) {
					e = document.createElement("remove-from-ereference");
					EObject eObject = ((RemoveFromEReferenceEvent) event).getTarget();
					EObject value = ((RemoveFromEReferenceEvent) event).getValue();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber, value);
				} else if (event instanceof SetEAttributeEvent) {
					e = document.createElement("set-eattribute");
					EObject eObject = ((SetEAttributeEvent) event).getTarget();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
				} else if (event instanceof SetEReferenceEvent) {
					e = document.createElement("set-ereference");
					EObject eObject = ((SetEReferenceEvent) event).getTarget();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
				} else if (event instanceof UnsetEReferenceEvent) {
					e = document.createElement("unset-ereference");
					EObject eObject = ((UnsetEReferenceEvent) event).getTarget();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
				} else if (event instanceof UnsetEAttributeEvent) {
					e = document.createElement("unset-eattribute");
					EObject eObject = ((UnsetEAttributeEvent) event).getTarget();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
				} else if (event instanceof AddToEAttributeEvent) {
					e = document.createElement("add-to-eattribute");
					EObject eObject = ((AddToEAttributeEvent) event).getTarget();
					Object value = ((AddToEAttributeEvent) event).getValue();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber, value);
				} else if (event instanceof RemoveFromEAttributeEvent) {
					e = document.createElement("remove-from-eattribute");
					EObject eObject = ((RemoveFromEAttributeEvent) event).getTarget();
					Object value = ((RemoveFromEAttributeEvent) event).getValue();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber, value);
				} else if (event instanceof MoveWithinEReferenceEvent) {
					e = document.createElement("move-in-ereference");
					EObject eObject = ((MoveWithinEReferenceEvent) event).getTarget();
					Object values = ((MoveWithinEReferenceEvent) event).getValues();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber, values);
				} else if (event instanceof MoveWithinEAttributeEvent) {
					e = document.createElement("move-in-eattribute");
					EObject eObject = ((MoveWithinEAttributeEvent) event).getTarget();
					Object values = ((MoveWithinEAttributeEvent) event).getValues();
					if (optimised == true)
						modelHistory.addObjectHistoryLine(eObject, event, eventNumber, values);
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
				StreamResult result = new StreamResult(out);
				transformer.transform(source, result);
				out.write(System.getProperty("line.separator").getBytes());

				eventNumber += 1;
			}
			documentBuilder.reset();
			//persistedEvents = getChangeEvents().size();
			persistedEvents = eventNumber;
			getChangeEvents().clear();
		} catch (

		Exception ex) {
			ex.printStackTrace();
			throw new IOException(ex);
		}
	}

	@Override
	public void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {

		setAttCount = 0;
		unsetAttCount = 0;
		addAttCount = 0;
		removeAttCount = 0;
		moveAttCount = 0;
		setRefCount = 0;
		unsetRefCount = 0;
		addRefCount = 0;
		removeRefCount = 0;
		moveRefCount = 0;
		deleteCount = 0;
		addResCount = 0;
		removeResCount = 0;
		packageCount = 0;
		sessionCount = 0;
		createCount = 0;

		setAttAvg = 0;
		unsetAttAvg = 0;
		addAttAvg = 0;
		removeAttAvg = 0;
		moveAttAvg = 0;
		setRefAvg = 0;
		unsetRefAvg = 0;
		addRefAvg = 0;
		removeRefAvg = 0;
		moveRefAvg = 0;
		deleteAvg = 0;
		addResAvg = 0;
		removeResAvg = 0;
		packageAvg = 0;
		sessionAvg = 0;
		createAvg = 0;

		setAttSum = 0;
		unsetAttSum = 0;
		addAttSum = 0;
		removeAttSum = 0;
		moveAttSum = 0;
		setRefSum = 0;
		unsetRefSum = 0;
		addRefSum = 0;
		removeRefSum = 0;
		moveRefSum = 0;
		deleteSum = 0;
		addResSum = 0;
		removeResSum = 0;
		packageSum = 0;
		sessionSum = 0;
		createSum = 0;

		changeEventAdapter.setEnabled(false);
		eObjectToIdMap.clear();
		getChangeEvents().clear();

		String errorMessage = null;
		boolean optimised = true;
		if (options != null && options.containsKey("optimise")) {
			optimised = (Boolean) options.get("optimise");
		}

		int eventNumber = 0;
		try {
			ByteArrayInputStream begin = new ByteArrayInputStream("<m>".getBytes());
			ByteArrayInputStream end = new ByteArrayInputStream("</m>".getBytes());
			InputStream stream = new SequenceInputStream(begin, inputStream);
			stream = new SequenceInputStream(stream, end);

			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(stream);

			ChangeEvent<?> event = null;
			boolean ignore = false;

			while (xmlReader.hasNext()) {
				XMLEvent xmlEvent = xmlReader.nextEvent();
				if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
					StartElement e = xmlEvent.asStartElement();
					String name = e.getName().getLocalPart();

					if (name.equals("m")) {
						continue;
					}

					if (!name.equals("value")) {
						if (optimised == true) {
							if (ignoreSet.remove(eventNumber)) { // remove is to
																	// save
																	// memory
								ignore = true;
								// if (ignoreSet.size() > 0)
								// ignoreSet.remove(o);
							}
						}

						if (ignore == false) {
							beforeEvent = System.nanoTime();
							errorMessage = name;
							switch (name) {
							case "session":
								event = new StartNewSessionEvent();
								break;
							case "register": {
								String packageName = e.getAttributeByName(new QName("epackage")).getValue();
								errorMessage = errorMessage + ", package: " + packageName;
								EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
								event = new RegisterEPackageEvent(ePackage, changeEventAdapter);
								break;
							}
							case "create": {
								String packageName = e.getAttributeByName(new QName("epackage")).getValue();
								String className = e.getAttributeByName(new QName("eclass")).getValue();
								String id = e.getAttributeByName(new QName("id")).getValue();
								errorMessage = errorMessage + ", package: " + packageName + ", class: " + className
										+ ", id: " + id;
								EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
								EClass eClass = (EClass) ePackage.getEClassifier(className);
								event = new CreateEObjectEvent(eClass, this, id);
								break;
							}
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
								break;
							}
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
						}

					} else if (name.equals("value")) {
						if (ignore == false) {
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
				}
				if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
					EndElement ee = xmlEvent.asEndElement();
					String name = ee.getName().getLocalPart();
					if (event != null && !name.equals("value") && !name.equals("m")) {
						if (ignore == false) {
							event.replay();
//							repopulateModelHistory(event, eventNumber);
//							getChangeEvents().add(event);
							errorMessage = "";

							long afterEvent = System.nanoTime();
							long eventDuration = afterEvent - beforeEvent;

							// ----
							switch (name) {
							case "session":
								sessionCount++;
								sessionSum += eventDuration;
								sessionAvg = sessionSum / sessionCount;
								break;
							case "register":
								packageCount++;
								packageSum += eventDuration;
								packageAvg = packageSum / packageCount;
								break;
							case "create":
								createCount++;
								createSum += eventDuration;
								createAvg = createSum / createCount;
								break;
							case "add-to-resource":
								addResCount++;
								addResSum += eventDuration;
								addResAvg = addResSum / addResCount;
								break;
							case "remove-from-resource":
								removeResCount++;
								removeResSum += eventDuration;
								removeResAvg = removeResSum / removeResCount;
								break;
							case "add-to-ereference":
								addRefCount++;
								addRefSum += eventDuration;
								addRefAvg = addRefSum / addRefCount;
								break;
							case "remove-from-ereference":
								removeRefCount++;
								removeRefSum += eventDuration;
								removeRefAvg = removeRefSum / removeRefCount;
								break;
							case "set-eattribute":
								setAttCount++;
								setAttSum += eventDuration;
								setAttAvg = setAttSum / setAttCount;
								break;
							case "set-ereference":
								setRefCount++;
								setRefSum += eventDuration;
								setRefAvg = setRefSum / setRefCount;
								break;
							case "unset-eattribute":
								unsetAttCount++;
								unsetAttSum += eventDuration;
								unsetAttAvg = unsetAttSum / unsetAttCount;
								break;
							case "unset-ereference":
								unsetRefCount++;
								unsetRefSum += eventDuration;
								unsetRefAvg = unsetRefSum / unsetRefCount;
								break;
							case "add-to-eattribute":
								addAttCount++;
								addAttSum += eventDuration;
								addAttAvg = addAttSum / addAttCount;
								break;
							case "remove-from-eattribute":
								removeAttCount++;
								removeAttSum += eventDuration;
								removeAttAvg = removeAttSum / removeAttCount;
								break;
							case "move-in-eattribute":
								moveAttCount++;
								moveAttSum += eventDuration;
								moveAttAvg = moveAttSum / moveAttCount;
								break;
							case "move-in-ereference":
								moveRefCount++;
								moveRefSum += eventDuration;
								moveRefAvg = moveRefSum / moveRefCount;
								break;
							case "delete":
								deleteCount++;
								deleteSum += eventDuration;
								deleteAvg = deleteSum / deleteCount;
								break;
							}
							// ---

						} else {
							ignore = false;
						}
						eventNumber += 1;
					}
				}
			}
			//persistedEvents = getChangeEvents().size();
			persistedEvents = eventNumber;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: " + eventNumber + " : " + errorMessage);
			throw new IOException("Error: " + eventNumber + " : " + errorMessage + "\n" + ex.toString() + "\n");
		}

		changeEventAdapter.setEnabled(true);
	}

	protected ChangeEvent<?> buildEvent(Element e, String name) {
		switch (name) {
		case "register": {
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(e.getAttribute("epackage"));
			return new RegisterEPackageEvent(ePackage, changeEventAdapter);
		}
		case "create": {
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(e.getAttribute("epackage"));
			EClass eClass = (EClass) ePackage.getEClassifier(e.getAttribute("eclass"));
			return new CreateEObjectEvent(eClass, this, e.getAttribute("id"));
		}
		case "add-to-resource":
			return new AddToResourceEvent();
		case "remove-from-resource":
			return new RemoveFromResourceEvent();
		case "add-to-ereference":
			return new AddToEReferenceEvent();
		case "remove-from-ereference":
			return new RemoveFromEReferenceEvent();
		case "set-eattribute":
			return new SetEAttributeEvent();
		case "set-ereference":
			return new SetEReferenceEvent();
		case "unset-eattribute":
			return new UnsetEAttributeEvent();
		case "unset-ereference":
			return new UnsetEReferenceEvent();
		case "add-to-eattribute":
			return new AddToEAttributeEvent();
		case "remove-from-eattribute":
			return new RemoveFromEAttributeEvent();
		case "move-in-eattribute":
			return new MoveWithinEAttributeEvent();
		case "move-in-ereference":
			return new MoveWithinEReferenceEvent();
		case "delete": {
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(e.getAttribute("epackage"));
			EClass eClass = (EClass) ePackage.getEClassifier(e.getAttribute("eclass"));
			return new DeleteEObjectEvent(eClass, this, e.getAttribute("id"));
		}
		case "session": {
			return new StartNewSessionEvent(e.getAttribute("id"));
		}
		}

		return null;
	}

	protected Object getLiteralValue(EObject eObject, EStructuralFeature eStructuralFeature, Element e) {
		EDataType eDataType = ((EDataType) eStructuralFeature.getEType());
		return eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType, e.getAttribute("literal"));
	}
	
	protected void repopulateModelHistory(ChangeEvent<?> event, int eventNumber) {
		if (event instanceof StartNewSessionEvent) {
		} else if (event instanceof RegisterEPackageEvent) {
			RegisterEPackageEvent r = ((RegisterEPackageEvent) event);
			modelHistory.addObjectHistoryLine(r.getEPackage(), event, eventNumber);
		} else if (event instanceof CreateEObjectEvent) {
			EObject eObject = ((CreateEObjectEvent) event).getValue();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
		} else if (event instanceof DeleteEObjectEvent) {
			EObject eObject = ((DeleteEObjectEvent) event).getValue();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
		} else if (event instanceof AddToResourceEvent) {
			EObject eObject = ((AddToResourceEvent) event).getValue();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
		} else if (event instanceof RemoveFromResourceEvent) {
			EObject eObject = ((RemoveFromResourceEvent) event).getValue();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
		} else if (event instanceof AddToEReferenceEvent) {
			EObject eObject = ((AddToEReferenceEvent) event).getTarget();
			EObject value = ((AddToEReferenceEvent) event).getValue();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber, value);
		} else if (event instanceof RemoveFromEReferenceEvent) {
			EObject eObject = ((RemoveFromEReferenceEvent) event).getTarget();
			EObject value = ((RemoveFromEReferenceEvent) event).getValue();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber, value);
		} else if (event instanceof SetEAttributeEvent) {
			EObject eObject = ((SetEAttributeEvent) event).getTarget();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
		} else if (event instanceof SetEReferenceEvent) {
			EObject eObject = ((SetEReferenceEvent) event).getTarget();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
		} else if (event instanceof UnsetEReferenceEvent) {
			EObject eObject = ((UnsetEReferenceEvent) event).getTarget();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
		} else if (event instanceof UnsetEAttributeEvent) {
			EObject eObject = ((UnsetEAttributeEvent) event).getTarget();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber);
		} else if (event instanceof AddToEAttributeEvent) {
			EObject eObject = ((AddToEAttributeEvent) event).getTarget();
			Object value = ((AddToEAttributeEvent) event).getValue();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber, value);
		} else if (event instanceof RemoveFromEAttributeEvent) {
			EObject eObject = ((RemoveFromEAttributeEvent) event).getTarget();
			Object value = ((RemoveFromEAttributeEvent) event).getValue();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber, value);
		} else if (event instanceof MoveWithinEReferenceEvent) {
			EObject eObject = ((MoveWithinEReferenceEvent) event).getTarget();
			Object values = ((MoveWithinEReferenceEvent) event).getValues();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber, values);
		} else if (event instanceof MoveWithinEAttributeEvent) {
			EObject eObject = ((MoveWithinEAttributeEvent) event).getTarget();
			Object values = ((MoveWithinEAttributeEvent) event).getValues();
			modelHistory.addObjectHistoryLine(eObject, event, eventNumber, values);
		} else {
			throw new RuntimeException("Unexpected event:" + event);
		}
}

	public double getSetAttAvg() {
		return setAttAvg;
	}

	public double getUnsetAttAvg() {
		return unsetAttAvg;
	}

	public double getAddAttAvg() {
		return addAttAvg;
	}

	public double getRemoveAttAvg() {
		return removeAttAvg;
	}

	public double getMoveAttAvg() {
		return moveAttAvg;
	}

	public double getSetRefAvg() {
		return setRefAvg;
	}

	public double getUnsetRefAvg() {
		return unsetRefAvg;
	}

	public double getAddRefAvg() {
		return addRefAvg;
	}

	public double getRemoveRefAvg() {
		return removeRefAvg;
	}

	public double getMoveRefAvg() {
		return moveRefAvg;
	}

	public double getDeleteAvg() {
		return deleteAvg;
	}

	public double getAddResAvg() {
		return addResAvg;
	}

	public double getRemoveResAvg() {
		return removeResAvg;
	}

	public double getPackageAvg() {
		return packageAvg;
	}

	public double getSessionAvg() {
		return sessionAvg;
	}

	public double getCreateAvg() {
		return createAvg;
	}
}