package org.eclipse.epsilon.cbp.comparison.emfstore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.CancelEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EObjectValuesEvent;
import org.eclipse.epsilon.cbp.event.EStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.event.FromPositionEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.StartNewSessionEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;

public class CBP2EMFStoreAdapter {

	protected int persistedEvents = 0;
	protected ESLocalProject localProject;
	protected Map<String, ESModelElementId> id2esIdMap;
	protected Map<ESModelElementId, String> esId2IdMap;
	protected Map<String, EObject> id2EObjectMap;
	protected Map<EObject, String> eObject2IdMap;

	public CBP2EMFStoreAdapter(ESLocalProject localProject, Map<String, EObject> id2EObjectMap,
		Map<EObject, String> eObject2IdMap, Map<String, ESModelElementId> id2esIdMap,
		Map<ESModelElementId, String> esId2IdMap) {
		this.localProject = localProject;
		this.id2EObjectMap = id2EObjectMap;
		this.eObject2IdMap = eObject2IdMap;
		this.id2esIdMap = id2esIdMap;
		this.esId2IdMap = esId2IdMap;
	}

	public void load(File cbpFile) throws FactoryConfigurationError, IOException {
		final InputStream inputStream = new FileInputStream(cbpFile);
		replayEvents(inputStream);
		inputStream.close();
	}

	public void load(InputStream inputStream) throws FactoryConfigurationError, IOException {
		replayEvents(inputStream);
	}

	public String register(EObject eObject, String id) {
		localProject.getModelElements().add(eObject);
		final ESModelElementId emfsId = localProject.getModelElementId(eObject);
		id2esIdMap.put(id, emfsId);
		esId2IdMap.put(emfsId, id);
		id2EObjectMap.put(id, eObject);
		eObject2IdMap.put(eObject, id);
		return id;
	}

