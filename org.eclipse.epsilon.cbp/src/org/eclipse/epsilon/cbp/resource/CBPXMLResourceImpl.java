package org.eclipse.epsilon.cbp.resource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
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
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EObjectValuesEvent;
import org.eclipse.epsilon.cbp.event.EStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.event.Event;
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

public class CBPXMLResourceImpl extends CBPResource {
	
	public static void main(String[] args) throws Exception {
		EPackage.Registry.INSTANCE.put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		CBPXMLResourceImpl resource = new CBPXMLResourceImpl(URI.createURI("foo"));
		
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
		
		resource = new CBPXMLResourceImpl(URI.createURI("foo"));
		resource.load(sos.getInputStream(), null);
		//resource.save(System.out, null);
	}
	
	protected int persistedEvents = 0;
	
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
			
			// Ignore the first #eventsAfterMostRecentLoad events
			for (Event<?> event : getEvents().subList(persistedEvents, getEvents().size())) {
				
				Document document = documentBuilder.newDocument();
				Element e = null;
				
				if (event instanceof RegisterEPackageEvent) {
					RegisterEPackageEvent r = ((RegisterEPackageEvent) event);
					e = document.createElement("register");
					e.setAttribute("epackage", r.getEPackage().getNsURI());
				}
				else if (event instanceof CreateEObjectEvent) {
					e = document.createElement("create");
					e.setAttribute("epackage", ((CreateEObjectEvent) event).getEClass().getEPackage().getNsURI());
					e.setAttribute("eclass", ((CreateEObjectEvent) event).getEClass().getName());
					e.setAttribute("id", ((CreateEObjectEvent) event).getId());
				}
				else if (event instanceof AddToResourceEvent) e = document.createElement("add-to-resource");
				else if (event instanceof RemoveFromResourceEvent) e = document.createElement("remove-from-resource");
				else if (event instanceof AddToEReferenceEvent) e = document.createElement("add-to-ereference");
				else if (event instanceof RemoveFromEReferenceEvent) e = document.createElement("remove-from-ereference");
				else if (event instanceof SetEAttributeEvent) e = document.createElement("set-eattribute");
				else if (event instanceof SetEReferenceEvent) e = document.createElement("set-ereference");
				else if (event instanceof UnsetEReferenceEvent) e = document.createElement("unset-ereference");
				else if (event instanceof UnsetEAttributeEvent) e = document.createElement("unset-eattribute");	
				else if (event instanceof AddToEAttributeEvent) e = document.createElement("add-to-eattribute");
				else if (event instanceof RemoveFromEAttributeEvent) e = document.createElement("remove-from-eattribute");
				
				else {
					throw new RuntimeException("Unexpected event:" + event);
				}
				
				if (event instanceof EStructuralFeatureEvent<?>) {
					e.setAttribute("name", ((EStructuralFeatureEvent<?>) event).getEStructuralFeature().getName());
					e.setAttribute("target", getURIFragment(((EStructuralFeatureEvent<?>) event).getTarget()));
				}
				
				if (event instanceof AddToEReferenceEvent || event instanceof AddToEAttributeEvent || event instanceof AddToResourceEvent) {
					e.setAttribute("position", event.getPosition() + "");
				}
				
				if (event instanceof EObjectValuesEvent) {
					for (EObject eObject : ((EObjectValuesEvent) event).getValues()) {
						Element o = document.createElement("value");
						o.setAttribute("eobject", getURIFragment(eObject));
						e.appendChild(o);
					}
				}
				else if (event instanceof EAttributeEvent) {
					for (Object object : ((EAttributeEvent) event).getValues()) {
						Element o = document.createElement("value");
						o.setAttribute("literal", object + "");
						e.appendChild(o);
					}
				}
				
				if (e != null) document.appendChild(e);
				
				
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(out);
				transformer.transform(source, result);
				out.write(System.getProperty("line.separator").getBytes());
			}
			persistedEvents = getEvents().size();
		}
		catch (Exception ex) {
			throw new IOException(ex);
		}
	}
	
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		eventAdapter.setEnabled(false);
		eObjectToIdMap.clear();
		getEvents().clear();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() > 0) {
					Document document = documentBuilder.parse(new ByteArrayInputStream(line.getBytes()));
					doLoad(document.getDocumentElement());
				}
			}
			persistedEvents = getEvents().size();
		}
		catch (Exception ex) {
			throw new IOException(ex);
		}
		eventAdapter.setEnabled(true);
	}
	
	protected void doLoad(Element e) {
		
		String name = e.getNodeName();
		
		Event<?> event = null;
		
		if ("register".equals(name)) {
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(e.getAttribute("epackage"));
			event = new RegisterEPackageEvent(ePackage, eventAdapter);
		}
		else if ("create".equals(name)) {
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(e.getAttribute("epackage"));
			EClass eClass = (EClass) ePackage.getEClassifier(e.getAttribute("eclass"));
			event = new CreateEObjectEvent(eClass, this, e.getAttribute("id"));
		}	
		else if ("add-to-resource".equals(name)) event = new AddToResourceEvent();
		else if ("remove-from-resource".equals(name)) event = new RemoveFromResourceEvent();
		else if ("add-to-ereference".equals(name)) event = new AddToEReferenceEvent();
		else if ("remove-from-ereference".equals(name)) event = new RemoveFromEReferenceEvent();
		else if ("set-eattribute".equals(name)) event = new SetEAttributeEvent();
		else if ("set-ereference".equals(name)) event = new SetEReferenceEvent();
		else if ("unset-eattribute".equals(name)) event = new UnsetEAttributeEvent();
		else if ("unset-ereference".equals(name)) event = new UnsetEReferenceEvent();
		else if ("add-to-eattribute".equals(name)) event = new AddToEAttributeEvent();
		else if ("remove-from-eattribute".equals(name)) event = new RemoveFromEAttributeEvent();
		
		if (event instanceof EStructuralFeatureEvent<?>) {
			EObject target = getEObject(e.getAttribute("target"));
			EStructuralFeature eStructuralFeature = target.eClass().getEStructuralFeature(e.getAttribute("name"));
			((EStructuralFeatureEvent<?>) event).setEStructuralFeature(eStructuralFeature);
			((EStructuralFeatureEvent<?>) event).setTarget(target);
		}
		else if (event instanceof ResourceEvent) {
			((ResourceEvent) event).setResource(this);
		}
		
		if (event instanceof AddToEAttributeEvent || event instanceof AddToEReferenceEvent || event instanceof AddToResourceEvent) {
			event.setPosition(Integer.parseInt(e.getAttribute("position")));
		}
		
		if (event instanceof EObjectValuesEvent) {
			NodeList values = e.getElementsByTagName("value");
			for (int i=0;i<values.getLength();i++) {
				((EObjectValuesEvent) event).getValues().add(getEObject(((Element) values.item(i)).getAttribute("eobject")));
			}
		}
		else if (event instanceof EAttributeEvent) {
			EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
			NodeList values = e.getElementsByTagName("value");
			for (int i=0;i<values.getLength();i++) {
				eAttributeEvent.getValues().add(getLiteralValue(eAttributeEvent.getTarget(), eAttributeEvent.getEStructuralFeature(), (Element) values.item(i)));
			}
		}
		
		event.replay();
		getEvents().add(event);
	}
	
	protected Object getLiteralValue(EObject eObject, EStructuralFeature eStructuralFeature, Element e) {
		EDataType eDataType = ((EDataType) eStructuralFeature.getEType());
		return eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType, e.getAttribute("literal"));
	}
	
}
