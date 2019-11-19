package org.eclipse.epsilon.cbp.conflict;

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
import org.eclipse.epsilon.cbp.comparison.event.CBPDeleteEObjectEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEObject;
import org.eclipse.epsilon.cbp.comparison.event.CBPEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEStructuralFeatureEvent;

public class CBPConflictDetector {

    @SuppressWarnings("rawtypes")
    public void computeConflicts(Map<String, CBPMatchObject> objects, List<CBPConflict> conflicts, Map<String, Set<CBPChangeEvent<?>>> leftCompositeEvents,
	    Map<String, Set<CBPChangeEvent<?>>> rightCompositeEvents) {

	Iterator<Entry<String, CBPMatchObject>> iterator = objects.entrySet().iterator();
	while (iterator.hasNext()) {

	    Entry<String, CBPMatchObject> objectEntry = iterator.next();
	    CBPMatchObject cTarget = objectEntry.getValue();

	    if (cTarget.getId().equals("O-25173") || cTarget.getId().equals("O-25173")) {
		// CBPMatchFeature x = cTarget.getFeatures().get("specific");
		System.console();
	    }

	    // if (cTarget.getLeftIsDeleted() && cTarget.getRightIsDeleted()) {
	    // continue;
	    // } else //
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
			leftEvents.stream().forEach(e -> e.addConflict(conflict));
			rightEvents.stream().forEach(e -> e.addConflict(conflict));
			conflict.getLeftEvents().addAll(leftEvents);
			conflict.getRightEvents().addAll(rightEvents);
		    } else {
			CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
			conflicts.add(conflict2);
			continue;
		    }
		}
	    }

	    // movement conflicts
	    else if (!cTarget.getLeftIsCreated() && !cTarget.getLeftIsDeleted() && !cTarget.getLeftIsCreated() && !cTarget.getRightIsDeleted()) {

		if (cTarget.getLeftContainer() != null && cTarget.getRightContainer() != null) {
		    CBPMatchObject leftContainer = cTarget.getLeftContainer();
		    CBPMatchFeature leftContainingFeature = cTarget.getLeftContainingFeature();
		    CBPMatchObject rightContainer = cTarget.getRightContainer();
		    CBPMatchFeature rightContainingFeature = cTarget.getRightContainingFeature();
		    CBPMatchObject oldContainer = cTarget.getOldLeftContainer();
		    CBPMatchFeature oldContainingFeature = cTarget.getOldLeftContainingFeature();

		    if (leftContainer.getLeftIsDeleted() || leftContainer.getRightIsDeleted() || rightContainer.getLeftIsDeleted() || rightContainer.getRightIsDeleted()) {
			// will be handled at the deletion conflict
			// do nothing
			System.console();
		    } else
		    //
		    if ((!leftContainer.equals(oldContainer) || !leftContainingFeature.equals(oldContainingFeature))
			    || (!rightContainer.equals(oldContainer) || !rightContainingFeature.equals(oldContainingFeature))) {

			Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>();
			Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>();

			// long a = 0;
			// long b = 0;
			// long c = 0;
			// long start = 0;
			// long end = 0;

			if (leftContainingFeature.getLeftObjectEvents().get(cTarget) != null) {
			    leftEvents.addAll(leftContainingFeature.getLeftObjectEvents().get(cTarget));
			}
			if (rightContainingFeature.getRightObjectEvents().get(cTarget) != null) {
			    rightEvents.addAll(rightContainingFeature.getRightObjectEvents().get(cTarget));
			}

			addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

			if (cTarget.getValueEvents(CBPSide.LEFT) != null) {
			    for (CBPChangeEvent<?> event : cTarget.getValueEvents(CBPSide.LEFT)) {
				if (event instanceof CBPEReferenceEvent) {
				    String featureName = ((CBPEReferenceEvent) event).getEStructuralFeature();
				    String id = ((CBPEReferenceEvent) event).getTarget();
				    CBPMatchObject target = objects.get(id);
				    CBPMatchFeature feature = target.getFeatures().get(featureName);
				    if (feature.isContainment()) {
					leftEvents.add(event);
				    }
				}
			    }
			}
			if (cTarget.getValueEvents(CBPSide.RIGHT) != null) {
			    for (CBPChangeEvent<?> event : cTarget.getValueEvents(CBPSide.RIGHT)) {
				if (event instanceof CBPEReferenceEvent) {
				    String featureName = ((CBPEReferenceEvent) event).getEStructuralFeature();
				    String id = ((CBPEReferenceEvent) event).getTarget();
				    CBPMatchObject target = objects.get(id);
				    CBPMatchFeature feature = target.getFeatures().get(featureName);
				    if (feature.isContainment()) {
					rightEvents.add(event);
				    }
				}
			    }
			}

			// addDependentEvents(objects, leftEvents,
			// leftCompositeEvents, CBPSide.LEFT);
			// addDependentEvents(objects, rightEvents,
			// rightCompositeEvents, CBPSide.RIGHT);

			if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
			    // start = System.nanoTime();
			    final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
			    // end = System.nanoTime();
			    // a = end - start;
			    if (conflict != null) {
				leftEvents.stream().forEach(e -> e.addConflict(conflict));
				rightEvents.stream().forEach(e -> e.addConflict(conflict));
				conflict.getLeftEvents().addAll(leftEvents);
				conflict.getRightEvents().addAll(rightEvents);

			    } else {
				CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
				if ((leftContainer.equals(rightContainer) && !leftContainingFeature.equals(rightContainingFeature))) {
				    if (leftEvents.size() == 1 && rightEvents.size() == 1)
					conflict2.setPseudo(true);
				}
				conflicts.add(conflict2);
			    }
			}

			// try {
			// if (a > 0) {
			// String x = String.valueOf(a) +
			// System.lineSeparator();
			//
			// Files.write(Paths.get("D:\\TEMP\\CONFLICTS\\performance\\move.csv"),
			// x.getBytes(), StandardOpenOption.APPEND);
			// }
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

		    } else if (cTarget.getLeftPosition() != cTarget.getRightPosition() && cTarget.getLeftPosition() != cTarget.getOldLeftPosition()
			    && cTarget.getRightPosition() != cTarget.getOldLeftPosition()) {
			// do nothing
		    }
		}
	    }

	    for (Entry<String, CBPMatchFeature> featureEntry : cTarget.getFeatures().entrySet()) {

		if (cTarget.getId().equals("O-5515")) {
		    System.console();
		}

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

			    if (cValue.getId().equals("O-13558") || cValue.getId().equals("O-13558")) {
				System.console();
			    }

			    CBPMatchObject leftContainer = cValue.getLeftContainer();
			    CBPMatchFeature leftContainingFeature = cValue.getLeftContainingFeature();
			    CBPMatchObject rightContainer = cValue.getRightContainer();
			    CBPMatchFeature rightContainingFeature = cValue.getRightContainingFeature();
			    CBPMatchObject oldContainer = cValue.getOldLeftContainer();
			    CBPMatchFeature oldContainingFeature = cValue.getOldLeftContainingFeature();

			    int leftPos = cFeature.getLeftPosition(cValue);
			    int rightPos = cFeature.getRightPosition(cValue);
			    int oldPos = cFeature.getOriginalPosition(cValue);

			    if (cValue != null && (cValue.getLeftIsDeleted() || cValue.getRightIsDeleted())) {
				// this will be handled on delete detection
				continue;
			    } else if (leftContainer != null && rightContainer != null && leftContainingFeature != null && rightContainingFeature != null && leftContainer.equals(oldContainer)
				    && leftContainingFeature.equals(oldContainingFeature) && rightContainer.equals(oldContainer) && rightContainingFeature.equals(oldContainingFeature)
				    && leftPos == oldPos && rightPos == oldPos) {
				continue;
			    } else if (leftContainer != null && rightContainer != null && leftContainingFeature != null && rightContainingFeature != null && (!leftContainer.equals(oldContainer)
				    || !leftContainingFeature.equals(oldContainingFeature) || !rightContainer.equals(oldContainer) || !rightContainingFeature.equals(oldContainingFeature))) {
				// this is the case when an item is moved to
				// another container on both sides
				// this will be handled on move conflict
				// detection
				// System.console();
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

			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				if (conflict != null) {
				    leftEvents.stream().forEach(e -> e.addConflict(conflict));
				    rightEvents.stream().forEach(e -> e.addConflict(conflict));
				    conflict.getLeftEvents().addAll(leftEvents);
				    conflict.getRightEvents().addAll(rightEvents);
				} else {
				    CBPConflict conflict2 = new CBPConflict(leftEvents, rightEvents);
				    if (leftPos == rightPos) {
					// if (leftEvents.size() == 1 &&
					// rightEvents.size() == 1)
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

			if ((originalValue != null && originalValue.getId().equals("O-13561"))) {
			    System.console();
			}
			if ((leftValue != null && leftValue.getId().equals("O-13561")) || (rightValue != null && rightValue.getId().equals("O-13561"))) {
			    System.console();
			}

			if (leftValue == null && rightValue == null) {
			    // this will be handled by move detection
			    // or deletion
			    // continue
			} else//
			if (leftValue != null && leftValue.equals(originalValue) && leftValue.equals(rightValue)) {
			    // continue
			} else //
			if (rightValue != null && rightValue.equals(originalValue) && rightValue.equals(leftValue)) {
			    // continue
			} else //
			if ((leftValue != null && leftValue.getOldLeftContainer() != null) || (rightValue != null && rightValue.getOldLeftContainer() != null)) {
			    // this will be handled by move detection
			    // or deletion
			    // continue
			} else//
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

				if (leftEvents.size() > 0 && rightEvents.size() > 0) {
				    final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				    if (conflict != null) {
					leftEvents.stream().forEach(e -> e.addConflict(conflict));
					rightEvents.stream().forEach(e -> e.addConflict(conflict));
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

			    if (cValue != null && cValue.getLeftIsDeleted() && cValue.getRightIsDeleted()) {
				continue;
			    } else if (cValue.getLeftPosition() == cValue.getOldLeftPosition() && cValue.getRightPosition() == cValue.getOldRightPosition()) {
				continue;
			    }

			    Set<CBPChangeEvent<?>> leftEvents = cFeature.getLeftObjectEvents(cValue);
			    Set<CBPChangeEvent<?>> rightEvents = cFeature.getRightObjectEvents(cValue);

			    addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			    addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				if (conflict != null) {
				    leftEvents.stream().forEach(e -> e.addConflict(conflict));
				    rightEvents.stream().forEach(e -> e.addConflict(conflict));
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

			if (leftValue == null && originalValue == null && rightValue == null) {
			    // continue
			} else //
			if (leftValue != null && leftValue.equals(originalValue) && leftValue.equals(rightValue)) {
			    // continue
			} else //
			if (rightValue != null && rightValue.equals(originalValue) && rightValue.equals(leftValue)) {
			    // continue
			} else //
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

			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				if (conflict != null) {
				    leftEvents.stream().forEach(e -> e.addConflict(conflict));
				    rightEvents.stream().forEach(e -> e.addConflict(conflict));
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

			    if (cFeature.getLeftPosition(value) == cFeature.getOriginalPosition(value) && cFeature.getRightPosition(value) == cFeature.getOriginalPosition(value)) {
				continue;
			    }

			    Set<CBPChangeEvent<?>> leftEvents = cFeature.getLeftObjectEvents(value);
			    Set<CBPChangeEvent<?>> rightEvents = cFeature.getRightObjectEvents(value);

			    addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			    addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

			    if (leftEvents != null && rightEvents != null && leftEvents.size() > 0 && rightEvents.size() > 0) {
				final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				if (conflict != null) {
				    leftEvents.stream().forEach(e -> e.addConflict(conflict));
				    rightEvents.stream().forEach(e -> e.addConflict(conflict));
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

			if (leftValue == null && originalValue == null && rightValue == null) {
			    // continue
			} else //
			if (leftValue != null && leftValue.equals(originalValue) && leftValue.equals(rightValue)) {
			    // continue
			} else //
			if (rightValue != null && rightValue.equals(originalValue) && rightValue.equals(leftValue)) {
			    // continue
			} else //
			if ((leftValue != null && !leftValue.equals(originalValue) || (originalValue != null && !originalValue.equals(leftValue)))
				|| (rightValue != null && !rightValue.equals(originalValue) || (originalValue != null && !originalValue.equals(rightValue)))) {

			    Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>(cFeature.getLeftEvents());
			    Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>(cFeature.getRightEvents());

			    addRelatedCompositeEvents(leftCompositeEvents, leftEvents);
			    addRelatedCompositeEvents(rightCompositeEvents, rightEvents);

			    if (leftEvents.size() > 0 && rightEvents.size() > 0) {
				final CBPConflict conflict = getExistingConflict(conflicts, leftEvents, rightEvents);
				if (conflict != null) {
				    leftEvents.stream().forEach(e -> e.addConflict(conflict));
				    rightEvents.stream().forEach(e -> e.addConflict(conflict));
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

	// // conflicts = new CBPConflict();
	// Set<CBPChangeEvent<?>> leftEventSet = new LinkedHashSet<>();
	// Set<CBPChangeEvent<?>> rightEventSet = new LinkedHashSet<>();
	//
	// Iterator<CBPChangeEvent<?>> leftIterator = leftEvents.iterator();
	// while (leftIterator.hasNext()) {
	// CBPChangeEvent<?> event = leftIterator.next();
	// CBPConflict c = event.getConflict();
	// if (c != null) {
	// conflicts = c;
	// break;
	// }
	// }
	//
	// if (conflicts == null) {
	// Iterator<CBPChangeEvent<?>> iterator = rightEvents.iterator();
	// while (iterator.hasNext()) {
	// CBPChangeEvent<?> event = iterator.next();
	// CBPConflict c = event.getConflict();
	// if (c != null) {
	// conflicts = c;
	// break;
	// }
	// }
	// }
	//
	// if (conflicts == null) {
	// return null;
	// }
	//
	// leftIterator = leftEvents.iterator();
	// while (leftIterator.hasNext()) {
	// CBPChangeEvent<?> event = leftIterator.next();
	// CBPConflict c = event.getConflict();
	// if (c != null) {
	// Iterator<CBPChangeEvent<?>> lei = c.getLeftEvents().iterator();
	// while (lei.hasNext()) {
	// CBPChangeEvent<?> e = lei.next();
	// leftEventSet.add(e);
	// e.setConflict(conflicts);
	// lei.remove();
	// }
	// c.getLeftEvents().clear();
	//
	// Iterator<CBPChangeEvent<?>> rei = c.getRightEvents().iterator();
	// while (rei.hasNext()) {
	// CBPChangeEvent<?> e = rei.next();
	// rightEventSet.add(e);
	// e.setConflict(conflicts);
	// rei.remove();
	// }
	// c.getRightEvents().clear();
	// }
	// if (c != null) {
	// conflicts.remove(c);
	// }
	// if (conflicts != null) {
	// leftEventSet.add(event);
	// event.setConflict(conflicts);
	// }
	// leftIterator.remove();
	// }
	// leftEvents.clear();
	//
	// Iterator<CBPChangeEvent<?>> rightIterator = rightEvents.iterator();
	// while (rightIterator.hasNext()) {
	// CBPChangeEvent<?> event = rightIterator.next();
	// CBPConflict c = event.getConflict();
	// if (c != null) {
	// Iterator<CBPChangeEvent<?>> lei = c.getLeftEvents().iterator();
	// while (lei.hasNext()) {
	// CBPChangeEvent<?> e = lei.next();
	// leftEventSet.add(e);
	// e.setConflict(conflicts);
	// lei.remove();
	// }
	// c.getLeftEvents().clear();
	// Iterator<CBPChangeEvent<?>> rei = c.getRightEvents().iterator();
	// while (rei.hasNext()) {
	// CBPChangeEvent<?> e = rei.next();
	// rightEventSet.add(e);
	// e.setConflict(conflicts);
	// rei.remove();
	// }
	// c.getRightEvents().clear();
	// }
	// if (c != null) {
	// conflicts.remove(c);
	// }
	// if (conflicts != null) {
	// rightEventSet.add(event);
	// event.setConflict(conflicts);
	// }
	// rightIterator.remove();
	//
	// }
	// rightEvents.clear();
	//
	// if (conflicts != null) {
	//// conflicts.remove(conflicts);
	// conflicts.add(conflicts);
	// conflicts.setLeftEvents(leftEventSet);
	// conflicts.setLeftEvents(rightEventSet);
	// }

	// // MERGING ALL CONFLICTS ------------------------------------------
	// Set<Set<CBPConflict>> leftConflictsSets =
	// leftEvents.stream().filter(event -> event.getConflicts() !=
	// null).map(event -> event.getConflicts()).collect(Collectors.toSet());
	// Set<Set<CBPConflict>> rightConflictSets =
	// rightEvents.stream().filter(event -> event.getConflicts() !=
	// null).map(event -> event.getConflicts()).collect(Collectors.toSet());
	//
	// Set<CBPConflict> leftConflicts = new LinkedHashSet<>();
	// Set<CBPConflict> rightConflicts = new LinkedHashSet<>();
	//
	// for (Set<CBPConflict> member : leftConflictsSets) {
	// leftConflicts.addAll(member);
	// }
	//
	// for (Set<CBPConflict> member : rightConflictSets) {
	// rightConflicts.addAll(member);
	// }
	//
	// if (leftConflicts != null && leftConflicts.size() > 0) {
	// conflict = leftConflicts.iterator().next();
	// }
	//
	// if (conflict == null && rightConflicts != null &&
	// rightConflicts.size() > 0) {
	// conflict = rightConflicts.iterator().next();
	// }
	//
	// final CBPConflict conf = conflict;
	//
	// if (conf != null) {
	// Set<CBPConflict> unionConflicts = leftConflicts;
	// unionConflicts.addAll(rightConflicts);
	// unionConflicts.remove(conf);
	// conflicts.removeAll(unionConflicts);
	//
	// for (CBPConflict c : unionConflicts) {
	// conf.getLeftEvents().addAll(c.getLeftEvents());
	// c.getLeftEvents().clear();
	// conf.getLeftEvents().stream().forEach(e -> {
	// e.getConflicts().clear();
	// e.addConflict(conf);
	// });
	//
	// conf.getRightEvents().addAll(c.getRightEvents());
	// c.getRightEvents().clear();
	// conf.getRightEvents().stream().forEach(e -> {
	// e.getConflicts().clear();
	// e.addConflict(conf);
	// });
	// }
	// unionConflicts.clear();
	// }
	//
	// if (leftConflicts != null)
	// leftConflicts.clear();
	//
	// if (rightConflicts != null)
	// rightConflicts.clear();

	// ----------------------------------------------
	// // if (conflicts != null) {
	// // if (leftConflicts != null && leftConflicts.size() > 0) {
	// // for (CBPConflict c : leftConflicts) {
	// // if (conflicts.equals(c)) {
	// // continue;
	// // }
	// // for (CBPChangeEvent<?> event : c.getLeftEvents()) {
	// // event.setConflict(conflicts);
	// // }
	// // conflicts.getLeftEvents().addAll(c.getLeftEvents());
	// // c.getLeftEvents().clear();
	// // for (CBPChangeEvent<?> event : c.getRightEvents()) {
	// // event.setConflict(conflicts);
	// // }
	// // conflicts.getRightEvents().addAll(c.getRightEvents());
	// // c.getRightEvents().clear();
	// // conflicts.remove(c);
	// // }
	// // }
	// // if (rightConflicts != null && rightConflicts.size() > 0) {
	// // for (CBPConflict c : rightConflicts) {
	// // if (conflicts.equals(c)) {
	// // continue;
	// // }
	// // for (CBPChangeEvent<?> event : c.getLeftEvents()) {
	// // event.setConflict(conflicts);
	// // }
	// // conflicts.getLeftEvents().addAll(c.getLeftEvents());
	// // c.getLeftEvents().clear();
	// // for (CBPChangeEvent<?> event : c.getRightEvents()) {
	// // event.setConflict(conflicts);
	// // }
	// // conflicts.getRightEvents().addAll(c.getRightEvents());
	// // c.getRightEvents().clear();
	// // conflicts.remove(c);
	// //
	// // }
	// // }
	// // }

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
		featureSet.add(feature);
		for (Object value : feature.getOldLeftValues().values()) {
		    if (value instanceof CBPMatchObject) {
			if (featureSet.contains(feature) && objectSet.contains(value)) {
			    continue;
			}
			CBPMatchObject cValue = (CBPMatchObject) value;
			if (cValue.isDeleted(side)) {
			    getAllEvents(events, cValue, side, featureSet, objectSet);
			}
		    }
		}
		for (Object value : feature.getValues(side).values()) {
		    if (value instanceof CBPMatchObject) {
			if (featureSet.contains(feature) && objectSet.contains(value)) {
			    continue;
			}
			CBPMatchObject cValue = (CBPMatchObject) value;
			if (cValue.isDeleted(side)) {
			    getAllEvents(events, cValue, side, featureSet, objectSet);
			}

		    }
		}
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
	values.remove(null);
	return values;
    }
}
