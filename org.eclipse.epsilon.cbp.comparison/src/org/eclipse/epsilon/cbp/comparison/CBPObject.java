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

    private String id;
    private CBPObject leftContainer = null;
    private CBPFeature leftContainingFeature = null;
    private CBPObject rightContainer = null;
    private CBPFeature rightContainingFeature = null;
    private Map<String, CBPFeature> features = new HashMap<>();
    private boolean leftIsCreated = false;
    private boolean leftIsDeleted = false;
    private boolean rightIsCreated = false;
    private boolean rightIsDeleted = false;
    private int leftPosition = -1;
    private int oldLeftPosition = -1;
    private int rightPosition = -1;
    private int oldRightPosition = -1;
    private Set<CBPChangeEvent<?>> events = new HashSet<>();
    private List<CBPDiff> diffs = new ArrayList<>();

    public CBPObject(String id) {
	this.id = id;
    }

    public int getOldLeftPosition() {
	return oldLeftPosition;
    }

    public void setOldLeftPosition(int oldLeftPosition) {
	this.oldLeftPosition = oldLeftPosition;
    }

    public int getOldRightPosition() {
	return oldRightPosition;
    }

    public void setOldRightPosition(int oldRightPosition) {
	this.oldRightPosition = oldRightPosition;
    }

    public CBPObject getLeftContainer() {
	return leftContainer;
    }

    public void setLeftContainer(CBPObject leftContainer) {
	this.leftContainer = leftContainer;
    }

    public CBPFeature getLeftContainingFeature() {
	return leftContainingFeature;
    }

    public void setLeftContainingFeature(CBPFeature leftContainingFeature) {
	this.leftContainingFeature = leftContainingFeature;
    }

    public CBPObject getRightContainer() {
	return rightContainer;
    }

    public void setRightContainer(CBPObject rightContainer) {
	this.rightContainer = rightContainer;
    }

    public CBPFeature getRightContainingFeature() {
	return rightContainingFeature;
    }

    public void setRightContainingFeature(CBPFeature rightContainingFeature) {
	this.rightContainingFeature = rightContainingFeature;
    }

    public boolean getLeftIsCreated() {
	return leftIsCreated;
    }

    public void setLeftIsCreated(boolean leftIsCreated) {
	this.leftIsCreated = leftIsCreated;
    }

    public boolean getLeftIsDeleted() {
	return leftIsDeleted;
    }

    public void setLeftIsDeleted(boolean leftIsDeleted) {
	this.leftIsDeleted = leftIsDeleted;
    }

    public boolean getRightIsCreated() {
	return rightIsCreated;
    }

    public void setRightIsCreated(boolean rightIsCreated) {
	this.rightIsCreated = rightIsCreated;
    }

    public boolean getRightIsDeleted() {
	return rightIsDeleted;
    }

    public void setRightIsDeleted(boolean rightIsDeleted) {
	this.rightIsDeleted = rightIsDeleted;
    }

    public int getLeftPosition() {
	return leftPosition;
    }

    public void setLeftPosition(int leftPosition) {
	this.leftPosition = leftPosition;
	if (oldLeftPosition == -1) {
	    this.oldLeftPosition = leftPosition;
	}
    }

    public int getRightPosition() {
	return rightPosition;
    }

    public void setRightPosition(int rightPosition) {
	this.rightPosition = rightPosition;
	if (oldRightPosition == -1) {
	    this.oldRightPosition = rightPosition;
	}
    }

    public boolean isCreated(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftIsCreated : rightIsCreated;
    }

    public void setCreated(boolean isCreated, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    this.leftIsCreated = isCreated;
	} else {
	    this.rightIsCreated = isCreated;
	}
    }

    public boolean isDeleted(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftIsDeleted : rightIsDeleted;
    }

    public void setDeleted(boolean isDeleted, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    this.leftIsDeleted = isDeleted;
	} else {
	    this.rightIsDeleted = isDeleted;
	}
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

    public Set<CBPChangeEvent<?>> getEvents() {
	return events;
    }

    public void setEvents(Set<CBPChangeEvent<?>> events) {
	this.events = events;
    }

    public void setContainer(CBPObject container, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftContainer = container;
	} else {
	    rightContainer = container;
	}
    }

    public Object getContainer(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftContainer : rightContainer;
    }

    public void setContainingFeature(CBPFeature containingFeature, CBPSide side) {
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
	    setLeftPosition(position);
	} else {
	    setRightPosition(position);
	}
    }
    
    public void setOldPosition(int position, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    setOldLeftPosition(position);
	} else {
	    setOldRightPosition(position);
	}
    }

    public int getPosition(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftPosition : rightPosition;
    }
    
    public int getOldPosition(CBPSide side) {
	return (side == CBPSide.LEFT) ? oldLeftPosition : oldRightPosition;
    }

    public List<CBPDiff> getDiffs() {
	return diffs;
    }

    public void setDiffs(List<CBPDiff> diffs) {
	this.diffs = diffs;
    }

}
