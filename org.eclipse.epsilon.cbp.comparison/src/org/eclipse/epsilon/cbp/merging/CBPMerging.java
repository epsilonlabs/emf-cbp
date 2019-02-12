package org.eclipse.epsilon.cbp.merging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.RemovedElementsCache;
import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.junit.Test;
import org.eclipse.epsilon.cbp.comparison.CBPDiffComparator;
import org.eclipse.epsilon.cbp.comparison.CBPMatchFeature;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEObject;
import org.eclipse.epsilon.cbp.comparison.event.CBPRemoveFromEReferenceEvent;
import org.eclipse.uml2.uml.VisibilityKind;
import org.hamcrest.core.IsInstanceOf;

public class CBPMerging {

    File leftXmiFile = null;
    File rightXmiFile = null;
    File targetXmiFile = null;

    Resource leftResource = null;
    Resource rightResource = null;
    Resource targetResource = null;

    Map<EObject, String> eObjectToIdMap = new HashMap<>();
    Map<String, EObject> idToEObjectMap = new HashMap<>();
    Set<String> deletedObjects = new HashSet<>();

    public void mergeAllLeftToRight(File targetXmiFile, File leftXmiFile, File rightXmiFile, List<CBPDiff> diffs) throws Exception {

	this.leftXmiFile = leftXmiFile;
	this.rightXmiFile = rightXmiFile;
	this.targetXmiFile = targetXmiFile;
	if (targetXmiFile.exists())
	    targetXmiFile.delete();

	FileUtils.copyFile(rightXmiFile, targetXmiFile);

	Map<Object, Object> options = new HashMap<>();
	options.put(XMIResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
	options.put(XMIResource.OPTION_PROCESS_DANGLING_HREF, XMIResource.OPTION_PROCESS_DANGLING_HREF_RECORD);

	leftResource = ((new XMIResourceFactoryImpl()).createResource(URI.createFileURI(leftXmiFile.getAbsolutePath())));
	rightResource = ((new XMIResourceFactoryImpl()).createResource(URI.createFileURI(rightXmiFile.getAbsolutePath())));
	targetResource = ((new XMIResourceFactoryImpl()).createResource(URI.createFileURI(targetXmiFile.getAbsolutePath())));

	CBPDependencyDeterminator dependencyDeterminator = new CBPDependencyDeterminator();
	dependencyDeterminator.determineDependencies(diffs);

	leftResource.load(options);
	// rightResource.load(options);
	targetResource.load(options);

	this.mergeAllLeftToRight(targetResource, leftResource, diffs);

	targetResource.save(options);

    }

    public void mergeAllLeftToRight(Resource targetResource, Resource leftResource, List<CBPDiff> diffs) throws Exception {

	Collections.sort(diffs, new CBPDiffComparator());

	List<CBPDiff> addAndMoveDiffs = new ArrayList<>();
	List<CBPDiff> addDiffs = new ArrayList<>();
	List<CBPDiff> moveDiffs = new ArrayList<>();
	List<CBPDiff> deleteDiffs = new ArrayList<>();
	List<CBPDiff> changeDiffs = new ArrayList<>();

	TreeIterator<EObject> iterator = targetResource.getAllContents();
	while (iterator.hasNext()) {
	    EObject eObject = iterator.next();
	    String id = targetResource.getURIFragment(eObject);
	    eObjectToIdMap.put(eObject, id);
	    idToEObjectMap.put(id, eObject);
	}

	for (CBPDiff diff : diffs) {
	    if (diff.getKind() == CBPDifferenceKind.ADD || diff.getKind() == CBPDifferenceKind.MOVE) {
		addAndMoveDiffs.add(diff);
	    }

	    if (diff.getKind() == CBPDifferenceKind.ADD) {
		addDiffs.add(diff);
	    } else if (diff.getKind() == CBPDifferenceKind.MOVE) {
		moveDiffs.add(diff);
	    } else if (diff.getKind() == CBPDifferenceKind.DELETE) {
		deleteDiffs.add(diff);
	    } else if (diff.getKind() == CBPDifferenceKind.CHANGE) {
		changeDiffs.add(diff);
	    }
	}

	handleDeleteDiffs(targetResource, deleteDiffs);
	handleAddToRoot(targetResource, leftResource, addDiffs);
	handleMoveDiffsToRoot(targetResource, moveDiffs);
	handleAddAndMoveDiffsToTheirPositions(targetResource, addAndMoveDiffs);
	handleChangeDiffs(targetResource, changeDiffs);

	// remove remaining undeleted objects in the root of resource
	for (String id : deletedObjects) {
	    EObject eObject = idToEObjectMap.get(id);
	    EcoreUtil.remove(eObject);
	}

	// System.out.println();
	// for (EObject eObject : leftResource.getContents()) {
	// String id = leftResource.getURIFragment(eObject);
	// System.out.println(id);
	// }
	// System.out.println();
	// for (EObject eObject : targetResource.getContents()) {
	// String id = eObjectToIdMap.get(eObject);
	// System.out.println(id);
	// }
    }

    @SuppressWarnings("unchecked")
    private void handleAddToRoot(Resource targetResource, Resource leftResource, List<CBPDiff> addDiffs) {
	for (CBPDiff diff : addDiffs) {
	    // System.out.println(diff);
	    CBPMatchObject target = diff.getObject();
	    CBPMatchFeature feature = diff.getFeature();

	    if (diff.getKind() == CBPDifferenceKind.ADD) {
		if (target.getId().equals("resource") && feature.getName().equals("resource")) {
		    CBPMatchObject value = (CBPMatchObject) diff.getValue();
		    EObject eLeftValue = leftResource.getEObject(value.toString());
		    EObject eValue = EcoreUtil.copy(eLeftValue);
		    clearContainedObjects(eValue);
		    targetResource.getContents().add(eValue);
		    ((XMIResource) targetResource).setID(eValue, value.getId());
		    eObjectToIdMap.put(eValue, value.getId());
		    idToEObjectMap.put(value.getId(), eValue);
		} else {
		    if (feature.isContainment()) {
			CBPMatchObject value = (CBPMatchObject) diff.getValue();
			EObject eLeftValue = leftResource.getEObject(value.toString());
			EObject eValue = EcoreUtil.copy(eLeftValue);
			clearContainedObjects(eValue);
			targetResource.getContents().add(eValue);
			((XMIResource) targetResource).setID(eValue, value.getId());
			eObjectToIdMap.put(eValue, value.getId());
			idToEObjectMap.put(value.getId(), eValue);
		    }
		}
	    }
	}

    }

    @SuppressWarnings("unchecked")
    private void handleMoveDiffsToRoot(Resource targetResource, List<CBPDiff> moveDiffs) {
	for (CBPDiff diff : moveDiffs) {
	    // System.out.println(diff);
	    CBPMatchObject target = diff.getObject();
	    CBPMatchFeature feature = diff.getFeature();
	    if (target.getId().equals("L-4")) {
		System.console();
	    }

	    if (target.getId().equals("resource") && feature.getName().equals("resource")) {
		CBPMatchObject value = (CBPMatchObject) diff.getValue();
		EObject eValue = targetResource.getEObject(value.getId());
		EcoreUtil.remove(eValue);
		targetResource.getContents().add(eValue);
		((XMIResource) targetResource).setID(eValue, value.getId());
	    } else {
		CBPMatchObject value = (CBPMatchObject) diff.getValue();
		if (value.getId().equals("R-0")) {
		    System.console();
		}
		EObject eValue = idToEObjectMap.get(value.getId());
		// EObject eValue = targetResource.getEObject(value.getId());
		EcoreUtil.remove(eValue);
		targetResource.getContents().add(eValue);
		((XMIResource) targetResource).setID(eValue, value.getId());
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private void handleAddAndMoveDiffsToTheirPositions(Resource targetResource, List<CBPDiff> addAndMoveDiffs) throws IOException {
	for (CBPDiff diff : addAndMoveDiffs) {
	    System.out.println(diff);
	    CBPMatchObject target = diff.getObject();
	    CBPMatchFeature feature = diff.getFeature();

	    if (diff.getKind() == CBPDifferenceKind.MOVE) {
		if (target.getId().equals("resource") && feature.getName().equals("resource")) {
		    CBPMatchObject value = (CBPMatchObject) diff.getValue();
		    int mergePos = diff.getPosition();
		    EObject eValue = targetResource.getEObject(value.getId());
		    targetResource.getContents().move(mergePos, eValue);
		    ((XMIResource) targetResource).setID(eValue, value.getId());
		} else {
		    EObject eTarget = targetResource.getEObject(target.getId());
		    EStructuralFeature eFeature = eTarget.eClass().getEStructuralFeature(feature.getName());
		    if (eFeature instanceof EReference) {
			EReference eReference = (EReference) eFeature;
			CBPMatchObject value = (CBPMatchObject) diff.getValue();
			if (value.getId().equals("O-43679")) {
			    System.console();
			}
			int mergePos = diff.getPosition();
			EObject eValue = targetResource.getEObject(value.getId());
			if (eReference.isContainment()) {
			    if (eReference.isMany()) {
				EList<EObject> eValues = (EList<EObject>) eTarget.eGet(eReference);
				EcoreUtil.remove(eValue);
				eValues.add(mergePos, eValue);
				((XMIResource) targetResource).setID(eValue, value.getId());
				reassignIdsToSubEObjects(eValue);
			    } else {
				EcoreUtil.remove(eValue);
				eTarget.eSet(eReference, eValue);
				((XMIResource) targetResource).setID(eValue, value.getId());
				reassignIdsToSubEObjects(eValue);
			    }
			} else {
			    EList<EObject> eValues = (EList<EObject>) eTarget.eGet(eReference);
			    eValues.move(mergePos, eValue);
			}
		    } else if (eFeature instanceof EAttribute) {
			Object value = (Object) diff.getValue();
			int position = diff.getPosition();
			EAttribute eAttribute = (EAttribute) eFeature;
			EList<Object> eValues = (EList<Object>) eTarget.eGet(eAttribute);
			eValues.move(position, value);
		    }
		}
	    } else if (diff.getKind() == CBPDifferenceKind.ADD) {
		if (target.getId().equals("resource") && feature.getName().equals("resource")) {
		    CBPMatchObject value = (CBPMatchObject) diff.getValue();
		    int mergePos = diff.getPosition();
		    EObject eValue = targetResource.getEObject(value.getId());
		    targetResource.getContents().move(mergePos, eValue);
		    ((XMIResource) targetResource).setID(eValue, value.getId());
		} else {
		    EObject eTarget = idToEObjectMap.get(target.getId());
		    // EObject eTarget =
		    // targetResource.getEObject(target.getId());
		    if (target.getId().equals("O-41331")) {
			printContentsOfFeatures();
			System.console();
		    }
		    EStructuralFeature eFeature = eTarget.eClass().getEStructuralFeature(feature.getName());
		    if (eFeature instanceof EReference) {
			EReference eReference = (EReference) eFeature;
			CBPMatchObject value = (CBPMatchObject) diff.getValue();
			int mergePos = diff.getPosition();
			if (eReference.isContainment()) {
			    EObject eValue = targetResource.getEObject(value.getId());
			    if (eReference.isMany()) {
				EList<EObject> eValues = (EList<EObject>) eTarget.eGet(eReference);
				EcoreUtil.remove(eValue);
				eValues.add(mergePos, eValue);
				((XMIResource) targetResource).setID(eValue, value.getId());
				reassignIdsToSubEObjects(eValue);
				if (value.getId().equals("L-0")) {
				    Object x = targetResource.getEObject("L-1");
				    System.console();
				}
				System.console();
			    } else {
				EcoreUtil.remove(eValue);
				eTarget.eSet(eReference, eValue);
				((XMIResource) targetResource).setID(eValue, value.getId());
				reassignIdsToSubEObjects(eValue);
			    }
			} else {
			    EObject eValue = idToEObjectMap.get(value.getId());
			    // EObject eValue =
			    // targetResource.getEObject(value.getId());
			    EList<EObject> eValues = (EList<EObject>) eTarget.eGet(eReference);
			    if (eValues.contains(eValues)) {
				eValues.move(mergePos, eValue);
			    } else {
				if (eReference.isUnique() && eValues.contains(eValue)) {
				} else {
				    eValues.add(mergePos, eValue);
				}
			    }
			}
		    } else if (eFeature instanceof EAttribute) {
			Object value = (Object) diff.getValue();
			int position = diff.getPosition();
			EAttribute eAttribute = (EAttribute) eFeature;
			EList<Object> eValues = (EList<Object>) eTarget.eGet(eAttribute);
			eValues.add(position, value);
		    }
		}
	    }
	}
    }

    private void reassignIdsToSubEObjects(EObject eValue) {
	TreeIterator<EObject> iterator = eValue.eAllContents();
	while (iterator.hasNext()) {
	    EObject eObject = iterator.next();
	    String id = eObjectToIdMap.get(eObject);
	    ((XMIResource) targetResource).setID(eObject, id);
	}

    }

    @SuppressWarnings("unchecked")
    private void handleDeleteDiffs(Resource targetResource, List<CBPDiff> deleteDiffs) {
	for (CBPDiff diff : deleteDiffs) {
	    System.out.println(diff);
	    CBPMatchObject target = diff.getObject();
	    CBPMatchFeature feature = diff.getFeature();

	    if (target.getId().equals("resource") && feature.getName().equals("resource")) {
		CBPMatchObject value = (CBPMatchObject) diff.getValue();
		EObject eValue = idToEObjectMap.get(value.getId());
		EcoreUtil.remove(eValue);

	    } else if (feature.isContainment()) {
		CBPMatchObject value = (CBPMatchObject) diff.getValue();
		deletedObjects.add(value.getId());
		if (value.getId().equals("R-18")) {
		    System.console();
		}
		EObject eValue = idToEObjectMap.get(value.getId());
		EcoreUtil.remove(eValue);
	    } else {
		if (diff.getValue() instanceof CBPMatchObject) {
		    EObject eTarget = idToEObjectMap.get(target.getId());
		    // EObject eTarget =
		    // targetResource.getEObject(target.getId());
		    EStructuralFeature eFeature = eTarget.eClass().getEStructuralFeature(feature.getName());
		    CBPMatchObject value = (CBPMatchObject) diff.getValue();
		    if (value.getId().equals("O-31083")) {
			System.console();
		    }
		    EObject eValue = targetResource.getEObject(value.getId());
		    if (eFeature.isMany()) {
			EList<EObject> eValues = (EList<EObject>) eTarget.eGet(eFeature);
			boolean result = eValues.remove(eValue);
			System.console();
		    } else {
			eTarget.eUnset(eFeature);
		    }
		} else {
		    EObject eTarget = targetResource.getEObject(target.getId());
		    EStructuralFeature eFeature = eTarget.eClass().getEStructuralFeature(feature.getName());
		    Object value = diff.getValue();
		    if (eFeature.isMany()) {
			EList<Object> eValues = (EList<Object>) eTarget.eGet(eFeature);
			eValues.remove(value);
		    } else {
			eTarget.eUnset(eFeature);
		    }
		}
	    }

	}
    }

    @SuppressWarnings("unchecked")
    private void handleChangeDiffs(Resource targetResource, List<CBPDiff> changeDiffs) {
	for (CBPDiff diff : changeDiffs) {
	    System.out.println(diff);
	    CBPMatchObject target = diff.getObject();
	    CBPMatchFeature feature = diff.getFeature();
	    EObject eTarget = idToEObjectMap.get(target.getId());
	    if (target.getId().equals("L-1")) {
		System.console();
	    }
	    EStructuralFeature eFeature = eTarget.eClass().getEStructuralFeature(feature.getName());
	    if (eFeature instanceof EReference) {
		CBPMatchObject value = (CBPMatchObject) diff.getValue();
		if (value.getId().equals("O-37167")) {
		    System.console();
		}
		EObject eValue = targetResource.getEObject(value.getId());
		EObject eLeftTarget = leftResource.getEObject(target.getId());
		if (eLeftTarget != null) {
		    EObject eLeftValue = (EObject) eLeftTarget.eGet(eFeature);
		    if (eLeftValue == null) {
			eTarget.eUnset(eFeature);
		    } else {
			eTarget.eSet(eFeature, eValue);
		    }
		} else {
		    eTarget.eSet(eFeature, eValue);
		}
	    } else if (eFeature instanceof EAttribute) {
		Object value = null;
		if (eTarget.eGet(eFeature) instanceof VisibilityKind) {
		    value = VisibilityKind.get((String) diff.getValue());
		} else {
		    value = diff.getValue();
		}
		eTarget.eSet(eFeature, value);
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private void clearContainedObjects(EObject eObject) {
	EClass eClass = eObject.eClass();
	for (EReference eReference : eClass.getEAllReferences()) {
	    if (eReference.isContainment()) {
		if (eReference.isMany()) {
		    List<EObject> list = (List<EObject>) eObject.eGet(eReference);
		    for (int i = list.size() - 1; i >= 0; i--) {
			EObject eValue = list.get(i);
			// clearContainedObjects(eValue);
			EcoreUtil.remove(eValue);

		    }
		} else {
		    EObject eValue = (EObject) eObject.eGet(eReference);
		    if (eValue != null) {
			// clearContainedObjects(eValue);
			eObject.eUnset(eReference);
			EcoreUtil.delete(eValue, true);
		    }
		}

	    } else if (eReference.isChangeable()) {
		if (eReference.isMany()) {
		    List<EObject> list = (List<EObject>) eObject.eGet(eReference);
		    list.clear();
		} else {
		    eObject.eUnset(eReference);
		}
	    }
	}
    }

    public void printContentsOfFeatures() throws IOException {
	boolean x = true;
	if (x == true) {
	    String objectId = "O-41331";
	    String featureName = "ownedParameter";

	    ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	    XMIResource leftResource = (XMIResource) this.leftResource;
	    XMIResource targetResource = (XMIResource) this.targetResource;

	    EObject leftObject = leftResource.getEObject(objectId);
	    EObject targetObject = targetResource.getEObject(objectId);

	    if (leftObject != null && targetObject != null) {
		EReference leftReference = (EReference) leftObject.eClass().getEStructuralFeature(featureName);
		EReference targetReference = (EReference) targetObject.eClass().getEStructuralFeature(featureName);

		if (leftReference.isMany()) {
		    EList<EObject> leftValues = (EList<EObject>) leftObject.eGet(leftReference);
		    EList<EObject> targetValues = (EList<EObject>) targetObject.eGet(targetReference);
		    int size = (leftValues.size() > targetValues.size()) ? leftValues.size() : targetValues.size();

		    System.out.println("\nPAIR LEFT-TARGET:");
		    for (int i = 0; i < size; i++) {
			String leftId = "";
			String rightId = "";

			if (i < leftValues.size()) {
			    EObject leftValue = leftValues.get(i);
			    leftId = leftResource.getURIFragment(leftValue);
			}

			if (i < targetValues.size()) {
			    EObject rightValue = targetValues.get(i);
			    rightId = targetResource.getURIFragment(rightValue);
			}

			if (i >= 0) {
			    String separator = (leftId.equals(rightId)) ? " = " : " x ";
			    System.out.println(i + ": " + leftId + separator + rightId);
			}

		    }
		} else {
		    EObject leftValue = (EObject) leftObject.eGet(leftReference);
		    EObject targetValue = (EObject) targetObject.eGet(targetReference);

		    System.out.println("\nPAIR LEFT-TARGET:");
		    String leftId = "";
		    String rightId = "";
		    if (leftValue != null) {
			leftId = leftResource.getURIFragment(leftValue);
		    }
		    if (targetValue != null) {
			rightId = targetResource.getURIFragment(targetValue);
		    }

		    String separator = (leftId.equals(rightId)) ? " = " : " x ";
		    System.out.println("0: " + leftId + separator + rightId);

		}
		System.out.println();

	    } else if (targetObject != null) {
		EReference targetReference = (EReference) targetObject.eClass().getEStructuralFeature(featureName);

		if (targetReference.isMany()) {
		    EList<EObject> targetValues = (EList<EObject>) targetObject.eGet(targetReference);
		    int size = targetValues.size();

		    System.out.println("\nPAIR LEFT-TARGET:");
		    for (int i = 0; i < size; i++) {
			String leftId = "";
			String rightId = "";

			if (i < targetValues.size()) {
			    EObject rightValue = targetValues.get(i);
			    rightId = targetResource.getURIFragment(rightValue);
			}

			String separator = (leftId.equals(rightId)) ? " = " : " x ";
			System.out.println(i + ": " + leftId + separator + rightId);

		    }
		} else {
		    EObject targetValue = (EObject) targetObject.eGet(targetReference);

		    System.out.println("\nPAIR LEFT-TARGET:");
		    String leftId = "";
		    String rightId = "";
		    if (targetValue != null) {
			rightId = targetResource.getURIFragment(targetValue);
		    }

		    String separator = (leftId.equals(rightId)) ? " = " : " x ";
		    System.out.println("0: " + leftId + separator + rightId);

		}
		System.out.println();
	    }
	}
    }

}
