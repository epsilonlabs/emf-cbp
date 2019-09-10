package org.eclipse.epsilon.cbp.conflict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.epsilon.cbp.comparison.CBPFeatureType;
import org.eclipse.epsilon.cbp.comparison.CBPMatchFeature;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPDeleteEObjectEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEObject;
import org.eclipse.epsilon.cbp.comparison.event.CBPEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPRemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPUnsetEReferenceEvent;
import org.eclipse.epsilon.cbp.conflict.test.CBPChangeEventSortComparator;
import org.eclipse.epsilon.cbp.event.EReferenceEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;

public class CBPConflictDetector3 {

    @SuppressWarnings("rawtypes")
    public void computeConflicts(Map<String, CBPMatchObject> objects, List<CBPConflict> conflicts, Map<String, Set<CBPChangeEvent<?>>> leftCompositeEvents,
	    Map<String, Set<CBPChangeEvent<?>>> rightCompositeEvents) {

	Iterator<Entry<String, CBPMatchObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {

	    Entry<String, CBPMatchObject> objectEntry = iterator.next();
	    CBPMatchObject cTarget = objectEntry.getValue();

	    // System.out.println(cTarget.getId());

	    if (cTarget.getId().equals("O-2")) {
		// CBPMatchFeature x = cTarget.getFeatures().get("specific");
		System.console();
	    }

	    if (cTarget.getLeftIsDeleted() && cTarget.getRightIsDeleted()) {
		// do nothing
	    } else //
	    if (cTarget.getLeftIsDeleted() || cTarget.getRightIsDeleted()) {

		Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>();
		getAllEvents(leftEvents, cTarget, CBPSide.LEFT, new HashSet<CBPMatchFeature>(), new HashSet<CBPMatchObject>());
		Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>();
		getAllEvents(rightEvents, cTarget, CBPSide.RIGHT, new HashSet<CBPMatchFeature>(), new HashSet<CBPMatchObject>());

		// also add composite events
		addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
		addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

		if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
		    final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
		    if (conflict != null) {
			leftEvents.stream().forEach(e -> e.setConflict(conflict));
			rightEvents.stream().forEach(e -> e.setConflict(conflict));
			conflict.getLeftEvents().addAll(leftEvents);
			conflict.getRightEvents().addAll(rightEvents);
		    } else {
			CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
			if (cTarget.getLeftIsDeleted() || cTarget.getRightIsDeleted()) {
			    if (leftEvents.size() == 1 && rightEvents.size() == 1)
				conflict2.setPseudo(true);
			}
			conflicts.add(conflict2);
		    }
		}
	    }

	    // movement conflict
	    else if (cTarget.getLeftIsCreated() && cTarget.getRightIsCreated()) {
		// do nothing
	    } //
	    else if (!cTarget.getLeftIsCreated() && !cTarget.getLeftIsDeleted() && !cTarget.getLeftIsCreated() && !cTarget.getRightIsDeleted()) {

		if (cTarget.getLeftContainer() != null && cTarget.getRightContainer() != null) {
		    CBPMatchObject leftContainer = cTarget.getLeftContainer();
		    CBPMatchFeature leftContainingFeature = cTarget.getLeftContainingFeature();
		    CBPMatchObject rightContainer = cTarget.getRightContainer();
		    CBPMatchFeature rightContainingFeature = cTarget.getRightContainingFeature();
		    CBPMatchObject oldContainer = cTarget.getOldLeftContainer();
		    CBPMatchFeature oldContainingFeature = cTarget.getOldLeftContainingFeature();

		    if (leftContainer.equals(rightContainer) && leftContainingFeature.equals(rightContainingFeature)
			    || leftContainer.equals(oldContainer) && leftContainingFeature.equals(oldContainingFeature)
			    || rightContainer.equals(oldContainer) && rightContainingFeature.equals(oldContainingFeature)) {
			// do nothing
		    } else//
		    if ((!leftContainer.equals(oldContainer) || !leftContainingFeature.equals(oldContainingFeature))
			    || (!rightContainer.equals(oldContainer) || !rightContainingFeature.equals(oldContainingFeature))) {

			Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>();
			Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>();
			if (leftContainingFeature.getLeftObjectEvents().get(cTarget) != null) {
			    leftEvents = leftContainingFeature.getLeftObjectEvents().get(cTarget);
			}
			if (rightContainingFeature.getRightObjectEvents().get(cTarget) != null) {
			    rightEvents.addAll(rightContainingFeature.getRightObjectEvents().get(cTarget));
			}
			if (cTarget.getValueEvents(CBPSide.LEFT) != null)
			    leftEvents.addAll(cTarget.getValueEvents(CBPSide.LEFT));
			if (cTarget.getValueEvents(CBPSide.RIGHT) != null)
			    rightEvents.addAll(cTarget.getValueEvents(CBPSide.RIGHT));
			addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

			if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
			    final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
			    if (conflict != null) {
				leftEvents.stream().forEach(e -> e.setConflict(conflict));
				rightEvents.stream().forEach(e -> e.setConflict(conflict));
				conflict.getLeftEvents().addAll(leftEvents);
				conflict.getRightEvents().addAll(rightEvents);
			    } else {
				CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
				if ((leftContainer.equals(rightContainer) && leftContainingFeature.equals(rightContainingFeature))) {
				    if (leftEvents.size() == 1 && rightEvents.size() == 1)
					conflict2.setPseudo(true);
				}
				conflicts.add(conflict2);
			    }
			}
		    } else if (cTarget.getLeftPosition() != cTarget.getRightPosition() && cTarget.getLeftPosition() != cTarget.getOldLeftPosition()
			    && cTarget.getRightPosition() != cTarget.getOldLeftPosition()) {
			// do nothing
		    }
		}
	    }

