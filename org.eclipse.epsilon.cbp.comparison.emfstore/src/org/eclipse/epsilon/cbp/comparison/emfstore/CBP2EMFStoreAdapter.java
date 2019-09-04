package org.eclipse.epsilon.cbp.comparison.emfstore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.client.ESCompositeOperationHandle;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.exceptions.ESInvalidCompositeOperationException;
import org.eclipse.emf.emfstore.common.ESSystemOutProgressMonitor;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.impl.ESModelElementIdImpl;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.exceptions.ESUpdateRequiredException;
import org.eclipse.epsilon.cbp.comparison.emfstore.test.Application;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.CancelEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EObjectValuesEvent;
import org.eclipse.epsilon.cbp.event.EReferenceEvent;
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
	protected ESLocalProject originalProject;
	protected ESLocalProject otherProject;
	protected Map<String, String> id2esIdMap;
	protected Map<String, String> esId2IdMap;
	protected Map<String, EObject> id2EObjectMap;
	protected Map<EObject, String> eObject2IdMap;
	protected String composite = null;
	protected ESCompositeOperationHandle compositeHandle = null;
	protected EObject previousRemovedEObject = null;

	public CBP2EMFStoreAdapter(ESLocalProject localProject, ESLocalProject originalProject,
		ESLocalProject otherProject) {

		// static File xmiFile = new File("D:\\TEMP\\CONFLICTS\\temp\\emfstore-target.xmi");
		// static XMIResource xmiResource = (XMIResource) new XMIResourceFactoryImpl()
		// .createResource(URI.createFileURI(xmiFile.getAbsolutePath()));

		this.localProject = localProject;
		this.originalProject = originalProject;
		this.otherProject = otherProject;
		id2EObjectMap = new HashMap<String, EObject>();
		eObject2IdMap = new HashMap<EObject, String>();
		id2esIdMap = new HashMap<String, String>();
		esId2IdMap = new HashMap<String, String>();
		EPackage.Registry.INSTANCE.put(NodePackage.eINSTANCE.getNsURI(), NodePackage.eINSTANCE);
	}

	public ESLocalProject getLocalProject() {
		return localProject;
	}

	public Map<String, String> getId2esIdMap() {
		return id2esIdMap;
	}

	public Map<String, String> getEsId2IdMap() {
		return esId2IdMap;
	}

	public Map<String, EObject> getId2EObjectMap() {
		return id2EObjectMap;
	}

	public Map<EObject, String> geteObject2IdMap() {
		return eObject2IdMap;
	}

	public void load(File cbpFile)
		throws FactoryConfigurationError, IOException, ESUpdateRequiredException, ESException {
		load(cbpFile, false);
	}

	public void load(File cbpFile, boolean isAutocommit)
		throws FactoryConfigurationError, IOException, ESUpdateRequiredException, ESException {
		final InputStream inputStream = new FileInputStream(cbpFile);
		replayEvents(inputStream, isAutocommit);
		inputStream.close();
		if (isAutocommit) {
			localProject.commit("AUTOCOMMIT", null, new ESSystemOutProgressMonitor());
		}
	}

	public void load(InputStream inputStream) throws FactoryConfigurationError, IOException {
		replayEvents(inputStream, false);
	}

	public String register(EObject eObject, String id) {
		// long start = System.currentTimeMillis();
		localProject.getModelElements().add(eObject);
		ESModelElementId emfsId = localProject.getModelElementId(eObject);
		String esId = ((ESModelElementIdImpl) emfsId).getId();
		id2esIdMap.put(id, esId);
		esId2IdMap.put(esId, id);
		id2EObjectMap.put(id, eObject);
		eObject2IdMap.put(eObject, id);
		// long end = System.currentTimeMillis();
		// System.out.println("Register Time: " + (end - start));
		return id;
	}

	@SuppressWarnings("restriction")
	private void replayEvents(InputStream inputStream, boolean isAutocommit)
		throws FactoryConfigurationError, IOException {
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

			int count = 0;

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
							if (event instanceof ChangeEvent<?>) {
								Attribute attribute = e.getAttributeByName(new QName("composite"));
								if (attribute != null) {
									String composite = attribute.getValue();
									if (composite != null) {
										event.setComposite(composite);
									}
								}
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

							if (eventNumber == 56) {
								System.console();
							}

							// reassignModelIDsBeforeReplay(event);
							handleCompositeEvents(event);

							// REPLAY
							try {
								System.out.println(eventNumber + ": " + event.toString());

								if (event.getComposite() != null && (event instanceof RemoveFromEReferenceEvent ||
									event instanceof RemoveFromResourceEvent
									|| event instanceof UnsetEReferenceEvent)) {
								} else {

									event.replay();
									count++;

									if (isAutocommit && count % 200000 == 0) {
										localProject.commit("AUTOCOMMIT", null, new ESSystemOutProgressMonitor());
									}
								}
								// {
								// EObject eObj = getId2EObjectMap().get("O-1911");
								// if (eObj != null) {
								// EStructuralFeature eFeature = eObj.eClass().getEStructuralFeature("method");
								// EObject x = (EObject) eObj.eGet(eFeature);
								// if (x == null) {
								// System.console();
								// } else {
								// System.console();
								// }
								// }
								// }
							} catch (Exception exe) {
								exe.printStackTrace();
								System.console();
							}
							// ----

							// if (eventNumber == 56) {
							// System.console();
							// }
							// reassignModelIDsPostReplay(event);

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

			persistedEvents = eventNumber;

			handleCompositeEventsAtEndCBP(event);

		} catch (final Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: Event Number " + eventNumber + " : " + errorMessage);
			throw new IOException(
				"Error: Event Number " + eventNumber + " : " + errorMessage + "\n" + ex.toString() + "\n");
		}
	}

	/**
	 * @param event
	 * @throws ESInvalidCompositeOperationException
	 */
	private void handleCompositeEventsAtEndCBP(ChangeEvent<?> event) throws ESInvalidCompositeOperationException {
		if (composite != null && compositeHandle != null) {
			ESModelElementId modelElementId = null;
			if (event.getValue() instanceof EObject) {
				modelElementId = localProject.getModelElementId((EObject) event.getValue());
			}
			if (modelElementId == null) {
				modelElementId = originalProject.getModelElementId((EObject) event.getValue());
			}
			if (modelElementId == null) {
				modelElementId = otherProject.getModelElementId((EObject) event.getValue());
			}
			// --
			if (modelElementId == null && event.getOldValue() instanceof EObject) {
				modelElementId = localProject
					.getModelElementId((EObject) event.getOldValue());
			}
			if (modelElementId == null && event.getOldValue() instanceof EObject) {
				modelElementId = originalProject
					.getModelElementId((EObject) event.getOldValue());
			}
			if (modelElementId == null && event.getOldValue() instanceof EObject) {
				modelElementId = otherProject
					.getModelElementId((EObject) event.getOldValue());
			}
			// --
			if (modelElementId == null && event instanceof EReferenceEvent) {
				EObject eTarget = ((EReferenceEvent) event).getTarget();
				modelElementId = localProject.getModelElementId(eTarget);
			}
			if (modelElementId == null && event instanceof EReferenceEvent) {
				EObject eTarget = ((EReferenceEvent) event).getTarget();
				modelElementId = originalProject.getModelElementId(eTarget);
			}
			if (modelElementId == null && event instanceof EReferenceEvent) {
				EObject eTarget = ((EReferenceEvent) event).getTarget();
				modelElementId = otherProject.getModelElementId(eTarget);
			}
			// --
			if (modelElementId == null && event instanceof EAttributeEvent) {
				EObject eTarget = ((EAttributeEvent) event).getTarget();
				modelElementId = localProject.getModelElementId(eTarget);
			}
			if (modelElementId == null && event instanceof EAttributeEvent) {
				EObject eTarget = ((EAttributeEvent) event).getTarget();
				modelElementId = originalProject.getModelElementId(eTarget);
			}
			if (modelElementId == null && event instanceof EAttributeEvent) {
				EObject eTarget = ((EAttributeEvent) event).getTarget();
				modelElementId = otherProject.getModelElementId(eTarget);
			}
			// --
			if (modelElementId == null) {
				modelElementId = localProject.getModelElementId(localProject.getModelElements().get(0));
				System.console();
			}
			if (compositeHandle.isValid()) {
				try {
					compositeHandle.end(composite, composite, modelElementId);
					compositeHandle = null;
					System.out.println("End Composite " + composite);
				} catch (Exception e) {
					System.console();
					return;
				}
				composite = null;
			}
		}
	}

	/**
	 * @param event
	 */
	private void reassignModelIDsPostReplay(ChangeEvent<?> event) {
		if (event instanceof AddToEReferenceEvent || event instanceof SetEReferenceEvent
			|| event instanceof UnsetEReferenceEvent
			|| event instanceof RemoveFromEReferenceEvent
		/* || event instanceof AddToResourceEvent || event instanceof RemoveFromResourceEvent */) {

			EObject eObject = null;
			if (event instanceof AddToEReferenceEvent || event instanceof SetEReferenceEvent
				|| event instanceof RemoveFromEReferenceEvent || event instanceof RemoveFromResourceEvent
				|| event instanceof AddToResourceEvent) {
				eObject = (EObject) event.getValue();
			} else if (event instanceof UnsetEReferenceEvent) {
				eObject = (EObject) event.getOldValue();
			}

			if (event instanceof AddToEReferenceEvent || event instanceof SetEReferenceEvent
				|| event instanceof UnsetEReferenceEvent
				|| event instanceof RemoveFromEReferenceEvent) {
				if (previousRemovedEObject != null) {
					copyObjects(previousRemovedEObject, eObject);
				}
			}

			EReference eReference = ((EReferenceEvent) event).getEReference();
			if (eReference.isContainment()) {
				TreeIterator<EObject> iterator = eObject.eAllContents();
				while (iterator.hasNext()) {
					EObject eObj = iterator.next();
					ESModelElementIdImpl x = (ESModelElementIdImpl) localProject
						.getModelElementId(eObj);
					String id = eObject2IdMap.get(eObj);
					if (id == null && x != null) {
						id = esId2IdMap.get(x.getId());
					}
					if (id == null) {
						id = Application.getOriginalAdapater().getEsId2IdMap().get(x.getId());
					}
					if (id != null) {
						id2EObjectMap.put(id, eObj);
						eObject2IdMap.put(eObj, id);
						String esId = null;
						if (x == null) {
							esId = id2esIdMap.get(id);
						} else {
							esId = x.getId();
						}
						int b = 1;
						id2esIdMap.put(id, esId);
						esId2IdMap.put(esId, id);
					}

				}
				ESModelElementIdImpl x = (ESModelElementIdImpl) localProject
					.getModelElementId(eObject);
				String id = eObject2IdMap.get(eObject);
				if (id == null && x != null) {
					id = esId2IdMap.get(x.getId());
				}
				if (id != null) {
					id2EObjectMap.put(id, eObject);
					String esId = null;
					if (x == null) {
						esId = id2esIdMap.get(id);
					} else {
						esId = x.getId();
					}
					id2esIdMap.put(id, esId);
					esId2IdMap.put(esId, id);
				}
			}
		}
		localProject.getModelElements().remove(previousRemovedEObject);
		previousRemovedEObject = null;
	}

	/**
	 * @param event
	 */
	@SuppressWarnings("restriction")
	private void reassignModelIDsBeforeReplay(ChangeEvent<?> event) {
		if (event instanceof UnsetEReferenceEvent
			|| event instanceof RemoveFromEReferenceEvent || event instanceof RemoveFromResourceEvent) {
			EObject eObject = null;
			if (event instanceof RemoveFromEReferenceEvent) {
				eObject = (EObject) event.getValue();
			} else if (event instanceof UnsetEReferenceEvent) {
				eObject = (EObject) event.getOldValue();
			}

			// set non-containment references of copied object -- and its sub-objects -- since they are not copied when
			// using EcoreUtil.copy
			previousRemovedEObject = EcoreUtil.copy(eObject);
			localProject.getModelElements().add(previousRemovedEObject);

			if (previousRemovedEObject != null) {
				copyObjects(eObject, previousRemovedEObject);
				// copyNonContaimentReferences(eObject, previousRemovedEObject);
				// copyObjects(eObject, previousRemovedEObject);

			}
			// -----------

			EReference eReference = ((EReferenceEvent) event).getEReference();
			if (eReference.isContainment()) {
				TreeIterator<EObject> iterator = eObject.eAllContents();
				while (iterator.hasNext()) {
					EObject eObj = iterator.next();
					ESModelElementIdImpl x = (ESModelElementIdImpl) localProject
						.getModelElementId(eObj);
					String id = eObject2IdMap.get(eObj);
					if (id == null && x != null) {
						id = esId2IdMap.get(x.getId());
					}
					if (id == null) {
						id = Application.getOriginalAdapater().getEsId2IdMap().get(x.getId());
					}
					if (id != null) {
						id2EObjectMap.put(id, eObj);
						eObject2IdMap.put(eObj, id);
						String esId = null;
						if (x == null) {
							esId = id2esIdMap.get(id);
						} else {
							esId = x.getId();
						}
						id2esIdMap.put(id, esId);
						esId2IdMap.put(esId, id);
					}

				}
				ESModelElementIdImpl x = (ESModelElementIdImpl) localProject
					.getModelElementId(eObject);
				String id = eObject2IdMap.get(eObject);
				if (id == null && x != null) {
					id = esId2IdMap.get(x.getId());
				}
				if (id != null) {
					id2EObjectMap.put(id, eObject);
					String esId = null;
					if (x == null) {
						esId = id2esIdMap.get(id);
					} else {
						esId = x.getId();
					}
					id2esIdMap.put(id, esId);
					esId2IdMap.put(esId, id);
				}

			}
		}
	}

	/**
	 * @param eObject
	 */
	private void copyObjects(EObject eObjectFrom, EObject eObjectTo) {
		copyNonContaimentReferences(eObjectFrom, eObjectTo);
		Iterator<EObject> iteratorFrom = eObjectFrom.eContents().iterator();
		Iterator<EObject> iteratorTo = eObjectTo.eContents().iterator();
		while (iteratorFrom.hasNext() && iteratorTo.hasNext()) {
			EObject eObjFrom = iteratorFrom.next();
			EObject eObjTo = iteratorTo.next();
			copyNonContaimentReferences(eObjFrom, eObjTo);
			copyObjects(eObjFrom, eObjTo);
		}
	}

	/**
	 * @param eObjectFrom
	 */
	private void copyNonContaimentReferences(EObject eObjectFrom, EObject eObjectTo) {
		Iterator<EReference> iteratorFrom = eObjectFrom.eClass().getEAllReferences().iterator();
		while (iteratorFrom.hasNext()) {
			EReference eRefFrom = iteratorFrom.next();
			if (!eRefFrom.isContainment() && eRefFrom.isChangeable() && eObjectFrom.eIsSet(eRefFrom)
			// && eRefFrom.isResolveProxies() == false
			// && eRefFrom.getEOpposite() == null
			) {
				Object a1 = null;
				String a2 = null;
				Object x1 = null;
				String x2 = null;
				Object y1 = null;
				String y2 = null;
				try {
					if (eRefFrom.isMany()) {
						EList<EObject> fromValues = (EList<EObject>) eObjectFrom.eGet(eRefFrom);
						EList<EObject> toValues = (EList<EObject>) eObjectTo.eGet(eRefFrom);
						toValues.clear();
						toValues.addAll(fromValues);
					} else {
						EObject fromValue = (EObject) eObjectFrom.eGet(eRefFrom);

						x1 = eObject2IdMap.get(fromValue);
						if (fromValue != null && localProject.getModelElementId(fromValue) != null) {
							x2 = localProject.getModelElementId(fromValue).getId();
						}

						if (eObjectFrom != null && localProject.getModelElementId(eObjectFrom) != null) {
							a1 = eObject2IdMap.get(eObjectFrom);
							a2 = localProject.getModelElementId(eObjectFrom).getId();
						}

						if (eObjectTo != null && localProject.getModelElementId(eObjectTo) != null) {
							y1 = eObject2IdMap.get(eObjectTo);
							y2 = localProject.getModelElementId(eObjectTo).getId();
						}
						Object temp = eObjectTo.eGet(eRefFrom);
						if (temp == null) {
							eObjectTo.eSet(eRefFrom, fromValue);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					// String id1 = geteObject2IdMap().get((EObject) x);
					// String id2 = geteObject2IdMap().get((EObject) y);
					System.console();
				}
			}
		}
	}

	/**
	 * @param event
	 * @throws ESInvalidCompositeOperationException
	 */
	private void handleCompositeEvents(ChangeEvent<?> event) throws ESInvalidCompositeOperationException {
		if (event.getComposite() != null && !event.getComposite().equals(composite)) {
			if (compositeHandle != null) {
				ESModelElementId modelElementId = null;
				if (event.getValue() instanceof EObject) {
					modelElementId = localProject.getModelElementId((EObject) event.getValue());
				}
				if (modelElementId == null && event.getValue() instanceof EObject) {
					modelElementId = originalProject.getModelElementId((EObject) event.getValue());
				}
				if (modelElementId == null && event.getValue() instanceof EObject) {
					modelElementId = otherProject.getModelElementId((EObject) event.getValue());
				}
				// --
				if (modelElementId == null && event.getOldValue() instanceof EObject) {
					modelElementId = localProject
						.getModelElementId((EObject) event.getOldValue());
				}
				if (modelElementId == null && event.getOldValue() instanceof EObject) {
					modelElementId = originalProject
						.getModelElementId((EObject) event.getOldValue());
				}
				if (modelElementId == null && event.getOldValue() instanceof EObject) {
					modelElementId = otherProject
						.getModelElementId((EObject) event.getOldValue());
				}
				// --
				if (modelElementId == null && event instanceof EReferenceEvent) {
					EObject eTarget = ((EReferenceEvent) event).getTarget();
					modelElementId = localProject.getModelElementId(eTarget);
				}
				if (modelElementId == null && event instanceof EReferenceEvent) {
					EObject eTarget = ((EReferenceEvent) event).getTarget();
					modelElementId = originalProject.getModelElementId(eTarget);
				}
				if (modelElementId == null && event instanceof EReferenceEvent) {
					EObject eTarget = ((EReferenceEvent) event).getTarget();
					modelElementId = otherProject.getModelElementId(eTarget);
				}
				// --
				if (modelElementId == null && event instanceof EAttributeEvent) {
					EObject eTarget = ((EAttributeEvent) event).getTarget();
					modelElementId = localProject.getModelElementId(eTarget);
				}
				if (modelElementId == null && event instanceof EAttributeEvent) {
					EObject eTarget = ((EAttributeEvent) event).getTarget();
					modelElementId = originalProject.getModelElementId(eTarget);
				}
				if (modelElementId == null && event instanceof EAttributeEvent) {
					EObject eTarget = ((EAttributeEvent) event).getTarget();
					modelElementId = otherProject.getModelElementId(eTarget);
				}
				// --
				if (modelElementId == null) {
					modelElementId = localProject.getModelElementId(localProject.getModelElements().get(0));
					System.console();
				}
				if (compositeHandle.isValid()) {
					try {
						compositeHandle.end(event.getComposite(), event.getComposite(), modelElementId);
						compositeHandle = null;
					} catch (Exception e) {
						System.console();
						return;
					}
					System.out.println("End Composite " + composite);
					composite = null;
				}
			}
			try {
				compositeHandle = localProject.beginCompositeOperation();
			} catch (Exception e) {
				System.console();
				return;
			}
			composite = event.getComposite();
			System.out.println("Start Composite " + composite);
		} else if (event.getComposite() == null && composite != null) {
			if (compositeHandle != null) {
				ESModelElementId modelElementId = null;
				if (event.getValue() instanceof EObject) {
					modelElementId = localProject.getModelElementId((EObject) event.getValue());
				}
				if (modelElementId == null) {
					modelElementId = originalProject.getModelElementId((EObject) event.getValue());
				}
				if (modelElementId == null) {
					modelElementId = otherProject.getModelElementId((EObject) event.getValue());
				}
				// --
				if (modelElementId == null && event.getOldValue() instanceof EObject) {
					modelElementId = localProject
						.getModelElementId((EObject) event.getOldValue());
				}
				if (modelElementId == null && event.getOldValue() instanceof EObject) {
					modelElementId = originalProject
						.getModelElementId((EObject) event.getOldValue());
				}
				if (modelElementId == null && event.getOldValue() instanceof EObject) {
					modelElementId = otherProject
						.getModelElementId((EObject) event.getOldValue());
				}
				// --
				if (modelElementId == null && event instanceof EReferenceEvent) {
					EObject eTarget = ((EReferenceEvent) event).getTarget();
					modelElementId = localProject.getModelElementId(eTarget);
				}
				if (modelElementId == null && event instanceof EReferenceEvent) {
					EObject eTarget = ((EReferenceEvent) event).getTarget();
					modelElementId = originalProject.getModelElementId(eTarget);
				}
				if (modelElementId == null && event instanceof EReferenceEvent) {
					EObject eTarget = ((EReferenceEvent) event).getTarget();
					modelElementId = otherProject.getModelElementId(eTarget);
				}
				// --
				if (modelElementId == null && event instanceof EAttributeEvent) {
					EObject eTarget = ((EAttributeEvent) event).getTarget();
					modelElementId = localProject.getModelElementId(eTarget);
				}
				if (modelElementId == null && event instanceof EAttributeEvent) {
					EObject eTarget = ((EAttributeEvent) event).getTarget();
					modelElementId = originalProject.getModelElementId(eTarget);
				}
				if (modelElementId == null && event instanceof EAttributeEvent) {
					EObject eTarget = ((EAttributeEvent) event).getTarget();
					modelElementId = otherProject.getModelElementId(eTarget);
				}
				// --
				if (modelElementId == null) {
					modelElementId = localProject
						.getModelElementId(localProject.getModelElements().get(0));
					System.console();
				}
				// try {
				if (compositeHandle.isValid()) {
					try {
						compositeHandle.end(event.getComposite(), event.getComposite(), modelElementId);
						compositeHandle = null;
						System.out.println("End Composite " + composite);
					} catch (Exception e) {
						System.console();
						return;
					}
					composite = null;
				}
			}
			// composite = null;
		}
	}

	@SuppressWarnings("restriction")
	public EObject getEObject(String uriFragment) {

		String esId = id2esIdMap.get(uriFragment);

		// if (uriFragment.equals("O-4013") || uriFragment.equals("O-4014")) {
		// String x = Application.getOriginalAdapater().getId2esIdMap().get("O-4014");
		// if (x != null) {
		// ModelElementId meId = org.eclipse.emf.emfstore.internal.common.model.ModelFactory.eINSTANCE
		// .createModelElementId();
		// meId.setId(x);
		// ESModelElementIdImpl modelElementId = meId.createAPI();
		// EObject eObject = localProject.getModelElement(modelElementId);
		// System.console();
		// }
		// }
		if (esId == null) {
			esId = Application.getOriginalAdapater().getId2esIdMap().get(uriFragment);
			if (esId == null) {
				return null;
			}
		}

		ModelElementId meId = org.eclipse.emf.emfstore.internal.common.model.ModelFactory.eINSTANCE
			.createModelElementId();
		meId.setId(esId);
		ESModelElementIdImpl modelElementId = meId.createAPI();
		EObject eObject = null;
		if (modelElementId != null) {
			eObject = localProject.getModelElement(modelElementId);
			if (eObject != null) {
				eObject2IdMap.put(eObject, uriFragment);
				id2EObjectMap.put(uriFragment, eObject);
			}
		}
		if (eObject == null) {
			eObject = id2EObjectMap.get(uriFragment);
			if (eObject != null) {
				eObject2IdMap.put(eObject, uriFragment);
			}
		}
		ESModelElementId x = localProject.getModelElementId(eObject);
		// long end = System.currentTimeMillis();
		// System.out.println("Get EObject Time: " + (end - start));
		return eObject;
	}

	protected EObject resolveXRef(final String sEObjectURI) {
		EObject eObject = getEObject(sEObjectURI);
		return eObject;
	}

}
