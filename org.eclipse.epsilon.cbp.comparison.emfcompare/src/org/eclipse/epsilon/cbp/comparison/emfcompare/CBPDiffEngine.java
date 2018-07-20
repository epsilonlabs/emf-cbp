package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.ComparisonCanceledException;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.MatchResource;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.EObjectEventTracker;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.EObjectEvent;
import org.eclipse.epsilon.cbp.event.MultiValueEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.ResourceEvent;
import org.eclipse.epsilon.cbp.event.SingleValueEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SingleValueEReferenceEvent;

public class CBPDiffEngine extends DefaultDiffEngine {

	public CBPDiffEngine() {
		super(new DiffBuilder());
	}

	public CBPDiffEngine(IDiffProcessor iDiffProcessor) {
		super(new DiffBuilder());
	}

	@Override
	public void diff(Comparison comparison, Monitor monitor) {
		for (Match match : comparison.getMatches()) {
			Object left = match.getLeft();
			Object right = match.getRight();
			String leftName = null;
			String rightName = null;
			if (left != null)
				leftName = ((Node) match.getLeft()).getName();
			if (right != null)
				rightName = ((Node) match.getRight()).getName();
			System.out.println(leftName + " vs " + rightName);
		}
		// super.diff(comparison, monitor);
		this.cbpDiff(comparison, monitor);
	}

