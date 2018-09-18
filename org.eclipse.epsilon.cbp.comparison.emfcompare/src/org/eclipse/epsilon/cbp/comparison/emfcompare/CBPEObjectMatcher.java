package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.compare.CompareFactory;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;

import com.google.common.collect.Maps;

public class CBPEObjectMatcher extends IdentifierEObjectMatcher implements ICBPEObjectMatcher {

    private final Map<EObject, Match> leftEObjectsToMatch;
    private final Map<EObject, Match> rightEObjectsToMatch;

    public CBPEObjectMatcher() {
	this.leftEObjectsToMatch = Maps.newHashMap();
	this.rightEObjectsToMatch = Maps.newHashMap();
    }

    public void createMatchesOld(Comparison comparison, Resource left, Resource right, Monitor monitor) {

	Map<String, TrackedObject> leftTrackedObjects = CBPEngine.getLeftTrackedObjects();
	Map<String, TrackedObject> rightTrackedObjects = CBPEngine.getRightTrackedObjects();

	Set<String> ids = new HashSet<>(leftTrackedObjects.keySet());
	ids.addAll(rightTrackedObjects.keySet());

	long start = System.nanoTime();
	Iterator<String> iterator = ids.iterator();
	while (iterator.hasNext()) {
	    String id = iterator.next();
	    EObject leftEObject = ((XMIResource) left).getEObject(id);
	    EObject rightEObject = ((XMIResource) right).getEObject(id);
	    if (leftEObject != null || rightEObject != null) {
		Match match = CompareFactory.eINSTANCE.createMatch();
		match.setLeft(leftEObject);
		match.setRight(rightEObject);
		comparison.getMatches().add(match);
	    } else {
		System.out.println("Removed id = " + id);
		iterator.remove();
		leftTrackedObjects.remove(id);
		rightTrackedObjects.remove(id);
	    }
	}
	long end = System.nanoTime();
	System.out.println("Create matches Time  = " + (end - start) / 1000000000.0);

	start = System.nanoTime();
	// move matches to their parent matches
	for (int i = 0; i < comparison.getMatches().size(); i++) {
	    Match match = comparison.getMatches().get(i);
	    EObject eContainer = null;
	    Match containerMatch = null;
	    if (match.getLeft() != null && match.getLeft().eContainer() != null && comparison.getMatch(match.getLeft().eContainer()) != null) {
		containerMatch = comparison.getMatch(match.getLeft().eContainer());

	    } else if (match.getRight() != null && match.getRight().eContainer() != null && comparison.getMatch(match.getRight().eContainer()) != null) {
		containerMatch = comparison.getMatch(match.getRight().eContainer());
	    }

	    if (containerMatch != null) {
		containerMatch.getSubmatches().add(match);
		comparison.getMatches().remove(match);
		i--;
	    }
	}
	end = System.nanoTime();
	System.out.println("Construct match tree Time  = " + (end - start) / 1000000000.0);

    }

    public void createMatches(Comparison comparison, Resource left, Resource right, Monitor monitor) {

	XMIResource leftResource = (XMIResource) left;
	XMIResource rightResource = (XMIResource) right;

	Set<String> matchedId = new HashSet<>();

	TreeIterator<EObject> leftIterator = leftResource.getAllContents();
	while (leftIterator.hasNext()) {
	    EObject leftEObject = leftIterator.next();
	    String id = leftResource.getID(leftEObject);

	    EObject rightEObject = null;
	    if (id != null) {
		rightEObject = rightResource.getEObject(id);
		Match match = CompareFactory.eINSTANCE.createMatch();
		match.setLeft(leftEObject);
		match.setRight(rightEObject);
		comparison.getMatches().add(match);
		matchedId.add(id);
		leftEObjectsToMatch.put(leftEObject, match);
		rightEObjectsToMatch.put(rightEObject, match);
	    } else {
		Match match = CompareFactory.eINSTANCE.createMatch();
		match.setLeft(leftEObject);
		match.setRight(rightEObject);
		comparison.getMatches().add(match);
		leftEObjectsToMatch.put(leftEObject, match);
	    }
	}

	TreeIterator<EObject> rightIterator = rightResource.getAllContents();
	while (rightIterator.hasNext()) {
	    EObject rightEObject = rightIterator.next();
	    String id = rightResource.getID(rightEObject);

	    if (!matchedId.contains(id)) {
		Match match = CompareFactory.eINSTANCE.createMatch();
		match.setRight(rightEObject);
		comparison.getMatches().add(match);
		matchedId.add(id);
		rightEObjectsToMatch.put(rightEObject, match);
	    }
	}

	// move matches to their parent matches
	for (int i = 0; i < comparison.getMatches().size(); i++) {
	    Match match = comparison.getMatches().get(i);
	    EObject object = match.getLeft() != null ? match.getLeft() : match.getRight();
	    if (object.eContainer() != null) {
		Match containerMatch = comparison.getMatch(object.eContainer());
		containerMatch.getSubmatches().add(match);
		comparison.getMatches().remove(match);
		i--;
	    }
	}
    }
}
