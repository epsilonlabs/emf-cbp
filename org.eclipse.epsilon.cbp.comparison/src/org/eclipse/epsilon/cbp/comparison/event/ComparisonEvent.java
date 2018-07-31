package org.eclipse.epsilon.cbp.comparison.event;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
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

public class ComparisonEvent {

	public static final String RESOURCE_STRING = "resource";

	protected ChangeEvent<?> changeEvent = null;
	protected String eClassName = null;
	protected String packageName = null;
	protected String sessionId = null;
	protected String time = null;
	protected EObject target = null;
	protected String targetId = null;
	protected Object oldValue = null;
	protected String oldValueId = null;
	protected Object value = null;
	protected String valueId = null;
	protected EStructuralFeature feature = null;
	protected String featureName = null;
	protected int position = -1;
	protected int from = -1;
	protected int to = -1;
	protected Class<?> eventType = null;
	protected String eventString = null;
	protected boolean isConflicted = false;
	protected String compositeId = null;
	protected List<ConflictedEventPair> conflictedEventPairList = new ArrayList<>();

	public ComparisonEvent() {
	}

	private void internalCreateComparisonEvent(Class<?> eventType, ChangeEvent<?> changeEvent, String eventString) {
		this.eventType = eventType;
		this.changeEvent = changeEvent;
		this.eventString = eventString;
	}

	private void internalCreateComparisonEvent(Class<?> eventType, ChangeEvent<?> changeEvent, EObject target,
			Object value, EStructuralFeature feature, int position, String eventString, String targetId, String valueId,
			String featureName) {
		this.eventType = eventType;
		this.changeEvent = changeEvent;
		this.target = target;
		this.value = value;
		this.feature = feature;
		this.featureName = featureName;
		this.position = position;
		this.eventString = eventString;
		this.targetId = targetId;
		this.valueId = valueId;
	}

	public String geteClassName() {
		return eClassName;
	}

