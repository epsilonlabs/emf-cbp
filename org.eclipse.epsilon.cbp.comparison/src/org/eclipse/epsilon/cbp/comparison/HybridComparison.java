package org.eclipse.epsilon.cbp.comparison;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
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
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.uml2.uml.UMLFactory;

public class HybridComparison {

	private Resource leftResource;
	private Resource rightResource;
	private File leftCbpFile;
	private File rightCbpFile;

	public List<ChangeEvent<?>> Compare(Resource leftResource, Resource rightResource, File leftCbpFile,
			File rightCbpFile) throws IOException, XMLStreamException {

		this.leftResource = leftResource;
		this.rightResource = rightResource;
		this.leftCbpFile = leftCbpFile;
		this.rightCbpFile = rightCbpFile;

		List<ChangeEvent<?>> leftChangeEvents = new ArrayList<>();
		List<ChangeEvent<?>> rightChangeEvents = new ArrayList<>();

		FileReader leftFileReader = new FileReader(leftCbpFile);
		BufferedReader leftBufferedReader = new BufferedReader(leftFileReader);

		FileReader rightFileReader = new FileReader(rightCbpFile);
		BufferedReader rightBufferedReader = new BufferedReader(rightFileReader);

		int leftInt = -1;
		int rightInt = -1;
		boolean keepReading = true;
		while (keepReading) {
			leftBufferedReader.mark(0);
			rightBufferedReader.mark(0);
			leftInt = leftBufferedReader.read();
			rightInt = rightBufferedReader.read();
			if (leftInt != rightInt) {
				break;
			}
			// System.out.print((char) leftInt);
		}
//		System.out.println();

		if (leftInt > -1)
			leftBufferedReader.reset();
		if (rightInt > -1)
			rightBufferedReader.reset();

		String leftString = null;
		while (leftInt > -1 && (leftString = leftBufferedReader.readLine()) != null) {
			ChangeEvent<?> changeEvent = createEvent(leftString, leftResource, rightResource);
			leftChangeEvents.add(changeEvent);

		}

		String rightString = null;
		while (rightInt > -1 && (rightString = rightBufferedReader.readLine()) != null) {
//			System.out.println(rightString);
			ChangeEvent<?> changeEvent = createEvent(rightString, rightResource, leftResource);
			rightChangeEvents.add(changeEvent);
		}

		return rightChangeEvents;
	}

