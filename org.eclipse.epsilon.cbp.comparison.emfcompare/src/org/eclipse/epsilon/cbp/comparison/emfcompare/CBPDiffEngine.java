package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.ComparisonCanceledException;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.Match;
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
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.MultiValueEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.ResourceEvent;
import org.eclipse.epsilon.cbp.event.SingleValueEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SingleValueEReferenceEvent;

import com.google.common.collect.Lists;

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
		// EList<Diff> x = comparison.getDifferences();
		// System.out.println();
	}

	/**
	 * @param comparison
	 */
	protected void printMatches(List<Match> matches, int level) {
		for (Match match : matches) {
			Object left = match.getLeft();
			Object right = match.getRight();
			String leftName = null;
			String rightName = null;
			if (left != null)
				leftName = ((Node) match.getLeft()).getName();
			if (right != null)
				rightName = ((Node) match.getRight()).getName();
			for (int i = 0; i < level; i++) {
				System.out.print("+--");
			}
			System.out.println(leftName + " vs " + rightName);
			printMatches(match.getSubmatches(), level + 1);
		}
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

			CBPObjectEventTracker rightTracker = createRightCBPEObjectEventTracker(right, rightComparisonEvents);
			CBPObjectEventTracker leftTracker = createLeftCBPEObjectEventTracker(left, leftComparisonEvents);

			computeDifferences(comparison, left, right, leftTracker, rightTracker, DifferenceSource.LEFT, monitor);

			// EObjectEventTracker leftTracker = createEObjectEventTracker(left,
			// leftComparisonEvents);
			// EObjectEventTracker rightTracker =
			// createEObjectEventTracker(right, rightComparisonEvents);
			//
			// computeDifferences(comparison, left, leftTracker,
			// DifferenceSource.LEFT, monitor);
			// computeDifferences(comparison, right, rightTracker,
			// DifferenceSource.RIGHT, monitor);

		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		System.out.println();
	}

	private void computeDifferences(Comparison comparison, Resource left, Resource right,
			CBPObjectEventTracker leftTracker, CBPObjectEventTracker rightTracker, DifferenceSource source,
			Monitor monitor) throws ParserConfigurationException, TransformerException {

		// process left tracker and intersection between left and right trackers
		MapIterator<MultiKey<? extends Object>, List<ComparisonEvent>> leftIterator = leftTracker.mapIterator();
		while (leftIterator.hasNext()) {
			MultiKey<? extends Object> multiKey = leftIterator.next();
			Object[] keys = multiKey.getKeys();

			String targetId = (keys.length > 1) ? (String) keys[1] : null;
			String featureName = (keys.length > 2) ? (String) keys[2] : null;
			Object value = (keys.length > 3) ? keys[3] : null;

			EObject target = left.getEObject(targetId);
			EStructuralFeature feature = target.eClass().getEStructuralFeature(featureName);
			if (feature instanceof EReference && value != null) {
				EObject object = left.getEObject(value.toString());
				if (object == null) {
					value = null;
				}
			}

			List<ComparisonEvent> leftComparisonEvents = null;
			List<ComparisonEvent> rightComparisonEvents = null;

			// get comparison events from left side and possibly also from right
			// side (if events are also exists on right side)
			if (feature == null && value == null) {
				leftComparisonEvents = leftTracker.get(targetId);
				if (rightTracker.containsKey(targetId)) {
					rightComparisonEvents = rightTracker.get(targetId);

					// if (rightComparisonEvents.size() > 0) {
					// rightComparisonEvents =
					// reverseComparisonEvents(rightComparisonEvents);
					// }

					// remove the keys from the right tracker to reduce the
					// right tracker only contains objects that do not exist
					// on the left side, so that we can easily process them
					// later
					rightTracker.remove(targetId);

				}
			} else if (feature != null && value != null) {
				leftComparisonEvents = leftTracker.get(targetId, featureName, value);
				if (rightTracker.containsKey(targetId, featureName)) {
					rightComparisonEvents = rightTracker.get(targetId, featureName);

					// if (rightComparisonEvents.size() > 0) {
					// rightComparisonEvents =
					// reverseComparisonEvents(rightComparisonEvents);
					// }

					// remove the keys from the right tracker to reduce the
					// right tracker only contains objects that do not exist
					// on the left side, so that we can easily process them
					// later
					rightTracker.removeMultiKey(targetId, featureName, value);
					rightTracker.removeMultiKey(targetId, featureName);
				}
			}

			// handle object events (Create, Delete, AddToResource,
			// RemoveFromResource)
			if (target != null && feature == null & value == null) {
				checkObjectResourceDifferences(comparison, left, source, target, leftComparisonEvents,
						rightComparisonEvents);
			}
			// handle single value feature
			else if (feature != null && feature.isMany() == false) {
				checkSingleValueFeatureDifferences(comparison, source, value, target, feature, leftComparisonEvents,
						rightComparisonEvents);
			}
			// handle multi value feature
			else if (feature != null && feature.isMany() == true) {
				checkMultiValueFeatureDifferences(comparison, source, value, target, feature, leftComparisonEvents,
						rightComparisonEvents, left, right);
			}

		}
		leftTracker.clear();

		// process only right tracker
		MapIterator<MultiKey<? extends Object>, List<ComparisonEvent>> rightIterator = rightTracker.mapIterator();
		while (rightIterator.hasNext()) {
			MultiKey<? extends Object> multiKey = rightIterator.next();
			Object[] keys = multiKey.getKeys();

			String targetId = (keys.length > 1) ? (String) keys[1] : null;
			String featureName = (keys.length > 2) ? (String) keys[2] : null;
			Object value = (keys.length > 3) ? keys[3] : null;

			System.out.println(targetId + ", " + featureName + ", " + value);

			EObject target = right.getEObject(targetId);
			EStructuralFeature feature = target.eClass().getEStructuralFeature(featureName);
			if (feature instanceof EReference && value != null) {
				EObject object = right.getEObject(value.toString());
				if (object == null) {
					value = null;
				}
			}

			List<ComparisonEvent> rightComparisonEvents = null;
			List<ComparisonEvent> leftComparisonEvents = new ArrayList<>();

			// get comparison events from left side and possibly also from right
			// side (if events are also exists on right side)
			if (feature == null && value == null) {
				rightComparisonEvents = rightTracker.get(targetId);
			} else if (feature != null && value != null) {
				rightComparisonEvents = rightTracker.get(targetId, featureName, value);
			}

			// handle object events (Create, Delete, AddToResource,
			// RemoveFromResource)
			if (target != null && feature == null & value == null) {
				checkObjectResourceDifferences(comparison, right, source, target, rightComparisonEvents,
						leftComparisonEvents);
			}
			// handle single value feature
			else if (feature != null && feature.isMany() == false) {
				checkSingleValueFeatureDifferences(comparison, source, value, target, feature, rightComparisonEvents,
						leftComparisonEvents);
			}
			// handle multi value feature
			else if (feature != null && feature.isMany() == true) {
				checkMultiValueFeatureDifferences(comparison, source, value, target, feature, rightComparisonEvents,
						leftComparisonEvents, right, left);
			}
		}
		rightTracker.clear();
	}

	/**
	 * @param comparison
	 * @param resource
	 * @param source
	 * @param target
	 * @param leftComparisonEvents
	 * @param rightComparisonEvents
	 */
	protected void checkObjectResourceDifferences(Comparison comparison, Resource resource, DifferenceSource source,
			EObject target, List<ComparisonEvent> leftComparisonEvents, List<ComparisonEvent> rightComparisonEvents) {
		Match match = comparison.getMatch(target);
		String uri = resource.getURI().toString();
		if (leftComparisonEvents != null && leftComparisonEvents.size() > 0) {
			if (rightComparisonEvents != null && rightComparisonEvents.size() > 0) {
				for (ComparisonEvent comparisonEvent : rightComparisonEvents) {
					if (comparisonEvent.getEventType() == CreateEObjectEvent.class) {
						getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.ADD, source);
					} else if (comparisonEvent.getEventType() == DeleteEObjectEvent.class) {
						getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.DELETE, source);
					}
				}
			}
			for (ComparisonEvent comparisonEvent : leftComparisonEvents) {
				if (comparisonEvent.getEventType() == CreateEObjectEvent.class) {
					getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.ADD, source);
				} else if (comparisonEvent.getEventType() == DeleteEObjectEvent.class) {
					getDiffProcessor().resourceAttachmentChange(match, uri, DifferenceKind.DELETE, source);
				}
			}
		}
	}

	/**
	 * @param comparison
	 * @param source
	 * @param value
	 * @param target
	 * @param feature
	 * @param leftComparisonEvents
	 * @param rightComparisonEvents
	 */
	protected void checkMultiValueFeatureDifferences(Comparison comparison, DifferenceSource source, Object value,
			EObject target, EStructuralFeature feature, List<ComparisonEvent> leftComparisonEvents,
			List<ComparisonEvent> rightComparisonEvents, Resource left, Resource right) {
		if (leftComparisonEvents != null && leftComparisonEvents.size() > 0) {
			Match match = comparison.getMatch(target);
			if (feature instanceof EAttribute) {
				checkMultiValueAttributeDifferences(source, value, feature, leftComparisonEvents, rightComparisonEvents,
						match);
			} else if (feature instanceof EReference) {

				checkMultiValueReferenceDifferences(source, (String) value, feature, leftComparisonEvents,
						rightComparisonEvents, match, left, right);
			}
		}
	}

	/**
	 * @param source
	 * @param value
	 * @param feature
	 * @param leftComparisonEvents
	 * @param match
	 */
	protected void checkMultiValueReferenceDifferences(DifferenceSource source, String valueId,
			EStructuralFeature feature, List<ComparisonEvent> leftComparisonEvents,
			List<ComparisonEvent> rightComparisonEvents, Match match, Resource left, Resource right) {

		if (rightComparisonEvents != null && rightComparisonEvents.size() > 0) {
			for (ComparisonEvent comparisonEvent : rightComparisonEvents) {
				EObject value = right.getEObject(comparisonEvent.getValueId());
				if (comparisonEvent.getEventType() == AddToEReferenceEvent.class) {
					getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value, DifferenceKind.ADD,
							source);
				}
				if (comparisonEvent.getEventType() == RemoveFromEReferenceEvent.class) {
					getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value,
							DifferenceKind.DELETE, DifferenceSource.LEFT);
				}
				if (comparisonEvent.getEventType() == MoveWithinEReferenceEvent.class) {
					getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value,
							DifferenceKind.MOVE, source);
				}
			}
		}
		if (leftComparisonEvents != null && leftComparisonEvents.size() > 0) {
			EObject value = left.getEObject(valueId);
			for (ComparisonEvent comparisonEvent : leftComparisonEvents) {
				if (comparisonEvent.getEventType() == AddToEReferenceEvent.class) {
					getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value, DifferenceKind.ADD,
							source);
				}
				if (comparisonEvent.getEventType() == RemoveFromEReferenceEvent.class) {
					getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value,
							DifferenceKind.DELETE, source);
				}
				if (comparisonEvent.getEventType() == MoveWithinEReferenceEvent.class) {
					getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value,
							DifferenceKind.MOVE, source);
				}
			}
		}
	}

	/**
	 * @param source
	 * @param value
	 * @param feature
	 * @param leftComparisonEvents
	 * @param rightComparisonEvents
	 * @param match
	 */
	protected void checkMultiValueAttributeDifferences(DifferenceSource source, Object value,
			EStructuralFeature feature, List<ComparisonEvent> leftComparisonEvents,
			List<ComparisonEvent> rightComparisonEvents, Match match) {

		if (rightComparisonEvents != null && rightComparisonEvents.size() > 0) {
			for (ComparisonEvent comparisonEvent : rightComparisonEvents) {
				if (comparisonEvent.getEventType() == AddToEAttributeEvent.class) {
					getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.ADD, source);
				}
				if (comparisonEvent.getEventType() == RemoveFromEAttributeEvent.class) {
					getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.DELETE,
							source);
				}
				if (comparisonEvent.getEventType() == MoveWithinEAttributeEvent.class) {
					getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.MOVE, source);
				}
			}
		}
		if (leftComparisonEvents != null && leftComparisonEvents.size() > 0) {
			for (ComparisonEvent comparisonEvent : leftComparisonEvents) {
				if (comparisonEvent.getEventType() == AddToEAttributeEvent.class) {
					getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.ADD, source);
				}
				if (comparisonEvent.getEventType() == RemoveFromEAttributeEvent.class) {
					getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.DELETE,
							source);
				}
				if (comparisonEvent.getEventType() == MoveWithinEAttributeEvent.class) {
					getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.MOVE, source);
				}
			}
		}
	}

	/**
	 * @param comparison
	 * @param source
	 * @param value
	 * @param target
	 * @param feature
	 * @param leftComparisonEvents
	 * @param rightComparisonEvents
	 */
	protected void checkSingleValueFeatureDifferences(Comparison comparison, DifferenceSource source, Object value,
			EObject target, EStructuralFeature feature, List<ComparisonEvent> leftComparisonEvents,
			List<ComparisonEvent> rightComparisonEvents) {

		if (leftComparisonEvents != null && leftComparisonEvents.size() > 0) {
			Match match = comparison.getMatch(target);
			if (feature instanceof EAttribute) {
				getDiffProcessor().attributeChange(match, (EAttribute) feature, value, DifferenceKind.CHANGE, source);
			} else if (feature instanceof EReference) {
				getDiffProcessor().referenceChange(match, (EReference) feature, (EObject) value, DifferenceKind.CHANGE,
						source);
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
		checkResourceAttachment(match, monitor);

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

	private CBPObjectEventTracker createLeftCBPEObjectEventTracker(final Resource resource,
			final List<ComparisonEvent> comparisonEvents) throws ParserConfigurationException, TransformerException {
		return this.createCBPEObjectEventTracker(resource, comparisonEvents, false);
	}

	private CBPObjectEventTracker createRightCBPEObjectEventTracker(final Resource resource,
			List<ComparisonEvent> comparisonEvents) throws ParserConfigurationException, TransformerException {
		return this.createCBPEObjectEventTracker(resource, comparisonEvents, true);
	}

	private CBPObjectEventTracker createCBPEObjectEventTracker(final Resource resource,
			List<ComparisonEvent> comparisonEvents, boolean isRight)
			throws ParserConfigurationException, TransformerException {

		CBPObjectEventTracker tracker = new CBPObjectEventTracker();

		// reverse if the comparison events come from the right side
		if (isRight) {
			comparisonEvents = this.reverseComparisonEvents(comparisonEvents);
		}

		// handle the right comparison events first
		for (ComparisonEvent event : comparisonEvents) {
			EObject target = null;
			EStructuralFeature feature = null;
			Object value = null;

			// handle target
			if (/*
				 * event.getTargetId() == null && event.getFeatureName() == null
				 * &&
				 */ event.getValueId() != null) {

				// get the value object using the value id and set the object as
				// the target
				// this is for Create, Delete, AddToResource, RemoveFromResource
				target = resource.getEObject(event.getValueId());
				if (target != null) {
					if (tracker.get(target) == null) {
						tracker.put(event.getValueId(), new ArrayList<ComparisonEvent>());
						List<ComparisonEvent> list = tracker.get(event.getValueId());
						list.add(event);
					} else {
						List<ComparisonEvent> list = tracker.get(event.getValueId());
						list.add(event);
					}
				}

			}
			if (event.getTargetId() != null && event.getFeatureName() != null
					&& (event.getValueId() != null || event.getValue() != null)) {

				// this time get the target object from the target id, not value
				// id as the previous condition does
				target = resource.getEObject(event.getTargetId());
				if (target != null) {

					if (event.getValueId() != null) {
						value = event.getValueId();
					} else if (event.getValue() != null) {
						value = event.getValue();
					}

					// there is a possibility the value is still null since the
					// object is already deleted in the eventual state
					if (value != null) {

						List<ComparisonEvent> targetFeatureValueList = null;
						List<ComparisonEvent> targetFeaturelist = null;

						//
						if (tracker.get(event.getTargetId(), event.getFeatureName(), value) == null) {
							tracker.put(event.getTargetId(), event.getFeatureName(), value,
									new ArrayList<ComparisonEvent>());
							targetFeatureValueList = tracker.get(event.getTargetId(), event.getFeatureName(), value);
							targetFeatureValueList.add(event);
						} else {
							targetFeatureValueList = tracker.get(event.getTargetId(), event.getFeatureName(), value);
							targetFeatureValueList.add(event);
						}

						// also accumulate all the events into the same feature,
						// regardless of its values
						if (tracker.get(event.getTargetId(), event.getFeatureName()) == null) {
							tracker.put(event.getTargetId(), event.getFeatureName(), new ArrayList<ComparisonEvent>());
							targetFeaturelist = tracker.get(event.getTargetId(), event.getFeatureName());
							targetFeaturelist.add(event);
						} else {
							targetFeaturelist = tracker.get(event.getTargetId(), event.getFeatureName());
							targetFeaturelist.add(event);
						}

						System.out.println(event.getEventString());

						// if (event.getValueId().equals("4") &&
						// event.getTargetId().equals("0")) {
						// System.out.println();
						// }

						// clear targetFeatureValueList and targetFeatureList
						// from superseded events
						if (event.getEventType() == RemoveFromEReferenceEvent.class) {
							targetFeaturelist.removeAll(targetFeatureValueList);
							targetFeatureValueList.clear();
							// targetFeaturelist.add(event);
							// targetFeatureValueList.add(event);
						} else if (event.getEventType() == AddToEReferenceEvent.class) {
							targetFeaturelist.removeAll(targetFeatureValueList);
							targetFeatureValueList.clear();
							targetFeaturelist.add(event);
							targetFeatureValueList.add(event);
						}
					}
				}

			}

		}

		return tracker;
	}

	private List<ComparisonEvent> reverseComparisonEvents(List<ComparisonEvent> comparisonEvents)
			throws ParserConfigurationException, TransformerException {
		comparisonEvents = Lists.reverse(comparisonEvents);
		for (int i = 0; i < comparisonEvents.size(); i++) {
			comparisonEvents.set(i, comparisonEvents.get(i).reverse());
		}
		return comparisonEvents;
	}
}
