package org.eclipse.epsilon.cbp.merging.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.junit.After;
import org.junit.Test;

public class CBPCompositeTest {

    private CBPResource cbpResource;
    private File cbpFile;
    private Script originalScript;
    private EolModule module;
    private InMemoryEmfModel model;
    private EPackage ePackage = NodePackage.eINSTANCE;

    public CBPCompositeTest() {

	cbpFile = new File("D:\\TEMP\\CONFLICTS\\temp\\model.cbpxml");
	if (cbpFile.exists())
	    cbpFile.delete();
	cbpResource = (CBPResource) (new CBPXMLResourceFactory()).createResource(URI.createFileURI(cbpFile.getAbsolutePath()));
	originalScript = new Script(cbpResource);
    }

    @Test
    public void generateCompositeDelete() {
	try {
	    // origin
	    originalScript.add("var node0 = new Node;");
	    originalScript.add("node0.name = \"Node 00\";");
	    originalScript.add("var node1 = new Node;");
	    originalScript.add("node1.name = \"Node 01\";");
	    originalScript.add("var node2 = new Node;");
	    originalScript.add("node2.name = \"Node 02\";");
	    originalScript.add("var node3 = new Node;");
	    originalScript.add("node3.name = \"Node 03\";");
	    originalScript.add("node0.valNodes.add(node1);");
	    originalScript.add("node1.refNodes.add(node0);");
	    originalScript.add("node1.valNodes.add(node2);");
	    originalScript.add("node2.refNodes.add(node1);");
	    originalScript.add("node0.refNodes.add(node1);");
	    originalScript.add("node0.refNodes.add(node2);");
	    originalScript.add("node3.refNodes.add(node0);");
	    originalScript.add("node3.refNodes.add(node1);");
	    originalScript.add("node3.refNodes.add(node2);");
	    // originalScript.add("node0.valNodes.remove(node1);");
	    originalScript.add("delete node1;");

	    executeScript(originalScript);

	} catch (Exception e) {
	    e.printStackTrace();
	}
	assertEquals(true, true);
    }

    @After
    public void postTest() throws IOException, XMLStreamException, ParserConfigurationException, TransformerException {
	// print results
	List<String> leftLines = Files.readAllLines(cbpFile.toPath());
	for (String line : leftLines) {
	    System.out.println(line);
	}

//	cbpResource.unload();
//	cbpResource.load(null);
//	TreeIterator<EObject> tree = cbpResource.getAllContents();
//	while (tree.hasNext()) {
//	    System.out.println(cbpResource.getURIFragment(tree.next()));
//	}
	cbpResource.unload();
    }

    public void executeScript(Script originalScript) throws Exception {
	// ROOT--------------------------------------------------
	cbpResource.startNewSession("ROOT");
	originalScript.run();
	cbpResource.save(null);
    }

    public class Script {
	private String text = new String();
	private Resource resource;

	public Script(Resource resource) {
	    this.resource = resource;
	}

	public void add(String line) {
	    text = text.concat(line).concat(System.lineSeparator());
	}

	public void clear() {
	    text = "";
	}

	public String toString() {
	    return text;
	}

	public void run() throws Exception {
	    module = new EolModule();
	    module.parse(text);
	    model = new InMemoryEmfModel("M", resource, ePackage);
	    module.getContext().getModelRepository().addModel(model);
	    module.execute();
	}
    }

}
