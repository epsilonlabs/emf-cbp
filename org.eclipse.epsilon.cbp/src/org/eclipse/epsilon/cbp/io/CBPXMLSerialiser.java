package org.eclipse.epsilon.cbp.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CBPXMLSerialiser extends AbstractCBPSerialiser {
	
	public static void main(String[] args) throws Exception {
		
		CBPXMLResourceImpl resource = new CBPXMLResourceImpl(URI.createURI("foo"));
		EClass c1 = EcoreFactory.eINSTANCE.createEClass();
		EClass c2 = EcoreFactory.eINSTANCE.createEClass();
		resource.getContents().add(c1);
		//resource.getContents().addAll(Arrays.asList(c1, c2));
		resource.save(System.out, null);
	}
	
	
	public CBPXMLSerialiser(CBPResource resource) {
		super(resource);
	}
	
	@Override
	public void serialise(OutputStream out, Map<?, ?> options) throws IOException {
		for (Event event : eventList) {
			try { serialise(event, out); }
			catch (Exception ex) { ex.printStackTrace(); }
		}
	}
	
	protected void serialise(Event event, OutputStream out) throws Exception {
		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element e = null;
		
		if (event instanceof RegisterEPackageEvent) {
			e = document.createElement("register");
			e.setAttribute("epackage", ((RegisterEPackageEvent) event).getePackage().getNsURI());
		}
		else if (event instanceof AddToResourceEvent) {
			e = document.createElement("add-to-resource");
			for (EObject eObject : event.getEObjects()) {
				Element o = document.createElement("object");
				o.setAttribute("id", resource.getObjectId(eObject) + "");
				e.appendChild(o);
			}
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
	
	
	@Override
	protected void handleEPackageRegistrationEvent(RegisterEPackageEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleAddToResourceEvent(AddToResourceEvent e) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleRemoveFromResourceEvent(RemoveFromResourceEvent e) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleSetEAttributeEvent(EAttributeEvent e) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleAddToEAttributeEvent(EAttributeEvent e) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleSetEReferenceEvent(SetEReferenceEvent e) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleAddToEReferenceEvent(AddToEReferenceEvent e) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleRemoveFromAttributeEvent(EAttributeEvent e) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handleRemoveFromEReferenceEvent(RemoveFromEReferenceEvent e) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	/*
	public static void main(String[] args) throws Exception {
		
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
		Element add = document.createElement("add");
		add.setAttribute("feature", "code");
		document.appendChild(add);
		
		
		for (int i=0;i<2;i++) {
			
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(System.out);
		transformer.transform(source, result);
	}*/
	
	
	
}
