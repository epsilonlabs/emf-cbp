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
import org.eclipse.epsilon.cbp.io.StringOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CBPXMLResourceImpl extends CBPResource {

	
	public static void main(String[] args) throws Exception {
		EPackage.Registry.INSTANCE.put(EcorePackage.eINSTANCE.getNsURI(), EcorePackage.eINSTANCE);
		CBPXMLResourceImpl resource = new CBPXMLResourceImpl(URI.createURI("foo"));
		
		EAnnotation a = EcoreFactory.eINSTANCE.createEAnnotation();
		a.getDetails().put("foo", "bar");
		
		//EClass c1 = EcoreFactory.eINSTANCE.createEClass();
		//EClass c2 = EcoreFactory.eINSTANCE.createEClass();
		//resource.getContents().add(c1);
		//c1.setName("C1");
		//resource.getContents().add(c2);
		//resource.getContents().remove(c1);
		//resource.getContents().add(c1);
		
		//EAttribute a1 = EcoreFactory.eINSTANCE.createEAttribute();
		//resource.getContents().add(a1);
		//c1.getEStructuralFeatures().add(a1);
		//c1.getESuperTypes().add(c2);
		//a1.setName("a1");
		//a1.setEType(c1);
		//System.out.println(((EClass)resource.getContents().get(0)).getESuperTypes());
		//resource.getContents().addAll(Arrays.asList(c1, c2));
		StringOutputStream sos = new StringOutputStream();
		resource.save(sos, null);
		//System.out.println(sos.toString());
		
		resource = new CBPXMLResourceImpl(URI.createURI("foo"));
		resource.load(sos.getInputStream(), null);
		//System.out.println(((EClass)resource.getContents().get(0)).getESuperTypes());
		//resource.save(System.out, null);
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
			
			for (Event<?> event : eventAdapter.getEvents()) {
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
				
				if (event instanceof EStructuralFeatureEvent<?> || event instanceof ResourceEvent) {
					if (event.getPosition() != -1) {
						e.setAttribute("position", event.getPosition() + "");
					}
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
		}
		catch (Exception ex) {
			throw new IOException(ex);
		}
	}
	
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		//eventAdapter.setEnabled(false);
		getEObjects().clear();
		getEvents().clear();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			while ((line = reader.readLine()) != null) {				
					Document document = documentBuilder.parse(new ByteArrayInputStream(line.getBytes()));
					doLoad(document.getDocumentElement());
			}
		}
		catch (Exception ex) {
			throw new IOException(ex);
		}
		//eventAdapter.setEnabled(true);
	}
	
	protected void doLoad(Element e) {
		
		String name = e.getNodeName();
		
		Event<?> event = null;
		
		if ("register".equals(name)) {
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(e.getAttribute("epackage"));
			event = new RegisterEPackageEvent(ePackage);
		}
		else if ("create".equals(name)) {
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(e.getAttribute("epackage"));
			EClass eClass = (EClass) ePackage.getEClassifier(e.getAttribute("eclass"));
			event = new CreateEObjectEvent(eClass, this);
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
			EObject target = eObjects.get(Integer.parseInt(e.getAttribute("target")));
			EStructuralFeature eStructuralFeature = target.eClass().getEStructuralFeature(e.getAttribute("name"));
			((EStructuralFeatureEvent<?>) event).setEStructuralFeature(eStructuralFeature);
			((EStructuralFeatureEvent<?>) event).setTarget(target);
		}
		else if (event instanceof ResourceEvent) {
			((ResourceEvent) event).setResource(this);
		}
		
		if (event instanceof AddToEAttributeEvent || event instanceof AddToEReferenceEvent || event instanceof ResourceEvent) {
			event.setPosition(Integer.parseInt(e.getAttribute("position")));
		}
		
		if (event instanceof EObjectValuesEvent) {
			NodeList values = e.getElementsByTagName("value");
			for (int i=0;i<values.getLength();i++) {
				((EObjectValuesEvent) event).getValues().add((EObject) getValue(null, null, (Element) values.item(i)));
			}
		}
		else if (event instanceof EAttributeEvent) {
			EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
			NodeList values = e.getElementsByTagName("value");
			for (int i=0;i<values.getLength();i++) {
				eAttributeEvent.getValues().add(getValue(eAttributeEvent.getTarget(), eAttributeEvent.getEStructuralFeature(), (Element) values.item(i)));
			}
		}
		
		event.replay();
		//getEvents().add(event);
		
	}
	
	protected Object getValue(EObject eObject, EStructuralFeature eStructuralFeature, Element e) {
		Object value = null;
		if (e.hasAttribute("literal")) {
			value = ((EDataType) eStructuralFeature.getEType()).getEPackage().getEFactoryInstance().createFromString((EDataType) eStructuralFeature.getEType(), e.getAttribute("literal"));
		}
		else {
			value = eObjects.get(Integer.parseInt(e.getAttribute("eobject")));
		}
		return value;
	}
	
}
