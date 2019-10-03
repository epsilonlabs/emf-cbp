package org.eclipse.epsilon.cbp.test;

import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class ThesisTest {
    
    UMLFactory umlFactory = null;
    
    public ThesisTest() {
	UMLPackage.eINSTANCE.eClass();
    }

    @Test
    public void testSaveLoadUpdateLoad() throws Exception {
	//initialise, save, and unload
	CBPResource cbpResource = (CBPResource) (new CBPXMLResourceFactory()). //
		createResource(URI.createFileURI("D:\\helloword.cbpxml"));
	cbpResource.startNewSession("Initial");
	Model model = UMLFactory.eINSTANCE.createModel();
	cbpResource.getContents().add(model);
	model.setName("Hello World");
	cbpResource.save(null);
	cbpResource.unload();
	
	//load and print
	cbpResource.load(null);
	model = (Model) cbpResource.getContents().get(0);
	System.out.println("1: " + model.getName());
	
	//update, save, and unload
	cbpResource.startNewSession("Update");
	model.setName("Foo");
	cbpResource.save(null);
	cbpResource.unload();
	
	//load and print
	cbpResource.load(null);
	model = (Model) cbpResource.getContents().get(0);
	System.out.println("2: " + model.getName());
    }
    
    @Test
    public void testSave() throws Exception {
	CBPResource cbpResource = (CBPResource) (new CBPXMLResourceFactory()). //
		createResource(URI.createFileURI("D:\\helloword.cbpxml"));
	cbpResource.startNewSession("Initial");
	Model model = umlFactory.createModel();
	cbpResource.getContents().add(model);
	model.setName("Hello World");
	cbpResource.save(null);
    }
    
    @Test
    public void testLoad() throws Exception {
	CBPResource cbpResource = (CBPResource) (new CBPXMLResourceFactory()). //
		createResource(URI.createFileURI("D:\\helloword.cbpxml"));
	cbpResource.load(null);
	Model model = (Model) cbpResource.getContents().get(0);
	System.out.println(model.getName());
    }
    
    @Test
    public void testUpdate() throws Exception {
	CBPResource cbpResource = (CBPResource) (new CBPXMLResourceFactory()). //
		createResource(URI.createFileURI("D:\\helloword.cbpxml"));
	cbpResource.load(null);
	Model model = (Model) cbpResource.getContents().get(0);
	model.setName("Foo");
	cbpResource.save(null);
    }
    
    @Test
    public void testLoadUpdate() throws Exception {
	CBPResource cbpResource = (CBPResource) (new CBPXMLResourceFactory()). //
		createResource(URI.createFileURI("D:\\helloword.cbpxml"));
	cbpResource.load(null);
	Model model = (Model) cbpResource.getContents().get(0);
	System.out.println(model.getName());
    }
}
