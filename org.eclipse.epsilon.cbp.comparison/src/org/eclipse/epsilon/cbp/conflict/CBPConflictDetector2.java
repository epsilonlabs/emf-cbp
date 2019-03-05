package org.eclipse.epsilon.cbp.conflict;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.epsilon.cbp.comparison.CBPFeatureType;
import org.eclipse.epsilon.cbp.comparison.CBPMatchFeature;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public class CBPConflictDetector2 {

    public void computeConflicts(Map<String, CBPMatchObject> objects, List<CBPConflict> conflicts) {

	Iterator<Entry<String, CBPMatchObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<String, CBPMatchObject> objectEntry = iterator.next();
	    CBPMatchObject cTarget = objectEntry.getValue();

	    if (cTarget.getId().equals("O-40914")) {
		System.console();
	    }

	    if (cTarget.getLeftIsDeleted() && cTarget.getRightIsDeleted()) {
		continue;
	    }

	    for (Entry<String, CBPMatchFeature> featureEntry : cTarget.getFeatures().entrySet()) {
		CBPMatchFeature cFeature = featureEntry.getValue();

		if (cFeature.getLeftEvents().size() == 0 && cFeature.getRightEvents().size() == 0) {
		    continue;
		}

		Map<Integer, Object> leftValues = cFeature.getValues(CBPSide.LEFT);
		Map<Integer, Object> rightValues = cFeature.getValues(CBPSide.RIGHT);
		Map<Integer, Object> originalValues = cFeature.getOldLeftValues();
		for (Entry<Integer, Object> valueEntry : leftValues.entrySet()) {
		    int pos = valueEntry.getKey();
		    Object leftValue = valueEntry.getValue();
		    Object rightValue = rightValues.get(pos);
		    Object originalValue = originalValues.get(pos);

		    if (leftValue == rightValue) {
			continue;
		    } else if (leftValue != null && rightValue != null && leftValue.equals(rightValue)) {
			continue;
		    }

		    if (leftValue instanceof CBPMatchObject) {
			if (((CBPMatchObject) leftValue).getId().equals("O-43655")) {
			    System.console();
			}
		    }

		    if (rightValue instanceof CBPMatchObject) {
			if (((CBPMatchObject) rightValue).getId().equals("O-43655")) {
			    System.console();
			}
		    }

		    if (originalValue instanceof CBPMatchObject) {
			if (((CBPMatchObject) originalValue).getId().equals("O-43655")) {
			    System.console();
			}
		    }

		    if (cFeature.getFeatureType() == CBPFeatureType.REFERENCE && cFeature.isContainment()) {

			CBPMatchObject cLeftValue = (CBPMatchObject) leftValue;
			CBPMatchObject cRightValue = (CBPMatchObject) rightValue;
			CBPMatchObject cOriginalValue = (CBPMatchObject) originalValue;

			// RIGHT
			if (cRightValue instanceof CBPMatchObject) {
			    // deletion conflict
			    if (!cRightValue.getLeftIsCreated() && !cRightValue.getLeftIsDeleted() && !cRightValue.getLeftIsCreated() && cRightValue.getRightIsDeleted()) {
				Set<CBPChangeEvent<?>> eventSet = new LinkedHashSet<>();
				getAllEvents(eventSet, cRightValue, CBPSide.RIGHT);
				if (eventSet.size() > 0) {
				    CBPConflict conflict = new CBPConflict(eventSet, cRightValue.getRightDeleteEvents());
				    conflicts.add(conflict);
				}
			    }
			    // deletion conflict
			    else if (!cRightValue.getLeftIsCreated() && cRightValue.getLeftIsDeleted() && !cRightValue.getLeftIsCreated() && !cRightValue.getRightIsDeleted()) {
				Set<CBPChangeEvent<?>> eventSet = new LinkedHashSet<>();
				getAllEvents(eventSet, cRightValue, CBPSide.RIGHT);
				if (eventSet.size() > 0) {
				    CBPConflict conflict = new CBPConflict(cRightValue.getLeftDeleteEvents(), eventSet);
				    conflicts.add(conflict);
				}
			    } else if (!cRightValue.getLeftIsCreated() && !cRightValue.getLeftIsDeleted() && !cRightValue.getLeftIsCreated() && !cRightValue.getRightIsDeleted()) {
				// this will be handled at its left value
			    }
			}

			// LEFT
			if (cLeftValue instanceof CBPMatchObject) {
			    // deletion conflict
			    if (!cLeftValue.getLeftIsCreated() && !cLeftValue.getLeftIsDeleted() && !cLeftValue.getLeftIsCreated() && cLeftValue.getRightIsDeleted()) {
				Set<CBPChangeEvent<?>> eventSet = new LinkedHashSet<>();
				getAllEvents(eventSet, cLeftValue, CBPSide.LEFT);
				if (eventSet.size() > 0) {
				    CBPConflict conflict = new CBPConflict(eventSet, cLeftValue.getRightDeleteEvents());
				    conflicts.add(conflict);
				}
			    }
			    // deletion conflict
			    else if (!cLeftValue.getLeftIsCreated() && cLeftValue.getLeftIsDeleted() && !cLeftValue.getLeftIsCreated() && !cLeftValue.getRightIsDeleted()) {
				Set<CBPChangeEvent<?>> eventSet = new LinkedHashSet<>();
				getAllEvents(eventSet, cLeftValue, CBPSide.LEFT);
				if (eventSet.size() > 0) {
				    CBPConflict conflict = new CBPConflict(cLeftValue.getLeftDeleteEvents(), eventSet);
				    conflicts.add(conflict);
				}
			    }
			    // movement conflict
			    else if (!cLeftValue.getLeftIsCreated() && !cLeftValue.getLeftIsDeleted() && !cLeftValue.getLeftIsCreated() && !cLeftValue.getRightIsDeleted()) {
				if (cLeftValue.getLeftContainer() == null) {
				    continue;
				} else if (cLeftValue.getRightContainer() == null) {
				    continue;
				} else if ((!cLeftValue.getLeftContainer().equals(cLeftValue.getRightContainer()) && !cLeftValue.getLeftContainer().equals(cLeftValue.getOldLeftContainer())
					&& !cLeftValue.getRightContainer().equals(cLeftValue.getOldLeftContainer()))
					|| (!cLeftValue.getLeftContainingFeature().equals(cLeftValue.getRightContainingFeature())
						&& !cLeftValue.getLeftContainingFeature().equals(cLeftValue.getOldLeftContainingFeature())
						&& !cLeftValue.getRightContainingFeature().equals(cLeftValue.getOldLeftContainingFeature()))) {
				    CBPConflict conflict = new CBPConflict(cLeftValue.getLeftValueEvents(), cLeftValue.getRightValueEvents());
				    conflicts.add(conflict);
				} else if (cLeftValue.getLeftPosition() != cLeftValue.getRightPosition() && cLeftValue.getLeftPosition() != cLeftValue.getOldLeftPosition()
					&& cLeftValue.getRightPosition() != cLeftValue.getOldLeftPosition()) {
				    CBPConflict conflict = new CBPConflict(cLeftValue.getLeftValueEvents(), cLeftValue.getRightValueEvents());
				    conflicts.add(conflict);
				}
			    }
			}
		    } else if (cFeature.getFeatureType() == CBPFeatureType.REFERENCE && !cFeature.isContainment()) {
			CBPMatchObject cLeftValue = (CBPMatchObject) leftValue;
			CBPMatchObject cRightValue = (CBPMatchObject) rightValue;

			Set<CBPChangeEvent<?>> leftEvents = cFeature.getLeftObjectEvents(cLeftValue);
			Set<CBPChangeEvent<?>> rightEvents = cFeature.getLeftObjectEvents(cRightValue);

			if (cFeature.isMany()) {
			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				CBPConflict conflict = new CBPConflict(leftEvents, rightEvents);
				conflicts.add(conflict);
			    }
			} else {
			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				CBPConflict conflict = new CBPConflict(leftEvents, rightEvents);
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
			    if (cFeature.getLeftEvents().size() > 0 && cFeature.getRightEvents().size() > 0) {
				CBPConflict conflict = new CBPConflict(cFeature.getLeftEvents(), cFeature.getRightEvents());
				conflicts.add(conflict);
			    }
			}
		    }

		}
	    }

	}
    }

    private void getAllEvents(Set<CBPChangeEvent<?>> events, CBPMatchObject object, CBPSide side) {
	events.addAll(object.getEvents(side));
	for (CBPMatchFeature feature : object.getFeatures().values()) {
	    if (feature.getFeatureType() == CBPFeatureType.REFERENCE && feature.getValues(side).size() > 0) {
		for (Object value : feature.getValues(side).values()) {
		    if (value instanceof CBPMatchObject) {
			CBPMatchObject cValue = (CBPMatchObject) value;
			getAllEvents(events, cValue, side);
		    }
		}
	    }
	}
    }
}
