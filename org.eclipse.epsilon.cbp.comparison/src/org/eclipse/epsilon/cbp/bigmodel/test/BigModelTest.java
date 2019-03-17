package org.eclipse.epsilon.cbp.bigmodel.test;

import org.junit.Test;

import com.google.common.io.Files;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.ResourceAttachmentChange;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope2;
import org.eclipse.emf.compare.uml2.internal.AssociationChange;
import org.eclipse.emf.compare.uml2.internal.DirectedRelationshipChange;
import org.eclipse.emf.compare.uml2.internal.MultiplicityElementChange;
import org.eclipse.emf.compare.uml2.internal.postprocessor.UMLPostProcessor;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.bigmodel.BigModelGenerator;
import org.eclipse.epsilon.cbp.bigmodel.ModifiedEMFCompare;
import org.eclipse.epsilon.cbp.bigmodel.ModifiedEMFCompare.ModifiedBuilder;
import org.eclipse.epsilon.cbp.comparison.CBPComparisonImpl;
import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.ICBPComparison;
import org.eclipse.epsilon.cbp.comparison.UMLObjectTreePostProcessor;
import org.eclipse.epsilon.cbp.comparison.test.Result;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.gmt.modisco.java.emf.JavaPackage;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;

public class BigModelTest {

    private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    private Random random = new Random();
    private EPackage ePackage = null;
    Map<Object, Object> options = new HashMap<>();

    enum ChangeType {
	CHANGE, ADD, MOVE, DELETE;
    }

    public BigModelTest() {
	JavaPackage.eINSTANCE.eClass();
	UMLPackage.eINSTANCE.eClass();
	MoDiscoXMLPackage.eINSTANCE.eClass();
	
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
	options.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
    }

    @Test
    public void generateBigModel() throws DiscoveryException, CoreException, IOException {

//	String sourcePath = "D:\\TEMP\\org.eclipse.uml2";
	 String sourcePath = "D:\\TEMP\\org.eclipse.epsilon\\plugins\\org.eclipse.epsilon.flexmi";
	String targetPath = "D:\\TEMP\\FASE\\bigmodel";
	BigModelGenerator generator = new BigModelGenerator(sourcePath, targetPath);
	generator.generate();
	assertEquals(true, true);
    }

