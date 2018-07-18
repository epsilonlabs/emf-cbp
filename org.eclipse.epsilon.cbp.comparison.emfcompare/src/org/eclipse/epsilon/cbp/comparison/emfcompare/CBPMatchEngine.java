package org.eclipse.epsilon.cbp.comparison.emfcompare;

import static java.util.Collections.emptyIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.CompareFactory;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.ComparisonCanceledException;
import org.eclipse.emf.compare.EMFCompareMessages;
import org.eclipse.emf.compare.MatchResource;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.WeightProvider;
import org.eclipse.emf.compare.match.eobject.WeightProviderDescriptorRegistryImpl;
import org.eclipse.emf.compare.match.resource.IResourceMatcher;
import org.eclipse.emf.compare.match.resource.IResourceMatchingStrategy;
import org.eclipse.emf.compare.match.resource.StrategyResourceMatcher;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class CBPMatchEngine extends DefaultMatchEngine {

	private final IResourceMatcher resourceMatcher;

	public CBPMatchEngine(IEObjectMatcher eObjectMatcher, IResourceMatcher resourceMatcher,
			IComparisonFactory comparisonFactory) {
		super(eObjectMatcher, resourceMatcher, comparisonFactory);
		this.resourceMatcher = super.getResourceMatcher();
	}

	public CBPMatchEngine(IEObjectMatcher matcher, IComparisonFactory comparisonFactory) {
		super(matcher, comparisonFactory);
		this.resourceMatcher = super.getResourceMatcher();
	}

	public static IMatchEngine create(UseIdentifiers useIDs,
			WeightProvider.Descriptor.Registry weightProviderRegistry) {
		return create(useIDs, weightProviderRegistry, null);
	}

	public static IMatchEngine create(UseIdentifiers useIDs, WeightProvider.Descriptor.Registry weightProviderRegistry,
			Collection<IResourceMatchingStrategy> strategies) {
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

	private static boolean atLeastTwo(boolean condition1, boolean condition2, boolean condition3) {
		// CHECKSTYLE:OFF This expression is alone in its method, and
		// documented.
		return condition1 && (condition2 || condition3) || (condition2 && condition3);
		// CHECKSTYLE:ON
	}

	@Override
	protected void match(Comparison comparison, IComparisonScope scope, ResourceSet left, ResourceSet right,
			ResourceSet origin, Monitor monitor) {
		
		super.match(comparison, scope, left, right, origin, monitor);
		
		
//		monitor.subTask(EMFCompareMessages.getString("DefaultMatchEngine.monitor.match.resourceSet")); //$NON-NLS-1$
//		
//		List<EObject> leftEObjectList = new ArrayList<>();
//		List<EObject> rightEObjectList = new ArrayList<>();
//		List<EObject> originEObjectList = new ArrayList<>();
//		
//		
////		leftEObjectList.add(left.getResources().get(0).getContents().get(0));
//		leftEObjectList.add(left.getResources().get(0).getContents().get(1));
//	
////		rightEObjectList.add(right.getResources().get(0).getContents().get(0));
//		rightEObjectList.add(right.getResources().get(0).getContents().get(1));
//	
//		final Iterator<? extends EObject> leftEObjects = leftEObjectList.iterator();
//		final Iterator<? extends EObject> rightEObjects = rightEObjectList.iterator();
//		final Iterator<? extends EObject> originEObjects = originEObjectList.iterator();
//		
//		getEObjectMatcher().createMatches(comparison, leftEObjects, rightEObjects, originEObjects, monitor);
	}

	@Override
	protected void match(Comparison comparison, IComparisonScope scope, Resource left, Resource right, Resource origin,
			Monitor monitor) {
		monitor.subTask(EMFCompareMessages.getString("DefaultMatchEngine.monitor.match.resource")); //$NON-NLS-1$
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

		getEObjectMatcher().createMatches(comparison, leftEObjects, rightEObjects, originEObjects, monitor);
	}
}
