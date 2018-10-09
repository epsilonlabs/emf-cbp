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

import org.eclipse.epsilon.cbp.comparison.CBPObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

enum CBPFeatureType {
    ATTRIBUTE, REFERENCE
}

enum CBPPositionEventType {
    UNDEFINED, ADD, REMOVE, MOVE
}

public class CBPFeature {

    private CBPObject owner;
    private String name;
    private Set<CBPChangeEvent<?>> events = new HashSet<>();
    private String type;
    private boolean isContainment = false;
    private boolean isMany = false;
    private CBPFeatureType featureType = CBPFeatureType.REFERENCE;
    private Map<Integer, Object> leftValues = new HashMap<Integer, Object>();
    private Map<Integer, Object> rightValues = new HashMap<Integer, Object>();
    private Map<Integer, Object> oldLeftValues = new HashMap<Integer, Object>();
    private Map<Integer, Object> oldRightValues = new HashMap<Integer, Object>();
    private List<CBPPositionEvent> leftPositionEvents = new ArrayList<>();
    private List<CBPPositionEvent> rightPositionEvents = new ArrayList<>();
    private List<CBPDiffPositionEvent> leftDiffPositionEvents = new ArrayList<>();
    private List<CBPDiffPositionEvent> rightDiffPositionEvents = new ArrayList<>();

