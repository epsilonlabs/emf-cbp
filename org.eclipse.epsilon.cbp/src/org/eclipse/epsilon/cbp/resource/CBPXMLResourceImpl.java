package org.eclipse.epsilon.cbp.resource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Collection;
import java.util.Map;

import javax.management.ValueExp;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
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
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;
import org.eclipse.epsilon.cbp.util.StringOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.XMLReader;

public class CBPXMLResourceImpl extends CBPResource {

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

	public CBPXMLResourceImpl() {
		super();
	}

	public CBPXMLResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	public void doSave(OutputStream out, Map<?, ?> options) throws IOException {

		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			//// alfa
			int line = 0;

			// Ignore the first #eventsAfterMostRecentLoad events
			for (ChangeEvent<?> event : getChangeEvents().subList(persistedEvents, getChangeEvents().size())) {

				Document document = documentBuilder.newDocument();
				Element e = null;

				if (event instanceof RegisterEPackageEvent) {
					RegisterEPackageEvent r = ((RegisterEPackageEvent) event);
					e = document.createElement("register");
					e.setAttribute("epackage", r.getEPackage().getNsURI());
					eObjectHistoryAdapater.add(r.getEPackage(), event, line);
				} else if (event instanceof CreateEObjectEvent) {
					e = document.createElement("create");
					e.setAttribute("epackage", ((CreateEObjectEvent) event).getEClass().getEPackage().getNsURI());
					e.setAttribute("eclass", ((CreateEObjectEvent) event).getEClass().getName());
					e.setAttribute("id", ((CreateEObjectEvent) event).getId());
					EObject eObject = ((CreateEObjectEvent) event).getValue();
					eObjectHistoryAdapater.add(eObject, event, line);
				} else if (event instanceof DeleteEObjectEvent) {
					e = document.createElement("delete");
					e.setAttribute("epackage", ((DeleteEObjectEvent) event).getEClass().getEPackage().getNsURI());
					e.setAttribute("eclass", ((DeleteEObjectEvent) event).getEClass().getName());
					e.setAttribute("id", ((DeleteEObjectEvent) event).getId());
					EObject eObject = ((DeleteEObjectEvent) event).getValue();
					eObjectHistoryAdapater.add(eObject, event, line);
				} else if (event instanceof AddToResourceEvent) {
					e = document.createElement("add-to-resource");
					EObject eObject = ((AddToResourceEvent) event).getValue();
					eObjectHistoryAdapater.add(eObject, event, line);
				} else if (event instanceof RemoveFromResourceEvent) {
					e = document.createElement("remove-from-resource");
					EObject eObject = ((RemoveFromResourceEvent) event).getValue();
					eObjectHistoryAdapater.add(eObject, event, line);
				} else if (event instanceof AddToEReferenceEvent) {
					e = document.createElement("add-to-ereference");
					EObject eObject = ((AddToEReferenceEvent) event).getTarget();
					EObject value = ((AddToEReferenceEvent) event).getValue();
					eObjectHistoryAdapater.add(eObject, event, line, value);
				} else if (event instanceof RemoveFromEReferenceEvent) {
					e = document.createElement("remove-from-ereference");
					EObject eObject = ((RemoveFromEReferenceEvent) event).getTarget();
					EObject value = ((RemoveFromEReferenceEvent) event).getValue();
					eObjectHistoryAdapater.add(eObject, event, line, value);
				} else if (event instanceof SetEAttributeEvent) {
					e = document.createElement("set-eattribute");
					EObject eObject = ((SetEAttributeEvent) event).getTarget();
					eObjectHistoryAdapater.add(eObject, event, line);
				} else if (event instanceof SetEReferenceEvent) {
					e = document.createElement("set-ereference");
					EObject eObject = ((SetEReferenceEvent) event).getTarget();
					eObjectHistoryAdapater.add(eObject, event, line);
				} else if (event instanceof UnsetEReferenceEvent) {
					e = document.createElement("unset-ereference");
					EObject eObject = ((UnsetEReferenceEvent) event).getTarget();
					eObjectHistoryAdapater.add(eObject, event, line);
				} else if (event instanceof UnsetEAttributeEvent) {
					e = document.createElement("unset-eattribute");
					EObject eObject = ((UnsetEAttributeEvent) event).getTarget();
					eObjectHistoryAdapater.add(eObject, event, line);
				} else if (event instanceof AddToEAttributeEvent) {
					e = document.createElement("add-to-eattribute");
					EObject eObject = ((AddToEAttributeEvent) event).getTarget();
					Object value = ((AddToEAttributeEvent) event).getValue();
					eObjectHistoryAdapater.add(eObject, event, line, value);
				} else if (event instanceof RemoveFromEAttributeEvent) {
					e = document.createElement("remove-from-eattribute");
					EObject eObject = ((RemoveFromEAttributeEvent) event).getTarget();
					Object value = ((RemoveFromEAttributeEvent) event).getValue();
					eObjectHistoryAdapater.add(eObject, event, line, value);
				} else if (event instanceof MoveWithinEReferenceEvent) {
					e = document.createElement("move-in-ereference");
					EObject eObject = ((MoveWithinEReferenceEvent) event).getTarget();
					Object values = ((MoveWithinEReferenceEvent) event).getValues();
					eObjectHistoryAdapater.add(eObject, event, line, values);
				} else if (event instanceof MoveWithinEAttributeEvent) {
					e = document.createElement("move-in-eattribute");
					EObject eObject = ((MoveWithinEAttributeEvent) event).getTarget();
					Object values = ((MoveWithinEAttributeEvent) event).getValues();
					eObjectHistoryAdapater.add(eObject, event, line, values);
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

				line += 1;
			}
			persistedEvents = getChangeEvents().size();
		} catch (

		Exception ex) {
			ex.printStackTrace();
			throw new IOException(ex);
		}
	}