    @Test
    public void randomModification() throws IOException, FactoryConfigurationError, XMLStreamException {
	System.out.println("START: " + (new Date()).toString());

	Logger.getRootLogger().setLevel(Level.OFF);

//	File outputFile = new File("D:\\TEMP\\FASE\\performance\\output.csv");
	File outputFile = new File("D:\\TEMP\\FASE\\debug\\output.csv");
	if (outputFile.exists())
	    outputFile.delete();
	PrintWriter writer = new PrintWriter(outputFile);

	// print header
	writer.println("num,levc,revc,aoc,clt,clm,cdc,ctt,ctm,cdt,cdm,cct,ccm,lelc,relc,slt,slm,sdc,smt,smm,sdt,sdm,sct,scm");
	writer.flush();
	
	String originXmiPath = "D:\\TEMP\\CONFLICTS\\debug\\origin.xmi";
	String leftXmiPath = "D:\\TEMP\\CONFLICTS\\debug\\left.xmi";
	String rightXmiPath = "D:\\TEMP\\CONFLICTS\\debug\\right.xmi";
	String originCbpPath = "D:\\TEMP\\CONFLICTS\\debug\\origin.cbpxml";
	String leftCbpPath = "D:\\TEMP\\CONFLICTS\\debug\\left.cbpxml";
	String rightCbpPath = "D:\\TEMP\\CONFLICTS\\debug\\right.cbpxml";
//	String originXmiPath = "D:\\TEMP\\CONFLICTS\\performance\\origin.xmi";
//	String leftXmiPath = "D:\\TEMP\\CONFLICTS\\performance\\left.xmi";
//	String rightXmiPath = "D:\\TEMP\\CONFLICTS\\performance\\right.xmi";
//	String originCbpPath = "D:\\TEMP\\CONFLICTS\\performance\\origin.cbpxml";
//	String leftCbpPath = "D:\\TEMP\\CONFLICTS\\performance\\left.cbpxml";
//	String rightCbpPath = "D:\\TEMP\\CONFLICTS\\performance\\right.cbpxml";

	File leftXmiFile = new File(leftXmiPath);
	File rightXmiFile = new File(rightXmiPath);
	File originXmiFile = new File(originXmiPath);
	File originCbpFile = new File(originCbpPath);
	File leftCbpFile = new File(leftCbpPath);
	File rightCbpFile = new File(rightCbpPath);

	if (originCbpFile.exists())
	    originCbpFile.delete();
	if (leftCbpFile.exists())
	    leftCbpFile.delete();
	if (rightCbpFile.exists())
	    rightCbpFile.delete();

	XMIResource originXmi = (XMIResource) ((new XMIResourceFactoryImpl()).createResource(URI.createFileURI(originXmiPath)));
	originXmi.load(options);

	CBPXMLResourceFactory cbpFactory = new CBPXMLResourceFactory();
	CBPXMLResourceImpl originCbp = (CBPXMLResourceImpl) cbpFactory.createResource(URI.createFileURI(originCbpPath));
	originCbp.setIdType(IdType.NUMERIC, "O-");
	System.out.println("Creating origin cbp");
	originCbp.startNewSession("ORIGIN");
	originCbp.getContents().addAll(originXmi.getContents());
	originCbp.save(null);
	saveXmiWithID(originCbp, originXmiFile);
	originCbp.unload();
	originXmi.unload();

	System.out.println("Copying origin cbp to left and right cbps");
	Files.copy(originCbpFile, leftCbpFile);
	Files.copy(originCbpFile, rightCbpFile);

	ePackage = this.getEPackageFromFiles(leftCbpFile, rightCbpFile);

	CBPXMLResourceImpl leftCbp = (CBPXMLResourceImpl) cbpFactory.createResource(URI.createFileURI(leftCbpFile.getAbsolutePath()));
	CBPXMLResourceImpl rightCbp = (CBPXMLResourceImpl) cbpFactory.createResource(URI.createFileURI(rightCbpFile.getAbsolutePath()));
	leftCbp.setIdType(IdType.NUMERIC, "L-");
	rightCbp.setIdType(IdType.NUMERIC, "R-");

	// Start modifying the models
	List<ChangeType> seeds = new ArrayList<>();
	setProbability(seeds, 30, ChangeType.CHANGE);
	setProbability(seeds, 1, ChangeType.ADD);
	setProbability(seeds, 1, ChangeType.DELETE);
	setProbability(seeds, 10, ChangeType.MOVE);

	System.out.println("Loading " + leftCbpFile.getName() + "...");
	leftCbp.load(null);
	leftCbp.startNewSession("LEFT");
	System.out.println("Loading " + rightCbpFile.getName() + "...");
	rightCbp.load(null);
	rightCbp.startNewSession("RIGHT");

	List<EObject> leftEObjectList = identifyAllEObjects(leftCbp);
	List<EObject> rightEObjectList = identifyAllEObjects(rightCbp);

	List<BigModelResult> results = new ArrayList<>();
	int modificationCount = 3000;
	int number = 0;
	for (int i = 1; i <= modificationCount; i++) {
	    System.out.print("Change " + i + ":");
	    startModification(leftCbp, leftEObjectList, seeds);
	    leftCbp.save(null);
	    startModification(rightCbp, rightEObjectList, seeds);
	    rightCbp.save(null);
	    System.out.println();

	    // do comparison
//	    if (i % 50000 == 0) {
	    if (i % modificationCount == 0) {
		number++;

		BigModelResult result = new BigModelResult();

		System.out.println("Reload from the Cbps ...");
		CBPXMLResourceImpl leftCbp2 = (CBPXMLResourceImpl) cbpFactory.createResource(URI.createFileURI(leftCbpFile.getAbsolutePath()));
		CBPXMLResourceImpl rightCbp2 = (CBPXMLResourceImpl) cbpFactory.createResource(URI.createFileURI(rightCbpFile.getAbsolutePath()));
		leftCbp2.load(null);
		rightCbp2.load(null);
		System.out.println("Saving changes to Xmis ...");
		XMIResource leftXmi = saveXmiWithID(leftCbp2, leftXmiFile);
		XMIResource rightXmi = saveXmiWithID(rightCbp2, rightXmiFile);
		leftCbp2.unload();
		rightCbp2.unload();

		ICBPComparison changeComparison = new CBPComparisonImpl();
		changeComparison.setDiffEMFCompareFile(new File(originCbpFile.getAbsolutePath().replaceAll("origin.cbpxml", "left.txt")));
		changeComparison.setObjectTreeFile(new File(originCbpFile.getAbsolutePath().replaceAll("origin.cbpxml", "tree.txt")));
		changeComparison.addObjectTreePostProcessor(new UMLObjectTreePostProcessor());
		changeComparison.compare(leftCbpFile, rightCbpFile, originCbpFile);

		result.setNumber(number);
		writer.print(result.getNumber());
		writer.print(",");
		result.setLeftEventCount(changeComparison.getLeftEvents().size());
		writer.print(result.getLeftEventCount());
		writer.print(",");
		result.setRightEventCount(changeComparison.getRightEvents().size());
		writer.print(result.getRightEventCount());
		writer.print(",");
		result.setAffectedObjectCount(changeComparison.getObjectTree().size());
		writer.print(result.getAffectedObjectCount());
		writer.print(",");
		result.setChangeLoadTime(changeComparison.getLoadTime());
		writer.print(result.getChangeLoadTime());
		writer.print(",");
		result.setChangeLoadMemory(changeComparison.getLoadMemory());
		writer.print(result.getChangeLoadMemory());
		writer.print(",");
		result.setChangeDiffCount(changeComparison.getDiffCount());
		writer.print(result.getChangeDiffCount());
		writer.print(",");
		result.setChangeTreeTime(changeComparison.getObjectTreeConstructionTime());
		writer.print(result.getChangeTreeTime());
		writer.print(",");
		result.setChangeTreeMemory(changeComparison.getObjectTreeConstructionMemory());
		writer.print(result.getChangeTreeMemory());
		writer.print(",");
		result.setChangeDiffTime(changeComparison.getDiffTime());
		writer.print(result.getChangeDiffTime());
		writer.print(",");
		result.setChangeDiffMemory(changeComparison.getDiffMemory());
		writer.print(result.getChangeDiffMemory());
		writer.print(",");
		result.setChangeComparisonTime(changeComparison.getComparisonTime());
		writer.print(result.getChangeComparisonTime());
		writer.print(",");
		result.setChangeComparisonMemory(changeComparison.getComparisonMemory());
		writer.print(result.getChangeComparisonMemory());
		writer.print(",");

		System.out.println("Perform EMF Compare comparison as a bechmark ...");

		leftXmi.unload();
		rightXmi.unload();

		long startTime = 0;
		long endTime = 0;
		long startMemory = 0;
		long endMemory = 0;

		System.out.println("Loading xmis ...");
		System.gc();
		startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		startTime = System.nanoTime();
		leftXmi.load(options);
		rightXmi.load(options);
		endTime = System.nanoTime();
		System.gc();
		endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		ModifiedEMFCompare stateComparison = doEMFComparison(leftXmi, rightXmi);

		result.setLeftElementCount(leftXmi.getEObjectToIDMap().size());
		writer.print(result.getLeftElementCount());
		writer.print(",");
		result.setRightElementCount(rightXmi.getEObjectToIDMap().size());
		writer.print(result.getRightElementCount());
		writer.print(",");
		result.setStateLoadTime(endTime - startTime);
		writer.print(result.getStateLoadTime());
		writer.print(",");
		result.setStateLoadMemory(endMemory - startMemory);
		writer.print(result.getStateLoadMemory());
		writer.print(",");
		result.setStateDiffCount(stateComparison.getDiffs().size());
		writer.print(result.getStateDiffCount());
		writer.print(",");
		result.setStateMatchTime(stateComparison.getMatchTime());
		writer.print(result.getStateMatchTime());
		writer.print(",");
		result.setStateMatchMemory(stateComparison.getMatchMemory());
		writer.print(result.getStateMatchMemory());
		writer.print(",");
		result.setStateDiffTime(stateComparison.getDiffTime());
		writer.print(result.getStateDiffTime());
		writer.print(",");
		result.setStateDiffMemory(stateComparison.getDiffMemory());
		writer.print(result.getStateDiffMemory());
		writer.print(",");
		result.setStateComparisonTime(stateComparison.getComparisonTime());
		writer.print(result.getStateComparisonTime());
		writer.print(",");
		result.setStateComparisonMemory(stateComparison.getComparisonMemory());
		writer.print(result.getStateComparisonMemory());
		writer.println();
		writer.flush();

		results.add(result);

		leftXmi.unload();
		rightXmi.unload();

		System.out.println();
	    }

	}
	writer.close();

	System.out.println("FINISHED! " + (new Date()).toString());
	assertEquals(true, true);
    }

