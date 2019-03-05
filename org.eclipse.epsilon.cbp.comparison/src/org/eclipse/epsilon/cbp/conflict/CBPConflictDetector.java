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

public class CBPConflictDetector {

    public void computeConflicts(Map<String, CBPMatchObject> objects, List<CBPConflict> conflicts, Map<String, Set<CBPChangeEvent<?>>> leftCompositeEvents,
	    Map<String, Set<CBPChangeEvent<?>>> rightCompositeEvents) {

	Iterator<Entry<String, CBPMatchObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPMatchObject> objectEntry = iterator.next();
	    CBPMatchObject cTarget = objectEntry.getValue();

	    if (cTarget.getId().equals("O-40914")) {
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
		Set<String> leftComposites = leftEvents.stream().map(x -> x.getComposite()).collect(Collectors.toSet());
		if (leftCompositeEvents.size() > 0) {
		    for (String composite : leftComposites) {
			Set<CBPChangeEvent<?>> events = leftCompositeEvents.get(composite);
			leftEvents.addAll(events);
		    }
		}

		Set<String> rightComposites = rightEvents.stream().map(x -> x.getComposite()).collect(Collectors.toSet());
		if (rightCompositeEvents.size() > 0) {
		    for (String composite : rightComposites) {
			Set<CBPChangeEvent<?>> events = rightCompositeEvents.get(composite);
			rightEvents.addAll(events);
		    }
		}

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
		Set<String> leftComposites = leftEvents.stream().map(x -> x.getComposite()).collect(Collectors.toSet());
		if (leftCompositeEvents.size() > 0) {
		    for (String composite : leftComposites) {
			Set<CBPChangeEvent<?>> events = leftCompositeEvents.get(composite);
			leftEvents.addAll(events);
		    }
		}

		Set<String> rightComposites = rightEvents.stream().map(x -> x.getComposite()).collect(Collectors.toSet());
		if (rightCompositeEvents.size() > 0) {
		    for (String composite : rightComposites) {
			Set<CBPChangeEvent<?>> events = rightCompositeEvents.get(composite);
			rightEvents.addAll(events);
		    }
		}

		if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
		    CBPConflict conflict = new CBPConflict(leftEvents, rightEvents);
		    conflicts.add(conflict);
		}
	    }

	    // movement conflict
	    else if (!cTarget.getLeftIsCreated() && !cTarget.getLeftIsDeleted() && !cTarget.getLeftIsCreated() && !cTarget.getRightIsDeleted()) {
		if (cTarget.getLeftContainer() == null) {
		    continue;
		} else if (cTarget.getRightContainer() == null) {
		    continue;
		} else if ((!cTarget.getLeftContainer().equals(cTarget.getRightContainer()) && !cTarget.getLeftContainer().equals(cTarget.getOldLeftContainer())
			&& !cTarget.getRightContainer().equals(cTarget.getOldLeftContainer()))
			|| (!cTarget.getLeftContainingFeature().equals(cTarget.getRightContainingFeature()) && !cTarget.getLeftContainingFeature().equals(cTarget.getOldLeftContainingFeature())
				&& !cTarget.getRightContainingFeature().equals(cTarget.getOldLeftContainingFeature()))) {
		    CBPConflict conflict = new CBPConflict(cTarget.getLeftValueEvents(), cTarget.getRightValueEvents());
		    conflicts.add(conflict);
		} else if (cTarget.getLeftPosition() != cTarget.getRightPosition() && cTarget.getLeftPosition() != cTarget.getOldLeftPosition()
			&& cTarget.getRightPosition() != cTarget.getOldLeftPosition()) {
		    CBPConflict conflict = new CBPConflict(cTarget.getLeftValueEvents(), cTarget.getRightValueEvents());
		    conflicts.add(conflict);
		}
	    }

	    for (Entry<String, CBPMatchFeature> featureEntry : cTarget.getFeatures().entrySet()) {
		CBPMatchFeature cFeature = featureEntry.getValue();
		if (cFeature.getLeftEvents().size() == 0 && cFeature.getRightEvents().size() == 0) {
		    continue;
		}
		Set<Object> values = cFeature.getAllValues();

		if (cFeature.getFeatureType() == CBPFeatureType.REFERENCE && cFeature.isContainment()) {
		    for (Object value : values) {
			CBPMatchObject cValue = (CBPMatchObject) value;
			Set<CBPChangeEvent<?>> leftEvents = cFeature.getLeftObjectEvents(cValue);
			Set<CBPChangeEvent<?>> rightEvents = cFeature.getRightObjectEvents(cValue);
			if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
			    CBPConflict conflict = new CBPConflict(leftEvents, rightEvents);
			    conflicts.add(conflict);
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
			    CBPConflict conflict = new CBPConflict(cFeature.getLeftEvents(), cFeature.getRightEvents());
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
		    }
		}

	    }

	}
    }

    private void getAllEvents(Set<CBPChangeEvent<?>> events, CBPMatchObject object, CBPSide side) {
	events.addAll(object.getEvents(side));
	for (CBPMatchFeature feature : object.getFeatures().values()) {
	    if (feature.getFeatureType() == CBPFeatureType.REFERENCE && feature.isContainment() && feature.getValues(side).size() > 0) {
		for (Object value : feature.getValues(side).values()) {
		    if (value instanceof CBPMatchObject) {
			CBPMatchObject cValue = (CBPMatchObject) value;
			getAllEvents(events, cValue, side);
		    }
		}
	    }
	    if (feature.getFeatureType() == CBPFeatureType.REFERENCE && !feature.isContainment() && feature.getValues(side).size() > 0) {
		for (Object value : feature.getValues(side).values()) {
		    if (value instanceof CBPMatchObject) {
			CBPMatchObject cValue = (CBPMatchObject) value;
			events.addAll(cValue.getEvents(side));
		    }
		}
	    }

	}
    }
}
