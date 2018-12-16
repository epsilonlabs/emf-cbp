package org.eclipse.epsilon.cbp.merging;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.management.ValueExp;

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
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.junit.Test;
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

    Map<String, EObject> deletedObjects1 = new HashMap<>();
    Map<EObject, String> deletedObjects2 = new HashMap<>();

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

	Collections.sort(diffs, new CBPDiffComparator());

	resolveDiff(targetResource, leftResource, diffs, null, null);
    }

    // public void resolveDiff(Resource targetResource, Resource leftResource,
    // List<CBPDiff> diffs) throws Exception {
    // this.resolveDiff(targetResource, leftResource, diffs, null);
    // }

    public void resolveDiff(Resource targetResource, Resource leftResource, List<CBPDiff> diffs, CBPMatchObject valueCaller, CBPMatchObject targetCaller) throws Exception {

	List<CBPDiff> temp = null;
	if (diffs.size() > 0 && diffs.get(0).getObject().getId().equals("O-37481")) {
	    // temp = new ArrayList<>();
	    // for (CBPDiff diff : diffs) {
	    // temp.add(diff);
	    // }
	    // diffs = temp;
	}
	// Collections.sort(diffs, new CBPDiffComparator());

	CBPDiff prevDiff = null;

	Map<Integer, EObject> moveToEObjects = new TreeMap<>();
	int previousPosition = 0;
	CBPMatchObject previousTarget = null;
	CBPMatchFeature previousFeature = null;

	for (int i = 0; i < diffs.size(); i++) {

	    // {
	    // EObject x = targetResource.getEObject("O-40430");
	    // if (x == null) {
	    // x = deletedObjects1.get("O-40430");
	    // }
	    // if (x != null) {
	    // EObject b = (EObject)
	    // x.eGet(x.eClass().getEStructuralFeature("type"));
	    // if (b == null) {
	    // System.out.println();
	    // }
	    // }
	    // }

	    CBPDiff diff = diffs.get(i);
	    if (diff.isResolved()) {
		continue;
	    }

	    CBPDiff nextDiff = (i + 1 < diffs.size()) ? diffs.get(i + 1) : null;

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

	    // if (valueMatchObject.equals(caller)) {
	    // System.out.println();
	    // continue;
	    // }

	    if (targetId.equals("L-697")) {
		System.out.println();
	    }

	    // ADD OBJECTS THAT HAS BEEN MOVE TO TEMPORARY MOVETOEOBJECTS
	    if (moveToEObjects.size() > 0) {
		int curPos = (diff.getKind() != CBPDifferenceKind.MOVE) ? diff.getPosition() : ((diff.getOrigin() < diff.getPosition()) ? diff.getOrigin() : diff.getPosition());
		// int curPos = position;
		Iterator<Entry<Integer, EObject>> iterator = moveToEObjects.entrySet().iterator();
		while (iterator.hasNext()) {
		    Entry<Integer, EObject> entry = iterator.next();
		    int pos = entry.getKey();
		    if (pos >= previousPosition && pos < curPos) {
			EObject obj = entry.getValue();
//			EObject targetObject = ((XMIResource) targetResource).getEObject(targetId);
//			EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);
			EObject targetObject = ((XMIResource) targetResource).getEObject(previousTarget.getId());
			EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(previousFeature.getName());
			EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
			if (pos >= list.size()) {
			    list.move(list.size() - 1, obj);
			} else {
			    try {
//				if (list.contains(obj)) {
				    list.move(pos, obj);
//				}
			    } catch (Exception e) {
				System.out.println();
			    }
			}
			iterator.remove();
		    }
		}
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

		    if (value.equals("O-39190")) {
			System.out.println();
		    }

		    EObject targetObject = ((XMIResource) targetResource).getEObject(targetId);
		    if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
			resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs(), targetMatchObject, null);
			targetObject = targetResource.getEObject(targetId);
		    }

		    if (diff.isResolved()) {
			continue;
		    }

		    if (targetObject == null) {
			targetObject = deletedObjects1.get(targetId);
		    }

		    if (value.equals("O-39190")) {
			System.out.println();
		    }

		    EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

		    printEvent(targetId, featureName, value, position, kind);

		    if (value != null) {
			valueObject = deletedObjects1.get(value.toString());
			if (valueObject == null) {
			    valueObject = targetResource.getEObject(value.toString());
			}

			// firstly process all related events related to the
			// container object of the right object
			boolean executeRightSideLater = false;
			CBPMatchObject matchRightObject = (CBPMatchObject) matchFeature.getRightValues().get(position);
			if (matchRightObject != null) {
			    if (matchRightObject.getLeftContainer() != null) {

				if (!targetMatchObject.equals(matchRightObject.getLeftContainer()) || !matchFeature.equals(matchRightObject.getLeftContainingFeature())) {
				    if (!matchRightObject.equals(valueCaller)) {
					resolveDiff(targetResource, leftResource, matchRightObject.getLeftContainer().getDiffs(), matchRightObject, null);
				    }
				    if (diff.isResolved()) {
					continue;
				    }
				} else {
				    if (feature.isMany()) {
					if (matchRightObject.getLeftPosition() > position) {
					    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
					    EObject currentObject = targetResource.getEObject(matchRightObject.getId());
					    list.move(list.size() - 1, currentObject);
					}
					// EList<EObject> list =
					// (EList<EObject>)
					// targetObject.eGet(feature);
					// if
					// (matchRightObject.getMoveDiffsAsValue().get(0).getPosition()
					// < list.size()) {
					// resolveDiff(targetResource,
					// leftResource,
					// matchRightObject.getMoveDiffsAsValue(),
					// matchRightObject, null);
					// } else {
					// executeRightSideLater = true;
					// }
				    } else {
					resolveDiff(targetResource, leftResource, matchRightObject.getDeleteDiffsAsValue(), matchRightObject, null);
					if (diff.isResolved()) {
					    continue;
					}
				    }
				}
			    }
			}

			// and the process the left object
			if (valueObject == null) {

			    if (((EReference) feature).isContainment() && prevDiff != null && diff.getObject().equals(prevDiff.getObject()) && diff.getFeature().equals(prevDiff.getFeature())
				    && ((CBPMatchObject) diff.getValue()).getClassName().equals(((CBPMatchObject) prevDiff.getValue()).getClassName()) && diff.getPosition() == prevDiff.getPosition()
				    && prevDiff.getKind() == CBPDifferenceKind.DELETE) {
				EObject rightObject = targetResource.getEObject(((CBPMatchObject) prevDiff.getValue()).getId());
				EObject copiedRightObject = EcoreUtil.copy(rightObject);
				valueObject = rightObject;

				// just do rename of id
				if (feature.isMany()) {
				    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);

				    deletedObjects1.put(((CBPMatchObject) prevDiff.getValue()).getId(), copiedRightObject);

				    // copy attributes values to the right
				    // object
				    EObject leftObject = leftResource.getEObject(value.toString());
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

				    list.move(position, valueObject);

				} else {
				    if (copiedRightObject != null && ((EReference) feature).isContainment()) {
					deletedObjects1.put(((CBPMatchObject) prevDiff.getValue()).getId(), copiedRightObject);
				    }

				    // copy attributes values to the right
				    // object
				    EObject leftObject = leftResource.getEObject(value.toString());
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

				}

			    } else {

				valueObject = EcoreUtil.copy(leftResource.getEObject(value.toString()));
				clearContainedObjects(valueObject);

				addObjectToItsContainer(targetResource, value, position, valueObject, targetObject, feature);

				if (executeRightSideLater) {
				    diff.setResolved(true);
				    resolveDiff(targetResource, leftResource, matchRightObject.getMoveDiffsAsValue(), matchRightObject, null);
				    if (diff.isResolved()) {
					continue;
				    }
				    // printContentsOfFeatures("O-37481",
				    // "packagedElement", position);
				}

			    }
			} else {
			    addObjectToItsContainer(targetResource, value, position, valueObject, targetObject, feature);
			}

		    }
		    diff.setResolved(true);
		    // printContentsOfFeatures("O-37481", "packagedElement",
		    // position);

		}

		// FOR CHANGE, DELETE, MOVE
		else {

		    if (value != null) {
			valueObject = targetResource.getEObject(value.toString());
			if (valueObject == null) {
			    valueObject = deletedObjects1.get(value.toString());
			}
		    }

		    if (kind == CBPDifferenceKind.CHANGE) {

			if (value.equals("L-708")) {
			    System.out.println();
			}

			EObject targetObject = deletedObjects1.get(targetId);
			if (targetObject == null) {
			    targetObject = targetResource.getEObject(targetId);
			    if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
				resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs(), targetMatchObject, null);
				targetObject = targetResource.getEObject(targetId);
			    }
			}

			if (valueObject == null) {
			    valueObject = deletedObjects1.get(value.toString());
			    if (valueObject == null) {
				valueObject = targetResource.getEObject(value.toString());
				if (valueObject == null && valueMatchObject.getLeftContainer() != null) {
				    resolveDiff(targetResource, leftResource, valueMatchObject.getLeftContainer().getDiffs(), valueMatchObject, targetMatchObject);
				    valueObject = targetResource.getEObject(value.toString());
				}
			    }
			}
			if (diff.isResolved()) {
			    continue;
			}

			printEvent(targetId, featureName, value, position, kind);

			EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

			targetObject.eSet(feature, valueObject);
			diff.setResolved(true);

		    } else if (kind == CBPDifferenceKind.DELETE) {

			if (value.equals("R-2609")) {
			    System.out.println();
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

			printEvent(targetId, featureName, value, position, kind);

			if (value.equals("R-2609")) {
			    System.out.println();
			}

			boolean isNextLeftEObjectContainerExists = false;
			boolean isNextLeftEObjectExists = false;
			if (nextDiff != null && nextDiff.getValue() instanceof CBPMatchObject) {
			    String id = ((CBPMatchObject) nextDiff.getValue()).getId();
			    EObject obj = targetResource.getEObject(id);
			    if (obj != null) {
				isNextLeftEObjectExists = true;
				// EObject container = obj.eContainer();
				// if (container != null) {
				// isNextLeftEObjectContainerExists = true;
				// }
			    }
			}

			if (nextDiff != null && matchFeature.isContainment()
				&& isNextLeftEObjectExists == false /*
								     * &&
								     * targetResource
								     * .
								     * getEObject
								     * (((
								     * CBPMatchObject
								     * )
								     * nextDiff.
								     * getValue(
								     * )).getId(
								     * )) ==
								     * null
								     */ && diff.getObject().equals(nextDiff.getObject()) && diff.getFeature().equals(nextDiff.getFeature())
				&& ((CBPMatchObject) diff.getValue()).getClassName().equals(((CBPMatchObject) nextDiff.getValue()).getClassName()) && diff.getPosition() == nextDiff.getPosition()
				&& nextDiff.getKind() == CBPDifferenceKind.ADD) {

			    // intentionally left blank
			} else {

			    EObject targetObject = deletedObjects1.get(targetId);
			    if (targetObject == null) {
				targetObject = targetResource.getEObject(targetId);
				if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
				    resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs(), targetMatchObject, null);
				    targetObject = targetResource.getEObject(targetId);
				}
			    }

			    if (valueObject == null) {
				valueObject = deletedObjects1.get(value.toString());
				if (valueObject == null) {
				    valueObject = targetResource.getEObject(value.toString());
				    if (valueObject == null && valueMatchObject.getLeftContainer() != null) {
					resolveDiff(targetResource, leftResource, valueMatchObject.getLeftContainer().getDiffs(), valueMatchObject, targetMatchObject);
					valueObject = targetResource.getEObject(value.toString());
				    }
				}
			    }

			    if (diff.isResolved()) {
				continue;
			    }

			    recordDeletedObjects(valueObject);
			    EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

			    if (feature.isMany()) {
				EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
				list.remove(valueObject);
			    } else {
				targetObject.eUnset(feature);
			    }
			}

			diff.setResolved(true);
			// printContentsOfFeatures("O-37481", "packagedElement",
			// position);

		    }

		    // MOVE
		    else if (kind == CBPDifferenceKind.MOVE) {

			EObject targetObject = targetResource.getEObject(targetId);
			if (targetObject == null && targetMatchObject.getLeftContainer() != null) {
			    resolveDiff(targetResource, leftResource, targetMatchObject.getLeftContainer().getDiffs(), valueMatchObject, targetMatchObject);
			    targetObject = targetResource.getEObject(targetId);
			}

			if (diff.isResolved()) {
			    continue;
			}

			printEvent(targetId, featureName, value, position, kind);

			if (value.equals("O-55004")) {
			    System.out.print("");
			}

			EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);

			if (feature.isMany()) {
			    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
			    // WITHIN CONTAINING FEATURE MOVE
			    if (targetObject.equals(valueObject.eContainer()) && feature.getName().equals(valueObject.eContainmentFeature().getName())) {
				if (list.size() > 0) {
				    if (diff.getOrigin() >= diff.getPosition()) {
					if (diff.getFeature().getRightValues().get(position) != null) {
					    // move right object to the last
					    // it will be moved to it's right
					    // position later
					    // when the iteration reach it's
					    // event
					    // to move to it's correct position
					    CBPMatchObject rightValue = (CBPMatchObject) diff.getFeature().getRightValues().get(position);
					    EObject currentObject = targetResource.getEObject(rightValue.getId());
					    if (rightValue.getLeftPosition() >= position) {
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

				    } else {
					CBPMatchObject rightValue = (CBPMatchObject) diff.getFeature().getRightValues().get(diff.getOrigin());
					EObject currentObject = targetResource.getEObject(rightValue.getId());
					moveToEObjects.put(diff.getPosition(), currentObject);
					// if (rightValue.getLeftContainer() !=
					// null) {
					// resolveDiff(targetResource,
					// leftResource, rightValue.getDiffs(),
					// rightValue, targetMatchObject);
					// }
					// if (diff.isResolved()) {
					// continue;
					// }
					list.move(list.size() - 1, currentObject);
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
			// printContentsOfFeatures("O-43655", "packagedElement",
			// position);
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

	    // MOVE THE REST OF THE TEMPORARY MOVETOEOBJECTS TO THEIR PROPER
	    // POSITION
	    if (i == diffs.size() - 1 && moveToEObjects.size() > 0) {
		Iterator<Entry<Integer, EObject>> iterator = moveToEObjects.entrySet().iterator();
		while (iterator.hasNext()) {
		    Entry<Integer, EObject> entry = iterator.next();
		    int pos = entry.getKey();
		    EObject obj = entry.getValue();
		    EObject targetObject = ((XMIResource) targetResource).getEObject(targetId);
		    EStructuralFeature feature = targetObject.eClass().getEStructuralFeature(featureName);
		    EList<EObject> list = (EList<EObject>) targetObject.eGet(feature);
		    list.move(pos, obj);
		    iterator.remove();
		}
	    }

	    prevDiff = diff;
	    previousPosition = (diff.getKind() != CBPDifferenceKind.MOVE) ? diff.getPosition() : ((diff.getOrigin() < diff.getPosition()) ? diff.getOrigin() : diff.getPosition());
	    previousTarget = targetMatchObject;
	    previousFeature = matchFeature;
	    // previousPosition = position;
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
			deletedObjects1.put(id, eValue);
			deletedObjects2.put(eValue, id);
			recordDeletedObjects(eValue);
		    }
		} else {
		    EObject eValue = (EObject) eObject.eGet(eReference);
		    if (eValue != null) {
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
    public void printContentsOfFeatures(String objectId, String featureName, int position) throws IOException {

	ResourceSet resourceSet = new ResourceSetImpl();
	resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	XMIResource leftResource = (XMIResource) this.leftResource;
	XMIResource targetResource = (XMIResource) this.targetResource;

	EObject leftObject = leftResource.getEObject(objectId);
	EReference leftReference = (EReference) leftObject.eClass().getEStructuralFeature(featureName);
	EList<EObject> leftValues = (EList<EObject>) leftObject.eGet(leftReference);

	EObject targetObject = targetResource.getEObject(objectId);
	EReference targetReference = (EReference) targetObject.eClass().getEStructuralFeature(featureName);
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

	    String separator = (leftId.equals(rightId)) ? " = " : " x ";
	    System.out.println(i + ": " + leftId + separator + rightId);

	}
	System.out.println();
    }
}
