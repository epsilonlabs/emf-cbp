package org.eclipse.epsilon.cbp.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CBPXMLResourceImpl extends CBPResource {

	
	public static void main(String[] args) throws Exception {
		
		CBPXMLResourceImpl resource = new CBPXMLResourceImpl(URI.createURI("foo"));
		EClass c1 = EcoreFactory.eINSTANCE.createEClass();
		EClass c2 = EcoreFactory.eINSTANCE.createEClass();
		resource.getContents().add(c1);
		//resource.getContents().add(c2);
		//resource.getContents().remove(c1);
		//resource.getContents().add(c1);
		
		EAttribute a1 = EcoreFactory.eINSTANCE.createEAttribute();
		c1.getEStructuralFeatures().add(a1);
		
		//resource.getContents().addAll(Arrays.asList(c1, c2));
		resource.save(System.out, null);
	}
	
	public CBPXMLResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	public AbstractCBPSerialiser getSerialiser() {
		return null;
	}

	@Override
	public AbstractCBPDeserialiser getDeserialiser() {
		return null;
	}

	@Override
	public void doSave(OutputStream out, Map<?, ?> options) throws IOException {
		for (Event event : eventAdapter.getEvents()) {
			try { doSave(event, out); }
			catch (Exception ex) { ex.printStackTrace(); }
		}
	}
	
	protected void doSave(Event event, OutputStream out) throws Exception {
		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element e = null;
		
		if (event instanceof RegisterEPackageEvent) {
			e = document.createElement("register");
			e.setAttribute("epackage", ((RegisterEPackageEvent) event).getePackage().getNsURI());
		}
		else if (event instanceof AddToResourceEvent || event instanceof RemoveFromResourceEvent || event instanceof AddToEReferenceEvent || event instanceof RemoveFromEReferenceEvent) {
			if (event instanceof AddToResourceEvent) e = document.createElement("add-to-resource");
			else if (event instanceof RemoveFromResourceEvent) e = document.createElement("remove-from-resource");
			else if (event instanceof AddToEReferenceEvent) e = document.createElement("add-to-ereference");
			else if (event instanceof RemoveFromEReferenceEvent) e = document.createElement("remove-from-ereference");
			
			for (EObject eObject : event.getEObjects()) {
				Element o = document.createElement("object");
				o.setAttribute("id", getURIFragment(eObject));
				e.appendChild(o);
			}
		}
		else {
			System.out.println(event);
		}
		
		
		if (e != null) document.appendChild(e);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
		out.write(System.getProperty("line.separator").getBytes());
	}
	
	
}