	public void seteClassName(String eClassName) {
		this.eClassName = eClassName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Class<?> getEventType() {
		return eventType;
	}

	public ChangeEvent<?> getChangeEvent() {
		return changeEvent;
	}

	public EObject getTarget() {
		return target;
	}

	public Object getValue() {
		return value;
	}

	public EStructuralFeature getFeature() {
		return feature;
	}

	public int getPosition() {
		return position;
	}

	public String getTargetId() {
		return targetId;
	}

	public String getValueId() {
		return valueId;
	}

	public String getEventString() throws ParserConfigurationException, TransformerException {
		if (eventString == null) {
			eventString = internalGetEventString();
		}
		return eventString;
	}

	public void setChangeEvent(ChangeEvent<?> changeEvent) {
		this.changeEvent = changeEvent;
	}

	public void setTarget(EObject target) {
		this.target = target;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setValueId(String valueId) {
		this.valueId = valueId;
	}

	public void setFeature(EStructuralFeature feature) {
		this.feature = feature;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setEventType(Class<?> eventType) {
		this.eventType = eventType;
	}

	public void setEventString(String eventString) {
		this.eventString = eventString;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public boolean isConflicted() {
		return isConflicted;
	}

	public void setIsConflicted(boolean isConflicted) {
		this.isConflicted = isConflicted;
	}

	public List<ConflictedEventPair> getConflictedEventPairList() {
		return conflictedEventPairList;
	}

	public void setConflictedEventPairList(List<ConflictedEventPair> conflictedEventPairList) {
		this.conflictedEventPairList = conflictedEventPairList;
	}

	public String getCompositeId() {
		return this.compositeId;
	}

	public void setCompositeId(String compositeId) {
		this.compositeId = compositeId;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

	public String getOldValueId() {
		return oldValueId;
	}

	public void setOldValueId(String oldValueId) {
		this.oldValueId = oldValueId;
	}

	public void setConflicted(boolean isConflicted) {
		this.isConflicted = isConflicted;
	}

	public ComparisonEvent reverse() throws ParserConfigurationException, TransformerException {
		return this.reverse(null);
	}

	public ComparisonEvent reverse(String compositeId) throws ParserConfigurationException, TransformerException {
		ChangeEvent<?> reversedChangeEvent = this.getChangeEvent().reverse();
		ComparisonEvent newComparisonEvent = new ComparisonEvent();

		newComparisonEvent.setTarget(this.target);
		newComparisonEvent.setValue(this.value);
		newComparisonEvent.setFeature(this.feature);
		newComparisonEvent.setFeatureName(this.featureName);
		newComparisonEvent.setPosition(this.position);

		newComparisonEvent.setTargetId(this.targetId);
		newComparisonEvent.setValueId(this.valueId);
		newComparisonEvent.setOldValue(this.oldValue);
		newComparisonEvent.setOldValueId(this.oldValueId);
		newComparisonEvent.seteClassName(this.eClassName);
		newComparisonEvent.setPackageName(this.packageName);
		newComparisonEvent.setTime(this.time);
		newComparisonEvent.setSessionId(this.sessionId);

		if (compositeId != null) {
			newComparisonEvent.setCompositeId(compositeId);
			reversedChangeEvent.setComposite(compositeId);
		} else {
			newComparisonEvent.setCompositeId(this.compositeId);
		}

		if (this.getChangeEvent() instanceof SetEAttributeEvent) {
			newComparisonEvent.setOldValue(this.value);
			newComparisonEvent.setValue(this.oldValue);
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof SetEReferenceEvent) {
			newComparisonEvent.setOldValue(this.value);
			newComparisonEvent.setValue(this.oldValue);
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof UnsetEAttributeEvent) {
			newComparisonEvent.setOldValue(this.value);
			newComparisonEvent.setValue(this.oldValue);
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof UnsetEReferenceEvent) {
			newComparisonEvent.setOldValue(this.value);
			newComparisonEvent.setValue(this.oldValue);
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof MoveWithinEAttributeEvent) {
			newComparisonEvent.setFrom(this.to);
			newComparisonEvent.setTo(this.from);
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof MoveWithinEReferenceEvent) {
			newComparisonEvent.setFrom(this.to);
			newComparisonEvent.setTo(this.from);
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof RemoveFromResourceEvent) {
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof AddToResourceEvent) {
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof RemoveFromEReferenceEvent) {
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof AddToEReferenceEvent) {
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof RemoveFromEAttributeEvent) {
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof AddToEAttributeEvent) {
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof CreateEObjectEvent) {
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		} else if (this.getChangeEvent() instanceof DeleteEObjectEvent) {
			newComparisonEvent.setEventType(reversedChangeEvent.getClass());
		}

		newComparisonEvent.setChangeEvent(reversedChangeEvent);
		newComparisonEvent.getEventString();

		return newComparisonEvent;
	}

	public ComparisonEvent(ChangeEvent<?> changeEvent) throws ParserConfigurationException, TransformerException {
		String eventString = null;
		if (changeEvent instanceof StartNewSessionEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, null, null, null, -1, null, null,
					null, null);
			this.time = ((StartNewSessionEvent) changeEvent).getTime();
			this.sessionId = ((StartNewSessionEvent) changeEvent).getSessionId();
		} else if (changeEvent instanceof RegisterEPackageEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, null, null, null, -1, eventString,
					null, null, null);
		} else if (changeEvent instanceof CreateEObjectEvent) {
			String id = ((CreateEObjectEvent) changeEvent).getId();
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, null, changeEvent.getValue(), null,
					-1, eventString, RESOURCE_STRING, id, null);
		} else if (changeEvent instanceof DeleteEObjectEvent) {
			String id = ((DeleteEObjectEvent) changeEvent).getId();
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, null, changeEvent.getValue(), null,
					-1, eventString, RESOURCE_STRING, id, null);
		} else if (changeEvent instanceof AddToResourceEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof RemoveFromResourceEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof AddToEReferenceEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof RemoveFromEReferenceEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof SetEAttributeEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof SetEReferenceEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof UnsetEReferenceEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof UnsetEAttributeEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof AddToEAttributeEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof RemoveFromEAttributeEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof MoveWithinEReferenceEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		} else if (changeEvent instanceof MoveWithinEAttributeEvent) {
			this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
		}

		// this.eventString = this.getEventString();
		// System.out.println();
	}

	public ComparisonEvent(String eventString) throws XMLStreamException {

		ByteArrayInputStream headerStream = new ByteArrayInputStream(new byte[0]);
		ByteArrayInputStream contentStream = new ByteArrayInputStream(eventString.getBytes());
		InputStream stream = new SequenceInputStream(headerStream, contentStream);

		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(stream);

		ChangeEvent<?> changeEvent = null;
		boolean ignore = false;

		while (xmlReader.hasNext()) {
			XMLEvent xmlEvent = xmlReader.nextEvent();
			if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
				StartElement e = xmlEvent.asStartElement();
				String name = e.getName().getLocalPart();

				if (name.equals("m")) {
					continue;
				}

				if (!name.equals("value") && !name.equals("old-value")) {
					if (ignore == false) {
						switch (name) {
						case "session": {
							String sessionId = e.getAttributeByName(new QName("id")).getValue();
							String time = e.getAttributeByName(new QName("time")).getValue();
							changeEvent = new StartNewSessionEvent(sessionId, time);
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, null, null, null,
									-1, eventString, null, null, null);

							this.sessionId = sessionId;
							this.time = time;
						}
							break;
						case "register": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							changeEvent = new RegisterEPackageEvent(ePackage, null);
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, null, null, null,
									-1, eventString, null, null, null);

							this.packageName = packageName;
						}
							break;
						case "create": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							String className = e.getAttributeByName(new QName("eclass")).getValue();
							String id = e.getAttributeByName(new QName("id")).getValue();
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);

							changeEvent = new CreateEObjectEvent(eClass, id);

							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, null,
									changeEvent.getValue(), null, -1, eventString, RESOURCE_STRING, id, null);

							this.packageName = packageName;
							this.eClassName = className;
						}
							break;
						case "add-to-resource": {
							changeEvent = new AddToResourceEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "remove-from-resource": {
							changeEvent = new RemoveFromResourceEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "add-to-ereference": {
							changeEvent = new AddToEReferenceEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "remove-from-ereference": {
							changeEvent = new RemoveFromEReferenceEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "set-eattribute": {
							changeEvent = new SetEAttributeEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "set-ereference": {
							changeEvent = new SetEReferenceEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "unset-eattribute": {
							changeEvent = new UnsetEAttributeEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "unset-ereference": {
							changeEvent = new UnsetEReferenceEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "add-to-eattribute": {
							changeEvent = new AddToEAttributeEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "remove-from-eattribute": {
							changeEvent = new RemoveFromEAttributeEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "move-in-eattribute": {
							changeEvent = new MoveWithinEAttributeEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "move-in-ereference": {
							changeEvent = new MoveWithinEReferenceEvent();
							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, eventString);
						}
							break;
						case "delete": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							String className = e.getAttributeByName(new QName("eclass")).getValue();
							String id = e.getAttributeByName(new QName("id")).getValue();
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							changeEvent = new DeleteEObjectEvent(eClass, id);

							this.internalCreateComparisonEvent(changeEvent.getClass(), changeEvent, null,
									changeEvent.getValue(), null, -1, eventString, RESOURCE_STRING, id, null);

							this.packageName = packageName;
							this.eClassName = className;
						}
							break;
						}

						if (changeEvent instanceof EStructuralFeatureEvent<?>) {
							String sTarget = e.getAttributeByName(new QName("target")).getValue();
							String sName = e.getAttributeByName(new QName("name")).getValue();

							if (sTarget != null) {
								this.setFeatureName(sName);
								this.setTargetId(sTarget);
							}
						}

						if (changeEvent instanceof AddToResourceEvent
								|| changeEvent instanceof RemoveFromResourceEvent) {
							this.setTargetId(RESOURCE_STRING);
						}

						if (changeEvent instanceof AddToEAttributeEvent
								|| changeEvent instanceof AddToEReferenceEvent) {
							String sPosition = e.getAttributeByName(new QName("position")).getValue();
							changeEvent.setPosition(Integer.parseInt(sPosition));
							this.setPosition(Integer.valueOf(sPosition));
						}

						if (changeEvent instanceof RemoveFromEAttributeEvent
								|| changeEvent instanceof RemoveFromEReferenceEvent) {
							String sPosition = e.getAttributeByName(new QName("position")).getValue();
							changeEvent.setPosition(Integer.parseInt(sPosition));
							this.setPosition(Integer.valueOf(sPosition));
						}

						if (changeEvent instanceof FromPositionEvent) {
							String sTo = e.getAttributeByName(new QName("to")).getValue();
							String sFrom = e.getAttributeByName(new QName("from")).getValue();
							changeEvent.setPosition(Integer.parseInt(sTo));
							((FromPositionEvent) changeEvent).setFromPosition(Integer.parseInt(sFrom));

							this.setFrom(Integer.valueOf(sFrom));
							this.setTo(Integer.valueOf(sTo));
						}

						Attribute compositeAttribute = e.getAttributeByName(new QName("composite"));
						if (compositeAttribute != null) {
							String composite = compositeAttribute.getValue();
							changeEvent.setComposite(composite);

							this.setCompositeId(composite);
						}

					}

				} else if (name.equals("value") || name.equals("old-value")) {
					if (ignore == false) {

						if (name.equals("value")) {
							if (changeEvent instanceof EObjectValuesEvent) {
								String seobject = e.getAttributeByName(new QName("eobject")).getValue();

								this.setValueId(seobject);
							} else if (changeEvent instanceof EAttributeEvent) {
								EAttributeEvent eAttributeEvent = (EAttributeEvent) changeEvent;
								String sliteral = e.getAttributeByName(new QName("literal")).getValue();

								if (eAttributeEvent.getEStructuralFeature() != null) {
									EDataType eDataType = ((EDataType) eAttributeEvent.getEStructuralFeature()
											.getEType());
									Object value = eDataType.getEPackage().getEFactoryInstance()
											.createFromString(eDataType, sliteral);
									eAttributeEvent.getValues().add(value);
								}

								this.setValue(sliteral);
							}
						} else if (name.equals("old-value")) {
							if (changeEvent instanceof EObjectValuesEvent) {
								String seobject = e.getAttributeByName(new QName("eobject")).getValue();

								this.setOldValueId(seobject);
							} else if (changeEvent instanceof EAttributeEvent) {
								EAttributeEvent eAttributeEvent = (EAttributeEvent) changeEvent;
								String sliteral = e.getAttributeByName(new QName("literal")).getValue();

								if (eAttributeEvent.getEStructuralFeature() != null) {
									EDataType eDataType = ((EDataType) eAttributeEvent.getEStructuralFeature()
											.getEType());
									Object value = eDataType.getEPackage().getEFactoryInstance()
											.createFromString(eDataType, sliteral);
									eAttributeEvent.getOldValues().add(value);
								}

								this.setOldValue(sliteral);
							}
						}
					}
				}
			}
			if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
				EndElement ee = xmlEvent.asEndElement();
				String name = ee.getName().getLocalPart();
				if (changeEvent != null && !name.equals("value") && !name.equals("m")) {
					if (ignore == false) {
						// comparisonEvent = event;
					} else {
						ignore = false;
					}
					// eventNumber += 1;
				}
			}
		}
		xmlReader.close();
	}

	private String internalGetEventString() throws ParserConfigurationException, TransformerException {
		String eventString = null;
		ChangeEvent<?> event = this.getChangeEvent();
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		Document document = documentBuilder.newDocument();
		Element e = null;

		if (event instanceof StartNewSessionEvent) {
			e = document.createElement("session");
			e.setAttribute("id", this.sessionId);
			e.setAttribute("time", this.time);
		} else if (event instanceof RegisterEPackageEvent) {
			e = document.createElement("register");
			e.setAttribute("epackage", this.packageName);
		} else if (event instanceof CreateEObjectEvent) {
			e = document.createElement("create");
			e.setAttribute("epackage", this.packageName);
			e.setAttribute("eclass", this.eClassName);
			e.setAttribute("id", this.valueId);
		} else if (event instanceof DeleteEObjectEvent) {
			e = document.createElement("delete");
			e.setAttribute("epackage", this.packageName);
			e.setAttribute("eclass", this.eClassName);
			e.setAttribute("id", this.valueId);
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
			e.setAttribute("name", this.featureName);
			e.setAttribute("target", this.targetId);
		}

		if (event instanceof AddToEReferenceEvent || event instanceof AddToEAttributeEvent
				|| event instanceof AddToResourceEvent) {
			e.setAttribute("position", this.position + "");
		}

		if (event instanceof RemoveFromEReferenceEvent || event instanceof RemoveFromEAttributeEvent
				|| event instanceof RemoveFromResourceEvent) {
			e.setAttribute("position", this.position + "");
		}

		if (event instanceof FromPositionEvent) {
			e.setAttribute("from", this.from + "");
			e.setAttribute("to", this.to + "");
		}

		if (event instanceof EObjectValuesEvent) {
			if (this.oldValueId != null) {
				Element o = document.createElement("old-value");
				o.setAttribute("eobject", this.oldValueId);
				e.appendChild(o);
			}
			if (this.valueId != null) {
				Element o = document.createElement("value");
				o.setAttribute("eobject", this.valueId);
				e.appendChild(o);

			}
		} else if (event instanceof EAttributeEvent) {
			if (this.oldValue != null) {
				Element o = document.createElement("old-value");
				o.setAttribute("literal", this.oldValue + "");
				e.appendChild(o);

			}
			if (this.value != null) {
				Element o = document.createElement("value");
				o.setAttribute("literal", this.value + "");
				e.appendChild(o);
			}

		}
		if (this.compositeId != null && e != null) {
			e.setAttribute("composite", this.compositeId);
		}

		if (e != null)
			document.appendChild(e);

		DOMSource source = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		eventString = writer.toString();

		return eventString;
	}

}