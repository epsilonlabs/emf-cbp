package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;

public class CBPDiffEngine extends DefaultDiffEngine {

    public CBPDiffEngine() {
	super(new DiffBuilder());
    }

    public CBPDiffEngine(IDiffProcessor iDiffProcessor) {
	super(new DiffBuilder());
    }

    @Override
    public void diff(Comparison comparison, Monitor monitor) {
	printMatches(comparison.getMatches(), 0);

	// super.diff(comparison, monitor);
	this.cbpDiff(comparison, monitor);
    }

    /**
     * @param comparison
     */
    protected void printMatches(List<Match> matches, int level) {
	for (Match match : matches) {
	    EObject left = match.getLeft();
	    EObject right = match.getRight();
	    String leftName = null;
	    String rightName = null;

	    if (left != null) {
		EStructuralFeature feature = left.eClass().getEStructuralFeature("name");
		if (feature != null) {
		    leftName = (String) left.eGet(feature);
		}
	    }

	    if (right != null) {
		EStructuralFeature feature = right.eClass().getEStructuralFeature("name");
		if (feature != null) {
		    rightName = (String) right.eGet(feature);
		}
	    }

	    for (int i = 0; i < level; i++) {
		System.out.print("+--");
	    }
	    System.out.println(leftName + " vs " + rightName);
	    printMatches(match.getSubmatches(), level + 1);
	}
    }

    private void cbpDiff(Comparison comparison, Monitor monitor) {

	Resource leftPartialResource = CBPEngine.getLeftPartialResource();
	Resource rightPartialResource = CBPEngine.getRightPartialResource();
	Map<String, TrackedObject> rightTrackedObjects = CBPEngine.getRightTrackedObjects();
	Map<String, TrackedObject> leftTrackedObjects = CBPEngine.getLeftTrackedObjects();

	computeDifferences(comparison, leftPartialResource, rightPartialResource, leftTrackedObjects, rightTrackedObjects, DifferenceSource.LEFT, monitor);
    }