	private void replayEvents(InputStream inputStream) throws FactoryConfigurationError, IOException {
		String errorMessage = null;

		int eventNumber = persistedEvents;
		try {
			InputStream stream = new ByteArrayInputStream(new byte[0]);

			final ByteArrayInputStream header = new ByteArrayInputStream(
				"<?xml version='1.0' encoding='ISO-8859-1' ?>".getBytes());
			final ByteArrayInputStream begin = new ByteArrayInputStream("<m>".getBytes());
			final ByteArrayInputStream end = new ByteArrayInputStream("</m>".getBytes());
			stream = new SequenceInputStream(stream, header);
			stream = new SequenceInputStream(stream, begin);
			stream = new SequenceInputStream(stream, inputStream);
			stream = new SequenceInputStream(stream, end);

			final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			final XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(stream, "ISO-8859-1");

			ChangeEvent<?> event = null;
			boolean ignore = false;

			while (xmlReader.hasNext()) {
				final XMLEvent xmlEvent = xmlReader.nextEvent();
				if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
					final StartElement e = xmlEvent.asStartElement();
					final String name = e.getName().getLocalPart();

					if (name.equals("m")) {
						continue;
					}

					if (!name.equals("value") && !name.equals("old-value")) {

						if (ignore == false) {
							errorMessage = name;
							if (name.equals("cancel")) {
								final int lineToCancelOffset = Integer
									.parseInt(e.getAttributeByName(new QName("offset")).getValue());
								event = new CancelEvent(lineToCancelOffset);
							} else if (name.equals("session")) {
								final String sessionId = e.getAttributeByName(new QName("id")).getValue();
								final String time = e.getAttributeByName(new QName("time")).getValue();
								event = new StartNewSessionEvent(sessionId, time);
							} else if (name.equals("register")) {
								final String packageName = e.getAttributeByName(new QName("epackage")).getValue();
								errorMessage = errorMessage + ", package: " + packageName;
								final EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
								// event = new RegisterEPackageEvent(ePackage, changeEventAdapter);
							} else if (name.equals("create")) {
								final String packageName = e.getAttributeByName(new QName("epackage")).getValue();
								final String className = e.getAttributeByName(new QName("eclass")).getValue();
								final String id = e.getAttributeByName(new QName("id")).getValue();
								errorMessage = errorMessage + ", package: " + packageName + ", class: " + className
									+ ", id: " + id;
								final EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
								final EClass eClass = (EClass) ePackage.getEClassifier(className);
								event = new CreateEObjectEvent(eClass, this, id);

							} else if (name.equals("add-to-resource")) {
								event = new AddToResourceEvent();
							} else if (name.equals("remove-from-resource")) {
								event = new RemoveFromResourceEvent();
							} else if (name.equals("add-to-ereference")) {
								event = new AddToEReferenceEvent();
							} else if (name.equals("remove-from-ereference")) {
								event = new RemoveFromEReferenceEvent();
							} else if (name.equals("set-eattribute")) {
								event = new SetEAttributeEvent();
							} else if (name.equals("set-ereference")) {
								event = new SetEReferenceEvent();
							} else if (name.equals("unset-eattribute")) {
								event = new UnsetEAttributeEvent();
							} else if (name.equals("unset-ereference")) {
								event = new UnsetEReferenceEvent();
							} else if (name.equals("add-to-eattribute")) {
								event = new AddToEAttributeEvent();
							} else if (name.equals("remove-from-eattribute")) {
								event = new RemoveFromEAttributeEvent();
							} else if (name.equals("move-in-eattribute")) {
								event = new MoveWithinEAttributeEvent();
							} else if (name.equals("move-in-ereference")) {
								event = new MoveWithinEReferenceEvent();
							} else if (name.equals("delete")) {
								final String packageName = e.getAttributeByName(new QName("epackage")).getValue();
								final String className = e.getAttributeByName(new QName("eclass")).getValue();
								final String id = e.getAttributeByName(new QName("id")).getValue();
								errorMessage = errorMessage + ", package: " + packageName + ", class: " + className
									+ ", id: " + id;
								final EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
								final EClass eClass = (EClass) ePackage.getEClassifier(className);
								event = new DeleteEObjectEvent(eClass, this, id);
							}

							if (event instanceof EStructuralFeatureEvent<?>) {
								final String sTarget = e.getAttributeByName(new QName("target")).getValue();
								final String sName = e.getAttributeByName(new QName("name")).getValue();
								errorMessage = errorMessage + ", target: " + sTarget + ", name: " + sName;
								final EObject target = getEObject(sTarget);
								if (target != null) {
									final EStructuralFeature eStructuralFeature = target.eClass()
										.getEStructuralFeature(sName);
									((EStructuralFeatureEvent<?>) event).setEStructuralFeature(eStructuralFeature);
									((EStructuralFeatureEvent<?>) event).setTarget(target);
								}
							} else if (event instanceof ResourceEvent) {
								((ResourceEvent) event).setResource(this);
							}

							if (event instanceof AddToEAttributeEvent || event instanceof AddToEReferenceEvent
								|| event instanceof AddToResourceEvent) {
								final String sPosition = e.getAttributeByName(new QName("position")).getValue();
								errorMessage = errorMessage + ", target: " + sPosition;
								event.setPosition(Integer.parseInt(sPosition));
							}
							if (event instanceof FromPositionEvent) {
								final String sTo = e.getAttributeByName(new QName("to")).getValue();
								errorMessage = errorMessage + ", to: " + sTo;
								final String sFrom = e.getAttributeByName(new QName("from")).getValue();
								errorMessage = errorMessage + ", from: " + sFrom;
								event.setPosition(Integer.parseInt(sTo));
								((FromPositionEvent) event).setFromPosition(Integer.parseInt(sFrom));
							}
						}

					} else if (name.equals("old-value") || name.equals("value")) {
						if (ignore == false) {
							if (name.equals("old-value")) {
								if (event instanceof EObjectValuesEvent) {
									final EObjectValuesEvent valuesEvent = (EObjectValuesEvent) event;
									final String seobject = e.getAttributeByName(new QName("eobject")).getValue();
									errorMessage = errorMessage + ", old-value: " + seobject;
									final EObject eob = resolveXRef(seobject);
									valuesEvent.getOldValues().add(eob);
								} else if (event instanceof EAttributeEvent) {
									final EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
									final String sliteral = e.getAttributeByName(new QName("literal")).getValue();
									errorMessage = errorMessage + ", old-value: " + sliteral;

									final EStructuralFeature sf = eAttributeEvent.getEStructuralFeature();
									if (sf != null) {
										final EDataType eDataType = (EDataType) eAttributeEvent.getEStructuralFeature()
											.getEType();
										final Object value = eDataType.getEPackage().getEFactoryInstance()
											.createFromString(eDataType, sliteral);
										eAttributeEvent.getOldValues().add(value);
									}
								}
							} else if (name.equals("value")) {
								if (event instanceof EObjectValuesEvent) {
									final EObjectValuesEvent valuesEvent = (EObjectValuesEvent) event;
									final String seobject = e.getAttributeByName(new QName("eobject")).getValue();
									errorMessage = errorMessage + ", value: " + seobject;
									final EObject eob = resolveXRef(seobject);
									valuesEvent.getValues().add(eob);
								} else if (event instanceof EAttributeEvent) {
									final EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
									final String sliteral = e.getAttributeByName(new QName("literal")).getValue();
									errorMessage = errorMessage + ", value: " + sliteral;
									final EStructuralFeature sf = eAttributeEvent.getEStructuralFeature();
									if (sf != null) {
										final EDataType eDataType = (EDataType) eAttributeEvent.getEStructuralFeature()
											.getEType();
										final Object value = eDataType.getEPackage().getEFactoryInstance()
											.createFromString(eDataType, sliteral);
										eAttributeEvent.getValues().add(value);
									}
								}
							}
						}
					}
				}
				if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
					final EndElement ee = xmlEvent.asEndElement();
					final String name = ee.getName().getLocalPart();
					if (event != null && !name.equals("old-value") && !name.equals("value") && !name.equals("m")) {
						if (ignore == false) {

							// Object value = event.getValue();
							// ESModelElementId id = null;
							// if (value instanceof EObject && event instanceof AddToEReferenceEvent) {
							// final EObject eObject = (EObject) value;
							// id = localProject.getModelElementId(eObject);
							// System.console();
							// }

							event.replay();

							if (event.getValue() instanceof EObject && event instanceof AddToEReferenceEvent) {
								EObject eObject = (EObject) event.getValue();
								EReference eReference = ((AddToEReferenceEvent) event).getEReference();
								if (eReference.isContainment()) {
									TreeIterator<EObject> iterator = eObject.eAllContents();
									while (iterator.hasNext()) {
										EObject eObj = iterator.next();
										ESModelElementId id = localProject.getModelElementId(eObj);
										String temp = eObject2IdMap.get(eObj);
										id2esIdMap.put(temp, id);
										esId2IdMap.put(id, temp);
									}
									ESModelElementId id = localProject.getModelElementId(eObject);
									String temp = eObject2IdMap.get(eObject);
									id2esIdMap.put(temp, id);
									esId2IdMap.put(id, temp);
								}
							}

							errorMessage = "";
						} else {
							ignore = false;
						}
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

		} catch (final Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: Event Number " + eventNumber + " : " + errorMessage);
			throw new IOException(
				"Error: Event Number " + eventNumber + " : " + errorMessage + "\n" + ex.toString() + "\n");
		}
	}

	public EObject getEObject(String uriFragment) {
		ESModelElementId modelElementId = id2esIdMap.get(uriFragment);
		EObject eObject = null;
		if (modelElementId != null) {
			eObject = localProject.getModelElement(modelElementId);
		}
		if (eObject == null) {
			eObject = id2EObjectMap.get(uriFragment);
		}
		return eObject;
	}

	protected EObject resolveXRef(final String sEObjectURI) {
		EObject eObject = getEObject(sEObjectURI);
		return eObject;
	}

}
