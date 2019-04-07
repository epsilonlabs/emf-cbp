package org.eclipse.epsilon.cbp.conflict;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.epsilon.cbp.comparison.CBPFeatureType;
import org.eclipse.epsilon.cbp.comparison.CBPMatchFeature;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEAttributeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.event.EReferenceEvent;

public class CBPConflictDetector {

    @SuppressWarnings("rawtypes")
    public void computeConflicts(Map<String, CBPMatchObject> objects, List<CBPConflict> conflicts, Map<String, Set<CBPChangeEvent<?>>> leftCompositeEvents,
	    Map<String, Set<CBPChangeEvent<?>>> rightCompositeEvents) {

	Iterator<Entry<String, CBPMatchObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPMatchObject> objectEntry = iterator.next();
	    CBPMatchObject cTarget = objectEntry.getValue();

	    if (cTarget.getId().equals("O-3")) {
		// CBPMatchFeature x = cTarget.getFeatures().get("specific");
		System.console();
	    }

	    if (cTarget.getLeftIsDeleted() && cTarget.getRightIsDeleted()) {
		continue;
	    } else if (cTarget.getLeftIsDeleted()) {

		Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>();
		getAllEvents(leftEvents, cTarget, CBPSide.LEFT);

		Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>();
		getAllEvents(rightEvents, cTarget, CBPSide.RIGHT);

		// also add composite events
		addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
		addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

		if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
		    CBPConflict conflict = new CBPConflict(leftEvents, rightEvents);
		    conflicts.add(conflict);
		}
	    } else if (cTarget.getRightIsDeleted()) {
		Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>();
		getAllEvents(leftEvents, cTarget, CBPSide.LEFT);
		Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>();
		getAllEvents(rightEvents, cTarget, CBPSide.RIGHT);

		// also add composite events
		addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
		addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

		if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
		    CBPConflict conflict = new CBPConflict(leftEvents, rightEvents);
		    conflicts.add(conflict);
		}
	    }

	    // movement conflict
	    else if (!cTarget.getLeftIsCreated() && !cTarget.getLeftIsDeleted() && !cTarget.getLeftIsCreated() && !cTarget.getRightIsDeleted()) {

		if (cTarget.getLeftContainer() != null && cTarget.getRightContainer() != null) {
		    CBPMatchObject leftContainer = cTarget.getLeftContainer();
		    CBPMatchFeature leftContainingFeature = cTarget.getLeftContainingFeature();
		    CBPMatchObject rightContainer = cTarget.getRightContainer();
		    CBPMatchFeature rightContainingFeature = cTarget.getRightContainingFeature();
		    CBPMatchObject oldContainer = cTarget.getOldLeftContainer();
		    CBPMatchFeature oldContainingFeature = cTarget.getOldLeftContainingFeature();

		    if ((!leftContainer.equals(rightContainer) && !leftContainer.equals(oldContainer) && !rightContainer.equals(oldContainer))
			    || (!leftContainingFeature.equals(rightContainingFeature) && !leftContainingFeature.equals(oldContainingFeature) && !rightContainingFeature.equals(oldContainingFeature))) {

			Set<CBPChangeEvent<?>> leftEvents = leftContainingFeature.getLeftObjectEvents().get(cTarget);
			Set<CBPChangeEvent<?>> rightEvents = rightContainingFeature.getRightObjectEvents().get(cTarget);
			addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			addRelatedCompositeEvents(rightCompositeEvents, rightEvents);
			addDependentEvents(objects, leftEvents, leftCompositeEvents, CBPSide.LEFT);
			addDependentEvents(objects, rightEvents, rightCompositeEvents, CBPSide.RIGHT);
			if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
//			    CBPConflict conflict = new CBPConflict(cTarget.getLeftValueEvents(), cTarget.getRightValueEvents());
			    CBPConflict conflict = new CBPConflict(leftEvents, rightEvents);
			    conflicts.add(conflict);
			}
		    } else if (cTarget.getLeftPosition() != cTarget.getRightPosition() && cTarget.getLeftPosition() != cTarget.getOldLeftPosition()
			    && cTarget.getRightPosition() != cTarget.getOldLeftPosition()) {

//			Set<CBPChangeEvent<?>> leftEvents = leftContainingFeature.getLeftObjectEvents().get(cTarget);
//			Set<CBPChangeEvent<?>> rightEvents = rightContainingFeature.getRightObjectEvents().get(cTarget);
//			if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
//			    CBPConflict conflict = new CBPConflict(cTarget.getLeftValueEvents(), cTarget.getRightValueEvents());
//			    conflicts.add(conflict);
//			}
		    }
		}
	    }

	    for (Entry<String, CBPMatchFeature> featureEntry : cTarget.getFeatures().entrySet()) {
		CBPMatchFeature cFeature = featureEntry.getValue();
		if (cFeature.getLeftEvents().size() == 0 && cFeature.getRightEvents().size() == 0) {
		    continue;
		}

		// Set<Object> values = cFeature.getAllValues();
		Set<Object> values = getAllUnequalValues(cFeature);

		if (cFeature.getFeatureType() == CBPFeatureType.REFERENCE && cFeature.isContainment()) {
		    if (cFeature.isMany()) {
			for (Object value : values) {
			    CBPMatchObject cValue = (CBPMatchObject) value;
			    Set<CBPChangeEvent<?>> leftEvents = cFeature.getLeftObjectEvents(cValue);
			    Set<CBPChangeEvent<?>> rightEvents = cFeature.getRightObjectEvents(cValue);
			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				CBPConflict conflict = new CBPConflict(leftEvents, rightEvents);
				conflicts.add(conflict);
			    }
			}
		    } else {
			CBPMatchObject leftValue = (CBPMatchObject) cFeature.getLeftValues().get(0);
			CBPMatchObject rightValue = (CBPMatchObject) cFeature.getRightValues().get(0);
			if (leftValue == rightValue) {
			    continue;
			} else if (leftValue != null && leftValue.equals(rightValue)) {
			    continue;
			} else if (rightValue != null && rightValue.equals(leftValue)) {
			    continue;
			}
			if (cFeature.getLeftEvents().size() > 0 && cFeature.getRightEvents().size() > 0) {
			    Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<CBPChangeEvent<?>>(cFeature.getLeftEvents());
			    Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<CBPChangeEvent<?>>(cFeature.getRightEvents());

			    if (leftEvents != null && rightEvents != null) {
				addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
				addRelatedCompositeEvents(rightCompositeEvents, rightEvents);
				addDependentEvents(objects, leftEvents, leftCompositeEvents, CBPSide.LEFT);
				addDependentEvents(objects, rightEvents, rightCompositeEvents, CBPSide.RIGHT);

				if (leftEvents.size() > 0 && rightEvents.size() > 0) {
				    CBPConflict conflict = new CBPConflict(leftEvents, rightEvents);
				    conflicts.add(conflict);
				}
			    }
			}
		    }
		} else if (cFeature.getFeatureType() == CBPFeatureType.REFERENCE && !cFeature.isContainment()) {
		    if (cFeature.isMany()) {
			for (Object value : values) {
			    CBPMatchObject cValue = (CBPMatchObject) value;
			    Set<CBPChangeEvent<?>> leftEvents = cFeature.getLeftObjectEvents(cValue);
			    Set<CBPChangeEvent<?>> rightEvents = cFeature.getRightObjectEvents(cValue);
			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				CBPConflict conflict = new CBPConflict(leftEvents, rightEvents);
				conflicts.add(conflict);
			    }
			}
		    } else {
			CBPMatchObject leftValue = (CBPMatchObject) cFeature.getLeftValues().get(0);
			CBPMatchObject rightValue = (CBPMatchObject) cFeature.getRightValues().get(0);
			if (leftValue == rightValue) {
			    continue;
			} else if (leftValue != null && leftValue.equals(rightValue)) {
			    continue;
			} else if (rightValue != null && rightValue.equals(leftValue)) {
			    continue;
			}
			if (cFeature.getLeftEvents().size() > 0 && cFeature.getRightEvents().size() > 0) {
			    CBPConflict conflict = new CBPConflict(cFeature.getLeftObjectEvents(leftValue), cFeature.getRightObjectEvents(rightValue));
			    conflicts.add(conflict);
			}
		    }
		} else if (cFeature.getFeatureType() == CBPFeatureType.ATTRIBUTE) {
		    if (cFeature.isMany()) {
			if (cFeature.getLeftEvents().size() > 0 && cFeature.getRightEvents().size() > 0) {
			    CBPConflict conflict = new CBPConflict(cFeature.getLeftEvents(), cFeature.getRightEvents());
			    conflicts.add(conflict);
			}
		    } else {
			Object leftValue = cFeature.getLeftValues().get(0);
			Object rightValue = cFeature.getRightValues().get(0);
			if (leftValue == rightValue) {
			    continue;
			} else if (leftValue != null && leftValue.equals(rightValue)) {
			    continue;
			} else if (rightValue != null && rightValue.equals(leftValue)) {
			    continue;
			}
			if (cFeature.getLeftEvents().size() > 0 && cFeature.getRightEvents().size() > 0) {
			    CBPConflict conflict = new CBPConflict(cFeature.getLeftEvents(), cFeature.getRightEvents());
			    conflicts.add(conflict);
			}
			// if (cFeature.getLeftEvents().size() > 0 &&
			// cFeature.getRightEvents().size() > 0) {
			// Set<CBPChangeEvent<?>> leftEvents =
			// cFeature.getLeftObjectEvents(leftValue);
			// Set<CBPChangeEvent<?>> rightEvents =
			// cFeature.getRightObjectEvents(rightValue);
			// CBPConflict conflict = new CBPConflict(leftEvents,
			// rightEvents);
			// conflicts.add(conflict);
			// }
		    }
		}

	    }

	}
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
		if (evt instanceof CBPEStructuralFeatureEvent) {
		    CBPMatchObject obj = objects.get(((CBPEStructuralFeatureEvent) evt).getTarget());
		    CBPMatchFeature fea = obj.getFeatures().get(((CBPEStructuralFeatureEvent) evt).getEStructuralFeature());

		    if (fea.isMany()) {
			Object val = objects.get(((CBPEStructuralFeatureEvent) evt).getValue().toString());
			if (val != null) {
			    Set<CBPChangeEvent<?>> es = fea.getObjectEvents(side).get(val);
			    if (es != null && es.size() > 0) {
				addRelatedCompositeEvents(compositeEventMap, es);
				temp.addAll(es);
			    }
			}
		    } else {
			Set<CBPChangeEvent<?>> es = new LinkedHashSet<>(fea.getEvents(side));
			if (es != null && es.size() > 0) {
			    addRelatedCompositeEvents(compositeEventMap, es);
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

    private void getAllEvents(Set<CBPChangeEvent<?>> events, CBPMatchObject object, CBPSide side) {
	events.addAll(object.getTargetEvents(side));
	events.addAll(object.getValueEvents(side));
	for (CBPMatchFeature feature : object.getFeatures().values()) {
	    if (feature.getFeatureType() == CBPFeatureType.REFERENCE && feature.isContainment() && feature.getValues(side).size() > 0) {
		for (Object value : feature.getValues(side).values()) {
		    if (value instanceof CBPMatchObject) {
			CBPMatchObject cValue = (CBPMatchObject) value;
			getAllEvents(events, cValue, side);
		    }
		}
	    }
	    // if (feature.getFeatureType() == CBPFeatureType.REFERENCE &&
	    // !feature.isContainment() && feature.getValues(side).size() > 0) {
	    // for (Object value : feature.getValues(side).values()) {
	    // if (value instanceof CBPMatchObject) {
	    // CBPMatchObject cValue = (CBPMatchObject) value;
	    // events.addAll(cValue.getTargetEvents(side));
	    // }
	    // }
	    // }

	}
    }

    public Set<Object> getAllUnequalValues(CBPMatchFeature cFeature) {
	Set<Object> values = new LinkedHashSet<>();
	Map<Integer, Object> leftValues = cFeature.getLeftValues();
	Map<Integer, Object> rightValues = cFeature.getRightValues();
	for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
	    int pos = valueEntry.getKey();
	    Object leftValue = valueEntry.getValue();
	    Object rightValue = rightValues.get(pos);

	    if (leftValue == rightValue || (leftValue != null && leftValue.equals(rightValue)) || (rightValue != null && rightValue.equals(leftValue))) {
		continue;
	    } else {
		if (leftValue != null) {
		    values.add(leftValue);
		}
		if (rightValue != null) {
		    values.add(rightValue);
		}
	    }

	}
	return values;
    }
}