    private void computeDifferences(Comparison comparison, Resource left, Resource right, Map<String, TrackedObject> leftTrackedObjects, Map<String, TrackedObject> rightTrackedObjects,
	    DifferenceSource source, Monitor monitor) {

	Set<String> ids = new HashSet<>(leftTrackedObjects.keySet());
	ids.addAll(rightTrackedObjects.keySet());

	for (String id : ids) {

	    System.out.println("Identifying differences of object with id = " + id);

	    TrackedObject leftTrackedObject = leftTrackedObjects.get(id);
	    TrackedObject rightTrackedObject = rightTrackedObjects.get(id);

	    if (leftTrackedObject == null) {
		EObject leftEObject = left.getEObject(id);
		if (leftEObject != null) {
		    leftTrackedObject = createTrackedEObject(id, left, leftEObject, leftTrackedObjects);
		}
	    }
	    if (rightTrackedObject == null) {
		EObject rightEObject = right.getEObject(id);
		if (rightEObject != null) {
		    rightTrackedObject = createTrackedEObject(id, left, rightEObject, rightTrackedObjects);
		}
	    }

	    // // set right container of the right tracked object based on the
	    // left
	    // // object container
	    // if (rightTrackedObject != null && leftTrackedObject != null &&
	    // rightTrackedObject.getContainer() == null &&
	    // leftTrackedObject.getOldContainer() != null) {
	    // rightTrackedObject.setContainer(leftTrackedObject.getOldContainer());
	    // }

	    // if an object only exists in one of the resources or does not
	    // exists at all
	    if (leftTrackedObject == null || rightTrackedObject == null) {
		// handle objects that don't exist in one of the resources
		if (leftTrackedObject == null && rightTrackedObject != null) {
		    EObject value = right.getEObject(id);
		    Match match = comparison.getMatch(value.eContainer());
		    EStructuralFeature feature = value.eContainer().eClass().getEStructuralFeature(rightTrackedObject.getContainingFeature());
		    if (feature instanceof EReference) {
			EReference reference = (EReference) feature;
			getDiffProcessor().referenceChange(match, reference, value, DifferenceKind.DELETE, source);
		    }
		} else if (leftTrackedObject != null && rightTrackedObject == null) {
		    EObject value = left.getEObject(id);
		    Match match = comparison.getMatch(value.eContainer());
		    EStructuralFeature feature = value.eContainer().eClass().getEStructuralFeature(leftTrackedObject.getContainingFeature());
		    if (feature instanceof EReference) {
			EReference reference = (EReference) feature;
			getDiffProcessor().referenceChange(match, reference, value, DifferenceKind.ADD, source);
		    }
		}
	    }

	    // if an object exists in both resources
	    else {

		// handle objects that have been moved cross containers
		if (leftTrackedObject.getContainer() != null && rightTrackedObject.getContainer() != null && leftTrackedObject.getContainingFeature() != null
			&& rightTrackedObject.getContainingFeature() != null
			&& !leftTrackedObject.getContainer().equals(rightTrackedObject.getContainer()) ^ !leftTrackedObject.getContainingFeature().equals(rightTrackedObject.getContainingFeature())) {
		    EObject value = right.getEObject(id);
		    Match match = comparison.getMatch(value);
		    EReference reference = (EReference) value.eClass().getEStructuralFeature(leftTrackedObject.getContainingFeature());
		    getDiffProcessor().referenceChange(match, reference, value, DifferenceKind.MOVE, source);
		}
		// handle objects that have been moved in the same containers
		else if (leftTrackedObject.getPosition() > -1 && rightTrackedObject.getPosition() > -1 && leftTrackedObject.getContainer() != null && rightTrackedObject.getContainer() != null
			&& leftTrackedObject.getContainer().equals(rightTrackedObject.getContainer()) && leftTrackedObject.getContainingFeature().equals(rightTrackedObject.getContainingFeature())
			&& leftTrackedObject.getPosition() != rightTrackedObject.getPosition()) {
		    EObject value = right.getEObject(id);
		    Match match = comparison.getMatch(value);
		    EReference reference = (EReference) value.eClass().getEStructuralFeature(leftTrackedObject.getContainingFeature());
		    getDiffProcessor().referenceChange(match, reference, value, DifferenceKind.MOVE, source);
		}

		// handle objects that have been modified on their features
		EObject leftTargetEObject = left.getEObject(id);
		Match match = comparison.getMatch(leftTargetEObject);
		EObject rightTargetEObject = match != null ? match.getRight() : null;
		EObject targetEObject = leftTargetEObject != null ? leftTargetEObject : rightTargetEObject;

		Set<String> featureNameSet = new HashSet<>(leftTrackedObject.getFeatures().keySet());
		featureNameSet.addAll(rightTrackedObject.getFeatures().keySet());

		for (String featureName : featureNameSet) {
		    TrackedFeature leftFeature = leftTrackedObject.getFeature(featureName);
		    TrackedFeature rightFeature = rightTrackedObject.getFeature(featureName);

		    EStructuralFeature leftEStructuralFeature = null;
		    EStructuralFeature rightEStructuralFeature = null;
		    EStructuralFeature feature = null;
		    boolean isMany = false;
		    boolean isAttribute = true;

		    if (leftFeature != null) {
			leftEStructuralFeature = leftTargetEObject.eClass().getEStructuralFeature(leftFeature.getFeatureName());
			feature = leftEStructuralFeature;
			isMany = leftEStructuralFeature.isMany();
			if (leftEStructuralFeature instanceof EReference) {
			    isAttribute = false;
			}
		    }
		    if (rightFeature != null) {
			rightEStructuralFeature = rightTargetEObject.eClass().getEStructuralFeature(rightFeature.getFeatureName());
			if (leftEStructuralFeature == null) {
			    feature = rightEStructuralFeature;
			    isMany = rightEStructuralFeature.isMany();
			    if (rightEStructuralFeature instanceof EReference) {
				isAttribute = false;
			    }
			}
		    }

		    // handle single-value attribute
		    if (isMany == false && isAttribute == true) {
			Object value = targetEObject.eGet(feature);
			getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.CHANGE, source);
		    }

		    // handle multi-value attribute
		    else if (isMany == true && isAttribute == true) {
			Map<Integer, String> leftValues = leftFeature.getValues();
			Map<Integer, String> rightValues = rightFeature.getValues();

			Set<Integer> positions = new HashSet<>(leftValues.keySet());
			positions.addAll(rightValues.keySet());

			for (int pos : positions) {
			    String leftValue = leftFeature.getValue(pos);
			    String rightValue = rightFeature.getValue(pos);
			    if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
				getDiffProcessor().featureMapChange(match, (EAttribute) feature, leftValue, DifferenceKind.CHANGE, source);
			    } else if (leftValue == null && rightValue != null) {
				getDiffProcessor().featureMapChange(match, (EAttribute) feature, rightValue, DifferenceKind.DELETE, source);
			    } else if (leftValue != null && rightValue == null) {
				getDiffProcessor().featureMapChange(match, (EAttribute) feature, leftValue, DifferenceKind.ADD, source);
			    }
			}

		    }
		    // handle single-value reference
		    else if (isMany == false && isAttribute == false) {

			EObject value = (EObject) targetEObject.eGet(feature);
			getDiffProcessor().referenceChange(match, (EReference) feature, value, DifferenceKind.CHANGE, source);

		    } else if (isMany == true && isAttribute == false) {

//			if (leftFeature != null && rightFeature == null) {
//			    Map<Integer, String> leftValues = leftFeature.getValues();
//			    Set<Integer> positions = new HashSet<>(leftValues.keySet());
//			    for (int pos : positions) {
//				String leftValue = leftFeature.getValue(pos);
//				EObject leftEObject = left.getEObject(leftValue);
//				getDiffProcessor().referenceChange(match, (EReference) feature, leftEObject, DifferenceKind.ADD, source);
//			    }
//			} else if (leftFeature == null && rightFeature != null) {
//			    Map<Integer, String> rightValues = rightFeature.getValues();
//			    Set<Integer> positions = new HashSet<>(rightValues.keySet());
//			    for (int pos : positions) {
//				String rightValue = rightFeature.getValue(pos);
//				EObject rightEObject = right.getEObject(rightValue);
//				getDiffProcessor().referenceChange(match, (EReference) feature, rightEObject, DifferenceKind.DELETE, source);
//			    }
//			} else if (leftFeature != null && rightFeature != null) {

//			    Map<Integer, String> leftValues = leftFeature.getValues();
//			    Map<Integer, String> rightValues = rightFeature.getValues();
//
//			    Set<Integer> positions = new HashSet<>(leftValues.keySet());
//			    positions.addAll(rightValues.keySet());
//
//			    for (int pos : positions) {
//				String leftValue = leftFeature.getValue(pos);
//				String rightValue = rightFeature.getValue(pos);
//				if (leftValue != null && rightValue != null && !leftValue.equals(rightValue)) {
//				    EObject leftEObject = left.getEObject(leftValue);
//				    EObject rightEObject = right.getEObject(rightValue);
//				    getDiffProcessor().referenceChange(match, (EReference) feature, leftEObject, DifferenceKind.CHANGE, source);
//				} else if (leftValue == null && rightValue != null) {
//				    EObject rightEObject = right.getEObject(rightValue);
//				    getDiffProcessor().referenceChange(match, (EReference) feature, rightEObject, DifferenceKind.DELETE, source);
//				} else if (leftValue != null && rightValue == null) {
//				    EObject leftEObject = left.getEObject(leftValue);
//				    getDiffProcessor().referenceChange(match, (EReference) feature, leftEObject, DifferenceKind.ADD, source);
//				}
//			    }
//			}
		    }
		}
	    }

	}

    }

    /***
     * 
     * @param id
     * @param resource
     * @param eObject
     * @param trackedObjects
     * @return
     */
    private TrackedObject createTrackedEObject(String id, Resource resource, EObject eObject, Map<String, TrackedObject> trackedObjects) {
	TrackedObject trackedObject = new TrackedObject(id);

	// get the containing eObject
	if (resource instanceof XMIResource && eObject.eContainer() != null) {
	    trackedObject.setContainer(((XMIResource) resource).getID(eObject.eContainer()));
	}
	// get the containing feature name
	if (eObject.eContainingFeature() != null) {
	    trackedObject.setContainingFeature(eObject.eContainingFeature().getName());
	}
	// get the position of the eObject
	if (eObject.eContainer() != null && eObject.eContainingFeature() != null) {
	    Object value = eObject.eContainer().eGet(eObject.eContainingFeature());
	    if (value instanceof EList<?>) {
		int pos = ((EList<?>) value).indexOf(value);
		trackedObject.setPosition(pos);
	    } else {
		trackedObject.setPosition(0);
	    }

	}

	trackedObjects.put(id, trackedObject);
	return trackedObject;
    }

}