	    for (Entry<String, CBPMatchFeature> featureEntry : cTarget.getFeatures().entrySet()) {
		CBPMatchFeature cFeature = featureEntry.getValue();
		if (cFeature.getLeftEvents().size() == 0 && cFeature.getRightEvents().size() == 0) {
		    continue;
		}

		// System.out.println(cTarget.getId() + " " +
		// cFeature.getName());
		// if (cTarget.getId().equals("O-5944") &&
		// cFeature.getName().equals("typeArguments")) {
		// System.console();
		// }
		// Set<CBPChangeEvent<?>> aaaa = null;
		// CBPMatchObject obj = objects.get("O-5944");
		// if (obj != null) {
		// CBPMatchFeature fea = obj.getFeatures().get("typeArguments");
		// if (fea != null) {
		// CBPMatchObject objVal = objects.get("O-5288");
		// if (objVal != null) {
		// aaaa = fea.getLeftObjectEvents().get(objVal);
		// if (aaaa != null && aaaa.size() > 1) {
		// System.console();
		// }
		// }
		// }
		// }

		// Set<Object> values = cFeature.getAllValues();

		if (cFeature.getFeatureType() == CBPFeatureType.REFERENCE && cFeature.isContainment()) {
		    if (cFeature.isMany()) {
			Set<Object> values = getAllUnequalValues(cFeature);
			for (Object value : values) {
			    CBPMatchObject cValue = (CBPMatchObject) value;
			    if (cValue != null && cValue.getLeftIsDeleted() && cValue.getRightIsDeleted()) {
				continue;
			    }
			    Set<CBPChangeEvent<?>> leftEvents = cFeature.getLeftObjectEvents(cValue);
			    Set<CBPChangeEvent<?>> rightEvents = cFeature.getRightObjectEvents(cValue);
			    addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			    addRelatedCompositeEvents(rightCompositeEvents, rightEvents);
			    // addDependentEvents(objects, leftEvents,
			    // leftCompositeEvents, CBPSide.LEFT);
			    // addDependentEvents(objects, rightEvents,
			    // rightCompositeEvents, CBPSide.RIGHT);

			    if (cValue.getLeftPosition() == cValue.getRightPosition() || cValue.getLeftPosition() == cValue.getOldLeftPosition()
				    || cValue.getRightPosition() == cValue.getOldRightPosition()) {
				// do nothing
			    } else //
			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				if (conflict != null) {
				    leftEvents.stream().forEach(e -> e.setConflict(conflict));
				    rightEvents.stream().forEach(e -> e.setConflict(conflict));
				    conflict.getLeftEvents().addAll(leftEvents);
				    conflict.getRightEvents().addAll(rightEvents);
				} else {
				    CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
				    if (cValue.getLeftPosition() == cValue.getRightPosition()) {
					if (leftEvents.size() == 1 && rightEvents.size() == 1)
					    conflict2.setPseudo(true);
				    }
				    conflicts.add(conflict2);
				}
			    }
			}
		    } else {
			CBPMatchObject originalValue = (CBPMatchObject) cFeature.getOldLeftValues().get(0);
			CBPMatchObject leftValue = (CBPMatchObject) cFeature.getLeftValues().get(0);
			CBPMatchObject rightValue = (CBPMatchObject) cFeature.getRightValues().get(0);
			if ((leftValue != null && !leftValue.equals(originalValue) || (originalValue != null && !originalValue.equals(leftValue)))
				|| (rightValue != null && !rightValue.equals(originalValue) || (originalValue != null && !originalValue.equals(rightValue)))) {
			    if (cFeature.getLeftEvents().size() > 0 && cFeature.getRightEvents().size() > 0) {

				Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<CBPChangeEvent<?>>(cFeature.getLeftEvents());
				Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<CBPChangeEvent<?>>(cFeature.getRightEvents());
				addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
				addRelatedCompositeEvents(rightCompositeEvents, rightEvents);
				// if (evensArePartOfDeletion(leftEvents,
				// rightEvents)) {
				// continue;
				// }
				// addDependentEvents(objects, leftEvents,
				// leftCompositeEvents, CBPSide.LEFT);
				// addDependentEvents(objects, rightEvents,
				// rightCompositeEvents, CBPSide.RIGHT);
				if (leftValue != null && (leftValue.equals(rightValue) || leftValue.equals(originalValue))
					|| rightValue != null && (rightValue.equals(leftValue) || rightValue.equals(originalValue))) {
				    // do nothing
				} else //
				if (leftEvents.size() > 0 && rightEvents.size() > 0) {
				    final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				    if (conflict != null) {
					leftEvents.stream().forEach(e -> e.setConflict(conflict));
					rightEvents.stream().forEach(e -> e.setConflict(conflict));
					conflict.getLeftEvents().addAll(leftEvents);
					conflict.getRightEvents().addAll(rightEvents);
				    } else {
					CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
					if ((leftValue != null && leftValue.equals(rightValue)) || (rightValue != null && rightValue.equals(leftValue)) || leftValue == rightValue) {
					    if (leftEvents.size() == 1 && rightEvents.size() == 1)
						conflict2.setPseudo(true);
					}
					conflicts.add(conflict2);
				    }
				}
			    }
			}
		    }
		} else if (cFeature.getFeatureType() == CBPFeatureType.REFERENCE && !cFeature.isContainment()) {
		    if (cFeature.isMany()) {
			Set<Object> values = getAllUnequalValues(cFeature);
			for (Object value : values) {
			    CBPMatchObject cValue = (CBPMatchObject) value;
			    Set<CBPChangeEvent<?>> leftEvents = cFeature.getLeftObjectEvents(cValue);
			    Set<CBPChangeEvent<?>> rightEvents = cFeature.getRightObjectEvents(cValue);

			    addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			    addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

			    if (cFeature.getLeftPosition(cValue) == cFeature.getRightPosition(cValue) || cFeature.getLeftPosition(cValue) == cFeature.getOriginalPosition(cValue)
				    || cFeature.getRightPosition(cValue) == cFeature.getOriginalPosition(cValue)) {
				// do nothing
			    } else //
			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				if (conflict != null) {
				    leftEvents.stream().forEach(e -> e.setConflict(conflict));
				    rightEvents.stream().forEach(e -> e.setConflict(conflict));
				    conflict.getLeftEvents().addAll(leftEvents);
				    conflict.getRightEvents().addAll(rightEvents);
				} else {
				    CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
				    if (cFeature.getLeftPosition(cValue) == cFeature.getRightPosition(cValue)) {
					if (leftEvents.size() == 1 && rightEvents.size() == 1)
					    conflict2.setPseudo(true);
				    }
				    conflicts.add(conflict2);
				}
			    }
			}
		    } else {
			CBPMatchObject originalValue = (CBPMatchObject) cFeature.getOldLeftValues().get(0);
			CBPMatchObject leftValue = (CBPMatchObject) cFeature.getLeftValues().get(0);
			CBPMatchObject rightValue = (CBPMatchObject) cFeature.getRightValues().get(0);

			if ((leftValue != null && !leftValue.equals(originalValue) || (originalValue != null && !originalValue.equals(leftValue)))
				|| (rightValue != null && !rightValue.equals(originalValue) || (originalValue != null && !originalValue.equals(rightValue)))) {

			    // Set<CBPChangeEvent<?>> leftEvents =
			    // cFeature.getLeftObjectEvents(leftValue);
			    // Set<CBPChangeEvent<?>> rightEvents =
			    // cFeature.getRightObjectEvents(rightValue);
			    Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>(cFeature.getLeftEvents());
			    Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>(cFeature.getRightEvents());

			    addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			    addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

			    if (leftValue != null && (leftValue.equals(rightValue) || leftValue.equals(originalValue))
				    || rightValue != null && (rightValue.equals(leftValue) || rightValue.equals(originalValue))) {
				// do nothing
			    } else //
			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				if (conflict != null) {
				    leftEvents.stream().forEach(e -> e.setConflict(conflict));
				    rightEvents.stream().forEach(e -> e.setConflict(conflict));
				    conflict.getLeftEvents().addAll(leftEvents);
				    conflict.getRightEvents().addAll(rightEvents);
				} else {
				    CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
				    if ((leftValue != null && leftValue.equals(rightValue)) || (rightValue != null && rightValue.equals(leftValue)) || leftValue == rightValue) {
					if (leftEvents.size() == 1 && rightEvents.size() == 1)
					    conflict2.setPseudo(true);
				    }
				    conflicts.add(conflict2);
				}
			    }
			}
		    }
		} else if (cFeature.getFeatureType() == CBPFeatureType.ATTRIBUTE) {
		    if (cFeature.isMany()) {
			Set<Object> values = getAllUnequalValues(cFeature);
			for (Object value : values) {
			    Set<CBPChangeEvent<?>> leftEvents = cFeature.getLeftObjectEvents(value);
			    Set<CBPChangeEvent<?>> rightEvents = cFeature.getRightObjectEvents(value);

			    addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			    addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

			    if (cFeature.getLeftPosition(value) == cFeature.getRightPosition(value) || cFeature.getLeftPosition(value) == cFeature.getOriginalPosition(value)
				    || cFeature.getRightPosition(value) == cFeature.getOriginalPosition(value)) {
				// do nothing
			    } else //
			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				if (conflict != null) {
				    leftEvents.stream().forEach(e -> e.setConflict(conflict));
				    rightEvents.stream().forEach(e -> e.setConflict(conflict));
				    conflict.getLeftEvents().addAll(leftEvents);
				    conflict.getRightEvents().addAll(rightEvents);
				} else {
				    CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
				    if (cFeature.getLeftPosition(value) == cFeature.getRightPosition(value)) {
					if (leftEvents.size() == 1 && rightEvents.size() == 1)
					    conflict2.setPseudo(true);
				    }
				    conflicts.add(conflict2);
				}
			    }
			}
		    } else {
			Object originalValue = cFeature.getOldLeftValues().get(0);
			Object leftValue = cFeature.getLeftValues().get(0);
			Object rightValue = cFeature.getRightValues().get(0);

			if ((leftValue != null && !leftValue.equals(originalValue) || (originalValue != null && !originalValue.equals(leftValue)))
				|| (rightValue != null && !rightValue.equals(originalValue) || (originalValue != null && !originalValue.equals(rightValue)))) {

			    Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>(cFeature.getLeftEvents());
			    Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>(cFeature.getRightEvents());

			    addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			    addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

			    if (leftValue != null && (leftValue.equals(rightValue) || leftValue.equals(originalValue))
				    || rightValue != null && (rightValue.equals(leftValue) || rightValue.equals(originalValue))) {
				// do nothing
			    } else //
			    if (leftEvents.size() > 0 && rightEvents.size() > 0) {
				final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				if (conflict != null) {
				    leftEvents.stream().forEach(e -> e.setConflict(conflict));
				    rightEvents.stream().forEach(e -> e.setConflict(conflict));
				    conflict.getLeftEvents().addAll(leftEvents);
				    conflict.getRightEvents().addAll(rightEvents);
				} else {
				    CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
				    if ((leftValue != null && leftValue.equals(rightValue)) || (rightValue != null && rightValue.equals(leftValue)) || leftValue == rightValue) {
					if (leftEvents.size() == 1 && rightEvents.size() == 1)
					    conflict2.setPseudo(true);
				    }
				    conflicts.add(conflict2);
				}
			    }
			}
		    }
		}

	    }

	}
    }

    private boolean evensArePartOfDeletion(Set<CBPChangeEvent<?>> leftEvents, Set<CBPChangeEvent<?>> rightEvents) {
	boolean result = false;
	boolean leftIsDeleted = false;
	boolean rightIsDeleted = false;
	for (CBPChangeEvent<?> event : leftEvents) {
	    if (event instanceof CBPDeleteEObjectEvent) {
		leftIsDeleted = true;
	    }
	}

	for (CBPChangeEvent<?> event : rightEvents) {
	    if (event instanceof CBPDeleteEObjectEvent) {
		rightIsDeleted = true;
	    }
	}
	result = leftIsDeleted & rightIsDeleted;
	return result;
    }

    /**
     * @param leftEvents
     * @param rightEvents
     * @return
     */
    private CBPConflict getExistingConflict(List<CBPConflict> conflicts, Set<CBPChangeEvent<?>> leftEvents, Set<CBPChangeEvent<?>> rightEvents) {
	CBPConflict conflict = null;
	Set<CBPConflict> leftConflicts = leftEvents.stream().filter(event -> event.getConflict() != null).map(event -> event.getConflict()).collect(Collectors.toSet());
	Set<CBPConflict> rightConflicts = rightEvents.stream().filter(event -> event.getConflict() != null).map(event -> event.getConflict()).collect(Collectors.toSet());

	if (leftConflicts != null && leftConflicts.size() > 0) {
	    conflict = leftConflicts.iterator().next();
	}

	if (conflict == null && rightConflicts != null && rightConflicts.size() > 0) {
	    conflict = rightConflicts.iterator().next();
	}

	if (conflict != null) {
	    if (leftConflicts != null && leftConflicts.size() > 0) {
		for (CBPConflict c : leftConflicts) {
		    if (conflict.equals(c)) {
			continue;
		    }
		    for (CBPChangeEvent<?> event : c.getLeftEvents()) {
			event.setConflict(conflict);
		    }
		    conflict.getLeftEvents().addAll(c.getLeftEvents());
		    c.getLeftEvents().clear();
		    for (CBPChangeEvent<?> event : c.getRightEvents()) {
			event.setConflict(conflict);
		    }
		    conflict.getRightEvents().addAll(c.getRightEvents());
		    c.getRightEvents().clear();
		    conflicts.remove(c);
		}
	    }
	    if (rightConflicts != null && rightConflicts.size() > 0) {
		for (CBPConflict c : rightConflicts) {
		    if (conflict.equals(c)) {
			continue;
		    }
		    for (CBPChangeEvent<?> event : c.getLeftEvents()) {
			event.setConflict(conflict);
		    }
		    conflict.getLeftEvents().addAll(c.getLeftEvents());
		    c.getLeftEvents().clear();
		    for (CBPChangeEvent<?> event : c.getRightEvents()) {
			event.setConflict(conflict);
		    }
		    conflict.getRightEvents().addAll(c.getRightEvents());
		    c.getRightEvents().clear();
		    conflicts.remove(c);

		}
	    }
	}

	return conflict;
    }

    /**
     * @param objects
     * @param events
     * @param compositeEventMap
     */
    private void addDependentEvents(Map<String, CBPMatchObject> objects, Set<CBPChangeEvent<?>> events, Map<String, Set<CBPChangeEvent<?>>> compositeEventMap, CBPSide side) {
	Set<CBPChangeEvent<?>> temp = new LinkedHashSet<>();
	if (events != null) {
	    for (CBPChangeEvent<?> evt : events) {
		// System.out.println(evt.toString());
		if (evt instanceof CBPEStructuralFeatureEvent) {
		    CBPMatchObject obj = objects.get(((CBPEStructuralFeatureEvent) evt).getTarget());
		    if (obj.getId().equals("O-4708")) {
			System.console();
		    }
		    CBPMatchFeature fea = obj.getFeatures().get(((CBPEStructuralFeatureEvent) evt).getEStructuralFeature());
		    if (fea.isMany()) {
			Object val = objects.get(((CBPEStructuralFeatureEvent) evt).getValue().toString());
			if (val != null) {
			    Set<CBPChangeEvent<?>> es = fea.getObjectEvents(side).get(val);
			    if (es != null && es.size() > 0) {
				addRelatedCompositeEvents(compositeEventMap, es);
				for (CBPChangeEvent<?> e : es) {
				    CBPEObject o = null;
				    if (e.getValue() instanceof CBPEObject) {
					o = (CBPEObject) e.getValue();
				    }
				    if (o != null) {
					CBPMatchObject o1 = objects.get(o.getId());
					Set<CBPChangeEvent<?>> list = ((CBPMatchObject) o1).getValueEvents(side);
					temp.addAll(list);
				    }
				}
				temp.addAll(es);
			    }
			}
		    } else {
			Set<CBPChangeEvent<?>> es = new LinkedHashSet<>(fea.getEvents(side));
			if (es != null && es.size() > 0) {
			    addRelatedCompositeEvents(compositeEventMap, es);
			    for (CBPChangeEvent<?> e : es) {
				CBPEObject o = null;
				if (e.getValue() instanceof CBPEObject) {
				    o = (CBPEObject) e.getValue();
				}
				if (o != null) {
				    CBPMatchObject o1 = objects.get(o.getId());
				    Set<CBPChangeEvent<?>> list = ((CBPMatchObject) o1).getValueEvents(side);
				    temp.addAll(list);
				}
			    }
			    temp.addAll(es);
			}
		    }
		}
	    }
	    events.addAll(temp);
	}
    }

    /**
     * @param compositeEventMap
     * @param events
     */
    private void addRelatedCompositeEvents(Map<String, Set<CBPChangeEvent<?>>> compositeEventMap, Set<CBPChangeEvent<?>> events) {
	if (events != null) {
	    Set<String> leftComposites = events.stream().map(x -> x.getComposite()).filter(x -> x != null).collect(Collectors.toSet());
	    if (compositeEventMap.size() > 0) {
		for (String composite : leftComposites) {
		    Set<CBPChangeEvent<?>> evts = compositeEventMap.get(composite);
		    events.addAll(evts);
		}
	    }
	}
    }

    private void getAllEvents(Set<CBPChangeEvent<?>> events, CBPMatchObject object, CBPSide side, HashSet<CBPMatchFeature> featureSet, HashSet<CBPMatchObject> objectSet) {
	objectSet.add(object);
	events.addAll(object.getEvents(side));
	events.addAll(object.getTargetEvents(side));
	events.addAll(object.getValueEvents(side));
	for (CBPMatchFeature feature : object.getFeatures().values()) {
	    events.addAll(feature.getEvents(side));
	    if (feature.getFeatureType() == CBPFeatureType.REFERENCE && feature.isContainment() && feature.getValues(side).size() > 0) {
		for (Object value : feature.getOldLeftValues().values()) {
		    if (value instanceof CBPMatchObject) {
			if (featureSet.contains(feature) && objectSet.contains(value)) {
			    continue;
			}
			CBPMatchObject cValue = (CBPMatchObject) value;
			getAllEvents(events, cValue, side, featureSet, objectSet);
		    }
		}
		for (Object value : feature.getValues(side).values()) {
		    if (value instanceof CBPMatchObject) {
			if (featureSet.contains(feature) && objectSet.contains(value)) {
			    continue;
			}
			CBPMatchObject cValue = (CBPMatchObject) value;
			getAllEvents(events, cValue, side, featureSet, objectSet);
		    }
		}
		featureSet.add(feature);
	    }
	    // if (feature.getFeatureType() == CBPFeatureType.REFERENCE &&
	    // !feature.isContainment() && feature.getValues(side).size() > 0) {
	    // for (Object value : feature.getValues(side).values()) {
	    // if (value instanceof CBPMatchObject) {
	    // if (featureSet.contains(feature) && objectSet.contains(value)) {
	    // continue;
	    // }
	    // CBPMatchObject cValue = (CBPMatchObject) value;
	    // Set<CBPChangeEvent<?>> eventSet =
	    // feature.getObjectEvents(side).get(cValue);
	    // if (eventSet != null) {
	    // events.addAll(eventSet);
	    // }
	    // }
	    // }
	    // featureSet.add(feature);
	    // }
	    // if (feature.getFeatureType() == CBPFeatureType.ATTRIBUTE &&
	    // feature.getValues(side).size() > 0) {
	    // for (Object value : feature.getValues(side).values()) {
	    // if (featureSet.contains(feature) && objectSet.contains(value)) {
	    // continue;
	    // }
	    // Set<CBPChangeEvent<?>> eventSet =
	    // feature.getObjectEvents(side).get(value);
	    // if (eventSet != null) {
	    // events.addAll(eventSet);
	    // }
	    // }
	    // featureSet.add(feature);
	    // }

	}
    }

    public Set<Object> getAllUnequalValues(CBPMatchFeature cFeature) {
	Set<Object> values = new LinkedHashSet<>();
	Map<Integer, Object> oldValues = cFeature.getOldLeftValues();
	Map<Integer, Object> leftValues = cFeature.getLeftValues();
	Map<Integer, Object> rightValues = cFeature.getRightValues();

	if (cFeature.isOrdered()) {
	    for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
		int pos = valueEntry.getKey();
		Object leftValue = valueEntry.getValue();
		Object oldValue = oldValues.get(pos);
		if (leftValue == oldValue || (leftValue != null && leftValue.equals(oldValue)) || (oldValue != null && oldValue.equals(leftValue))) {
		    continue;
		} else {
		    if (leftValue != null) {
			values.add(leftValue);
		    }
		    if (oldValue != null) {
			values.add(oldValue);
		    }
		}
	    }
	    for (Entry<Integer, Object> valueEntry : rightValues.entrySet()) {
		int pos = valueEntry.getKey();
		Object rightValue = valueEntry.getValue();
		Object oldValue = oldValues.get(pos);
		if (rightValue == oldValue || (rightValue != null && rightValue.equals(oldValue)) || (oldValue != null && oldValue.equals(rightValue))) {
		    continue;
		} else {
		    if (rightValue != null) {
			values.add(rightValue);
		    }
		    if (oldValue != null) {
			values.add(oldValue);
		    }
		}
	    }
	} else {
	    for (Object leftValue : leftValues.values()) {
		if (!oldValues.values().contains(leftValue)) {
		    values.add(leftValue);
		}
	    }
	    for (Object oldValue : oldValues.values()) {
		if (!leftValues.values().contains(oldValue)) {
		    values.add(oldValue);
		}
	    }
	    for (Object rightValue : rightValues.values()) {
		if (!oldValues.values().contains(rightValue)) {
		    values.add(rightValue);
		}
	    }
	    for (Object oldValue : oldValues.values()) {
		if (!rightValues.values().contains(oldValue)) {
		    values.add(oldValue);
		}
	    }
	}
	return values;
    }
}
