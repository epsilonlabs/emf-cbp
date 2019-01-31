package org.eclipse.epsilon.cbp.merging;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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

public class CBPMergingBackup2 {

    File leftXmiFile = null;
    File rightXmiFile = null;
    File targetXmiFile = null;

    Resource leftResource = null;
    Resource rightResource = null;
    Resource targetResource = null;

    Map<String, EObject> tempDeletedObjects = new HashMap<>();
    Set<CBPMatchFeature> recursiveStack = new HashSet<>();

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

	resolveDiff(targetResource, leftResource, diffs, null, null);
    }

    public void resolveDiff(Resource targetResource, Resource leftResource, List<CBPDiff> diffs, CBPMatchObject valueCaller, CBPMatchObject targetCaller) throws Exception {

	for (int i = 0; i < diffs.size(); i++) {

	    // System.out.println(targetResource.getEObject("O-52497"));
	    CBPDiff diff = diffs.get(i);

	    // printContentsOfFeatures();

	    System.out.println(diff.toString());
	    if (diff.isResolved()) {
		continue;
	    }

	    // OBJECT AT RESOURCE
	    if (diff.getObject().getId().equals("resource") && diff.getFeature().getName().equals("resource")) {
		this.handleResourceDiff(diff, targetResource, leftResource);
	    }
	    // OTHERS
	    else {
		if (diff.getKind() == CBPDifferenceKind.ADD) {
		    this.handleAddDiff(diff, targetResource, leftResource);
		} else if (diff.getKind() == CBPDifferenceKind.DELETE) {
		    this.handleDeleteDiff(diff, targetResource, leftResource);
		} else if (diff.getKind() == CBPDifferenceKind.CHANGE) {
		    this.handleChangeDiff(diff, targetResource, leftResource);
		} else if (diff.getKind() == CBPDifferenceKind.MOVE) {
		    this.handleMoveDiff(diff, targetResource, leftResource);
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private void handleMoveDiff(CBPDiff diff, Resource targetResource, Resource leftResource) {
	CBPMatchObject valueObject = null;
	Object value = null;
	if (diff.getValue() instanceof CBPMatchObject) {
	    value = ((CBPMatchObject) diff.getValue()).getId();
	    valueObject = (CBPMatchObject) diff.getValue();
	} else {
	    value = diff.getValue();
	}
	CBPMatchObject target = diff.getObject();
	CBPMatchFeature feature = diff.getFeature();
	int position = valueObject.getLeftMergePosition(feature);
	CBPMatchObject fromTarget = diff.getOriginObject();
	CBPMatchFeature fromFeature = diff.getOriginFeature();

	if (value.toString().equals("O-41884")) {
	    System.console();
	}

	EObject eTarget = targetResource.getEObject(target.toString());
	if (eTarget == null) {
	    eTarget = tempDeletedObjects.get(target.toString());
	}
	EReference eReference = (EReference) eTarget.eClass().getEStructuralFeature(feature.getName());
	EObject eFromTarget = targetResource.getEObject(fromTarget.toString());
	if (eFromTarget == null) {
	    eFromTarget = tempDeletedObjects.get(fromTarget.toString());
	}
	EReference eFromReference = (EReference) eTarget.eClass().getEStructuralFeature(fromFeature.getName());
	EObject eValue = targetResource.getEObject(value.toString());
	if (eValue == null) {
	    eValue = tempDeletedObjects.get(value.toString());
	}

	// within
	if (eTarget.equals(eFromTarget) && eReference.equals(eFromReference)) {
	    EList<EObject> eList = (EList<EObject>) eTarget.eGet(eReference);
	    eList.move(position, eValue);
	}
	// between
	else {
	    if (eReference.isMany()) {
		EList<EObject> eList = (EList<EObject>) eTarget.eGet(eReference);
		eList.add(position, eValue);
		setID(eValue, value.toString());
	    } else {
		eTarget.eSet(eReference, eValue);
		setID(eValue, value.toString());
	    }
	}
	diff.setResolved(true);
    }

    private void handleChangeDiff(CBPDiff diff, Resource targetResource, Resource leftResource) {
	CBPMatchObject target = diff.getObject();
	CBPMatchFeature feature = diff.getFeature();
	Object value = null;
	if (diff.getValue() instanceof CBPMatchObject) {
	    value = ((CBPMatchObject) diff.getValue()).getId();
	} else {
	    value = diff.getValue();
	}

	if (value.toString().equals("O-55448")) {
	    System.console();
	}

	EObject eTarget = targetResource.getEObject(target.toString());
	if (eTarget == null) {
	    eTarget = tempDeletedObjects.get(target.toString());
	}
	if (eTarget == null) {
	    EObject eLeftTarget = leftResource.getEObject(target.toString());
	    eTarget = EcoreUtil.copy(eLeftTarget);
	    clearContainedObjects(eTarget);
	    targetResource.getContents().add(eTarget);
	    setID(eTarget, target.toString());
	}
	EStructuralFeature eFeature = eTarget.eClass().getEStructuralFeature(feature.getName());

	// FEATURES
	if (eFeature instanceof EReference) {
	    EObject eValue = targetResource.getEObject(value.toString());
	    EReference eReference = (EReference) eFeature;
	    eTarget.eSet(eReference, eValue);
	}
	// ATTRIBUTES
	else {
	    EAttribute eAttribute = (EAttribute) eFeature;
	    eTarget.eSet(eAttribute, value);
	}
	diff.setResolved(true);
    }

    @SuppressWarnings("unchecked")
    private void handleDeleteDiff(CBPDiff diff, Resource targetResource, Resource leftResource) {
	CBPMatchObject target = diff.getObject();
	CBPMatchFeature feature = diff.getFeature();
	CBPMatchObject valueObject = null;
	Object value = null;
	if (diff.getValue() instanceof CBPMatchObject) {
	    value = ((CBPMatchObject) diff.getValue()).getId();
	    valueObject = (CBPMatchObject) diff.getValue();
	} else {
	    value = diff.getValue();
	}
	// int position = valueObject.getRightMergePosition();

	if (value.toString().equals("R-217")) {
	    System.console();
	}

	EObject eTarget = targetResource.getEObject(target.toString());
	if (eTarget == null) {
	    eTarget = tempDeletedObjects.get(target.toString());
	}
	EStructuralFeature eFeature = eTarget.eClass().getEStructuralFeature(feature.getName());
	EObject eValue = targetResource.getEObject(value.toString());

	// FEATURES
	if (eFeature instanceof EReference) {
	    EReference eReference = (EReference) eFeature;
	    if (eValue == null) {
		diff.setResolved(true);
		return;
	    }
	    if (eReference.isContainment()) {
		// EcoreUtil.delete(eValue);
		TreeIterator<EObject> iterator = eValue.eAllContents();
		while (iterator.hasNext()) {
		    EObject eSubValue = iterator.next();
		    String eSubValueId = targetResource.getURIFragment(eSubValue);
		    tempDeletedObjects.put(eSubValueId, eSubValue);
		}
		EcoreUtil.remove(eValue);
	    } else {
		if (eReference.isMany()) {
		    EList<Object> eList = (EList<Object>) eTarget.eGet(eReference);
		    eList.remove(value);
		} else {
		    eTarget.eUnset(eReference);
		}
	    }
	}
	// ATTRIBUTES
	else {
	    EAttribute eAttribute = (EAttribute) eFeature;
	    if (eAttribute.isMany()) {
		EList<Object> eList = (EList<Object>) eTarget.eGet(eAttribute);
		eList.remove(value);
	    } else {
		eTarget.eUnset(eAttribute);
	    }
	}
	diff.setResolved(true);
    }

    @SuppressWarnings("unchecked")
    private void handleAddDiff(CBPDiff diff, Resource targetResource, Resource leftResource) {
	CBPMatchObject target = diff.getObject();
	CBPMatchFeature feature = diff.getFeature();
	CBPMatchObject valueObject = null;
	Object value = null;
	if (diff.getValue() instanceof CBPMatchObject) {
	    value = ((CBPMatchObject) diff.getValue()).getId();
	    valueObject = (CBPMatchObject) diff.getValue();
	} else {
	    value = diff.getValue();
	}
	int position = valueObject.getLeftMergePosition(feature);

	if (value.toString().equals("L-24")) {
	    System.console();
	}

	EObject eTarget = targetResource.getEObject(target.toString());
	if (eTarget == null) {
	    eTarget = tempDeletedObjects.get(target.toString());
	}
	if (eTarget == null) {
	    EObject eLeftTarget = leftResource.getEObject(target.getId().toString());
	    eTarget = EcoreUtil.copy(eLeftTarget);
	    clearContainedObjects(eTarget);
	    targetResource.getContents().add(eTarget);
	    setID(eTarget, target.getId());
	}
	EStructuralFeature eFeature = eTarget.eClass().getEStructuralFeature(feature.getName());
	EObject eValue = targetResource.getEObject(value.toString());
	if (eValue == null) {
	    EObject eLeftValue = leftResource.getEObject(value.toString());
	    eValue = EcoreUtil.copy(eLeftValue);
	    clearContainedObjects(eValue);
	}

	// FEATURES
	if (eFeature instanceof EReference) {
	    EReference eReference = (EReference) eFeature;
	    if (eReference.isContainment()) {
		if (eReference.isMany()) {
		    EList<EObject> eList = (EList<EObject>) eTarget.eGet(eReference);
		    if (position <= eList.size()) {
			eList.add(position, eValue);
			targetResource.getContents().remove(eValue);
		    } else {
			eList.add(valueObject.getLeftPosition(), eValue);
		    }
		} else {
		    eTarget.eSet(eReference, eValue);
		}
		setID(eValue, value.toString());
	    } else {
		if (eValue.eContainer() == null) {
		    targetResource.getContents().add(eValue);
		    setID(eValue, value.toString());
		}
		if (eReference.isMany()) {
		    EList<EObject> eList = (EList<EObject>) eTarget.eGet(eReference);
		    if (!eList.contains(eValue)) {
			eList.add(position, eValue);
		    }
		} else {
		    eTarget.eSet(eReference, eValue);
		}
	    }
	}
	// ATTRIBUTES
	else {
	    EAttribute eAttribute = (EAttribute) eFeature;
	    if (eAttribute.isMany()) {
		EList<Object> eList = (EList<Object>) eTarget.eGet(eAttribute);
		eList.add(position, value);
	    } else {
		eTarget.eSet(eAttribute, value);
	    }
	}
	diff.setResolved(true);
    }

    private void handleResourceDiff(CBPDiff diff, Resource targetResource, Resource leftResource) {
	CBPDifferenceKind kind = diff.getKind();
	CBPMatchObject valueObject = null;
	Object value = null;
	if (diff.getValue() instanceof CBPMatchObject) {
	    value = ((CBPMatchObject) diff.getValue()).getId();
	    valueObject = (CBPMatchObject) diff.getValue();
	} else {
	    value = diff.getValue();
	}

	if (value.toString().equals("R-0")) {
	    System.console();
	}

	if (kind == CBPDifferenceKind.ADD) {
	    int position = valueObject.getLeftMergePosition(diff.getFeature());
	    EObject eLeftValue = leftResource.getEObject(value.toString());
	    EObject eValue = EcoreUtil.copy(eLeftValue);
	    clearContainedObjects(eValue);
	    targetResource.getContents().add(position, eValue);
	    setID(eValue, value.toString());
	    diff.setResolved(true);
	} else if (kind == CBPDifferenceKind.DELETE) {
	    EObject eValue = targetResource.getEObject(value.toString());
	    EcoreUtil.remove(eValue);
	    diff.setResolved(true);
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

    private void setID(EObject valueElement, String id) {
	if (targetResource instanceof XMIResource) {
	    ((XMIResource) targetResource).setID(valueElement, id);
	}
    }

    @Test
    public void printContentsOfFeatures() throws IOException {
	boolean x = true;
	if (x == true) {
	    String objectId = "O-43943";
	    String featureName = "packagedElement";

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