	private ChangeEvent<?> createEvent(String line, Resource mainResource, Resource otherResource)
			throws XMLStreamException {
		ChangeEvent<?> changeEvent = null;

		int eventNumber = 0;

		// ByteArrayInputStream headerStream = new ByteArrayInputStream(
		// "<?xml version='1.0' encoding='ISO-8859-1' ?>".getBytes());
		ByteArrayInputStream headerStream = new ByteArrayInputStream(new byte[0]);
		ByteArrayInputStream contentStream = new ByteArrayInputStream(line.getBytes());
		InputStream stream = new SequenceInputStream(headerStream, contentStream);

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
					if (ignore == false) {
						switch (name) {
						case "session": {
							String sessionId = e.getAttributeByName(new QName("id")).getValue();
							String time = e.getAttributeByName(new QName("time")).getValue();
							event = new StartNewSessionEvent(sessionId, time);
						}
							break;
						case "register": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							event = new RegisterEPackageEvent(ePackage, null);
						}
							break;
						case "create": {
							String packageName = e.getAttributeByName(new QName("epackage")).getValue();
							String className = e.getAttributeByName(new QName("eclass")).getValue();
							String id = e.getAttributeByName(new QName("id")).getValue();
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							event = new CreateEObjectEvent(eClass, (CBPResource) mainResource , id);
							EObject eObject = getEObject(id, mainResource, otherResource);
							event.setValues(eObject);
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
							EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(packageName);
							EClass eClass = (EClass) ePackage.getEClassifier(className);
							event = new DeleteEObjectEvent(eClass, (CBPResource) mainResource, id);
							EObject eObject = getEObject(id, mainResource, otherResource);
							event.setValues(eObject);
						}
							break;
						}

						if (event instanceof EStructuralFeatureEvent<?>) {
							String sTarget = e.getAttributeByName(new QName("target")).getValue();
							String sName = e.getAttributeByName(new QName("name")).getValue();
							EObject target = getEObject(sTarget, mainResource, otherResource);
							if (target != null) {
								EStructuralFeature eStructuralFeature = target.eClass().getEStructuralFeature(sName);
								((EStructuralFeatureEvent<?>) event).setEStructuralFeature(eStructuralFeature);
								((EStructuralFeatureEvent<?>) event).setTarget(target);
							}
						} else if (event instanceof ResourceEvent) {
						}

						if (event instanceof AddToEAttributeEvent || event instanceof AddToEReferenceEvent
								|| event instanceof AddToResourceEvent) {
							String sPosition = e.getAttributeByName(new QName("position")).getValue();
							event.setPosition(Integer.parseInt(sPosition));
						}
						if (event instanceof FromPositionEvent) {
							String sTo = e.getAttributeByName(new QName("to")).getValue();
							String sFrom = e.getAttributeByName(new QName("from")).getValue();
							event.setPosition(Integer.parseInt(sTo));
							((FromPositionEvent) event).setFromPosition(Integer.parseInt(sFrom));
						}
					}

				} else if (name.equals("value")) {
					if (ignore == false) {
						if (event instanceof EObjectValuesEvent) {
							EObjectValuesEvent valuesEvent = (EObjectValuesEvent) event;
							String seobject = e.getAttributeByName(new QName("eobject")).getValue();
							EObject eob = resolveXRef(seobject, mainResource, otherResource);
							valuesEvent.getValues().add(eob);
						} else if (event instanceof EAttributeEvent) {
							EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
							String sliteral = e.getAttributeByName(new QName("literal")).getValue();

							if (eAttributeEvent.getEStructuralFeature() != null) {
								EDataType eDataType = ((EDataType) eAttributeEvent.getEStructuralFeature().getEType());
								Object value = eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType,
										sliteral);
								eAttributeEvent.getValues().add(value);
							}
						}
					}
				}
			}
			if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
				EndElement ee = xmlEvent.asEndElement();
				String name = ee.getName().getLocalPart();
				if (event != null && !name.equals("value") && !name.equals("m")) {
					if (ignore == false) {

						changeEvent = event;

					} else {
						ignore = false;
					}
					eventNumber += 1;
				}
			}
		}
		xmlReader.close();

		return changeEvent;
	}

	private EObject resolveXRef(final String sEObjectURI, Resource mainResource, Resource otherResource) {
		EObject eob = getEObject(sEObjectURI, mainResource, otherResource);
		if (eob == null) {
			URI uri = URI.createURI(sEObjectURI);

			String nsURI = uri.trimFragment().toString();
			ResourceSet resourceSet = mainResource.getResourceSet();
			EPackage pkg = null;
			if (resourceSet != null) {
				EPackage.Registry registry = resourceSet.getPackageRegistry();
				pkg = (EPackage) registry.get(nsURI);
			}
			if (pkg == null) {
				resourceSet = mainResource.getResourceSet();
				if (resourceSet != null) {
					EPackage.Registry registry = resourceSet.getPackageRegistry();
					pkg = (EPackage) registry.get(nsURI);
				}
			}
			if (pkg != null) {
				eob = pkg.eResource().getEObject(uri.fragment());
			}
		}
		return eob;
	}

	private EObject getEObject(String uriFragment, Resource mainResource, Resource otherResource) {
		EObject eObject = null;
		EObject mainEObject = mainResource.getEObject(uriFragment);
		EObject otherEObject = otherResource.getEObject(uriFragment);

		if (mainEObject != null) {
			eObject = mainEObject;
		} else if (otherEObject != null) {
			eObject = otherEObject;
		}
		return eObject;
	}
}
