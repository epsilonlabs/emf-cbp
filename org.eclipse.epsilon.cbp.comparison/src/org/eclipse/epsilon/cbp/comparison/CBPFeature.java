package org.eclipse.epsilon.cbp.comparison;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

enum CBPFeatureType {
    ATTRIBUTE, REFERENCE
}

public class CBPFeature {

    private CBPObject owner;
    private String name;
    private Set<CBPChangeEvent<?>> events = new HashSet<>();
    private String type;
    private boolean isContainment = false;
    private CBPFeatureType featureType = CBPFeatureType.REFERENCE;
    private Map<Integer, Object> leftValues = new LinkedHashMap<Integer, Object>();
    private Map<Integer, Object> rightValues = new LinkedHashMap<Integer, Object>();

    public CBPFeature(CBPObject owner, String name, CBPFeatureType featureType, boolean isContainer) {
	this.owner = owner;
	this.name = name;
	this.featureType = featureType;
	this.isContainment = isContainer;
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

    public CBPFeatureType getFeatureType() {
	return featureType;
    }

    public void setFeatureType(CBPFeatureType featureType) {
	this.featureType = featureType;
    }

    public Object getValue(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftValues.get(0) : leftValues.get(0);
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
	Map<Integer, Object> temp = new LinkedHashMap<>();
	Map<Integer, Object> values = null;
	values = (side == CBPSide.LEFT) ? leftValues : rightValues;
	values.remove(from);
	Iterator<Entry<Integer, Object>> iterator = values.entrySet().iterator();
	if (from < to) {
	    while (iterator.hasNext()) {
		Entry<Integer, Object> entry = iterator.next();
		int pos = entry.getKey();
		Object val = entry.getValue();
		if (pos > from && pos <= to) {
		    temp.put(pos - 1, val);
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
		    entry.setValue(null);
		}
	    }
	}
	values.put(to, value);
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

    public void addValue(Object value, int position, CBPSide side) {
	Map<Integer, Object> temp = new LinkedHashMap<>();
	Map<Integer, Object> values = null;
	values = (side == CBPSide.LEFT) ? leftValues : rightValues;
	Iterator<Entry<Integer, Object>> iterator = values.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<Integer, Object> entry = iterator.next();
	    int pos = entry.getKey();
	    Object val = entry.getValue();
	    if (pos >= position) {
		temp.put(pos + 1, val);
		entry.setValue(null);
	    }
	}
	values.put(position, value);
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
	Map<Integer, Object> temp = new LinkedHashMap<>();
	Map<Integer, Object> values = null;
	values = (side == CBPSide.LEFT) ? leftValues : rightValues;
	values.remove(position);
	Iterator<Entry<Integer, Object>> iterator = values.entrySet().iterator();
	while (iterator.hasNext()) {
	    Entry<Integer, Object> entry = iterator.next();
	    int pos = entry.getKey();
	    Object val = entry.getValue();
	    if (pos >= position) {
		temp.put(pos - 1, val);
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
}
