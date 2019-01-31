package org.eclipse.epsilon.cbp.comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public class CBPMatchObject implements Comparable<Object> {

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
    private boolean leftIsMoved = false;
    private boolean rightIsCreated = false;
    private boolean rightIsDeleted = false;
    private boolean rightIsMoved = false;

    private int leftPosition = -1;
    private int oldLeftPosition = -1;
    private int rightPosition = -1;
    private int oldRightPosition = -1;

    private List<CBPDiff> addDiffs = new ArrayList<>();
    private List<CBPDiff> deleteDiffs = new ArrayList<>();
    private List<CBPDiff> moveDiffs = new ArrayList<>();
    private List<CBPDiff> changeDiffs = new ArrayList<>();
    private List<CBPDiff> diffs = new ArrayList<>();
    private List<CBPDiff> addDiffsAsValue = new ArrayList<>();
    private List<CBPDiff> deleteDiffsAsValue = new ArrayList<>();
    private List<CBPDiff> moveDiffsAsValue = new ArrayList<>();
    private List<CBPDiff> changeDiffsAsValue = new ArrayList<>();
    private List<CBPDiff> diffsAsValue = new ArrayList<>();
    private List<CBPChangeEvent<?>> leftValueEvents = new ArrayList<>();
    private List<CBPChangeEvent<?>> rightValueEvents = new ArrayList<>();
    private List<CBPChangeEvent<?>> leftTargetEvents = new ArrayList<>();
    private List<CBPChangeEvent<?>> rightTargetEvents = new ArrayList<>();
    private List<CBPChangeEvent<?>> leftEvents = new ArrayList<>();
    private List<CBPChangeEvent<?>> rightEvents = new ArrayList<>();
    private Map<CBPMatchFeature, Integer> leftMergePosition = new HashMap<>();
    private Map<CBPMatchFeature, Integer> rightMergePosition = new HashMap<>();

    public CBPMatchObject(String className, String id) {
	this.className = className;
	this.id = id;
    }

    public void setMergePosition(int position, CBPMatchFeature feature, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    setLeftMergePosition(position, feature);
	} else {
	    setRightMergePosition(position, feature);
	}
    }

    public int getMergePosition(CBPMatchFeature feature, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    return getLeftMergePosition(feature);
	} else {
	    return getRightMergePosition(feature);
	}
    }

    public int getLeftMergePosition(CBPMatchFeature feature) {
	return leftMergePosition.get(feature);
    }

    public int getRightMergePosition(CBPMatchFeature feature) {
	return rightMergePosition.get(feature);
    }

    public void setLeftMergePosition(int position, CBPMatchFeature feature) {
	this.leftMergePosition.put(feature, position);
    }

    public void setRightMergePosition(int position, CBPMatchFeature feature) {
	this.rightMergePosition.put(feature, position);
    }

    public List<CBPChangeEvent<?>> getEvents(CBPSide side) {
	if (side == CBPSide.LEFT) {
	    return leftEvents;
	} else {
	    return rightEvents;
	}
    }

    public List<CBPChangeEvent<?>> getLeftEvents() {
	return leftEvents;
    }

    public List<CBPChangeEvent<?>> getRightEvents() {
	return rightEvents;
    }

    public List<CBPChangeEvent<?>> getValueEvents(CBPSide side) {
	if (side == CBPSide.LEFT) {
	    return leftValueEvents;
	} else {
	    return rightValueEvents;
	}
    }

    public List<CBPChangeEvent<?>> getLeftValueEvents() {
	return leftValueEvents;
    }

    public List<CBPChangeEvent<?>> getRightValueEvents() {
	return rightValueEvents;
    }

    public List<CBPChangeEvent<?>> getTargetEvents(CBPSide side) {
	if (side == CBPSide.LEFT) {
	    return leftTargetEvents;
	} else {
	    return rightTargetEvents;
	}
    }

    public List<CBPChangeEvent<?>> getLeftTargetEvents() {
	return leftTargetEvents;
    }

    public List<CBPChangeEvent<?>> getRightTargetEvents() {
	return rightTargetEvents;
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

    public void addValueEvents(CBPChangeEvent<?> event, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    this.leftValueEvents.add(event);
	} else {
	    this.rightValueEvents.add(event);
	}
    }

    public void addLeftValueEvents(CBPChangeEvent<?> event) {
	this.leftValueEvents.add(event);
    }

    public void addRightValueEvents(CBPChangeEvent<?> event) {
	this.rightValueEvents.add(event);
    }

    public void addTargetEvents(CBPChangeEvent<?> event, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    this.leftTargetEvents.add(event);
	} else {
	    this.rightTargetEvents.add(event);
	}
    }

    public void addLeftTargetEvents(CBPChangeEvent<?> event) {
	this.leftTargetEvents.add(event);
    }

    public void addRightTargetEvents(CBPChangeEvent<?> event) {
	this.rightTargetEvents.add(event);
    }

    public void setLeftPosition(int leftPosition) {
	this.leftPosition = leftPosition;
    }

    public void setRightPosition(int rightPosition) {
	this.rightPosition = rightPosition;
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

    public void setLeftPosition(int leftPosition, boolean isContainment) {
	this.leftPosition = leftPosition;
	if (oldLeftPosition == -1 && isContainment) {
	    this.oldLeftPosition = leftPosition;
	}
    }

    public int getRightPosition() {
	return rightPosition;
    }

    public void setRightPosition(int rightPosition, boolean isContainment) {
	this.rightPosition = rightPosition;
	if (oldRightPosition == -1 && isContainment) {
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

    public void setPosition(int position, CBPSide side, boolean isContainment) {
	if (side == CBPSide.LEFT) {
	    setLeftPosition(position, isContainment);
	} else {
	    setRightPosition(position, isContainment);
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

	// if (((CBPMatchObject)diff.getValue()).getId().equals("O-28309")){
	// System.out.println("");
	// }

	insertDiff(diff, this.diffs);
	// Collections.sort(diffs, new CBPDiffComparator());
    }

    private void insertDiff(CBPDiff diff, List<CBPDiff> diffs) {
	CBPDiffComparator comparator = new CBPDiffComparator();
	CBPDiff prevDiff = null;
	for (int i = 0; i < diffs.size(); i++) {
	    CBPDiff cursorDiff = diffs.get(i);
	    if (comparator.compare(diff, cursorDiff) < 0) {
		diffs.add(i, diff);
		return;
	    }
	    // do adjustment to the diff origin since its position will be
	    // shifted when merging
	    if (prevDiff != null && diff.getKind() == CBPDifferenceKind.MOVE) {
		if (prevDiff.getKind() == CBPDifferenceKind.ADD) {
		    if (prevDiff.getPosition() < diff.getOrigin()) {
			diff.setOrigin(diff.getOrigin() + 1);
		    }
		} else if (prevDiff.getKind() == CBPDifferenceKind.DELETE) {
		    if (prevDiff.getPosition() < diff.getOrigin()) {
			diff.setOrigin(diff.getOrigin() - 1);
		    }
		} else if (prevDiff.getKind() == CBPDifferenceKind.MOVE) {
		    // move down
		    if (prevDiff.getPosition() <= diff.getOrigin() && prevDiff.getOrigin() > diff.getOrigin()) {
			diff.setOrigin(diff.getOrigin() + 1);
		    }
		    // move up
		    else if (prevDiff.getOrigin() < diff.getOrigin() && prevDiff.getPosition() >= diff.getOrigin()) {
			diff.setOrigin(diff.getOrigin() - 1);
		    }
		}
	    }
	    prevDiff = cursorDiff;
	}
	diffs.add(diff);
    }

    public void addDiffAsValue(CBPDiff diff) {
	if (diff.getKind() == CBPDifferenceKind.ADD) {
	    this.addDiffsAsValue.add(diff);
	} else if (diff.getKind() == CBPDifferenceKind.DELETE) {
	    this.deleteDiffsAsValue.add(diff);
	} else if (diff.getKind() == CBPDifferenceKind.CHANGE) {
	    this.changeDiffsAsValue.add(diff);
	} else if (diff.getKind() == CBPDifferenceKind.MOVE) {
	    this.moveDiffsAsValue.add(diff);
	}
	insertDiff(diff, this.getDiffsAsValue());
	// this.diffsAsValue.add(diff);
	// Collections.sort(diffsAsValue, new CBPDiffComparator());

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

    public List<CBPDiff> getAddDiffsAsValue() {
	return addDiffsAsValue;
    }

    public void setAddDiffsAsValue(List<CBPDiff> addDiffsAsValue) {
	this.addDiffsAsValue = addDiffsAsValue;
    }

    public List<CBPDiff> getDeleteDiffsAsValue() {
	return deleteDiffsAsValue;
    }

    public void setDeleteDiffsAsValue(List<CBPDiff> deleteDiffsAsValue) {
	this.deleteDiffsAsValue = deleteDiffsAsValue;
    }

    public List<CBPDiff> getMoveDiffsAsValue() {
	return moveDiffsAsValue;
    }

    public void setMoveDiffsAsValue(List<CBPDiff> moveDiffsAsValue) {
	this.moveDiffsAsValue = moveDiffsAsValue;
    }

    public List<CBPDiff> getChangeDiffsAsValue() {
	return changeDiffsAsValue;
    }

    public void setChangeDiffsAsValue(List<CBPDiff> changeDiffsAsValue) {
	this.changeDiffsAsValue = changeDiffsAsValue;
    }

    public List<CBPDiff> getDiffsAsValue() {
	return diffsAsValue;
    }

    public void setDiffsAsValue(List<CBPDiff> diffsAsValue) {
	this.diffsAsValue = diffsAsValue;
    }

    public void setAddDiffs(List<CBPDiff> addDiffs) {
	this.addDiffs = addDiffs;
    }

    public void setDeleteDiffs(List<CBPDiff> deleteDiffs) {
	this.deleteDiffs = deleteDiffs;
    }

    public void setMoveDiffs(List<CBPDiff> moveDiffs) {
	this.moveDiffs = moveDiffs;
    }

    public void setChangeDiffs(List<CBPDiff> changeDiffs) {
	this.changeDiffs = changeDiffs;
    }

    @Override
    public String toString() {
	return this.getId();
    }

    @Override
    public int compareTo(Object o) {
	return this.toString().compareTo(o.toString());
    }
}
