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
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.ICBPEObjectMatcher;
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
    protected void match(Comparison comparison, IComparisonScope scope, ResourceSet left, ResourceSet right, ResourceSet origin, Monitor monitor) {

	// super.match(comparison, scope, left, right, origin, monitor);

	cbpMatch(comparison, scope, left, right, origin, monitor);
    }

    /**
     * @param comparison
     * @param scope
     * @param left
     * @param right
     * @param origin
     * @param monitor
     */
    private void cbpMatch(Comparison comparison, IComparisonScope scope, ResourceSet left, ResourceSet right, ResourceSet origin, Monitor monitor) {
	
	//custom initialisation
	Resource leftResource = null;
	Resource rightResource = null;
	
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
	
	CBPEngine.createCBPEngine(leftResource, rightResource, CBPEngine.PARTIAL_MODE);
//	CBPEngine.createCBPEngine(leftResource, rightResource, CBPEngine.FULL_MODE);
	
	left.getResources().clear();
	left.getResources().add(CBPEngine.getLeftPartialResource());
	right.getResources().clear();
	right.getResources().add(CBPEngine.getRightPartialResource());
	
	
	//override parent's initialisation
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
//	((ICBPEObjectMatcher) getEObjectMatcher()).createMatches(comparison, CBPEngine.getLeftResource(), CBPEngine.getRightResource(), monitor);
    }

    public static IEObjectMatcher createDefaultEObjectMatcher(UseIdentifiers useIDs, WeightProvider.Descriptor.Registry weightProviderRegistry) {
	final IEObjectMatcher matcher;
	matcher = new CBPEObjectMatcher();
	return matcher;
    }
}
