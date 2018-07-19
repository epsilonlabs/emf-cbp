package org.eclipse.epsilon.cbp.comparison.emfcompare;

import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.resource.IResourceMatcher;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.resource.ResourceSet;


public class CBPMatchEngine extends DefaultMatchEngine {

//	private static final Logger LOGGER = Logger.getLogger(DefaultMatchEngine.class);
//	private final IResourceMatcher resourceMatcher;
//	private final IComparisonFactory comparisonFactory;

	public CBPMatchEngine(IEObjectMatcher eObjectMatcher, IResourceMatcher resourceMatcher,
			IComparisonFactory comparisonFactory) {
		super(eObjectMatcher, resourceMatcher, comparisonFactory);
//		this.resourceMatcher = super.getResourceMatcher();
//		this.comparisonFactory = comparisonFactory;
	}

	public CBPMatchEngine(IEObjectMatcher matcher, IComparisonFactory comparisonFactory) {
		super(matcher, comparisonFactory);
//		this.resourceMatcher = super.getResourceMatcher();
//		this.comparisonFactory = comparisonFactory;
	}

	@Override
	protected void match(Comparison comparison, IComparisonScope scope, ResourceSet left, ResourceSet right,
			ResourceSet origin, Monitor monitor) {
		super.match(comparison, scope, left, right, origin, monitor);
				
//		monitor.subTask(EMFCompareMessages.getString("DefaultMatchEngine.monitor.match.resourceSet")); //$NON-NLS-1$
////		
//		List<EObject> leftEObjectList = new ArrayList<>();
//		List<EObject> rightEObjectList = new ArrayList<>();
//		List<EObject> originEObjectList = new ArrayList<>();
//		
//		leftEObjectList.add(left.getResources().get(0).getContents().get(0));
//		leftEObjectList.add(left.getResources().get(0).getContents().get(1));
////		leftEObjectList.add(left.getResources().get(0).getContents().get(1).eContents().get(0));
//		leftEObjectList.add(left.getResources().get(0).getContents().get(1).eContents().get(1));
////		leftEObjectList.add(left.getResources().get(0).getContents().get(2));
//	
//		rightEObjectList.add(right.getResources().get(0).getContents().get(0));
//		rightEObjectList.add(right.getResources().get(0).getContents().get(1));
////		rightEObjectList.add(right.getResources().get(0).getContents().get(1).eContents().get(0));
//		rightEObjectList.add(right.getResources().get(0).getContents().get(3));
////		rightEObjectList.add(right.getResources().get(0).getContents().get(3));
//	
//		final Iterator<? extends EObject> leftEObjects = leftEObjectList.iterator();
//		final Iterator<? extends EObject> rightEObjects = rightEObjectList.iterator();
//		final Iterator<? extends EObject> originEObjects = originEObjectList.iterator();
//		
//		getEObjectMatcher().createMatches(comparison, leftEObjects, rightEObjects, originEObjects, monitor);
		
		
	}

}