	@Override
	public void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		changeEventAdapter.setEnabled(false);
		eObjectToIdMap.clear();
		getChangeEvents().clear();

		String line = null;
		boolean optimised = true;
		if (options != null && options.containsKey("optimise")) {
			optimised = (Boolean) options.get("optimise");
		}

		int lineNumber = 0;
		try {
			ByteArrayInputStream begin = new ByteArrayInputStream("<m>".getBytes());
			ByteArrayInputStream end = new ByteArrayInputStream("</m>".getBytes());
			InputStream stream = new SequenceInputStream(begin, inputStream);
			stream = new SequenceInputStream(stream, end);

			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(stream);

			ChangeEvent<?> event = null;
			boolean ignore = false;

			// BufferedReader reader = new BufferedReader(new
			// InputStreamReader(inputStream));
			// while ((line = reader.readLine()) != null) {
			// //String[] strings = line.split("><");
			// }

			// BufferedInputStream bis = new BufferedInputStream(inputStream);
			// int b;
			// while ( (b=bis.read()) != -1 ){
			// char c = (char) b;
			// if (c == '\n'){
			// //System.out.println("NEW LINE");
			// }
			// }

			while (xmlReader.hasNext()) {
				XMLEvent xmlEvent = xmlReader.nextEvent();
				if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
					StartElement e = xmlEvent.asStartElement();
					String name = e.getName().getLocalPart();

					if (name.equals("m")) {
						continue;
					}

					if (!name.equals("value")) {
						if (ignoreList.contains(lineNumber) && optimised == true) {
							ignore = true;
						}

						if (ignore == false) {
							line = name;
							switch (name) {
							case "register": {
								String packageName = e.getAttributeByName(new QName("epackage")).getValue();
								line = line + ", package: " + packageName;
								EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
								event = new RegisterEPackageEvent(ePackage, changeEventAdapter);
								break;
							}
							case "create": {
								String packageName = e.getAttributeByName(new QName("epackage")).getValue();
								String className = e.getAttributeByName(new QName("eclass")).getValue();
								String id = e.getAttributeByName(new QName("id")).getValue();
								line = line + ", package: " + packageName + ", class: " + className + ", id: " + id;
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
								line = line + ", package: " + packageName + ", class: " + className + ", id: " + id;
								EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
								EClass eClass = (EClass) ePackage.getEClassifier(className);
								event = new DeleteEObjectEvent(eClass, this, id);
								break;
							}
							}

							if (event instanceof EStructuralFeatureEvent<?>) {
								String sTarget = e.getAttributeByName(new QName("target")).getValue();
								String sName = e.getAttributeByName(new QName("name")).getValue();
								line = line + ", target: " + sTarget + ", name: " + sName;
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
								line = line + ", target: " + sPosition;
								event.setPosition(Integer.parseInt(sPosition));
							}
							if (event instanceof FromPositionEvent) {
								String sTo = e.getAttributeByName(new QName("to")).getValue();
								line = line + ", to: " + sTo;
								String sFrom = e.getAttributeByName(new QName("from")).getValue();
								line = line + ", from: " + sFrom;
								event.setPosition(Integer.parseInt(sTo));
								((FromPositionEvent) event).setFromPosition(Integer.parseInt(sFrom));
							}
						}

					} else if (name.equals("value")) {
						if (ignore == false) {
							if (event instanceof EObjectValuesEvent) {
								EObjectValuesEvent valuesEvent = (EObjectValuesEvent) event;
								String seobject = e.getAttributeByName(new QName("eobject")).getValue();
								line = line + ", value: " + seobject;
								EObject eob = resolveXRef(seobject);
								valuesEvent.getValues().add(eob);
							} else if (event instanceof EAttributeEvent) {
								EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
								String sliteral = e.getAttributeByName(new QName("literal")).getValue();
								line = line + ", value: " + sliteral;
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
							getChangeEvents().add(event);
							line = "";
						} else {
							ignore = false;
						}
						lineNumber += 1;
					}
				}
			}
			persistedEvents = getChangeEvents().size();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: " + lineNumber + " : " + line);
			throw new IOException("Error: " + lineNumber + " : " + line + "\n" + ex.toString() + "\n");
		}