	private void cbpDiff(Comparison comparison, Monitor monitor) {
		Resource left = null;
		Resource right = null;
		left = comparison.getMatchedResources().get(0).getLeft();
		right = (comparison.getMatchedResources().get(0).getRight() != null)
				? comparison.getMatchedResources().get(0).getRight()
				: comparison.getMatchedResources().get(1).getRight();

		String leftPath = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(left.getURI().toPlatformString(true))).getRawLocation().toOSString();
		leftPath = leftPath.replaceAll(".xmi", ".cbpxml");
		String rightPath = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(right.getURI().toPlatformString(true))).getRawLocation().toOSString();
		rightPath = rightPath.replaceAll(".xmi", ".cbpxml");

		try {
			CBPComparison cbpComparison = new CBPComparison(new File(leftPath), new File(rightPath));
			cbpComparison.compare();
			List<ComparisonEvent> leftComparisonEvents = cbpComparison.getLeftComparisonEvents();
			List<ComparisonEvent> rightComparisonEvents = cbpComparison.getRightComparisonEvents();

			EObjectEventTracker leftTracker = createEObjectEventTracker(left, leftComparisonEvents);
			EObjectEventTracker rightTracker = createEObjectEventTracker(right, rightComparisonEvents);

			computeDifferences(comparison, left, leftTracker, DifferenceSource.LEFT, monitor);
			computeDifferences(comparison, right, rightTracker, DifferenceSource.RIGHT, monitor);

		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		System.out.println();
	}

	/**
	 * @param comparison
	 * @param left
	 * @param leftTracker
	 */
	private void computeDifferences(Comparison comparison, Resource left, EObjectEventTracker leftTracker,
			DifferenceSource source, Monitor monitor) {

		for (EObject eObject : leftTracker.getTrackedEObjects()) {

			Match match = comparison.getMatch(eObject);

			Map<Class<?>, List<ComparisonEvent>> eObjectEvents = leftTracker.getEObjectEvents(eObject);
			for (Entry<Class<?>, List<ComparisonEvent>> entry : eObjectEvents.entrySet()) {
				Class<?> eventType = entry.getKey();
				List<ComparisonEvent> eventList = entry.getValue();

				for (ComparisonEvent comparisonEvent : eventList) {
					// if (comparisonEvent.getFeatureName() != null) {
					// EStructuralFeature feature = eObject.eClass()
					// .getEStructuralFeature(comparisonEvent.getFeatureName());
					// checkForDifferences(match, monitor, feature);
					// }

					checkForDifferences(left, source, eObject, match, eventType, comparisonEvent, monitor);
				}

			}

		}
	}

	/**
	 * @param left
	 * @param source
	 * @param eObject
	 * @param match
	 * @param eventType
	 * @param comparisonEvent
	 */
	protected void checkForDifferences(Resource left, DifferenceSource source, EObject eObject, Match match,
			Class<?> eventType, ComparisonEvent comparisonEvent, Monitor monitor) {

		if (monitor.isCanceled()) {
			throw new ComparisonCanceledException();
		}
//		checkResourceAttachment(match, monitor);

		if (eventType == SingleValueEAttributeEvent.class) {

			EAttribute attribute = (EAttribute) eObject.eClass()
					.getEStructuralFeature(comparisonEvent.getFeatureName());
			Object value = comparisonEvent.getValue();
			getDiffProcessor().attributeChange(match, attribute, value, DifferenceKind.CHANGE, source);

		} else if (eventType == SingleValueEReferenceEvent.class) {

			EReference attribute = (EReference) eObject.eClass()
					.getEStructuralFeature(comparisonEvent.getFeatureName());
			EObject value = ((XMIResource) left).getEObject(comparisonEvent.getValueId());
			getDiffProcessor().referenceChange(match, attribute, value, DifferenceKind.CHANGE, source);

		} else if (eventType == MultiValueEReferenceEvent.class) {

			EReference reference = (EReference) eObject.eClass()
					.getEStructuralFeature(comparisonEvent.getFeatureName());
			EObject value = ((XMIResource) left).getEObject(comparisonEvent.getValueId());
			DifferenceKind kind = null;

			if (comparisonEvent.getEventType() == AddToEReferenceEvent.class) {
				kind = DifferenceKind.ADD;
			} else if (comparisonEvent.getEventType() == RemoveFromEReferenceEvent.class) {
				kind = DifferenceKind.DELETE;
			}
			getDiffProcessor().referenceChange(match, reference, value, kind, source);

		} else if (eventType == ResourceEvent.class) {

			DifferenceKind kind = null;

			if (comparisonEvent.getEventType() == AddToResourceEvent.class) {
				kind = DifferenceKind.ADD;
			} else if (comparisonEvent.getEventType() == RemoveFromResourceEvent.class) {
				kind = DifferenceKind.DELETE;
			}

			getDiffProcessor().resourceAttachmentChange(match, left.getURI().toString(), kind, source);

		}

		// for (Match submatch : match.getSubmatches()) {
		// checkForDifferences(left, source, eObject, submatch, eventType,
		// comparisonEvent, monitor);
		// }
	}

	protected void checkForDifferences(Match match, Monitor monitor, EStructuralFeature feature) {
		if (monitor.isCanceled()) {
			throw new ComparisonCanceledException();
		}
		checkResourceAttachment(match, monitor);

		final FeatureFilter featureFilter = createFeatureFilter();

		if (feature instanceof EReference) {
			final boolean considerOrdering = featureFilter.checkForOrderingChanges((EReference) feature);
			computeDifferences(match, (EReference) feature, considerOrdering);
		} else if (feature instanceof EAttribute) {
			final boolean considerOrdering = featureFilter.checkForOrderingChanges((EAttribute) feature);
			computeDifferences(match, (EAttribute) feature, considerOrdering);
		}

		for (Match submatch : match.getSubmatches()) {
			checkForDifferences(submatch, monitor);
		}
	}

	private EObjectEventTracker createEObjectEventTracker(final Resource resource,
			final List<ComparisonEvent> comparisonEvents) {
		EObjectEventTracker tracker = new EObjectEventTracker();

		if (resource instanceof XMIResourceImpl) {
			for (ComparisonEvent comparisonEvent : comparisonEvents) {
				String targetId = comparisonEvent.getTargetId();
				if (targetId != null) {
					EObject eObject = ((XMIResourceImpl) resource).getEObject(targetId);
					tracker.addComparisonEvent(eObject, comparisonEvent);
				}
				if (comparisonEvent.getEventType().getSuperclass() == ResourceEvent.class
						|| comparisonEvent.getEventType().getSuperclass() == EObjectEvent.class) {
					String valueId = comparisonEvent.getValueId();
					if (valueId != null) {
						EObject eObject = ((XMIResourceImpl) resource).getEObject(valueId);
						tracker.addComparisonEvent(eObject, comparisonEvent);
					}
				}
			}
		}
		return tracker;
	}
}
