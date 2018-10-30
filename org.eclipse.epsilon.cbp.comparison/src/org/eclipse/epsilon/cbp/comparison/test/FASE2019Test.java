package org.eclipse.epsilon.cbp.comparison.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;
import org.eclipse.epsilon.cbp.hybrid.HybridResource.IdType;
import org.eclipse.epsilon.cbp.hybrid.xmi.HybridXMIResourceImpl;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Test;

public class FASE2019Test {

    public EPackage ePackage = (EPackage) UMLPackage.eINSTANCE;

    @Test
    public void testExample() throws Exception {
	File originXmiFile = new File("D:\\TEMP\\FASE\\Example\\origin.xmi");
	File originCbpFile = new File("D:\\TEMP\\FASE\\Example\\origin.cbpxml");
	File leftXmiFile = new File("D:\\TEMP\\FASE\\Example\\left.xmi");
	File leftCbpFile = new File("D:\\TEMP\\FASE\\Example\\left.cbpxml");
	File rightXmiFile = new File("D:\\TEMP\\FASE\\Example\\right.xmi");
	File rightCbpFile = new File("D:\\TEMP\\FASE\\Example\\right.cbpxml");
	
	originXmiFile.delete();
	originCbpFile.delete();
	leftXmiFile.delete();
	leftCbpFile.delete();
	rightXmiFile.delete();
	rightCbpFile.delete();

	// ORIGIN
	XMIResource originXmiResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(originXmiFile.getAbsolutePath()));
	HybridResource originResource = new HybridXMIResourceImpl(originXmiResource, new FileOutputStream(originCbpFile, true));
	originResource.setIdType(IdType.NUMERIC, "O-");
	String originSessionName = "ORIGIN";
	
	StringBuilder originScript = new StringBuilder();
	originScript.append("var p1 = new Package;");
	originScript.append("p1.name = \"Package 1\";");
	originScript.append("var p2 = new Package;");
	originScript.append("p2.name = \"Package 2\";");
	originScript.append("var c1 = new Class;");
	originScript.append("c1.name = \"Class A\";");
	originScript.append("var c2 = new Class;");
	originScript.append("c2.name = \"Class B\";");
	originScript.append("var c3 = new Class;");
	originScript.append("c3.name = \"Class C\";");
	originScript.append("p1.packagedElements.add(c1);");
	originScript.append("p1.packagedElements.add(c2);");
	originScript.append("p1.packagedElements.add(c3);");
	
	saveModel(originResource, originSessionName, originScript);
	
	FileUtils.copyFile(originXmiFile, leftXmiFile);
	FileUtils.copyFile(originXmiFile, rightXmiFile);
	FileUtils.copyFile(originCbpFile, leftCbpFile);
	FileUtils.copyFile(originCbpFile, rightCbpFile);
	
	// LEFT
	XMIResource leftXmiResource = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiFile.getAbsolutePath()));
	HybridResource leftResource = new HybridXMIResourceImpl(leftXmiResource, new FileOutputStream(leftCbpFile, true));
	leftResource.setIdType(IdType.NUMERIC, "L-");
	leftResource.load(null);
	String leftSessionName = "LEFT";
	
	StringBuilder leftScript = new StringBuilder();
	leftScript.append("var p1 = Package.allInstances.selectOne(node | node.name == \"Package 1\");");
	leftScript.append("p1.name = \"Package 01\";");
	leftScript.append("var c4 = new Class;");
	leftScript.append("c4.name = \"Class E\";");
	leftScript.append("p1.packagedElements.add(2, c4);");
	
	saveModel(leftResource, leftSessionName, leftScript);

    }

    /**
     * @param hybridResource
     * @param sessionName
     * @param script
     * @throws Exception
     * @throws EolRuntimeException
     * @throws IOException
     */
    private void saveModel(HybridResource hybridResource, String sessionName, StringBuilder script) throws Exception, EolRuntimeException, IOException {
	EolModule module = new EolModule();
	InMemoryEmfModel model = new InMemoryEmfModel("M", hybridResource, ePackage);
	module.getContext().getModelRepository().addModel(model);

	hybridResource.startNewSession(sessionName);
	module.parse(script.toString());
	module.execute();
	hybridResource.save(null);
	hybridResource.unload();
    }

}