		changeEventAdapter.setEnabled(true);
	}

	protected void doLoad(Element e) {

		String name = e.getNodeName();

		ChangeEvent<?> event = buildEvent(e, name);

		if (event instanceof EStructuralFeatureEvent<?>) {
			String x = e.getAttribute("target");
			EObject target = getEObject(e.getAttribute("target"));
			EStructuralFeature eStructuralFeature = target.eClass().getEStructuralFeature(e.getAttribute("name"));
			((EStructuralFeatureEvent<?>) event).setEStructuralFeature(eStructuralFeature);
			((EStructuralFeatureEvent<?>) event).setTarget(target);
		} else if (event instanceof ResourceEvent) {
			((ResourceEvent) event).setResource(this);
		}

		if (event instanceof AddToEAttributeEvent || event instanceof AddToEReferenceEvent
				|| event instanceof AddToResourceEvent) {
			event.setPosition(Integer.parseInt(e.getAttribute("position")));
		}
		if (event instanceof FromPositionEvent) {
			event.setPosition(Integer.parseInt(e.getAttribute("to")));
			((FromPositionEvent) event).setFromPosition(Integer.parseInt(e.getAttribute("from")));
		}

		if (event instanceof EObjectValuesEvent) {
			EObjectValuesEvent valuesEvent = (EObjectValuesEvent) event;
			NodeList values = e.getElementsByTagName("value");
			for (int i = 0; i < values.getLength(); i++) {
				final String sEObject = ((Element) values.item(i)).getAttribute("eobject");
				EObject eob = resolveXRef(sEObject);
				valuesEvent.getValues().add(eob);
			}
		} else if (event instanceof EAttributeEvent) {
			EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
			NodeList values = e.getElementsByTagName("value");
			for (int i = 0; i < values.getLength(); i++) {
				eAttributeEvent.getValues().add(getLiteralValue(eAttributeEvent.getTarget(),
						eAttributeEvent.getEStructuralFeature(), (Element) values.item(i)));
			}
		}

		event.replay();
		getChangeEvents().add(event);
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
		}

		return null;
	}

	protected Object getLiteralValue(EObject eObject, EStructuralFeature eStructuralFeature, Element e) {
		EDataType eDataType = ((EDataType) eStructuralFeature.getEType());
		return eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType, e.getAttribute("literal"));
	}

}