    public CBPFeature(CBPObject owner, String name, CBPFeatureType featureType, boolean isContainer, boolean isMany) {
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

    public CBPObject getOwner() {
	return owner;
    }

    public void setOwner(CBPObject owner) {
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

    public void setLeftValues(Map<Integer, Object> leftValues) {
	this.leftValues = leftValues;
    }

    public Map<Integer, Object> getRightValues() {
	return rightValues;
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
		    oldRightValues.put(0, null);
		}
	    }
	} else {
	    if (oldRightValues.get(0) == null) {
		oldRightValues.put(0, value);
		if (!oldLeftValues.containsKey(0)) {
		    oldLeftValues.put(0, null);
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
	if (value instanceof CBPObject) {
	    updateOldPosition((CBPObject) value, from, side);
	}
	addPositionEvent(new CBPPositionEvent(CBPPositionEventType.MOVE, to, from), side);

	Map<Integer, Object> temp = new HashMap<>();
	Map<Integer, Object> values = null;
	values = (side == CBPSide.LEFT) ? leftValues : rightValues;
	Object removedValue = values.put(from, null);
	if (removedValue instanceof CBPObject) {
	    ((CBPObject) removedValue).setPosition(-1, side);
	}
	Iterator<Entry<Integer, Object>> iterator = values.entrySet().iterator();
	if (from < to) {
	    while (iterator.hasNext()) {
		Entry<Integer, Object> entry = iterator.next();
		int pos = entry.getKey();
		Object val = entry.getValue();
		if (pos > from && pos <= to) {
		    temp.put(pos - 1, val);
		    if (val instanceof CBPObject) {
			((CBPObject) val).setPosition(pos - 1, side);
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
		    if (val instanceof CBPObject) {
			((CBPObject) val).setPosition(pos + 1, side);
		    }
		    entry.setValue(null);
		}
	    }
	}
	values.put(to, value);
	if (value instanceof CBPObject) {
	    ((CBPObject) value).setPosition(to, side);
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
    }

    public void addValue(Object value, int position, CBPSide side) {
	addPositionEvent(new CBPPositionEvent(CBPPositionEventType.ADD, position), side);
	// if (value instanceof CBPObject && ((CBPObject)
	// value).getOldPosition(side) == -1) {
	// updateOldPosition((CBPObject) value, position, side);
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
		if (val instanceof CBPObject) {
		    ((CBPObject) val).setPosition(pos + 1, side);
		}
		entry.setValue(null);
	    }
	}
	values.put(position, value);
	if (value instanceof CBPObject) {
	    ((CBPObject) value).setPosition(position, side);
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
	addPositionEvent(new CBPPositionEvent(CBPPositionEventType.REMOVE, position), side);
	if (value instanceof CBPObject) {
	    updateOldPosition((CBPObject) value, position, side);
	}

	Map<Integer, Object> temp = new HashMap<>();
	Map<Integer, Object> values = null;
	values = (side == CBPSide.LEFT) ? leftValues : rightValues;
	Object deletedValue = values.put(position, null);
	if (deletedValue instanceof CBPObject) {
	    ((CBPObject) deletedValue).setPosition(-1, side);
	}
	Iterator<Entry<Integer, Object>> iterator = values.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<Integer, Object> entry = iterator.next();
	    int pos = entry.getKey();
	    Object val = entry.getValue();
	    if (pos > position) {
		temp.put(pos - 1, val);
		if (val instanceof CBPObject) {
		    ((CBPObject) val).setPosition(pos - 1, side);
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
	for (CBPDiffPositionEvent positionEvent : positionEvents) {
	    if (positionEvent.getEventType() == CBPPositionEventType.ADD) {
		if (positionEvent.getPosition() <= newPosition) {
		    newPosition = newPosition + 1;
		    System.out.println(positionEvent.getEventType() + " at " + positionEvent.getPosition() + ": from " + position + " to " + newPosition);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.REMOVE) {
		if (positionEvent.getPosition() < newPosition) {
		    newPosition = newPosition - 1;
		    System.out.println(positionEvent.getEventType() + " at " + positionEvent.getPosition() + ": from " + position + " to " + newPosition);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.MOVE) {
		// move from left to right
		if (positionEvent.getFrom() < positionEvent.getTo()) {
		    if (newPosition >= positionEvent.getFrom() && newPosition < positionEvent.getTo()) {
			newPosition = newPosition - 1;
			System.out.println(positionEvent.getEventType() + " origin " + positionEvent.getFrom() + " at " + positionEvent.getPosition() + ": from " + position + " to " + newPosition);
		    }
		    // move from right to left
		} else if (positionEvent.getFrom() > positionEvent.getTo()) {
		    if (newPosition < positionEvent.getFrom() && newPosition >= positionEvent.getTo()) {
			newPosition = newPosition + 1;
			System.out.println(positionEvent.getEventType() + " origin " + positionEvent.getFrom() + " at " + positionEvent.getPosition() + ": from " + position + " to " + newPosition);
		    }
		}
	    }
	}
	return newPosition;
    }

    private void updateOldPosition(CBPObject object, int position, CBPSide side) {
	int oldPosition = position;
	List<CBPPositionEvent> positionEvents = (side == CBPSide.LEFT) ? leftPositionEvents : rightPositionEvents;
	for (CBPPositionEvent positionEvent : positionEvents) {
	    if (positionEvent.getEventType() == CBPPositionEventType.ADD) {
		if (positionEvent.getPosition() <= oldPosition) {
		    oldPosition = oldPosition - 1;
		    // System.out.println(positionEvent.getEventType() + " at "
		    // + positionEvent.getPosition() + ": from " + position + "
		    // to " + oldPosition);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.REMOVE) {
		if (positionEvent.getPosition() < oldPosition) {
		    oldPosition = oldPosition + 1;
		    // System.out.println(positionEvent.getEventType() + " at "
		    // + positionEvent.getPosition() + ": from " + position + "
		    // to " + oldPosition);
		}
	    } else if (positionEvent.getEventType() == CBPPositionEventType.MOVE) {
		// move from left to right
		if (positionEvent.getFrom() < positionEvent.getTo()) {
		    if (oldPosition >= positionEvent.getFrom() && oldPosition < positionEvent.getTo()) {
			oldPosition = oldPosition + 1;
			// System.out.println(positionEvent.getEventType() + "
			// origin " + positionEvent.getFrom() + " at " +
			// positionEvent.getPosition() + ": from " + position +
			// " to " + oldPosition);
		    }
		    // move from right to left
		} else if (positionEvent.getFrom() > positionEvent.getTo()) {
		    if (oldPosition < positionEvent.getFrom() && oldPosition >= positionEvent.getTo()) {
			oldPosition = oldPosition - 1;
			// System.out.println(positionEvent.getEventType() + "
			// origin " + positionEvent.getFrom() + " at " +
			// positionEvent.getPosition() + ": from " + position +
			// " to " + oldPosition);
		    }
		}
	    }
	}
	object.setOldPosition(oldPosition, side);
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
}
