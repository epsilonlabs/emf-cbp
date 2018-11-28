package org.eclipse.epsilon.cbp.merging;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.ValueExp;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.eclipse.epsilon.cbp.comparison.CBPDiffComparator;
import org.eclipse.epsilon.cbp.comparison.CBPMatchFeature;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject;

public class CBPMerging {

    File leftXmiFile = null;
    File rightXmiFile = null;
    File targetXmiFile = null;

    Resource leftResource = null;
    Resource rightResource = null;
    Resource targetResource = null;

    Map<String, EObject> deletedObjects = new HashMap<>();

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

	leftResource.load(options);
	rightResource.load(options);
	targetResource.load(options);

	this.mergeAllLeftToRight(targetResource, leftResource, diffs);

	targetResource.save(options);

    }

    public void mergeAllLeftToRight(Resource targetResource, Resource leftResource, List<CBPDiff> diffs) throws Exception {

	// for (CBPDiff diff : diffs) {
	// String targetId = diff.getObject().getId();
	// CBPMatchObject targetMatchOject = diff.getObject();
	// String featureName = diff.getFeature().getName();
	// CBPMatchObject valueMatchObject = null;
	// Object value = null;
	// if (diff.getValue() instanceof CBPMatchObject) {
	// value = ((CBPMatchObject) diff.getValue()).getId();
	// valueMatchObject = (CBPMatchObject) diff.getValue();
	// } else {
	// value = diff.getValue();
	// }
	//
	// int index = diff.getPosition();
	// CBPDifferenceKind kind = diff.getKind();
	//
	// System.out.println(targetId + "." + featureName + "." + value + "." +
	// index + "." + kind);
	// }

	resolveDiff(targetResource, leftResource, diffs);
    }

    public void resolveDiff(Resource targetResource, Resource leftResource, List<CBPDiff> diffs) throws Exception {

	Collections.sort(diffs, new CBPDiffComparator());

	for (CBPDiff diff : diffs) {

	    if (diff.isResolved()) {
		continue;
	    }

	    String targetId = diff.getObject().getId();
	    CBPMatchObject targetMatchObject = diff.getObject();
	    String featureName = diff.getFeature().getName();
	    CBPMatchFeature matchFeature = diff.getFeature();
	    CBPMatchObject valueMatchObject = null;
	    Object value = null;
	    if (diff.getValue() instanceof CBPMatchObject) {
		value = ((CBPMatchObject) diff.getValue()).getId();
		valueMatchObject = (CBPMatchObject) diff.getValue();
	    } else {
		value = diff.getValue();
		// value = new
		// String(diff.getValue().toString().getBytes(Charset.forName("ISO-8859-1")),
		// Charset.forName("ISO-8859-1"));
		// System.out.println(value);
	    }

	    int index = diff.getPosition();
	    CBPDifferenceKind kind = diff.getKind();

	    if (value.equals("L-470")) {
		System.out.println();
	    }

	    // OBJECT AT RESOURCE
	    if (targetId.equals("resource") && featureName.equals("resource")) {

		EObject valueObject = null;

		System.out.println(targetId + "." + featureName + "." + value + "." + index + "." + kind);

		if (kind == CBPDifferenceKind.ADD) {
		    valueObject = EcoreUtil.copy(leftResource.getEObject(value.toString()));
		    clearContainedObjects(valueObject);
		    targetResource.getContents().add(index, valueObject);
		    if (targetResource instanceof XMIResource) {
			((XMIResource) targetResource).setID(valueObject, value.toString());
		    }
		    diff.setResolved(true);
		} else if (kind == CBPDifferenceKind.CHANGE) {
		} else if (kind == CBPDifferenceKind.DELETE) {
		    valueObject = targetResource.getEObject(value.toString());
		    if (valueObject == null) {
			valueObject = deletedObjects.get(value.toString());
		    }
		    recordDeletedObjects(valueObject);
		    targetResource.getContents().remove(valueObject);
		    // EcoreUtil.delete(valueObject, true);
		    diff.setResolved(true);
		} else if (kind == CBPDifferenceKind.MOVE) {
		}
	    } else

	    // OBJECT VALUE
	    if (diff.getValue() instanceof CBPMatchObject) {
		EObject valueObject = null;

		// ADD
		if (kind == CBPDifferenceKind.ADD) {

		    EObject targetObject = targetResource.getEObject(targetId);
		    if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
			resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs());
			targetObject = targetResource.getEObject(targetId);
		    }

		    if (targetObject == null) {
			targetObject = deletedObjects.get(targetId);
		    }

		    EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

		    System.out.println(targetId + "." + featureName + "." + value + "." + index + "." + kind);

		    if (value != null) {
			valueObject = targetResource.getEObject(value.toString());
			if (valueObject == null) {
			    valueObject = EcoreUtil.copy(leftResource.getEObject(value.toString()));
			    clearContainedObjects(valueObject);
			}
		    }

		    if (feature.isMany()) {
			EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);

			list.add(index, valueObject);

			if (targetResource instanceof XMIResource) {
			    ((XMIResource) targetResource).setID(valueObject, value.toString());
			}

		    } else {
			Object oldValue = targetObject.eGet(feature);
			String oldId = null;
			if (oldValue != null) {
			    oldId = targetResource.getURIFragment((EObject) oldValue);
			}
			if (oldValue != null && ((EReference) feature).isContainment()) {
			    deletedObjects.put(oldId, (EObject) oldValue);
			}

			targetObject.eSet(feature, valueObject);

			if (targetResource instanceof XMIResource) {
			    ((XMIResource) targetResource).setID(valueObject, value.toString());
			}
		    }
		    diff.setResolved(true);

		    // EObject x = targetResource.getEObject("L-246");
		    // if (x != null) {
		    // EReference ref = (EReference)
		    // x.eClass().getEStructuralFeature("memberEnd");
		    // EList<EObject> list = (EList<EObject>) x.eGet(ref);
		    // System.out.println("L-246 memberEnd size = " +
		    // list.size());
		    // }

		}

		// FOR CHANGE, DELETE, MOVE
		else {

		    if (value != null) {
			valueObject = targetResource.getEObject(value.toString());
			if (valueObject == null) {
			    valueObject = deletedObjects.get(value.toString());
			}
		    }

		    if (kind == CBPDifferenceKind.CHANGE) {

			System.out.println(targetId + "." + featureName + "." + value + "." + index + "." + kind);

			EObject targetObject = targetResource.getEObject(targetId);
			if (targetObject == null) {
			    targetObject = deletedObjects.get(targetId);
			}
			if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
			    resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs());
			    targetObject = targetResource.getEObject(targetId);
			    if (targetObject == null) {
				targetObject = deletedObjects.get(targetId);
			    }
			}
			EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

			targetObject.eSet(feature, valueObject);
			diff.setResolved(true);

		    } else if (kind == CBPDifferenceKind.DELETE) {

			System.out.println(targetId + "." + featureName + "." + value + "." + index + "." + kind);

			if (diff.getFeature().isContainment()) {
			    for (CBPMatchFeature featureMatchObject : valueMatchObject.getFeatures().values()) {
				if (featureMatchObject.isContainment()) {
				    for (Object subValue : featureMatchObject.getRightValues().values()) {
					if (subValue != null) {
					    resolveDiff(targetResource, leftResource, ((CBPMatchObject) subValue).getDiffs());

					}
				    }
				}
			    }
			    resolveDiff(targetResource, leftResource, valueMatchObject.getDiffs());
			}

			EObject targetObject = targetResource.getEObject(targetId);
			if (targetObject == null) {
			    targetObject = deletedObjects.get(targetId);
			}

			recordDeletedObjects(valueObject);

			EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

			if (feature.isMany()) {
			    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
			    list.remove(valueObject);

			    // if(((EReference) feature).getEOpposite() != null)
			    // {
			    // EReference opposite = ((EReference)
			    // feature).getEOpposite();
			    // if (opposite.isMany()) {
			    // ((EList<EObject>)
			    // valueObject.eGet(opposite)).remove(targetObject);
			    // }else {
			    // valueObject.eUnset(opposite);
			    // }
			    // }

			} else {
			    targetObject.eUnset(feature);
			}

			// EObject removedElement = list.remove(index);
			// if (!removedElement.equals(valueObject)) {
			// throw new Exception("Removed element and value object
			// are not equal!");
			// }
			diff.setResolved(true);
		    } else if (kind == CBPDifferenceKind.MOVE) {

			System.out.println(targetId + "." + featureName + "." + value + "." + index + "." + kind);

			EObject targetObject = targetResource.getEObject(targetId);
			if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
			    resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs());
			    targetObject = targetResource.getEObject(targetId);
			    // if (targetObject == null) {
			    // targetObject = deletedObjects.get(targetId);
			    // }
			}

			// if (valueObject == null &&
			// valueMatchObject.getLeftContainer() != null) {
			// valueObject = deletedObjects.get(value.toString());
			// }

			EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

			if (feature.isMany()) {
			    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);

			    // System.out.println("index = " + index + ", size =
			    // " + list.size());

			    // for (EObject e : list) {
			    // System.out.println(targetResource.getURIFragment(e));
			    // }
			    //
			    // if (value.equals("O-43745")) {
			    // System.out.println();
			    // }

			    if (targetObject.equals(valueObject.eContainer()) && feature.getName().equals(valueObject.eContainmentFeature().getName())) {
				if (list.size() > 0) {
				    if (diff.getFeature().getRightValues().get(index) != null) {

					// EObject currentObject =
					// list.get(index);
					// String currentObjectId =
					// targetResource.getURIFragment(currentObject);

					CBPMatchObject rightValue = (CBPMatchObject) diff.getFeature().getRightValues().get(index);
					EObject currentObject = targetResource.getEObject(rightValue.getId());
					if (rightValue.getLeftPosition() >= index) {
					    list.move(list.size() - 1, currentObject);
					}
					list.move(index, valueObject);
				    } else {
					list.move(index, valueObject);
				    }
				}
			    } else {
				list.add(index, valueObject);
				if (targetResource instanceof XMIResource) {
				    ((XMIResource) targetResource).setID(valueObject, value.toString());
				}
				deletedObjects.remove(value);
			    }
			} else {

			    Object oldValue = targetObject.eGet(feature);
			    String oldId = null;
			    if (oldValue != null) {
				oldId = targetResource.getURIFragment((EObject) oldValue);
			    }
			    if (oldValue != null && ((EReference) feature).isContainment()) {
				deletedObjects.put(oldId, (EObject) oldValue);
			    }

			    targetObject.eSet(feature, valueObject);
			    if (targetResource instanceof XMIResource) {
				((XMIResource) targetResource).setID(valueObject, value.toString());
			    }
			    deletedObjects.remove(value);
			}
			diff.setResolved(true);
		    }

		}
	    }
	    // NON OBJECT VALUE
	    else {

		System.out.println(targetId + "." + featureName + "." + value + "." + index + "." + kind);

		EObject targetObject = targetResource.getEObject(targetId);
		EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

		if (kind == CBPDifferenceKind.ADD) {
		    EList<Object> list = (EList<Object>) targetObject.eGet(feature);
		    list.add(index, value);
		    diff.setResolved(true);
		} else if (kind == CBPDifferenceKind.CHANGE) {
		    targetObject.eSet(feature, value);
		    diff.setResolved(true);
		} else if (kind == CBPDifferenceKind.DELETE) {
		    EList<Object> list = (EList<Object>) targetObject.eGet(feature);
		    Object removedElement = list.remove(index);
		    if (!removedElement.equals(value)) {
			throw new Exception("Removed element and value object are not equal!");
		    }
		    diff.setResolved(true);
		} else if (kind == CBPDifferenceKind.MOVE) {
		    EList<Object> list = (EList<Object>) targetObject.eGet(feature);
		    list.move(index, value);
		    diff.setResolved(true);
		}
	    }
	}

    }

    private void copyIDs(EObject oldValueObject, EObject valueObject) {
	// TODO Auto-generated method stub

    }

    private void clearContainedObjects(EObject eObject) {
	EClass eClass = eObject.eClass();
	for (EReference eReference : eClass.getEAllReferences()) {
	    if (eReference.isContainment()) {
		if (eReference.isMany()) {
		    List<EObject> list = (List<EObject>) eObject.eGet(eReference);
		    for (int i = list.size() - 1; i >= 0; i--) {
			EObject eValue = list.get(i);
			// clearContainedObjects(eValue);
			EcoreUtil.delete(eValue, true);

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

    private void recordDeletedObjects(EObject eObject) {
	EClass eClass = eObject.eClass();
	for (EReference eReference : eClass.getEAllReferences()) {
	    if (eReference.isContainment()) {
		if (eReference.isMany()) {
		    List<EObject> list = (List<EObject>) eObject.eGet(eReference);
		    for (EObject eValue : list) {
			String id = eValue.eResource().getURIFragment(eValue);
			deletedObjects.put(id, eValue);
			recordDeletedObjects(eValue);
		    }
		} else {
		    EObject eValue = (EObject) eObject.eGet(eReference);
		    if (eValue != null) {
			String id = eValue.eResource().getURIFragment(eValue);
			deletedObjects.put(id, eValue);
			recordDeletedObjects(eValue);
		    }
		}

	    }
	}
	if (eObject != null && eObject.eResource() != null) {
	    String id = eObject.eResource().getURIFragment(eObject);
	    deletedObjects.put(id, eObject);
	}
    }
}