    /**
     * @param cbp
     * @return
     */
    private List<EObject> identifyAllEObjects(CBPXMLResourceImpl cbp) {
	List<EObject> eObjectList = new ArrayList<>();
	// int objectCount = originXmi.getEObjectToIDMap().size();
	TreeIterator<EObject> iterator = cbp.getAllContents();
	while (iterator.hasNext()) {
	    EObject eObject = iterator.next();
	    // String id = originXmi.getURIFragment(eObject);
	    EClass eClass = eObject.eClass();
	    eObjectList.add(eObject);
	}
	return eObjectList;
    }

    /**
     * @param cbp
     * @param modificationCount
     * @param seeds
     */
    private void startModification(CBPXMLResourceImpl cbp, List<EObject> eObjectList, List<ChangeType> seeds) {
	ChangeType changeType = seeds.get(random.nextInt(seeds.size()));
	if (changeType == ChangeType.CHANGE) {
	    System.out.print(" " + changeType);
	    changeModification(eObjectList, cbp);
	} else if (changeType == ChangeType.ADD) {
	    System.out.print(" " + changeType);
	    addModification(eObjectList, cbp);
	} else if (changeType == ChangeType.DELETE) {
	    System.out.print(" " + changeType);
	    deleteModification(eObjectList, cbp);
	} else if (changeType == ChangeType.MOVE) {
	    System.out.print(" " + changeType);
	    moveModification(eObjectList, cbp);
	}
	System.out.print(" " + eObjectList.size());

    }

