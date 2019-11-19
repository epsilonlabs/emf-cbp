package org.eclipse.epsilon.cbp.comparison;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.epsilon.cbp.comparison.CBPMatchObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPMoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPRemoveFromEReferenceEvent;

public class CBPMatchFeature {

    private CBPMatchObject owner;
    private String oppositeFeatureName;
    private String name;
    private String type;
    private boolean isContainment = false;
    private boolean isMany = false;
    private boolean isUnique = false;
    private boolean isOrdered = false;
    private boolean isOldSet = false;
    private CBPFeatureType featureType = CBPFeatureType.REFERENCE;
    private Map<Integer, Boolean> leftIsSet = new TreeMap<Integer, Boolean>();
    private Map<Integer, Boolean> rightIsSet = new TreeMap<Integer, Boolean>();
    private Map<Integer, Object> leftValues = new TreeMap<Integer, Object>();
    private Map<Integer, Object> rightValues = new TreeMap<Integer, Object>();
    private Map<Integer, Object> oldLeftValues = new TreeMap<Integer, Object>();
    private Map<Integer, Object> oldRightValues = new TreeMap<Integer, Object>();
    private Map<Object, Integer> leftValueLineNum = new TreeMap<Object, Integer>();
    private Map<Object, Integer> rightValueLineNum = new TreeMap<Object, Integer>();
    private List<CBPPositionEvent> leftPositionEvents = new ArrayList<>();
    private List<CBPPositionEvent> rightPositionEvents = new ArrayList<>();
    private List<CBPDiffPositionEvent> leftDiffPositionEvents = new ArrayList<>();
    private List<CBPDiffPositionEvent> rightDiffPositionEvents = new ArrayList<>();
    private List<CBPChangeEvent<?>> leftEvents = new ArrayList<>();
    private List<CBPChangeEvent<?>> rightEvents = new ArrayList<>();
    private List<CBPDiff> diffs = new ArrayList<>();
    private Map<Object, Set<CBPChangeEvent<?>>> leftObjectEvents = new HashMap<Object, Set<CBPChangeEvent<?>>>();
    private Map<Object, Set<CBPChangeEvent<?>>> rightObjectEvents = new HashMap<Object, Set<CBPChangeEvent<?>>>();

    public CBPMatchFeature(CBPMatchObject owner, String name, CBPFeatureType featureType, boolean isContainer, boolean isMany, boolean isUnique, boolean isOrdered) {
	this.owner = owner;
	this.name = name;
	this.featureType = featureType;
	this.isContainment = isContainer;
	this.isMany = isMany;
	this.isUnique = isUnique;
	this.isOrdered = isOrdered;
    }

    public boolean isUnique() {
	return isUnique;
    }

    public boolean isOrdered() {
	return isOrdered;
    }

    public void setOrdered(boolean isOrdered) {
	this.isOrdered = isOrdered;
    }

    public void setUnique(boolean isUnique) {
	this.isUnique = isUnique;
    }

    public List<CBPDiff> getDiffs() {
	return diffs;
    }

    public void setDiffs(List<CBPDiff> diffs) {
	this.diffs = diffs;
    }

    public Map<Object, Set<CBPChangeEvent<?>>> getObjectEvents(CBPSide side) {
	if (side == CBPSide.LEFT) {
	    return this.getLeftObjectEvents();
	} else {
	    return this.getRightObjectEvents();
	}

    }

