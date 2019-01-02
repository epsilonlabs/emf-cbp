package org.eclipse.epsilon.cbp.bigmodel.test;

import org.junit.Test;

import com.google.common.io.Files;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
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
import org.eclipse.epsilon.cbp.comparison.CBPComparisonImpl;
import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.ICBPComparison;
import org.eclipse.epsilon.cbp.comparison.UMLObjectTreePostProcessor;
import org.eclipse.epsilon.cbp.comparison.test.ModifiedEMFCompare;
import org.eclipse.epsilon.cbp.comparison.test.ModifiedEMFCompare.ModifiedBuilder;
import org.eclipse.epsilon.cbp.comparison.test.Result;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource.IdType;
import org.eclipse.gmt.modisco.java.emf.JavaPackage;
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
	Logger.getRootLogger().setLevel(Level.OFF);
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
	options.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
    }

    @Test
    public void generateBigModel() throws DiscoveryException, CoreException, IOException {

	String sourcePath = "D:\\TEMP\\org.eclipse.uml2";
	// String sourcePath = "D:\\TEMP\\org.eclipse.epsilon";
	String targetPath = "D:\\TEMP\\FASE\\bigmodel";
	BigModelGenerator generator = new BigModelGenerator(sourcePath, targetPath);
	generator.generate();
	assertEquals(true, true);
    }

    @Test
    public void randomModification() throws IOException, FactoryConfigurationError, XMLStreamException {
	String originXmiPath = "D:\\TEMP\\FASE\\performance\\origin.xmi";
	String leftXmiPath = "D:\\TEMP\\FASE\\performance\\left.xmi";
	String rightXmiPath = "D:\\TEMP\\FASE\\performance\\right.xmi";
	String originCbpPath = "D:\\TEMP\\FASE\\performance\\origin.cbpxml";
	String leftCbpPath = "D:\\TEMP\\FASE\\performance\\left.cbpxml";
	String rightCbpPath = "D:\\TEMP\\FASE\\performance\\right.cbpxml";

	File originXmiFile = new File(originXmiPath);
	File leftXmiFile = new File(leftXmiPath);
	File rightXmiFile = new File(rightXmiPath);
	File originCbpFile = new File(originCbpPath);
	File leftCbpFile = new File(leftCbpPath);
	File rightCbpFile = new File(rightCbpPath);

	// System.out.println("Copying origin xmi to left and right xmis");
	// Files.copy(originXmiFile, leftXmiFile);
	// Files.copy(originXmiFile, rightXmiFile);

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
	setProbability(seeds, 6, ChangeType.CHANGE);
	setProbability(seeds, 2, ChangeType.ADD);
	setProbability(seeds, 1, ChangeType.DELETE);
	setProbability(seeds, 3, ChangeType.MOVE);

	System.out.println("Loading " + leftCbpFile.getName() + "...");
	leftCbp.load(null);
	leftCbp.startNewSession("LEFT");
	System.out.println("Loading " + rightCbpFile.getName() + "...");
	rightCbp.load(null);
	rightCbp.startNewSession("RIGHT");

	List<EObject> leftEObjectList = identifyAllEObjects(leftCbp);
	List<EObject> rightEObjectList = identifyAllEObjects(rightCbp);

	int modificationCount = 1000000;
	for (int i = 1; i <= modificationCount; i++) {
	    System.out.print("Change " + i + ":");
	    startModification(leftCbp, leftEObjectList, seeds);
	    leftCbp.save(null);
	    startModification(rightCbp, rightEObjectList, seeds);
	    rightCbp.save(null);
	    System.out.println();

	    // do comparison
	    if (i % 1000000 == 0) {
		ICBPComparison comparison = new CBPComparisonImpl();
		comparison.setDiffEMFCompareFile(new File(originCbpFile.getAbsolutePath().replaceAll("origin.cbpxml", "left.txt")));
		comparison.setObjectTreeFile(new File(originCbpFile.getAbsolutePath().replaceAll("origin.cbpxml", "tree.txt")));
		comparison.addObjectTreePostProcessor(new UMLObjectTreePostProcessor());
		comparison.compare(leftCbpFile, rightCbpFile, originCbpFile);

		System.out.println("Saving changes to Xmis ...");
		XMIResource leftXmi = saveXmiWithID(leftCbp, leftXmiFile);
		XMIResource rightXmi = saveXmiWithID(rightCbp, rightXmiFile);

		System.out.println("Perform EMF Compare comparison as a bechmark ...");
		doEMFComparison(leftXmi, rightXmi);

		System.out.println();
	    }
	}

	System.out.println("FINISHED!");
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

    }

    private void addModification(List<EObject> eObjectList, CBPXMLResourceImpl leftCbp) {
	// 0 for Attributes
	boolean found = false;
	while (!found) {
	    if (random.nextInt(2) == 0) {
		int index1 = random.nextInt(eObjectList.size());
		EObject eObject1 = eObjectList.get(index1);
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
			value = true;
		    } else if (eAttribute.getEAttributeType().getName().equals("EString")) {
			value = "Z" + EcoreUtil.generateUUID();
		    } else if (eAttribute.getEAttributeType().getName().equals("EInt")) {
			value = 0;
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
		    int index2 = random.nextInt(eObjectList.size());
		    EObject eObject2 = eObjectList.get(index2);
		    EList<EReference> references1 = eObject1.eClass().getEAllContainments();
		    List<EReference> filteredEReferences = new ArrayList<>();
		    for (EReference eReference : references1) {
			if (eReference.isChangeable()) {
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
			    }
			} else {
			    try {
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
			eObject1.eSet(eAttribute, true);
			found = true;
		    } else if (eAttribute.getEAttributeType().getName().equals("EString")) {
			eObject1.eSet(eAttribute, "Z" + EcoreUtil.generateUUID());
			found = true;
		    } else if (eAttribute.getEAttributeType().getName().equals("EInt")) {
			eObject1.eSet(eAttribute, 0);
			found = true;
		    } else {
			// System.out.println(eAttribute.getEAttributeType().getName());
		    }
		}
	    }
	    // 1 for References
	    else {
		int index1 = random.nextInt(eObjectList.size());
		EObject eObject1 = eObjectList.get(index1);
		int index2 = random.nextInt(eObjectList.size());
		EObject eObject2 = eObjectList.get(index2);
		if (eObject2.eContainmentFeature() == null) {
		    continue;
		}
		EList<EReference> references1 = eObject1.eClass().getEAllReferences();
		List<EReference> filteredEReferences = new ArrayList<>();
		for (EReference eReference : references1) {
		    if (!eReference.isMany() && !eReference.isContainment() && eReference.isChangeable()) {
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
			eObject1.eSet(eReference, eObject2);
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
		if (eObject.eContainingFeature() == null || eObject.eContainer() == null) {
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
    private void doEMFComparison(Resource left, Resource right) throws FileNotFoundException, IOException {
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
	Comparison comparison = comparator.compare(scope);
	EList<Diff> diffs = comparison.getDifferences();

	System.out.println("Matching Time = " + comparator.getMatchTime() / 1000000.0 + " ms");
	System.out.println("Diffing Time = " + comparator.getDiffTime() / 1000000.0 + " ms");
	System.out.println("Comparison Time = " + comparator.getComparisonTime() / 1000000.0 + " ms");
	System.out.println("State-based Diffs Size = " + diffs.size());
    }
}
