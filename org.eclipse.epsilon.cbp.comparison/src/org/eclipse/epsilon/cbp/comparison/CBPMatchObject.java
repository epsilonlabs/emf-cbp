package org.eclipse.epsilon.cbp.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public class CBPMatchObject {

    public enum CBPSide {
	LEFT, RIGHT;
    }

    private String id;
    private String className = null;
    private boolean diffed = false;
    private boolean inMergedAdded = false; 
    private CBPMatchObject leftContainer = null;
    private CBPMatchFeature leftContainingFeature = null;
    private CBPMatchObject oldLeftContainer = null;
    private CBPMatchFeature oldLeftContainingFeature = null;
    private CBPMatchObject rightContainer = null;
    private CBPMatchFeature rightContainingFeature = null;
    private CBPMatchObject oldRightContainer = null;
    private CBPMatchFeature oldRightContainingFeature = null;
    private Map<String, CBPMatchFeature> features = new HashMap<>();
    private boolean leftIsCreated = false;
    private boolean leftIsDeleted = false;
    private boolean leftIsMoved= false;
    private boolean rightIsCreated = false;
    private boolean rightIsDeleted = false;
    private boolean rightIsMoved= false;
    private int leftPosition = -1;
    private int oldLeftPosition = -1;
    private int rightPosition = -1;
    private int oldRightPosition = -1;
    private Set<CBPChangeEvent<?>> events = new HashSet<>();
    private List<CBPDiff> addDiffs = new ArrayList<>();
    private List<CBPDiff> deleteDiffs = new ArrayList<>();
    private List<CBPDiff> moveDiffs = new ArrayList<>();
    private List<CBPDiff> changeDiffs = new ArrayList<>();
    private List<CBPDiff> diffs = new ArrayList<>();

    public CBPMatchObject(String className, String id) {
	this.className = className;
	this.id = id;
    }

    public int getOldLeftPosition() {
	return oldLeftPosition;
    }

    public void setOldLeftPosition(int oldLeftPosition) {
	this.oldLeftPosition = oldLeftPosition;
	if (this.oldRightPosition == -1) {
	    oldRightPosition = oldLeftPosition;
	}
    }

    public int getOldRightPosition() {
	return oldRightPosition;
    }

    public void setOldRightPosition(int oldRightPosition) {
	this.oldRightPosition = oldRightPosition;
	if (this.oldLeftPosition == -1) {
	    oldLeftPosition = oldRightPosition;
	}
    }

    public CBPMatchObject getOldLeftContainer() {
	return oldLeftContainer;
    }

    public void setOldLeftContainer(CBPMatchObject oldLeftContainer) {
	this.oldLeftContainer = oldLeftContainer;
    }

    public CBPMatchFeature getOldLeftContainingFeature() {
	return oldLeftContainingFeature;
    }

    public void setOldLeftContainingFeature(CBPMatchFeature oldLeftContainingFeature) {
	this.oldLeftContainingFeature = oldLeftContainingFeature;
    }

    public CBPMatchObject getOldRightContainer() {
	return oldRightContainer;
    }

    public void setOldRightContainer(CBPMatchObject oldRightContainer) {
	this.oldRightContainer = oldRightContainer;
    }

    public CBPMatchFeature getOldRightContainingFeature() {
	return oldRightContainingFeature;
    }

    public void setOldRightContainingFeature(CBPMatchFeature oldRightContainingFeature) {
	this.oldRightContainingFeature = oldRightContainingFeature;
    }

    public CBPMatchObject getLeftContainer() {
	return leftContainer;
    }

    public void setLeftContainer(CBPMatchObject leftContainer) {
	this.leftContainer = leftContainer;
    }

    public CBPMatchFeature getLeftContainingFeature() {
	return leftContainingFeature;
    }

    public void setLeftContainingFeature(CBPMatchFeature leftContainingFeature) {
	this.leftContainingFeature = leftContainingFeature;
    }

    public CBPMatchObject getRightContainer() {
	return rightContainer;
    }

    public void setRightContainer(CBPMatchObject rightContainer) {
	this.rightContainer = rightContainer;
    }

    public CBPMatchFeature getRightContainingFeature() {
	return rightContainingFeature;
    }

    public void setRightContainingFeature(CBPMatchFeature rightContainingFeature) {
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
    
    public boolean isMoved(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftIsMoved : rightIsMoved;
    }
    
    public void setMoved(boolean isMoved, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    this.leftIsMoved = isMoved;
	} else {
	    this.rightIsMoved = isMoved;
	}
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public Map<String, CBPMatchFeature> getFeatures() {
	return features;
    }

    public void setFeatures(Map<String, CBPMatchFeature> features) {
	this.features = features;
    }

    public Set<CBPChangeEvent<?>> getEvents() {
	return events;
    }

    public void setEvents(Set<CBPChangeEvent<?>> events) {
	this.events = events;
    }

    public void setContainer(CBPMatchObject container, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftContainer = container;
	    if (oldLeftContainer == null)
		oldLeftContainer = container;
	} else {
	    rightContainer = container;
	    if (oldRightContainer == null)
		oldRightContainer = container;
	}
    }

    public Object getContainer(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftContainer : rightContainer;
    }

    public void setContainingFeature(CBPMatchFeature containingFeature, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    leftContainingFeature = containingFeature;
	    if (oldLeftContainingFeature == null)
		oldLeftContainingFeature = containingFeature;
	} else {
	    rightContainingFeature = containingFeature;
	    if (oldRightContainingFeature == null)
		oldRightContainingFeature = containingFeature;
	}
    }

    public void setOldContainer(CBPMatchObject oldContainer, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    oldLeftContainer = oldContainer;
	} else {
	    oldRightContainer = oldContainer;
	}
    }

    public Object getOldContainer(CBPSide side) {
	return (side == CBPSide.LEFT) ? oldLeftContainer : oldRightContainer;
    }

    public void setOldContainingFeature(CBPMatchFeature oldContainingFeature, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    oldLeftContainingFeature = oldContainingFeature;
	} else {
	    oldRightContainingFeature = oldContainingFeature;
	}
    }

    public Object getContainingFeature(CBPSide side) {
	return (side == CBPSide.LEFT) ? leftContainingFeature : rightContainingFeature;
    }

    public Object getOldContainingFeature(CBPSide side) {
	return (side == CBPSide.LEFT) ? oldLeftContainingFeature : oldRightContainingFeature;
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

    public void addDiff(CBPDiff diff) {
	if (diff.getKind() == CBPDifferenceKind.ADD) {
	    this.addDiffs.add(diff);
	} else if (diff.getKind() == CBPDifferenceKind.DELETE) {
	    this.deleteDiffs.add(diff);
	} else if (diff.getKind() == CBPDifferenceKind.CHANGE) {
	    this.changeDiffs.add(diff);
	} else if (diff.getKind() == CBPDifferenceKind.MOVE) {
	    this.moveDiffs.add(diff);
	}
	this.diffs.add(diff);

    }

    public List<CBPDiff> getAddDiffs() {
        return addDiffs;
    }

    public List<CBPDiff> getDeleteDiffs() {
        return deleteDiffs;
    }

    public List<CBPDiff> getMoveDiffs() {
        return moveDiffs;
    }

    public List<CBPDiff> getChangeDiffs() {
        return changeDiffs;
    }

    public List<CBPDiff> getDiffs() {
	return diffs;
    }

    public void setDiffs(List<CBPDiff> diffs) {
	this.diffs = diffs;
    }

    public boolean isDiffed() {
	return diffed;
    }

    public void setDiffed(boolean diffed) {
	this.diffed = diffed;
    }

    public String getClassName() {
	return className;
    }

    public void setClassName(String className) {
	this.className = className;
    }

    public boolean isInMergedAdded() {
        return inMergedAdded;
    }

    public void setInMergedAdded(boolean inMergedAdded) {
        this.inMergedAdded = inMergedAdded;
    }

    public boolean isLeftIsMoved() {
        return leftIsMoved;
    }

    public void setLeftIsMoved(boolean leftIsMoved) {
        this.leftIsMoved = leftIsMoved;
    }

    public boolean isRightIsMoved() {
        return rightIsMoved;
    }

    public void setRightIsMoved(boolean rightIsMoved) {
        this.rightIsMoved = rightIsMoved;
    }

}