    public Set<CBPChangeEvent<?>> getObjectEvent(CBPMatchObject object, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    return this.getLeftObjectEvents(object);
	} else {
	    return this.getRightObjectEvents(object);
	}
    }

    public Set<CBPChangeEvent<?>> getLeftObjectEvents(Object object) {
	return leftObjectEvents.get(object);
    }

    public Set<CBPChangeEvent<?>> getRightObjectEvents(Object object) {
	return rightObjectEvents.get(object);
    }

    public Map<Object, Set<CBPChangeEvent<?>>> getLeftObjectEvents() {
	return leftObjectEvents;
    }

    public Map<Object, Set<CBPChangeEvent<?>>> getRightObjectEvents() {
	return rightObjectEvents;
    }

    public void addObjectEvent(Object object, CBPChangeEvent<?> event, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    this.addLeftObjectEvent(object, event);
	} else {
	    this.addRightObjectEvent(object, event);
	}

    }

    public void addLeftObjectEvent(Object object, CBPChangeEvent<?> event) {
	if (this.leftObjectEvents.get(object) == null) {
	    Set<CBPChangeEvent<?>> events = new LinkedHashSet<>();
	    this.leftObjectEvents.put(object, events);
	    events.add(event);
	} else {
	    this.leftObjectEvents.get(object).add(event);
	}
    }

    public void addRightObjectEvent(Object object, CBPChangeEvent<?> event) {
	if (this.rightObjectEvents.get(object) == null) {
	    Set<CBPChangeEvent<?>> events = new LinkedHashSet<>();
	    this.rightObjectEvents.put(object, events);
	    events.add(event);
	} else {
	    this.rightObjectEvents.get(object).add(event);
	}
    }

    public void putValueLineNum(Object value, int lineNum, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftValueLineNum.put(value, lineNum);
	} else {
	    rightValueLineNum.put(value, lineNum);
	}
    }

    public Integer getValueLineNum(Object value, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    return leftValueLineNum.get(value);
	} else {
	    return rightValueLineNum.get(value);
	}
    }

    public Integer getLeftValueLineNum(Object value) {
	return leftValueLineNum.get(value);
    }

    public Integer getRightValueLineNum(Object value) {
	return rightValueLineNum.get(value);
    }

    public void setLeftValueLineNum(Map<Object, Integer> leftValueLineNum) {
	this.leftValueLineNum = leftValueLineNum;
    }

    public void setRightValueLineNum(Map<Object, Integer> rightValueLineNum) {
	this.rightValueLineNum = rightValueLineNum;
    }

    public List<CBPChangeEvent<?>> getEvents(CBPSide side) {
	if (side == CBPSide.LEFT) {
	    return getLeftEvents();
	} else {
	    return getRightEvents();
	}
    }

    public List<CBPChangeEvent<?>> getLeftEvents() {
	return leftEvents;
    }

    public List<CBPChangeEvent<?>> getRightEvents() {
	return rightEvents;
    }

    public void addEvents(CBPChangeEvent<?> event, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    this.leftEvents.add(event);
	} else {
	    this.rightEvents.add(event);
	}
    }

    public void addLeftEvents(CBPChangeEvent<?> event) {
	this.leftEvents.add(event);
    }

    public void addRightEvents(CBPChangeEvent<?> event) {
	this.rightEvents.add(event);
    }

    public boolean isMany() {
	return isMany;
    }

    public void setMany(boolean isMany) {
	this.isMany = isMany;
    }

    public CBPMatchObject getOwner() {
	return owner;
    }

    public void setOwner(CBPMatchObject owner) {
	this.owner = owner;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public boolean isContainment() {
	return isContainment;
    }

    public void setContainment(boolean isContainment) {
	this.isContainment = isContainment;
    }

    public Map<Integer, Object> getLeftValues() {
	return leftValues;
    }

    public Map<Integer, Object> getOldLeftValues() {
	return oldLeftValues;
    }

    public void setLeftValues(Map<Integer, Object> leftValues) {
	this.leftValues = leftValues;
    }

    public Map<Integer, Object> getRightValues() {
	return rightValues;
    }

    public Map<Integer, Object> getOldRightValues() {
	return oldRightValues;
    }

    public void setRightValues(Map<Integer, Object> rightValues) {
	this.rightValues = rightValues;
    }

    public CBPFeatureType getFeatureType() {
	return featureType;
    }

    public void setFeatureType(CBPFeatureType featureType) {
	this.featureType = featureType;
    }

    public Object getValue(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftValues.get(0) : rightValues.get(0);
    }

    public Object getOldValue(CBPSide side) {
	return (side == CBPSide.LEFT) ? oldLeftValues.get(0) : oldRightValues.get(0);
    }

    public void setValue(Object value, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftValues.put(0, value);
	    if (!rightValues.containsKey(0)) {
		rightValues.put(0, null);
	    }
	} else {
	    rightValues.put(0, value);
	    if (!leftValues.containsKey(0)) {
		leftValues.put(0, null);
	    }
	}
    }

    public void setOldValue(Object value, CBPSide side) {
	this.setOldValue(0, value, side);
    }

    public void setOldValue(int index, Object value, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    if (oldLeftValues.get(index) == null) {
		oldLeftValues.put(index, value);
		if (!oldRightValues.containsKey(index)) {
		    oldRightValues.put(index, value);
		    // rightValues.put(0, value);
		}
	    }
	} else {
	    if (oldRightValues.get(index) == null) {
		oldRightValues.put(index, value);
		if (!oldLeftValues.containsKey(index)) {
		    oldLeftValues.put(index, value);
		    // leftValues.put(0, value);
		}
	    }
	}
    }

    public void unsetValue(Object value, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftValues.put(0, null);
	} else {
	    rightValues.put(0, null);
	}
    }

    public Map<Integer, Object> getValues(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftValues : rightValues;
    }

    public void moveValue(Object value, int from, int to, CBPSide side) {
	moveValue(value, from, to, side, true);
    }

    public void moveValue(Object value, int from, int to, CBPSide side, boolean recordCBPPositionEvent) {
	if (value instanceof CBPMatchObject && ((CBPMatchObject) value).getOldPosition(side) == -1) {
	    updateOldPosition((CBPMatchObject) value, from, side);
	}
	if (recordCBPPositionEvent) {
	    addPositionEvent(new CBPPositionEvent(CBPPositionEventType.MOVE, to, from, value), side);
	}

	Map<Integer, Object> temp = new HashMap<>();
	Map<Integer, Object> values = null;
	values = (side == CBPSide.LEFT) ? leftValues : rightValues;
	Object removedValue = values.put(from, null);
	if (removedValue instanceof CBPMatchObject && isContainment) {
	    ((CBPMatchObject) removedValue).setPosition(-1, side, isContainment);
	}
	Iterator<Entry<Integer, Object>> iterator = values.entrySet().iterator();
	if (from < to) {
	    while (iterator.hasNext()) {
		Entry<Integer, Object> entry = iterator.next();
		int pos = entry.getKey();
		Object val = entry.getValue();
		if (pos > from && pos <= to) {
		    temp.put(pos - 1, val);
		    if (val instanceof CBPMatchObject && isContainment) {
			((CBPMatchObject) val).setPosition(pos - 1, side, isContainment);
		    }
		    entry.setValue(null);
		}
	    }
	} else if (from > to) {
	    while (iterator.hasNext()) {
		Entry<Integer, Object> entry = iterator.next();
		int pos = entry.getKey();
		Object val = entry.getValue();

		if (pos >= to && pos < from) {
		    temp.put(pos + 1, val);
		    if (val instanceof CBPMatchObject && isContainment) {
			((CBPMatchObject) val).setPosition(pos + 1, side, isContainment);
		    }
		    entry.setValue(null);
		}
	    }
	}
	values.put(to, value);
	if (value instanceof CBPMatchObject && isContainment) {
	    ((CBPMatchObject) value).setPosition(to, side, isContainment);
	}
	if (side == CBPSide.LEFT) {
	    if (!rightValues.containsKey(to)) {
		rightValues.put(to, null);
	    }
	} else {
	    if (!leftValues.containsKey(to)) {
		leftValues.put(to, null);
	    }
	}

	for (Entry<Integer, Object> entry : temp.entrySet()) {
	    values.put(entry.getKey(), entry.getValue());
	    if (side == CBPSide.LEFT) {
		rightValues.putIfAbsent(entry.getKey(), null);
	    } else {
		leftValues.putIfAbsent(entry.getKey(), null);
	    }
	}
	temp.clear();
    }

    public void updateValue(Object value, int position, CBPSide side) {
	Map<Integer, Object> values = null;
	values = (side == CBPSide.LEFT) ? leftValues : rightValues;
	values.put(position, value);
	if (value instanceof CBPMatchObject && isContainment) {
	    ((CBPMatchObject) value).setPosition(position, side, isContainment);
	}
	if (side == CBPSide.LEFT) {
	    rightValues.putIfAbsent(position, null);
	} else {
	    leftValues.putIfAbsent(position, null);
	}
    }

    public void addValue(Object value, int position, CBPSide side) {
	addValue(value, position, side, true);
    }

    public void addValue(Object value, int position, CBPSide side, boolean recordCBPPositionEvent) {
	if (recordCBPPositionEvent) {
	    addPositionEvent(new CBPPositionEvent(CBPPositionEventType.ADD, position, value), side);
	}
	// if (value instanceof CBPMatchObject && ((CBPMatchObject)
	// value).getOldPosition(side) == -1) {
	// updateOldPosition((CBPMatchObject) value, position, side);
	// }

	Map<Integer, Object> temp = new HashMap<>();
	Map<Integer, Object> values = null;
	values = (side == CBPSide.LEFT) ? leftValues : rightValues;
	Iterator<Entry<Integer, Object>> iterator = values.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<Integer, Object> entry = iterator.next();
	    int pos = entry.getKey();
	    Object val = entry.getValue();
	    if (value.equals(val) && isUnique) {
		return;
	    }
	    if (pos >= position) {
		temp.put(pos + 1, val);
		if (val instanceof CBPMatchObject && isContainment) {
		    ((CBPMatchObject) val).setPosition(pos + 1, side, isContainment);
		}
		entry.setValue(null);
	    }
	}
	values.put(position, value);
	if (value instanceof CBPMatchObject && isContainment) {
	    ((CBPMatchObject) value).setPosition(position, side, isContainment);
	}
	if (side == CBPSide.LEFT) {
	    if (!rightValues.containsKey(position)) {
		rightValues.put(position, null);
	    }
	} else {
	    if (!leftValues.containsKey(position)) {
		leftValues.put(position, null);
	    }
	}
	for (Entry<Integer, Object> entry : temp.entrySet()) {
	    values.put(entry.getKey(), entry.getValue());
	    if (side == CBPSide.LEFT) {
		rightValues.putIfAbsent(entry.getKey(), null);
	    } else {
		leftValues.putIfAbsent(entry.getKey(), null);
	    }
	}
	temp.clear();
    }

    public void removeValue(Object value, int position, CBPSide side) {
	removeValue(value, position, side, true);
    }

    public void removeValue(Object value, int position, CBPSide side, boolean recordCBPPositionEvent) {
	if (recordCBPPositionEvent) {
	    addPositionEvent(new CBPPositionEvent(CBPPositionEventType.REMOVE, position, value), side);
	}
	// if (value instanceof CBPMatchObject && this.isContainment()) {
	// updateOldPosition((CBPMatchObject) value, position, side);
	// }

	Map<Integer, Object> temp = new HashMap<>();
	Map<Integer, Object> values = null;
	values = (side == CBPSide.LEFT) ? leftValues : rightValues;
	Object deletedValue = values.put(position, null);
	if (deletedValue instanceof CBPMatchObject && isContainment) {
	    ((CBPMatchObject) deletedValue).setPosition(-1, side, isContainment);
	}
	Iterator<Entry<Integer, Object>> iterator = values.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<Integer, Object> entry = iterator.next();
	    int pos = entry.getKey();
	    Object val = entry.getValue();
	    if (pos > position) {
		temp.put(pos - 1, val);
		if (val instanceof CBPMatchObject && isContainment) {
		    ((CBPMatchObject) val).setPosition(pos - 1, side, isContainment);
		}
		entry.setValue(null);
	    }
	}

	for (Entry<Integer, Object> entry : temp.entrySet()) {
	    values.put(entry.getKey(), entry.getValue());
	    if (side == CBPSide.LEFT) {
		rightValues.putIfAbsent(entry.getKey(), null);
	    } else {
		leftValues.putIfAbsent(entry.getKey(), null);
	    }
	}
	temp.clear();
    }

    public int updatePosition(int position, CBPSide side) {
	int newPosition = position;
	List<CBPDiffPositionEvent> positionEvents = (side == CBPSide.LEFT) ? leftDiffPositionEvents : rightDiffPositionEvents;
	// System.out.println("size = " + positionEvents.size());
	for (CBPDiffPositionEvent positionEvent : positionEvents) {

	    Object value = (positionEvent.getValue() instanceof CBPMatchObject) ? ((CBPMatchObject) positionEvent.getValue()).getId() : positionEvent.getValue();

	    if (positionEvent.getEventType() == CBPPositionEventType.ADD) {
		if (positionEvent.getPosition() <= newPosition) {
		    newPosition = newPosition + 1;
		    // System.out.println(positionEvent.getEventType() + " at "
		    // + positionEvent.getPosition() + ": from " + position + "
		    // to " + newPosition + " value " + value);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.REMOVE) {
		if (positionEvent.getPosition() < newPosition) {
		    newPosition = newPosition - 1;
		    // System.out.println(positionEvent.getEventType() + " at "
		    // + positionEvent.getPosition() + ": from " + position + "
		    // to " + newPosition + " value " + value);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.MOVE) {
		// move from left to right
		if (positionEvent.getFrom() < positionEvent.getTo()) {
		    if (newPosition >= positionEvent.getFrom() && newPosition < positionEvent.getTo()) {
			newPosition = newPosition - 1;
			// System.out.println(positionEvent.getEventType() + "
			// origin " + positionEvent.getFrom() + " at " +
			// positionEvent.getPosition() + ": from " + position +
			// " to " + newPosition
			// + " value " + value);
		    }
		    // move from right to left
		} else if (positionEvent.getFrom() > positionEvent.getTo()) {
		    if (newPosition < positionEvent.getFrom() && newPosition >= positionEvent.getTo()) {
			newPosition = newPosition + 1;
			// System.out.println(positionEvent.getEventType() + "
			// origin " + positionEvent.getFrom() + " at " +
			// positionEvent.getPosition() + ": from " + position +
			// " to " + newPosition
			// + " value " + value);
		    }
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.MOVEIN) {
		if (positionEvent.getPosition() <= newPosition) {
		    newPosition = newPosition + 1;
		    // System.out.println(positionEvent.getEventType() + " at "
		    // + positionEvent.getPosition() + ": from " + position + "
		    // to " + newPosition + " value " + value);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.MOVEOUT) {
		if (positionEvent.getPosition() < newPosition) {
		    newPosition = newPosition - 1;
		    // System.out.println(positionEvent.getEventType() + " at "
		    // + positionEvent.getPosition() + ": from " + position + "
		    // to " + newPosition + " value " + value);
		}
	    }
	}
	return newPosition;
    }

    public int updatePositionWhenCreatingObject(int position, CBPSide side) {
	int newPosition = position;
	List<CBPPositionEvent> positionEvents = (side == CBPSide.LEFT) ? leftPositionEvents : rightPositionEvents;
	for (int i = positionEvents.size() - 1; i >= 0; i--) {
	    CBPPositionEvent positionEvent = positionEvents.get(i);

	    if (positionEvent.getEventType() == CBPPositionEventType.ADD) {
		if (positionEvent.getPosition() <= newPosition) {
		    newPosition = newPosition + 1;
		    // System.out.println(positionEvent.getEventType() + " at "
		    // + positionEvent.getPosition() + ": from " + position + "
		    // to " + newPosition);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.REMOVE) {
		if (positionEvent.getPosition() < newPosition) {
		    newPosition = newPosition - 1;
		    // System.out.println(positionEvent.getEventType() + " at "
		    // + positionEvent.getPosition() + ": from " + position + "
		    // to " + newPosition);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.MOVE) {
		// move from left to right
		if (positionEvent.getFrom() < positionEvent.getTo()) {
		    if (newPosition > positionEvent.getFrom() && newPosition <= positionEvent.getTo()) {
			newPosition = newPosition - 1;
			// System.out.println(positionEvent.getEventType() + "
			// origin " + positionEvent.getFrom() + " at " +
			// positionEvent.getPosition() + ": from " + position +
			// " to " + newPosition);
		    }
		    // move from right to left
		} else if (positionEvent.getFrom() > positionEvent.getTo()) {
		    if (newPosition < positionEvent.getFrom() && newPosition >= positionEvent.getTo()) {
			newPosition = newPosition + 1;
			// System.out.println(positionEvent.getEventType() + "
			// origin " + positionEvent.getFrom() + " at " +
			// positionEvent.getPosition() + ": from " + position +
			// " to " + newPosition);
		    }
		}
	    }
	}
	return newPosition;
    }

    private void updateOldPosition(CBPMatchObject object, int position, CBPSide side) {
	int oldPosition = position;
	oldPosition = determineOldPosition(oldPosition, side);
	object.setOldPosition(oldPosition, side);
    }

    /**
     * @param side
     * @param oldPosition
     * @return
     */
    public int determineOldPosition(int oldPosition, CBPSide side) {
	int position = oldPosition;
	List<CBPPositionEvent> positionEvents = (side == CBPSide.LEFT) ? leftPositionEvents : rightPositionEvents;
	for (CBPPositionEvent positionEvent : positionEvents) {

	    Object value = (positionEvent.getValue() instanceof CBPMatchObject) ? ((CBPMatchObject) positionEvent.getValue()).getId() : positionEvent.getValue();

	    if (positionEvent.getEventType() == CBPPositionEventType.ADD) {
		if (positionEvent.getPosition() < oldPosition) {
		    oldPosition = oldPosition - 1;
		    // System.out.println(positionEvent.getEventType() + " " +
		    // value + " to "
		    // + positionEvent.getPosition() + " -> old position from "
		    // + position
		    // + " to " + oldPosition);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.REMOVE) {
		if (positionEvent.getPosition() <= oldPosition) {
		    oldPosition = oldPosition + 1;
		    // System.out.println(positionEvent.getEventType() + " " +
		    // value + " from "
		    // + positionEvent.getPosition() + " -> old position from "
		    // + position
		    // + " to " + oldPosition);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.MOVE) {
		// move from left to right
		if (positionEvent.getFrom() < positionEvent.getTo()) {
		    if (oldPosition >= positionEvent.getFrom() && oldPosition < positionEvent.getTo()) {
			oldPosition = oldPosition + 1;
			// System.out.println(positionEvent.getEventType() + " "
			// + value + " from "
			// + positionEvent.getFrom() + " to " +
			// positionEvent.getPosition()
			// + " -> old position from " + position + " to " +
			// oldPosition);
		    }
		    // move from right to left
		} else if (positionEvent.getFrom() > positionEvent.getTo()) {
		    if (oldPosition <= positionEvent.getFrom() && oldPosition > positionEvent.getTo()) {
			oldPosition = oldPosition - 1;
			// System.out.println(positionEvent.getEventType() + " "
			// + value + " from "
			// + positionEvent.getFrom() + " to " +
			// positionEvent.getPosition()
			// + " -> old position from " + position + " to " +
			// oldPosition);
		    }
		}

		// System.out.println(positionEvent.getEventType() + " " + value
		// + " from " + positionEvent.getFrom() + " to " +
		// positionEvent.getPosition() + " -> old position from " +
		// position + " to "
		// + oldPosition);
	    }
	}
	return oldPosition;
    }

    public void addPositionEvent(CBPPositionEvent positionEvent, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftPositionEvents.add(0, positionEvent);
	} else {
	    rightPositionEvents.add(0, positionEvent);
	}
    }

    public void addAdjustPositionEvent(CBPDiffPositionEvent positionEvent, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftDiffPositionEvents.add(0, positionEvent);
	} else {
	    rightDiffPositionEvents.add(0, positionEvent);
	}
    }

    public String getOppositeFeatureName() {
	return oppositeFeatureName;
    }

    public void setOppositeFeatureName(String oppositeFeatureName) {
	this.oppositeFeatureName = oppositeFeatureName;
    }

    public Map<Integer, Boolean> getLeftIsSet() {
	return leftIsSet;
    }

    public Map<Integer, Boolean> getRightIsSet() {
	return rightIsSet;
    }

    // public boolean getIsSet(int position, CBPSide side) {
    // Boolean result = false;
    // if (side == CBPSide.LEFT) {
    // if (leftIsSet.get(position) != null) {
    // result = leftIsSet.get(position);
    // }else {
    // leftIsSet.put(position, false);
    // result = false;
    // }
    // } else {
    // if (rightIsSet.get(position) != null) {
    // result = rightIsSet.get(position);
    // }else {
    // rightIsSet.put(position, false);
    // result = false;
    // }
    // }
    // return result;
    //
    // }

    public void setIsOldSet() {
	isOldSet = true;
    }

    public boolean isOldSet() {
	return isOldSet;
    }

    public void setIsSet(CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftIsSet.put(0, true);
	} else {
	    rightIsSet.put(0, true);
	}
    }

    public void setIsSet(boolean value, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftIsSet.put(0, value);
	} else {
	    rightIsSet.put(0, value);
	}
    }

    public void setIsSet(int position, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftIsSet.put(position, true);
	} else {
	    rightIsSet.put(position, true);
	}
    }

    public void setIsSet(int position, boolean value, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftIsSet.put(position, value);
	} else {
	    rightIsSet.put(position, value);
	}
    }

    public boolean getIsSet(CBPSide side) {
	return getIsSet(0, side);
    }

    public boolean getIsSet(int position, CBPSide side) {
	Boolean result = false;
	if (side == CBPSide.LEFT) {
	    if ((result = leftIsSet.get(position)) == null) {
		leftIsSet.put(position, false);
		result = false;
	    }
	} else {
	    if ((result = rightIsSet.get(position)) == null) {
		rightIsSet.put(position, false);
		result = false;
	    }
	}
	return result;
    }

    @Override
    public String toString() {
	if (owner != null) {
	    return owner.getId() + "." + this.getName();
	} else {
	    return "null." + this.getName();
	}
    }

    public void updateMergePosition(CBPChangeEvent<?> event, CBPSide side) {
	Map<Integer, Object> vals = getValues(side);
	for (Object val : vals.values()) {
	    if (val == null)
		continue;
	    CBPMatchObject valObj = (CBPMatchObject) val;
	    int pos = valObj.getMergePosition(this, side);
	    if (event instanceof CBPRemoveFromEReferenceEvent) {
		if (pos > event.getPosition()) {
		    valObj.setMergePosition(pos - 1, this, side);
		}
	    } else if (event instanceof CBPMoveWithinEReferenceEvent) {
		CBPMoveWithinEReferenceEvent evt = (CBPMoveWithinEReferenceEvent) event;
		if (evt.getPosition() > evt.getFromPosition()) {
		    if (pos > evt.getFromPosition() && pos <= evt.getPosition()) {
			valObj.setMergePosition(pos - 1, this, side);
			// this.putValueLineNum(valObj, event.getLineNumber(),
			// side);
		    }
		} else if (event.getPosition() < evt.getFromPosition()) {
		    if (pos >= evt.getPosition() && pos < evt.getFromPosition()) {
			valObj.setMergePosition(pos + 1, this, side);
			// this.putValueLineNum(valObj, event.getLineNumber(),
			// side);
		    }
		}
	    }
	}
    }

    public Integer getPosition(CBPMatchObject cObject, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    return this.getLeftPosition(cObject);
	} else if (side == CBPSide.RIGHT) {
	    return this.getRightPosition(cObject);
	} else {
	    return this.getOriginalPosition(cObject);
	}
    }

    public Integer getOriginalPosition(CBPMatchObject cObject) {
	Integer pos = -1;
	Integer first = oldLeftValues.entrySet().stream().filter(entry -> cObject.equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
	if (first != null) {
	    pos = first;
	}
	return pos;
    }

    public Integer getLeftPosition(CBPMatchObject cObject) {
	Integer pos = -1;
	Integer first = leftValues.entrySet().stream().filter(entry -> cObject.equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
	if (first != null) {
	    pos = first;
	}
	return pos;
    }

    public Integer getRightPosition(CBPMatchObject cObject) {
	Integer pos = -1;
	Integer first = rightValues.entrySet().stream().filter(entry -> cObject.equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
	if (first != null) {
	    pos = first;
	}
	return pos;
    }

    public Set<Object> getAllValues() {
	Set<Object> values = new LinkedHashSet<>();
	values.addAll(getLeftValues().values().stream().filter(x -> x != null).collect(Collectors.toSet()));
	values.addAll(getRightValues().values().stream().filter(x -> x != null).collect(Collectors.toSet()));
	return values;
    }

    public Integer getOriginalPosition(Object object) {
	Integer pos = -1;
	Stream<Integer> stream = oldLeftValues.entrySet().stream().filter(entry -> object.equals(entry.getValue())).map(Map.Entry::getKey);
	pos = stream.findFirst().get();
	return pos;
    }

    public Integer getLeftPosition(Object object) {
	Integer pos = -1;
	Stream<Integer> stream = leftValues.entrySet().stream().filter(entry -> object.equals(entry.getValue())).map(Map.Entry::getKey);
	pos = stream.findFirst().get();
	return pos;
    }

    public Integer getRightPosition(Object object) {
	Integer pos = -1;
	Stream<Integer> stream = rightValues.entrySet().stream().filter(entry -> object.equals(entry.getValue())).map(Map.Entry::getKey);
	pos = stream.findFirst().get();
	return pos;
    }
}
