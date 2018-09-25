package org.eclipse.epsilon.cbp.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public class CBPObject {

    public enum CBPSide {
	LEFT, RIGHT;
    }

    public enum CBPLifeStatus {
	DEFAULT, CREATED, DELETED
    }

    public static final Object UNIDENTIFIED_VALUE = new Object();

    private String id;
    private Object leftContainer = UNIDENTIFIED_VALUE;
    private Object leftContainingFeature = UNIDENTIFIED_VALUE;
    private Object rightContainer = UNIDENTIFIED_VALUE;
    private Object rightContainingFeature = UNIDENTIFIED_VALUE;
    private Map<String, CBPFeature> features = new HashMap<>();
    private CBPLifeStatus leftLifeStatus = CBPLifeStatus.DEFAULT;
    private CBPLifeStatus rightLifeStatus = CBPLifeStatus.DEFAULT;
    private int leftPosition = -1;
    private int rightPosition = -1;
    private Set<CBPChangeEvent<?>> events = new HashSet<>();
    private List<CBPDiff> diffs = new ArrayList<>();

    public CBPObject(String id) {
	this.id = id;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public Map<String, CBPFeature> getFeatures() {
	return features;
    }

    public void setFeatures(Map<String, CBPFeature> features) {
	this.features = features;
    }

    public CBPLifeStatus getLifeStatus(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftLifeStatus : rightLifeStatus;
    }

    public void setLifeStatus(CBPLifeStatus lifeStatus, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    this.leftLifeStatus = lifeStatus;
	} else {
	    this.rightLifeStatus = lifeStatus;
	}
    }

    public Set<CBPChangeEvent<?>> getEvents() {
	return events;
    }

    public void setEvents(Set<CBPChangeEvent<?>> events) {
	this.events = events;
    }

    public void setContainer(Object container, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftContainer = container;
	} else {
	    rightContainer = container;
	}
    }

    public Object getContainer(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftContainer : rightContainer;
    }

    public void setContainingFeature(Object containingFeature, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftContainingFeature = containingFeature;
	} else {
	    rightContainingFeature = containingFeature;
	}
    }

    public Object getContainingFeature(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftContainingFeature : rightContainingFeature;
    }

    public void setPosition(int position, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftPosition = position;
	} else {
	    rightPosition = position;
	}
    }

    public int getPosition(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftPosition : rightPosition;
    }

    public List<CBPDiff> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<CBPDiff> diffs) {
        this.diffs = diffs;
    }
    
    

}
