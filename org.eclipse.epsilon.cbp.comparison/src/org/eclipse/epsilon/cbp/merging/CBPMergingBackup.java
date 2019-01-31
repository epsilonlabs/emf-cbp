package org.eclipse.epsilon.cbp.merging;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
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
import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.eclipse.epsilon.cbp.comparison.CBPDiffComparator;
import org.eclipse.epsilon.cbp.comparison.CBPMatchFeature;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject;
import org.junit.Test;

public class CBPMergingBackup {

    File leftXmiFile = null;
    File rightXmiFile = null;
    File targetXmiFile = null;

    Resource leftResource = null;
    Resource rightResource = null;
    Resource targetResource = null;

    Map<String, EObject> deletedObjects1 = new HashMap<>();
    Map<EObject, String> deletedObjects2 = new HashMap<>();
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

	leftResource.load(options);
	rightResource.load(options);
	targetResource.load(options);

	this.mergeAllLeftToRight(targetResource, leftResource, diffs);

	targetResource.save(options);

    }

    public void mergeAllLeftToRight(Resource targetResource, Resource leftResource, List<CBPDiff> diffs) throws Exception {

	Collections.sort(diffs, new CBPDiffComparator());

	resolveDiff(targetResource, leftResource, diffs, null, null);
    }

    public void resolveDiff(Resource targetResource, Resource leftResource, List<CBPDiff> diffs, CBPMatchObject valueCaller, CBPMatchObject targetCaller) throws Exception {

	Map<Integer, EObject> moveToEObjects = new TreeMap<>();
	for (int i = 0; i < diffs.size(); i++) {

	    CBPDiff diff = diffs.get(i);
	    if (diff.isResolved()) {
		continue;
	    } else if (recursiveStack.contains(diff.getFeature())) {
		continue;
	    }

	    // System.out.println(targetResource.getEObject("R-410"));

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
	    }

	    int position = diff.getPosition();
	    CBPDifferenceKind kind = diff.getKind();

	    if (targetResource.getEObject("O-11063") == null) {
		System.out.println();
	    }
	    if (deletedObjects1.get("O-11063") != null) {
		System.out.println();
	    }

	    if (targetId.equals("O-37506")) {
		System.out.println();
	    }

	    // OBJECT AT RESOURCE
	    if (targetId.equals("resource") && featureName.equals("resource")) {

		EObject valueObject = null;

		printEvent(targetId, featureName, value, position, kind);

		if (kind == CBPDifferenceKind.ADD) {
		    valueObject = EcoreUtil.copy(leftResource.getEObject(value.toString()));
		    clearContainedObjects(valueObject);
		    targetResource.getContents().add(position, valueObject);
		    if (targetResource instanceof XMIResource) {
			((XMIResource) targetResource).setID(valueObject, value.toString());
		    }
		    diff.setResolved(true);
		} else if (kind == CBPDifferenceKind.CHANGE) {
		} else if (kind == CBPDifferenceKind.DELETE) {
		    valueObject = targetResource.getEObject(value.toString());
		    if (valueObject == null) {
			valueObject = deletedObjects1.get(value.toString());
		    }

		    if (diff.getFeature().isContainment()) {
			for (CBPMatchFeature featureMatchObject : valueMatchObject.getFeatures().values()) {
			    if (featureMatchObject.isContainment()) {
				for (Object subValue : featureMatchObject.getRightValues().values()) {
				    if (subValue != null) {
					if (!subValue.equals(targetCaller)) {
					    resolveDiff(targetResource, leftResource, ((CBPMatchObject) subValue).getDiffs(), ((CBPMatchObject) subValue), valueMatchObject);
					}

				    }
				}
			    }
			}
			resolveDiff(targetResource, leftResource, valueMatchObject.getDiffs(), valueMatchObject, targetMatchObject);
		    }

		    if (diff.isResolved()) {
			continue;
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

		    printEvent(targetId, featureName, value, position, kind);
		    if (value.equals("L-5308")) {
			System.out.println();
		    }

		    EObject targetObject = ((XMIResource) targetResource).getEObject(targetId);
		    if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
			resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs(), targetMatchObject, null);
			targetObject = targetResource.getEObject(targetId);
		    }
		    if (targetObject == null) {
			    targetObject = deletedObjects1.get(targetId);
			}
		    if (diff.isResolved()) {
			continue;
		    }

//		    if (targetObject == null) {
//			diff.setResolved(true);
//			continue;
//		    }
		    
		    EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

		    if (value != null) {
			valueObject = targetResource.getEObject(value.toString());
			if (valueObject == null) {
			    valueObject = deletedObjects1.get(value.toString());
			}

			// and the process the left object
			if (valueObject == null) {
			    CBPMatchObject otherSideValue = (CBPMatchObject) diff.getOtherSideValue();

			    if (otherSideValue != null && valueMatchObject.getClassName().equals(otherSideValue.getClassName())) {
				EObject rightObject = targetResource.getEObject(otherSideValue.getId());
				EObject copiedRightObject = EcoreUtil.copy(rightObject);
				valueObject = rightObject;

				// process the right side first
				recursiveStack.add(matchFeature);
				resolveDiff(targetResource, leftResource, otherSideValue.getDiffs(), otherSideValue, targetMatchObject);
				recursiveStack.remove(matchFeature);

				if (otherSideValue.getLeftContainer() != null && !targetMatchObject.equals(otherSideValue.getLeftContainer())) {
				    recursiveStack.add(matchFeature);
				    resolveDiff(targetResource, leftResource, otherSideValue.getLeftContainer().getDiffs(), otherSideValue, targetMatchObject);
				    recursiveStack.remove(matchFeature);
				} else {
				    if (feature.isMany()) {
					if (otherSideValue.getLeftPosition() > position + 1) {
					    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
					    EObject currentObject = targetResource.getEObject(otherSideValue.getId());
					    list.move(list.size() - 1, currentObject);
					    // printContentsOfFeatures();
					}
				    }
				}

				// just do rename of id
				if (feature.isMany()) {
				    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
				    if ((!targetMatchObject.equals(otherSideValue.getLeftContainer()) || !matchFeature.equals(otherSideValue.getRightContainingFeature())
					    || (otherSideValue.getLeftPosition() > -1)) && otherSideValue.getLeftContainer() != null) {
					valueObject = EcoreUtil.copy(leftResource.getEObject(value.toString()));
					clearContainedObjects(valueObject);
					addObjectToItsContainer(targetResource, value, position, valueObject, targetObject, feature);
				    } else if (list.contains(rightObject)) {
					if (copiedRightObject != null && ((EReference) feature).isContainment()) {
					    deletedObjects1.put(otherSideValue.getId(), copiedRightObject);
					}

					// copy attributes values to the right
					// object
					EObject leftObject = leftResource.getEObject(value.toString());
					for (EAttribute eAttribute : leftObject.eClass().getEAllAttributes()) {
					    if (eAttribute.isChangeable()) {
						if (valueObject.eClass().getEAllAttributes().contains(eAttribute)) {
						    Object a = valueObject.eGet(eAttribute);
						    Object b = leftObject.eGet(eAttribute);
						    if ((a != null && !a.equals(b)) || (b != null && !b.equals(a))) {
							valueObject.eSet(eAttribute, leftObject.eGet(eAttribute));
						    }
						}
					    }
					}
					// -----
					list.move(position, valueObject);
					if (targetResource instanceof XMIResource) {
					    ((XMIResource) targetResource).setID(valueObject, value.toString());
					}

					// printContentsOfFeatures();

				    } else {
					System.out.println();
				    }

				} else {

				    if ((!targetMatchObject.equals(otherSideValue.getLeftContainer()) || !matchFeature.equals(otherSideValue.getRightContainingFeature()))
					    && otherSideValue.getLeftContainer() != null) {
					valueObject = EcoreUtil.copy(leftResource.getEObject(value.toString()));
					clearContainedObjects(valueObject);
					addObjectToItsContainer(targetResource, value, position, valueObject, targetObject, feature);
				    } else if (rightObject != null) {
					EObject leftObject = leftResource.getEObject(value.toString());
					if (copiedRightObject != null && ((EReference) feature).isContainment()) {
					    deletedObjects1.put(otherSideValue.getId(), copiedRightObject);
					}

					// copy attributes values to the right
					// object
					for (EAttribute eAttribute : leftObject.eClass().getEAllAttributes()) {
					    if (eAttribute.isChangeable()) {
						Object a = valueObject.eGet(eAttribute);
						Object b = leftObject.eGet(eAttribute);
						if ((a != null && !a.equals(b)) || (b != null && !b.equals(a))) {
						    valueObject.eSet(eAttribute, leftObject.eGet(eAttribute));
						}
					    }
					}
					// -----
					if (targetResource instanceof XMIResource) {
					    ((XMIResource) targetResource).setID(valueObject, value.toString());
					}
				    } else {
					System.out.println();
				    }
				}

			    } else {
				valueObject = EcoreUtil.copy(leftResource.getEObject(value.toString()));
				clearContainedObjects(valueObject);
				if (otherSideValue != null && otherSideValue.getLeftContainer() == null) {
				    EObject otherSideObject = targetResource.getEObject(otherSideValue.getId());
				    if (feature.isMany()) {
					EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
					if (list.contains(otherSideObject)) {
					    list.remove(otherSideObject);
					    deletedObjects1.put(otherSideValue.getId(), otherSideObject);
					    deletedObjects2.put(otherSideObject, otherSideValue.getId());
					}

				    } else {
					if (otherSideObject != null && otherSideObject.equals(targetObject.eGet(feature))) {
					    targetObject.eUnset(feature);
					    deletedObjects1.put(otherSideValue.getId(), otherSideObject);
					    deletedObjects2.put(otherSideObject, otherSideValue.getId());
					}
				    }
				}
				addObjectToItsContainer(targetResource, value, position, valueObject, targetObject, feature);
			    }
			} else {
			    CBPMatchObject temp = (CBPMatchObject) diff.getOtherSideValue();
			    if (temp != null && temp.getLeftContainer() == null) {
				EObject otherSideValue = targetResource.getEObject(temp.getId());
				if (feature.isMany()) {
				    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
				    if (list.contains(otherSideValue)) {
					list.remove(otherSideValue);
					deletedObjects1.put(temp.getId(), otherSideValue);
					deletedObjects2.put(otherSideValue, temp.getId());
				    }

				} else {
				    if (otherSideValue != null && otherSideValue.equals(targetObject.eGet(feature))) {
					targetObject.eUnset(feature);
					deletedObjects1.put(temp.getId(), otherSideValue);
					deletedObjects2.put(otherSideValue, temp.getId());
				    }
				}
			    }
			    addObjectToItsContainer(targetResource, value, position, valueObject, targetObject, feature);
			}

		    }
		    printEvent(targetId, featureName, value, position, kind);
		    diff.setResolved(true);
		    printContentsOfFeatures();

		}

		// FOR CHANGE, DELETE, MOVE
		else {

		    if (value.equals("O-11063")) {
			System.out.println();
		    }

		    if (value != null) {
			valueObject = targetResource.getEObject(value.toString());
			if (valueObject == null) {
			    valueObject = deletedObjects1.get(value.toString());
			}
		    }

		    if (kind == CBPDifferenceKind.CHANGE) {

//			printEvent(targetId, featureName, value, position, kind);

			if (value.equals("L-1986")) {
			    System.out.println();
			}

			EObject targetObject = targetResource.getEObject(targetId);
			if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
			    resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs(), targetMatchObject, null);
			    targetObject = targetResource.getEObject(targetId);
			}
			if (targetObject == null) {
			    targetObject = deletedObjects1.get(targetId);
			}

			if (diff.isResolved()) {
			    continue;
			}

			if (valueObject == null) {
			    valueObject = targetResource.getEObject(value.toString());
			    if (valueObject == null && valueMatchObject.getLeftContainer() != null) {
				resolveDiff(targetResource, leftResource, valueMatchObject.getLeftContainer().getDiffs(), valueMatchObject, targetMatchObject);
				valueObject = targetResource.getEObject(value.toString());
			    }
			    if (valueObject == null) {
				valueObject = deletedObjects1.get(value.toString());
			    }
			}

			if (targetObject == null || valueObject == null) {
			    diff.setResolved(true);
			    continue;
			}

			if (diff.isResolved()) {
			    continue;
			}

			

			EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

			targetObject.eSet(feature, valueObject);
			diff.setResolved(true);
			printEvent(targetId, featureName, value, position, kind);
			printContentsOfFeatures();

		    } else if (kind == CBPDifferenceKind.DELETE) {

			if (value.equals("R-520") || value.equals("R-521") || value.equals("R-526")) {
			    System.out.println();
			}
//			printEvent(targetId, featureName, value, position, kind);

			EObject targetObject = targetResource.getEObject(targetId);
			if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
			    resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs(), targetMatchObject, null);
			    targetObject = targetResource.getEObject(targetId);
			}
			if (targetObject == null) {
			    targetObject = deletedObjects1.get(targetId);
			}

			if (diff.isResolved()) {
			    continue;
			}

			if (valueObject == null) {
			    valueObject = targetResource.getEObject(value.toString());
			    if (valueObject == null && valueMatchObject.getLeftContainer() != null) {
				resolveDiff(targetResource, leftResource, valueMatchObject.getLeftContainer().getDiffs(), valueMatchObject, targetMatchObject);
				valueObject = targetResource.getEObject(value.toString());

			    }
			    if (valueObject == null) {
				valueObject = deletedObjects1.get(value.toString());
			    }
			}

			if (diff.isResolved()) {
			    continue;
			}

			if (targetObject == null || valueObject == null) {
			    diff.setResolved(true);
			    continue;
			}

			EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

			if (diff.getOtherSideValue() != null && ((CBPMatchObject) diff.getOtherSideValue()).getRightContainer() == null) {
			    if (feature.isMany()) {

//				if (diff.getFeature().isContainment()) {
//				    for (CBPMatchFeature featureMatchObject : valueMatchObject.getFeatures().values()) {
//					if (featureMatchObject.isContainment()) {
//					    for (Object subValue : featureMatchObject.getRightValues().values()) {
//						if (subValue != null) {
//						    if (!subValue.equals(targetCaller)) {
//							recursiveStack.add(matchFeature);
//							resolveDiff(targetResource, leftResource, ((CBPMatchObject) subValue).getDiffs(), ((CBPMatchObject) subValue), valueMatchObject);
//							recursiveStack.remove(matchFeature);
//						    }
//
//						}
//					    }
//					}
//				    }
//				    recursiveStack.add(matchFeature);
//				    resolveDiff(targetResource, leftResource, valueMatchObject.getDiffs(), valueMatchObject, targetMatchObject);
//				    recursiveStack.remove(matchFeature);
//				}
//
//				if (diff.isResolved()) {
//				    continue;
//				}
//
//				recordDeletedObjects(valueObject);

				// just move object to the last position. It
				// will be replaced later in ADD or CHANGE
				// operations.
				EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
				if (list.size() > 0 && list.contains(valueObject)) {
				    list.move(list.size() - 1, valueObject);
				}
			    } else {
				// do nothing, it will be replaced later in ADD
				// or CHANGE operations
			    }

			} else {

			    if (diff.getFeature().isContainment()) {
				for (CBPMatchFeature featureMatchObject : valueMatchObject.getFeatures().values()) {
				    if (featureMatchObject.isContainment()) {
					for (Object subValue : featureMatchObject.getRightValues().values()) {
					    if (subValue != null) {
						if (!subValue.equals(targetCaller)) {
						    recursiveStack.add(matchFeature);
						    resolveDiff(targetResource, leftResource, ((CBPMatchObject) subValue).getDiffs(), ((CBPMatchObject) subValue), valueMatchObject);
						    recursiveStack.remove(matchFeature);
						}

					    }
					}
				    }
				}
				recursiveStack.add(matchFeature);
				resolveDiff(targetResource, leftResource, valueMatchObject.getDiffs(), valueMatchObject, targetMatchObject);
				recursiveStack.remove(matchFeature);
			    }

			    if (diff.isResolved()) {
				continue;
			    }

			    recordDeletedObjects(valueObject);

			    if (feature.isMany()) {
				EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
				// String x =
				// targetResource.getURIFragment(list.get(0));
				// String b =
				// targetResource.getURIFragment(valueObject);
				list.remove(valueObject);
				// String c =
				// targetResource.getURIFragment(valueObject);
				// String d =
				// targetResource.getURIFragment(valueObject);

			    } else {
				targetObject.eUnset(feature);
			    }
			}

			if (value.equals("O-40429")) {
			    System.out.println();
			}

			printEvent(targetId, featureName, value, position, kind);

			diff.setResolved(true);
			printContentsOfFeatures();

		    }

		    // MOVE
		    else if (kind == CBPDifferenceKind.MOVE) {

//			printEvent(targetId, featureName, value, position, kind);

			EObject targetObject = targetResource.getEObject(targetId);
			if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
			    recursiveStack.add(matchFeature);
			    resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs(), valueMatchObject, targetMatchObject);
			    recursiveStack.remove(matchFeature);
			    targetObject = targetResource.getEObject(targetId);
			}
			if (targetObject == null) {
			    targetObject = deletedObjects1.get(targetId);
			}

			if (diff.isResolved()) {
			    continue;
			}

			if (targetId.equals("O-13265")) {
			    System.out.print("");
			}

			printEvent(targetId, featureName, value, position, kind);

			EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

			if (feature.isMany()) {
			    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
			    // WITHIN CONTAINING FEATURE MOVE
			    if (targetObject.equals(valueObject.eContainer()) && feature.getName().equals(valueObject.eContainmentFeature().getName())) {
				if (list.size() > 0) {
				    if (diff.getFeature().getRightValues().get(position) != null) {
					// move right object to the last
					// it will be moved to it's right
					// position later
					// when the iteration reach it's
					// event
					// to move to it's correct position
					CBPMatchObject rightValue = (CBPMatchObject) diff.getFeature().getRightValues().get(position);
					EObject currentObject = targetResource.getEObject(rightValue.getId());
					if (rightValue.getLeftPosition() >= position + 2) {
					    list.move(list.size() - 1, currentObject);
					}
					list.move(position, valueObject);
				    } else {
					if (position >= list.size()) {
					    list.move(list.size() - 1, valueObject);
					} else {
					    list.move(position, valueObject);
					}
				    }
				}
			    }
			    // CROSS CONTAINING FEAUTURE MOVE
			    else {

				// remove the object first
				Map<EObject, String> map = new HashMap<>();
				for (EReference ref : valueObject.eClass().getEAllReferences()) {
				    if (ref.isContainment()) {
					if (ref.isMany()) {
					    EList<EObject> values = (EList<EObject>) valueObject.eGet(ref);
					    for (EObject val : values) {
						String id = targetResource.getURIFragment(val);
						if (id.equals("/-1")) {
						    map.put(val, deletedObjects2.get(val));
						    deletedObjects2.remove(val);
						}
					    }
					} else {
					    Object val = valueObject.eGet(ref);
					    if (val != null) {
						String id = targetResource.getURIFragment((EObject) val);
						if (id.equals("/-1")) {
						    map.put((EObject) val, deletedObjects2.get((EObject) val));
						    deletedObjects2.remove(val);
						}
					    }
					}
				    }
				}

				// and add it to its new position
				list.add(position, valueObject);

				for (Entry<EObject, String> entry : map.entrySet()) {
				    ((XMIResource) targetResource).setID(entry.getKey(), entry.getValue());
				}

				if (targetResource instanceof XMIResource) {
				    ((XMIResource) targetResource).setID(valueObject, value.toString());
				}
				deletedObjects1.remove(value);
			    }
			} else {

			    Object oldValue = targetObject.eGet(feature);
			    String oldId = null;
			    if (oldValue != null) {
				oldId = targetResource.getURIFragment((EObject) oldValue);
			    }
			    if (oldValue != null && ((EReference) feature).isContainment()) {
				deletedObjects1.put(oldId, (EObject) oldValue);
			    }

			    targetObject.eSet(feature, valueObject);
			    if (targetResource instanceof XMIResource) {
				((XMIResource) targetResource).setID(valueObject, value.toString());
			    }
			    deletedObjects1.remove(value);
			}
			diff.setResolved(true);
			printContentsOfFeatures();
		    }

		}
	    }
	    // NON OBJECT VALUE
	    else {

		printEvent(targetId, featureName, value, position, kind);

		EObject targetObject = targetResource.getEObject(targetId);
		EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

		if (kind == CBPDifferenceKind.ADD) {
		    EList<Object> list = (EList<Object>) targetObject.eGet(feature);
		    list.add(position, value);
		    diff.setResolved(true);
		} else if (kind == CBPDifferenceKind.CHANGE) {
		    targetObject.eSet(feature, value);
		    diff.setResolved(true);
		} else if (kind == CBPDifferenceKind.DELETE) {
		    EList<Object> list = (EList<Object>) targetObject.eGet(feature);
		    Object removedElement = list.remove(position);
		    if (!removedElement.equals(value)) {
			throw new Exception("Removed element and value object are not equal!");
		    }
		    diff.setResolved(true);
		} else if (kind == CBPDifferenceKind.MOVE) {
		    EList<Object> list = (EList<Object>) targetObject.eGet(feature);
		    list.move(position, value);
		    diff.setResolved(true);
		}
	    }
	}
    }

    /**
     * @param targetId
     * @param featureName
     * @param value
     * @param position
     * @param kind
     */
    private void printEvent(String targetId, String featureName, Object value, int position, CBPDifferenceKind kind) {
	System.out.println(targetId + "." + featureName + "." + position + "." + value + "." + kind);
    }

    /**
     * @param targetResource
     * @param value
     * @param index
     * @param valueObject
     * @param targetObject
     * @param feature
     */
    private void addObjectToItsContainer(Resource targetResource, Object value, int index, EObject valueObject, EObject targetObject, EStructuralFeature feature) {
	if (feature.isMany()) {
	    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);

	    if (list.contains(valueObject)) {
		// if (index >= list.size()) {
		// list.move(list.size() - 1, valueObject);
		// } else {
		list.move(index, valueObject);
		// }
	    } else {
		if (index > list.size()) {
		    list.add(valueObject);
		} else {
		    list.add(index, valueObject);
		}
	    }

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
		deletedObjects1.put(oldId, (EObject) oldValue);
	    }

	    targetObject.eSet(feature, valueObject);

	    if (targetResource instanceof XMIResource) {
		((XMIResource) targetResource).setID(valueObject, value.toString());
	    }
	}
    }

    private void iterateContainedObjects(EObject rightObject) {
	for (EReference ref : rightObject.eClass().getEAllReferences()) {
	    if (ref.isContainment()) {
		if (ref.isMany()) {
		    EList<EObject> eObjects = (EList<EObject>) rightObject.eGet(ref);
		    for (EObject eObject : eObjects) {
			System.out.println(targetResource.getURIFragment(eObject));
			iterateContainedObjects(eObject);
		    }
		} else {
		    EObject eObject = (EObject) rightObject.eGet(ref);
		    if (eObject != null) {
			System.out.println(targetResource.getURIFragment(eObject));
			iterateContainedObjects(eObject);
		    }
		}
	    }
	}

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
			if (eValue != null && eValue.eResource() != null) {
			    String id = eValue.eResource().getURIFragment(eValue);
			    deletedObjects1.put(id, eValue);
			    deletedObjects2.put(eValue, id);
			    recordDeletedObjects(eValue);
			}
		    }
		} else {
		    EObject eValue = (EObject) eObject.eGet(eReference);
		    if (eValue != null && eValue.eResource() != null) {
			String id = eValue.eResource().getURIFragment(eValue);
			deletedObjects1.put(id, eValue);
			deletedObjects2.put(eValue, id);
			recordDeletedObjects(eValue);
		    }
		}

	    }
	}
	if (eObject != null && eObject.eResource() != null) {
	    String id = eObject.eResource().getURIFragment(eObject);
	    deletedObjects1.put(id, eObject);
	}
    }

    @Test
    public void printContentsOfFeatures() throws IOException {
	boolean x = false;
	if (x == true) {
	    String objectId = "O-37211";
	    String featureName = "memberEnd";

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