    private void addModification(List<EObject> eObjectList, CBPXMLResourceImpl leftCbp) {
	// 0 for Attributes
	boolean found = false;
	while (!found) {
	    if (random.nextInt(2) == 0) {
		int index1 = random.nextInt(eObjectList.size());
		EObject eObject1 = eObjectList.get(index1);
		if (eObject1 != null) {
		    if (eObject1.eResource() == null) {
			eObjectList.remove(eObject1);
			continue;
		    }
		    String id = leftCbp.getURIFragment(eObject1);
		    if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
			eObjectList.remove(eObject1);
			continue;
		    }
		}
		EList<EAttribute> attributes1 = eObject1.eClass().getEAllAttributes();
		List<EAttribute> filteredEAttributes = new ArrayList<>();
		for (EAttribute eAttribute : attributes1) {
		    if (eAttribute.isMany() && eAttribute.isChangeable()) {
			filteredEAttributes.add(eAttribute);
		    }
		}
		if (filteredEAttributes.size() > 0) {
		    EAttribute eAttribute = filteredEAttributes.get(random.nextInt(filteredEAttributes.size()));
		    EList<Object> values = (EList<Object>) eObject1.eGet(eAttribute);
		    Object value = null;
		    if (eAttribute.getEAttributeType().getName().equals("String")) {
			value = "Z" + EcoreUtil.generateUUID();
		    } else if (eAttribute.getEAttributeType().getName().equals("Integer")) {
			value = 0;
		    } else if (eAttribute.getEAttributeType().getName().equals("Boolean")) {
			value = true;
		    } else if (eAttribute.getEAttributeType().getName().equals("UnlimitedNatural")) {
			value = 0;
		    } else if (eAttribute.getEAttributeType().getName().equals("EBoolean")) {
			value = random.nextBoolean();
		    } else if (eAttribute.getEAttributeType().getName().equals("EString")) {
			value = "Z" + EcoreUtil.generateUUID();
		    } else if (eAttribute.getEAttributeType().getName().equals("EInt")) {
			value = random.nextInt(10);
		    }

		    if (value != null) {
			if (values.size() > 0) {
			    int pos = random.nextInt(values.size() - 1);
			    values.add(pos, value);
			    found = true;
			} else {
			    values.add(value);
			    found = true;
			}
		    }

		}
	    }
	    // 1 for References
	    else {

		int index1 = random.nextInt(eObjectList.size());
		EObject eObject1 = eObjectList.get(index1);
		if (eObject1 != null) {
		    if (eObject1.eResource() == null) {
			eObjectList.remove(eObject1);
			continue;
		    }
		    String id = leftCbp.getURIFragment(eObject1);
		    if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
			eObjectList.remove(eObject1);
			continue;
		    }
		}
		EList<EReference> references1 = eObject1.eClass().getEAllContainments();
		List<EReference> filteredEReferences = new ArrayList<>();
		for (EReference eReference : references1) {
		    if (eReference.isChangeable()) {
			filteredEReferences.add(eReference);
		    }
		}
		if (filteredEReferences.size() > 0) {
		    EReference eReference = filteredEReferences.get(random.nextInt(filteredEReferences.size()));
		    EFactory factory = ePackage.getEFactoryInstance();
		    if (!eReference.getEReferenceType().isAbstract()) {
			EObject eObject2 = factory.create(eReference.getEReferenceType());
			// eObject2.eSet(eObject2.eClass().getEStructuralFeature("name"),
			// "Z" + EcoreUtil.generateUUID());
			if (eReference.isMany()) {
			    EList<EObject> values = (EList<EObject>) eObject1.eGet(eReference);
			    if (values.size() > 0) {
				values.add(random.nextInt(values.size()), eObject2);
				found = true;
			    } else {
				try {
				    values.add(eObject2);
				    eObjectList.add(eObject2);
				    found = true;
				} catch (Exception e) {
				    // e.printStackTrace();
				}
			    }
			} else {
			    try {
				if (eObject1.eGet(eReference) != null) {
				    continue;
				}
				eObject1.eSet(eReference, eObject2);
				eObjectList.add(eObject2);
				found = true;
			    } catch (Exception e) {
				// e.printStackTrace();
			    }
			}
		    }

		}
	    }
	}

    }

    private void moveModification(List<EObject> eObjectList, CBPXMLResourceImpl leftCbp) {
	// 0 for Attributes
	boolean found = false;
	while (!found) {
	    if (random.nextInt(2) == 0) {
		int index1 = random.nextInt(eObjectList.size());
		EObject eObject1 = eObjectList.get(index1);
		if (eObject1.eResource() == null) {
		    eObjectList.remove(eObject1);
		    continue;
		}
		if (eObject1 != null) {
		    String id = leftCbp.getURIFragment(eObject1);
		    if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
			eObjectList.remove(eObject1);
			continue;
		    }
		}
		EList<EAttribute> attributes1 = eObject1.eClass().getEAllAttributes();
		List<EAttribute> filteredEAttributes = new ArrayList<>();
		for (EAttribute eAttribute : attributes1) {
		    if (eAttribute.isMany() && eAttribute.isChangeable()) {
			filteredEAttributes.add(eAttribute);
		    }
		}
		if (filteredEAttributes.size() > 0) {
		    EAttribute eAttribute = filteredEAttributes.get(random.nextInt(filteredEAttributes.size()));
		    EList<Object> values = (EList<Object>) eObject1.eGet(eAttribute);
		    if (values.size() > 1) {
			int from = random.nextInt(values.size() - 1);
			int to = random.nextInt(values.size() - 1);
			if (from == to && from != 0) {
			    from--;
			}
			values.move(from, to);
			found = true;
		    }

		}
	    }
	    // 1 for References
	    else {
		// 0 move-within
		if (random.nextInt(2) == 0) {
		    int index1 = random.nextInt(eObjectList.size());
		    EObject eObject1 = eObjectList.get(index1);
		    if (eObject1 != null) {
			if (eObject1.eResource() == null) {
			    eObjectList.remove(eObject1);
			    continue;
			}
			String id = leftCbp.getURIFragment(eObject1);
			if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
			    eObjectList.remove(eObject1);
			    continue;
			}
		    }
		    EList<EReference> references1 = eObject1.eClass().getEAllContainments();
		    List<EReference> filteredEReferences = new ArrayList<>();
		    for (EReference eReference : references1) {
			if (eReference.isMany() && eReference.isChangeable()) {
			    filteredEReferences.add(eReference);
			}
		    }
		    if (filteredEReferences.size() > 0) {
			EReference eReference = filteredEReferences.get(random.nextInt(filteredEReferences.size()));
			EList<EObject> values = (EList<EObject>) eObject1.eGet(eReference);
			if (values.size() > 1) {
			    int from = random.nextInt(values.size() - 1);
			    int to = random.nextInt(values.size() - 1);
			    if (from == to && from != 0) {
				from--;
			    }
			    values.move(from, to);
			    found = true;
			}
		    }
		}
		// 1 move-between
		else {
		    int index1 = random.nextInt(eObjectList.size());
		    EObject eObject1 = eObjectList.get(index1);

		    if (eObject1 != null) {
			if (eObject1.eResource() == null) {
			    eObjectList.remove(eObject1);
			    continue;
			}
			String id = leftCbp.getURIFragment(eObject1);
			if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
			    eObjectList.remove(eObject1);
			    continue;
			}
		    }
		    int index2 = random.nextInt(eObjectList.size());
		    EObject eObject2 = eObjectList.get(index2);

		    if (eObject2 != null) {
			if (eObject2.eResource() == null) {
			    eObjectList.remove(eObject2);
			    continue;
			}
			String id = leftCbp.getURIFragment(eObject2);
			if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
			    eObjectList.remove(eObject2);
			    continue;
			}
		    }
		    EList<EReference> references1 = eObject1.eClass().getEAllContainments();
		    List<EReference> filteredEReferences = new ArrayList<>();
		    for (EReference eReference : references1) {
			if (eReference.isChangeable() && eReference.getEOpposite() == null) {
			    EClass referenceType = eReference.getEReferenceType();
			    EClass eObjectType = eObject2.eClass();
			    if (eObjectType.equals(referenceType)) {
				filteredEReferences.add(eReference);
			    }
			}
		    }
		    if (filteredEReferences.size() > 0) {
			EReference eReference = filteredEReferences.get(random.nextInt(filteredEReferences.size()));
			if (eReference.isMany()) {
			    EList<EObject> values = (EList<EObject>) eObject1.eGet(eReference);
			    if (values.size() > 0 && !values.contains(eObject2)) {
				try {
				    int pos = random.nextInt(values.size());
				    values.add(pos, eObject2);
				    found = true;
				} catch (Exception e) {
				}
			    } else {
				try {
				    values.add(eObject2);
				    found = true;
				} catch (Exception e) {
				}
			    }
			} else {
			    try {
				if (eObject1.eGet(eReference) != null) {
				    continue;
				}
				eObject1.eSet(eReference, eObject2);
				found = true;
			    } catch (Exception e) {
			    }
			}
		    }
		}
	    }
	}

    }

    private void changeModification(List<EObject> eObjectList, CBPXMLResourceImpl leftCbp) {
	// 0 for Attributes
	boolean found = false;
	while (!found) {
	    if (random.nextInt(2) == 0) {
		int index1 = random.nextInt(eObjectList.size());
		EObject eObject1 = eObjectList.get(index1);
		if (eObject1 != null) {
		    String id = leftCbp.getURIFragment(eObject1);
		    if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
			eObjectList.remove(eObject1);
			continue;
		    }
		    if (eObject1.eResource() == null) {
			eObjectList.remove(eObject1);
			continue;
		    }

		}
		EList<EAttribute> attributes1 = eObject1.eClass().getEAllAttributes();
		List<EAttribute> filteredEAttributes = new ArrayList<>();
		for (EAttribute eAttribute : attributes1) {
		    if (!eAttribute.isMany() && eAttribute.isChangeable()) {
			filteredEAttributes.add(eAttribute);
		    }
		}
		if (filteredEAttributes.size() > 0) {
		    EAttribute eAttribute = filteredEAttributes.get(random.nextInt(filteredEAttributes.size()));
		    if (eAttribute.getEAttributeType().getName().equals("String")) {
			eObject1.eSet(eAttribute, "Z" + EcoreUtil.generateUUID());
			found = true;
		    } else if (eAttribute.getEAttributeType().getName().equals("Integer")) {
			eObject1.eSet(eAttribute, 0);
			found = true;
		    } else if (eAttribute.getEAttributeType().getName().equals("Boolean")) {
			eObject1.eSet(eAttribute, true);
			found = true;
		    } else if (eAttribute.getEAttributeType().getName().equals("UnlimitedNatural")) {
			eObject1.eSet(eAttribute, 0);
			found = true;
		    } else if (eAttribute.getEAttributeType().getName().equals("EBoolean")) {
			eObject1.eSet(eAttribute, !(Boolean) eObject1.eGet(eAttribute));
			found = true;
		    } else if (eAttribute.getEAttributeType().getName().equals("EString")) {
			eObject1.eSet(eAttribute, "Z" + EcoreUtil.generateUUID());
			found = true;
		    } else if (eAttribute.getEAttributeType().getName().equals("EInt")) {
			eObject1.eSet(eAttribute, 1 + (Integer) eObject1.eGet(eAttribute));
			found = true;
		    } else {
			continue;
			// System.out.println(eAttribute.getEAttributeType().getName());
		    }
		}
	    }
	    // 1 for References
	    else {
		int index1 = random.nextInt(eObjectList.size());
		EObject eObject1 = eObjectList.get(index1);
		if (eObject1 != null) {

		    String id = leftCbp.getURIFragment(eObject1);
		    if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
			eObjectList.remove(eObject1);
			continue;
		    }
		    if (eObject1.eResource() == null) {
			eObjectList.remove(eObject1);
			continue;
		    }
		}
		int index2 = random.nextInt(eObjectList.size());
		EObject eObject2 = eObjectList.get(index2);
		if (eObject2 != null) {

		    String id = leftCbp.getURIFragment(eObject2);
		    if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
			eObjectList.remove(eObject2);
			continue;
		    }
		    if (eObject2.eResource() == null) {
			eObjectList.remove(eObject2);
			continue;
		    }
		}
		EList<EReference> references1 = eObject1.eClass().getEAllReferences();
		List<EReference> filteredEReferences = new ArrayList<>();
		for (EReference eReference : references1) {
		    if (!eReference.isMany() && !eReference.isContainment() && eReference.isChangeable() && eReference.getEOpposite() == null) {
			EClass referenceType = eReference.getEReferenceType();
			EClass eObjectType = eObject2.eClass();
			if (eObjectType.equals(referenceType)) {
			    filteredEReferences.add(eReference);
			}
		    }
		}
		if (filteredEReferences.size() > 0) {
		    try {
			EReference eReference = filteredEReferences.get(random.nextInt(filteredEReferences.size()));
			EObject oldEObject = (EObject) eObject1.eGet(eReference);
			eObject1.eSet(eReference, eObject2);

			if (oldEObject != null && oldEObject.eResource() == null) {
			    eObjectList.remove(oldEObject);
			}

			// System.out.println( leftCbp.getURIFragment(eObject1)
			// + "." + eReference.getName() + "." +
			// leftCbp.getURIFragment(eObject2) + ".SET");
			// if (eReference.getName().equals("association")) {
			// System.out.println();
			// }
			found = true;
		    } catch (Exception e) {
		    }
		}
	    }
	}
    }

    /**
     * @param eObjectList
     * @param leftCbp
     */
    private void deleteModification(List<EObject> eObjectList, CBPXMLResourceImpl leftCbp) {
	boolean found = false;

	while (!found) {
	    // 0 for Attributes
	    if (random.nextInt(2) == 0) {
		int index = random.nextInt(eObjectList.size());
		EObject eObject = eObjectList.get(index);
		EList<EAttribute> eAttributes = eObject.eClass().getEAllAttributes();
		if (eAttributes.size() > 0) {
		    EAttribute eAttribute = eAttributes.get(random.nextInt(eAttributes.size()));
		    if (eAttribute.isMany()) {
			EList<Object> values = (EList<Object>) eObject.eGet(eAttribute);
			if (values.size() > 0) {
			    values.remove(random.nextInt(values.size()));
			    found = true;
			}
		    }
		}

	    }
	    // 1 for References
	    else {
		int index = random.nextInt(eObjectList.size());
		EObject eObject = eObjectList.get(index);
		if (eObject != null) {
		    String id = leftCbp.getURIFragment(eObject);
		    if (id != null && !(id.startsWith("O") || id.startsWith("L") || id.startsWith("R"))) {
			eObjectList.remove(eObject);
			continue;
		    }
		}
		if (eObject.eResource() == null) {
		    eObjectList.remove(eObject);
		    continue;
		}

		if (!eObject.eContents().isEmpty()) {
		    continue;
		}

		if (((EReference) eObject.eContainingFeature()).isContainment()) {
		    removeObjectFromEObjectList(eObject, eObjectList);
		    EcoreUtil.remove(eObject);
		    eObjectList.remove(eObject);
		    found = true;
		} else {
		    EcoreUtil.remove(eObject);
		    if (eObject.eContainer() == null) {
			eObjectList.remove(eObject);
		    }
		    found = true;
		}
	    }
	}
    }

    private void removeObjectFromEObjectList(EObject eObject, List<EObject> eObjectList) {
	for (EReference eReference : eObject.eClass().getEAllContainments()) {
	    if (eReference.isChangeable()) {
		if (eReference.isMany()) {
		    EList<EObject> values = (EList<EObject>) eObject.eGet(eReference);
		    for (EObject value : values) {
			removeObjectFromEObjectList(value, eObjectList);
			eObjectList.remove(value);
		    }
		} else {
		    EObject value = (EObject) eObject.eGet(eReference);
		    if (value != null) {
			removeObjectFromEObjectList(value, eObjectList);
			eObjectList.remove(value);
		    }
		}
	    }
	}

    }

    private EPackage getEPackageFromFiles(File leftFile, File rightFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	BufferedReader reader = new BufferedReader(new FileReader(leftFile));
	String line = null;
	String eventString = null;
	// try to read ePackage from left file
	while ((line = reader.readLine()) != null) {
	    if (line.contains("epackage=")) {
		eventString = line;
		reader.close();
		break;
	    }
	}

	// if line is still null then try from right file
	if (eventString == null) {
	    reader = new BufferedReader(new FileReader(rightFile));
	    while ((line = reader.readLine()) != null) {
		if (line.contains("epackage=")) {
		    eventString = line;
		    reader.close();
		    break;
		}
	    }
	}
	String nsUri = null;
	XMLEventReader xmlReader = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(eventString.getBytes()));
	while (xmlReader.hasNext()) {
	    XMLEvent xmlEvent = xmlReader.nextEvent();
	    if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
		StartElement e = xmlEvent.asStartElement();
		String name = e.getName().getLocalPart();
		if (name.equals("register") || name.equals("create")) {
		    nsUri = e.getAttributeByName(new QName("epackage")).getValue();
		    xmlReader.close();
		    break;
		}
	    }
	}

	EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsUri);
	return ePackage;
    }

    private XMIResource saveXmiWithID(CBPXMLResourceImpl cbp, File xmiFile) throws IOException {
	XMIResource xmi = (XMIResource) (new XMIResourceFactoryImpl()).createResource(URI.createFileURI(xmiFile.getAbsolutePath()));
	xmi.getContents().addAll(EcoreUtil.copyAll(cbp.getContents()));
	TreeIterator<EObject> cbpIterator = cbp.getAllContents();
	TreeIterator<EObject> xmiIterator = xmi.getAllContents();

	while (cbpIterator.hasNext() && xmiIterator.hasNext()) {
	    EObject cbpEObject = cbpIterator.next();
	    String id = cbp.getURIFragment(cbpEObject);
	    EObject xmiEObject = xmiIterator.next();
	    xmi.setID(xmiEObject, id);
	}
	xmi.save(options);
	// xmi.unload();
	return xmi;
    }

    /**
     * @param seeds
     * @param probability
     */
    private void setProbability(List<ChangeType> seeds, int probability, ChangeType type) {
	for (int i = 0; i < probability; i++) {
	    seeds.add(type);
	}
    }

    /**
     * @param right
     * @param left
     * @throws IOException
     * @throws FileNotFoundException
     */
    private ModifiedEMFCompare doEMFComparison(Resource left, Resource right) throws FileNotFoundException, IOException {
	IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.WHEN_AVAILABLE);
	IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
	IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
	matchEngineFactory.setRanking(100);
	IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
	matchEngineRegistry.add(matchEngineFactory);

	IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
	BasicPostProcessorDescriptorImpl post = new BasicPostProcessorDescriptorImpl(new UMLPostProcessor(), Pattern.compile("http://www.eclipse.org/uml2/5.0.0/UML"), null);
	postProcessorRegistry.put(UMLPostProcessor.class.getName(), post);

	ModifiedBuilder builder = ModifiedEMFCompare.modifiedBuilder();
	builder.setPostProcessorRegistry(postProcessorRegistry);
	builder.setMatchEngineFactoryRegistry(matchEngineRegistry);
	ModifiedEMFCompare comparator = (ModifiedEMFCompare) builder.build();

	// System.out.println("Compare " + cbp.getURI().lastSegment() + " and "
	// + xmi.getURI().lastSegment());
	IComparisonScope2 scope = new DefaultComparisonScope(left, right, null);
	System.out.println("Start comparison ...");
	Comparison comparison = comparator.compare(scope);
	EList<Diff> diffs = comparison.getDifferences();

	System.out.println("Matching Time = " + comparator.getMatchTime() / 1000000.0 + " ms");
	System.out.println("Diffing Time = " + comparator.getDiffTime() / 1000000.0 + " ms");
	System.out.println("Comparison Time = " + comparator.getComparisonTime() / 1000000.0 + " ms");
	System.out.println("State-based Diffs Size = " + diffs.size());

	return comparator;
    }

    private List<String> exportEMFCompareDiffs(File outputFile, Resource left, Resource right, EList<Diff> diffs) throws FileNotFoundException, IOException {
	Set<String> set = new HashSet<>();
	for (Diff diff : diffs) {
	    String feature = null;
	    String id = null;
	    String value = null;

	    if (diff.getMatch().getLeft() != null) {
		id = left.getURIFragment(diff.getMatch().getLeft());
	    } else {
		id = right.getURIFragment(diff.getMatch().getRight());
	    }

	    if (diff instanceof AttributeChange) {
		feature = ((AttributeChange) diff).getAttribute().getName();
		value = String.valueOf(((AttributeChange) diff).getValue());
	    } else if (diff instanceof ReferenceChange) {
		feature = ((ReferenceChange) diff).getReference().getName();
		EObject eObject = ((ReferenceChange) diff).getValue();
		value = left.getURIFragment(eObject);
		if (value == null || "/-1".equals(value)) {
		    value = right.getURIFragment(eObject);
		}
	    } else if (diff instanceof MultiplicityElementChange) {
		// MultiplicityElementChange change =
		// (MultiplicityElementChange) diff;
		// if (change.getEReference() != null) {
		// EObject eObject = change.getDiscriminant();
		// value = left.getURIFragment(eObject);
		// if (value == null || "/-1".equals(value)) {
		// value = right.getURIFragment(eObject);
		// }
		// feature = change.getEReference().getName();
		// } else {
		// continue;
		// }
		continue;
	    } else if (diff instanceof ResourceAttachmentChange) {
		feature = "resource";
		value = new String(id);
		id = new String(feature);

	    } else if (diff instanceof AssociationChange) {
		// AssociationChange change = (AssociationChange) diff;
		// if (change.getEReference() != null) {
		// EObject eObject = change.getDiscriminant();
		// value = left.getURIFragment(eObject);
		// if (value == null || "/-1".equals(value)) {
		// value = right.getURIFragment(eObject);
		// }
		// feature = change.getEReference().getName();
		// } else {
		// continue;
		// }
		continue;

	    } else if (diff instanceof DirectedRelationshipChange) {

		// DirectedRelationshipChange change =
		// (DirectedRelationshipChange) diff;
		// if (change.getEReference() != null) {
		// EObject eObject = change.getDiscriminant();
		// value = left.getURIFragment(eObject);
		// if (value == null || "/-1".equals(value)) {
		// value = right.getURIFragment(eObject);
		// }
		// feature = change.getEReference().getName();
		// } else {
		// continue;
		// }
		continue;
	    } else {
		System.out.println("UNHANDLED DIFF: " + diff.getClass().getName());
	    }

	    String x = id + "." + feature + "." + value + "." + diff.getKind();
	    set.add(x.trim());
	}
	// System.out.println("Before Merge Diffs: " + diffs.size());

	List<String> list = new ArrayList<>(set);
	Collections.sort(list);

	// System.out.println("\nEXPORT FOR COMPARISON WITH CBP:");
	FileOutputStream output = new FileOutputStream(outputFile);
	for (String item : list) {
	    output.write(item.getBytes());
	    output.write(System.lineSeparator().getBytes());
	}
	System.out.println("State-based Diffs Size: " + list.size());
	return list;
    }

    @Test
    public void saveCBPasXMI() throws IOException {

	String leftCbpPath = "D:\\TEMP\\FASE\\Debug\\right.cbpxml";
	String leftXmiPath = leftCbpPath.replace(".cbpxml", ".xmi");

	File leftXmiFile = new File(leftXmiPath);
	File leftCbpFile = new File(leftCbpPath);

	CBPXMLResourceFactory cbpFactory = new CBPXMLResourceFactory();

	CBPXMLResourceImpl leftCbp = (CBPXMLResourceImpl) cbpFactory.createResource(URI.createFileURI(leftCbpFile.getAbsolutePath()));

	System.out.println("Loading " + leftCbpFile.getName() + "...");
	leftCbp.load(null);

	System.out.println("Saving changes to Xmis ...");
	XMIResource leftXmi = saveXmiWithID(leftCbp, leftXmiFile);

    }

    @Test
    public void testEMFComparison() throws FileNotFoundException, IOException {
	String leftXmiPath = "D:\\TEMP\\FASE\\Debug\\left.xmi";
	String rightXmiPath = "D:\\TEMP\\FASE\\Debug\\right.xmi";

	File outputFile = new File("D:\\TEMP\\FASE\\Debug\\right.txt");

	XMIResource leftXmi = (XMIResource) ((new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiPath)));
	XMIResource rightXmi = (XMIResource) ((new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightXmiPath)));

	System.out.println("Start loading models ...");
	long start = System.nanoTime();
	leftXmi.load(options);
	rightXmi.load(options);
	long end = System.nanoTime();
	System.out.println("Loading time = " + ((end - start) / 1000000000.0));
	System.out.println("Left element count = " + leftXmi.getEObjectToIDMap().size());
	System.out.println("Right element count = " + rightXmi.getEObjectToIDMap().size());

	System.out.println("Perform comparison ...");
	EList<Diff> diffs = this.doEMFComparison(leftXmi, rightXmi).getDiffs();
	System.out.println("Exporting identified diffs ...");
	exportEMFCompareDiffs(outputFile, leftXmi, rightXmi, diffs);

	System.out.println("Finished!");
    }
}
