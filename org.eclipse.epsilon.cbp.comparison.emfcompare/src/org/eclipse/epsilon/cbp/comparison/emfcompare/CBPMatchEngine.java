package org.eclipse.epsilon.cbp.comparison.emfcompare;

import static java.util.Collections.emptyIterator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.CompareFactory;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.ComparisonCanceledException;
import org.eclipse.emf.compare.EMFCompareMessages;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.MatchResource;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.WeightProvider;
import org.eclipse.emf.compare.match.resource.IResourceMatcher;
import org.eclipse.emf.compare.match.resource.IResourceMatchingStrategy;
import org.eclipse.emf.compare.match.resource.StrategyResourceMatcher;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.cbp.comparison.CBPComparisonOld;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CBPMatchEngine extends DefaultMatchEngine {

    private final IResourceMatcher resourceMatcher;

    public CBPMatchEngine(IEObjectMatcher eObjectMatcher, IResourceMatcher resourceMatcher, IComparisonFactory comparisonFactory) {
	super(eObjectMatcher, resourceMatcher, comparisonFactory);
	this.resourceMatcher = super.getResourceMatcher();

    }

    public CBPMatchEngine(IEObjectMatcher matcher, IComparisonFactory comparisonFactory) {
	super(matcher, comparisonFactory);
	this.resourceMatcher = super.getResourceMatcher();
    }

    public static IMatchEngine create(UseIdentifiers useIDs) {
	return create(useIDs, null, null);
    }

    public static IMatchEngine create(UseIdentifiers useIDs, WeightProvider.Descriptor.Registry weightProviderRegistry) {
	return create(useIDs, weightProviderRegistry, null);
    }

    public static IMatchEngine create(UseIdentifiers useIDs, WeightProvider.Descriptor.Registry weightProviderRegistry, Collection<IResourceMatchingStrategy> strategies) {
	final IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
	final IEObjectMatcher eObjectMatcher = createDefaultEObjectMatcher(useIDs, weightProviderRegistry);

	final IResourceMatcher resourceMatcher;
	if (strategies == null || strategies.isEmpty()) {
	    resourceMatcher = new StrategyResourceMatcher();
	} else {
	    resourceMatcher = new StrategyResourceMatcher(strategies);
	}

	final IMatchEngine matchEngine = new CBPMatchEngine(eObjectMatcher, resourceMatcher, comparisonFactory);
	return matchEngine;
    }

    @Override
    protected void match(Comparison comparison, IComparisonScope scope, Resource left, Resource right, Resource origin, Monitor monitor) {
	try {
	    CBPEngine.createCBPEngine(left, right, origin, CBPEngine.PARTIAL_MODE);

	    left = CBPEngine.getLeftPartialResource();
	    right = CBPEngine.getRightPartialResource();
	    origin = CBPEngine.getOriginPartialResource();

	    long start = System.nanoTime();
	    super.match(comparison, scope, left, right, origin, monitor);
	    // this.cbpMatch(comparison, scope, left, right, origin, monitor);
	    long end = System.nanoTime();
	    System.out.println("Matching Time  = " + (end - start) / 1000000000.0);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println();
	}
    }

    @Override
    protected void match(Comparison comparison, IComparisonScope scope, ResourceSet left, ResourceSet right, ResourceSet origin, Monitor monitor) {
	try {

	    Resource leftResource = left.getResources().get(0);
	    Resource rightResource = right.getResources().get(0);
	    Resource originResource = origin.getResources().get(0);
	    
	    countElements(leftResource, "Left");
	    countElements(rightResource, "Right");

	    long start = System.nanoTime();
	    CBPEngine.createCBPEngine(leftResource, rightResource, originResource, CBPEngine.PARTIAL_MODE);
	    long end = System.nanoTime();
	    System.out.println("Matching Time  = " + (end - start) / 1000000000.0);
	    
	    left.getResources().clear();
	    left.getResources().add(CBPEngine.getLeftPartialResource());
	    right.getResources().clear();
	    right.getResources().add(CBPEngine.getRightPartialResource());
	    origin.getResources().clear();
	    origin = null;

	    super.match(comparison, scope, left, right, null, monitor);

	    countElements(CBPEngine.getLeftPartialResource(), "Partial Left");
	    countElements(CBPEngine.getRightPartialResource(), "Partial Right");
	    
	    // cbpMatch(comparison, scope, left, right, origin, monitor);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void cbpMatch(Comparison comparison, IComparisonScope scope, Resource left, Resource right, Resource origin, Monitor monitor) {

	CBPEngine.createCBPEngine(left, right, origin, CBPEngine.PARTIAL_MODE);

	left = CBPEngine.getLeftPartialResource();
	right = CBPEngine.getRightPartialResource();
	origin = CBPEngine.getOriginPartialResource();

	// Our "roots" are Resources. Consider them matched
	final MatchResource match = CompareFactory.eINSTANCE.createMatchResource();

	match.setLeft(left);
	match.setRight(right);
	match.setOrigin(origin);

	if (left != null) {
	    URI uri = left.getURI();
	    if (uri != null) {
		match.setLeftURI(uri.toString());
	    }
	}

	if (right != null) {
	    URI uri = right.getURI();
	    if (uri != null) {
		match.setRightURI(uri.toString());
	    }
	}

	if (origin != null) {
	    URI uri = origin.getURI();
	    if (uri != null) {
		match.setOriginURI(uri.toString());
	    }
	}

	comparison.getMatchedResources().add(match);

	// We need at least two resources to match them
	if (atLeastTwo(left == null, right == null, origin == null)) {
	    /*
	     * TODO But if we have only one resource, which is then unmatched,
	     * should we not still do something with it?
	     */
	    return;
	}

	final Iterator<? extends EObject> leftEObjects;
	if (left != null) {
	    leftEObjects = scope.getCoveredEObjects(left);
	} else {
	    leftEObjects = emptyIterator();
	}
	final Iterator<? extends EObject> rightEObjects;
	if (right != null) {
	    rightEObjects = scope.getCoveredEObjects(right);
	} else {
	    rightEObjects = emptyIterator();
	}
	final Iterator<? extends EObject> originEObjects;
	if (origin != null) {
	    originEObjects = scope.getCoveredEObjects(origin);
	} else {
	    originEObjects = emptyIterator();
	}

	((ICBPEObjectMatcher) getEObjectMatcher()).createMatches(comparison, left, right, monitor);

    }

    private void cbpMatch(Comparison comparison, IComparisonScope scope, ResourceSet left, ResourceSet right, ResourceSet origin, Monitor monitor) {

	// custom initialisation
	Resource leftResource = null;
	Resource rightResource = null;
	Resource originResource = null;

	for (Resource resource : left.getResources()) {
	    if (resource != null) {
		leftResource = resource;
		break;
	    }
	}

	for (Resource resource : right.getResources()) {
	    if (resource != null) {
		rightResource = resource;
		break;
	    }
	}

	if (origin != null) {
	    for (Resource resource : origin.getResources()) {
		if (resource != null) {
		    originResource = resource;
		    break;
		}
	    }
	}

	// CBPEngine.createCBPEngine(leftResource, rightResource,
	// originResource, CBPEngine.PARTIAL_MODE);
	CBPEngine.createCBPEngine(leftResource, rightResource, originResource, CBPEngine.PARTIAL_MODE);

	long start = System.nanoTime();

	left.getResources().clear();
	left.getResources().add(CBPEngine.getLeftPartialResource());
	right.getResources().clear();
	right.getResources().add(CBPEngine.getRightPartialResource());

	// override parent's initialisation
	final Iterator<? extends Resource> leftChildren = scope.getCoveredResources(left);
	final Iterator<? extends Resource> rightChildren = scope.getCoveredResources(right);
	final Iterator<? extends Resource> originChildren;
	if (origin != null) {
	    originChildren = scope.getCoveredResources(origin);
	} else {
	    originChildren = emptyIterator();
	}

	// TODO Change API to pass the monitor to createMappings()
	final Iterable<MatchResource> mappings = this.resourceMatcher.createMappings(leftChildren, rightChildren, originChildren);

	final List<Iterator<? extends EObject>> leftIterators = Lists.newLinkedList();
	final List<Iterator<? extends EObject>> rightIterators = Lists.newLinkedList();
	final List<Iterator<? extends EObject>> originIterators = Lists.newLinkedList();

	for (MatchResource mapping : mappings) {
	    if (monitor.isCanceled()) {
		throw new ComparisonCanceledException();
	    }
	    comparison.getMatchedResources().add(mapping);

	    final Resource leftRes = mapping.getLeft();
	    final Resource rightRes = mapping.getRight();
	    final Resource originRes = mapping.getOrigin();

	    if (leftRes != null) {
		leftIterators.add(scope.getCoveredEObjects(leftRes));
	    }

	    if (rightRes != null) {
		rightIterators.add(scope.getCoveredEObjects(rightRes));
	    }

	    if (originRes != null) {
		originIterators.add(scope.getCoveredEObjects(originRes));
	    }
	}

	((ICBPEObjectMatcher) getEObjectMatcher()).createMatches(comparison, CBPEngine.getLeftPartialResource(), CBPEngine.getRightPartialResource(), monitor);
	// ((ICBPEObjectMatcher) getEObjectMatcher()).createMatches(comparison,
	// CBPEngine.getLeftResource(), CBPEngine.getRightResource(), monitor);

	long end = System.nanoTime();
	System.out.println("Matching Time  = " + (end - start) / 1000000000.0);
    }

    public static IEObjectMatcher createDefaultEObjectMatcher(UseIdentifiers useIDs, WeightProvider.Descriptor.Registry weightProviderRegistry) {
	final IEObjectMatcher matcher;
	matcher = new CBPEObjectMatcher();
	return matcher;
    }

    /**
     * This will check that at least two of the three given booleans are
     * <code>true</code>.
     * 
     * @param condition1
     *            First of the three booleans.
     * @param condition2
     *            Second of the three booleans.
     * @param condition3
     *            Third of the three booleans.
     * @return <code>true</code> if at least two of the three given booleans are
     *         <code>true</code>, <code>false</code> otherwise.
     */
    private static boolean atLeastTwo(boolean condition1, boolean condition2, boolean condition3) {
	// CHECKSTYLE:OFF This expression is alone in its method, and
	// documented.
	return condition1 && (condition2 || condition3) || (condition2 && condition3);
	// CHECKSTYLE:ON
    }

    protected void countElements(Resource leftResource, String name) {
	TreeIterator<EObject> iterator = leftResource.getAllContents();
	int count = 0;
	while (iterator.hasNext()) {
	    iterator.next();
	    count += 1;
	}
	System.out.println(name + " Element Count = " + count);
    }
}
