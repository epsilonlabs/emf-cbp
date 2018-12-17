package org.eclipse.epsilon.cbp.comparison;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.epsilon.cbp.comparison.CBPMatchObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

enum CBPFeatureType {
    ATTRIBUTE, REFERENCE
}

enum CBPPositionEventType {
    UNDEFINED, ADD, REMOVE, MOVE, MOVEIN, MOVEOUT
}

public class CBPMatchFeature {

    private CBPMatchObject owner;
    private String oppositeFeatureName;
    private String name;
    private Set<CBPChangeEvent<?>> events = new HashSet<>();
    private String type;
    private boolean isContainment = false;
    private boolean isMany = false;
    private CBPFeatureType featureType = CBPFeatureType.REFERENCE;
    private Map<Integer, Boolean> leftIsSet = new TreeMap<Integer, Boolean>();
    private Map<Integer, Boolean> rightIsSet = new TreeMap<Integer, Boolean>();
    private Map<Integer, Object> leftValues = new TreeMap<Integer, Object>();
    private Map<Integer, Object> rightValues = new TreeMap<Integer, Object>();
    private Map<Integer, Object> oldLeftValues = new TreeMap<Integer, Object>();
    private Map<Integer, Object> oldRightValues = new TreeMap<Integer, Object>();
    private List<CBPPositionEvent> leftPositionEvents = new ArrayList<>();
    private List<CBPPositionEvent> rightPositionEvents = new ArrayList<>();
    private List<CBPDiffPositionEvent> leftDiffPositionEvents = new ArrayList<>();
    private List<CBPDiffPositionEvent> rightDiffPositionEvents = new ArrayList<>();

    public CBPMatchFeature(CBPMatchObject owner, String name, CBPFeatureType featureType, boolean isContainer, boolean isMany) {
	this.owner = owner;
	this.name = name;
	this.featureType = featureType;
	this.isContainment = isContainer;
	this.isMany = isMany;
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

    public Set<CBPChangeEvent<?>> getEvents() {
	return events;
    }

    public void setEvents(Set<CBPChangeEvent<?>> events) {
	this.events = events;
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
	if (side == CBPSide.LEFT) {
	    if (oldLeftValues.get(0) == null) {
		oldLeftValues.put(0, value);
		if (!oldRightValues.containsKey(0)) {
		    oldRightValues.put(0, value);
		    // rightValues.put(0, value);
		}
	    }
	} else {
	    if (oldRightValues.get(0) == null) {
		oldRightValues.put(0, value);
		if (!oldLeftValues.containsKey(0)) {
		    oldLeftValues.put(0, value);
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
	if (value instanceof CBPMatchObject) {
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

}
