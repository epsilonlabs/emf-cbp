package org.eclipse.epsilon.cbp.merging;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.epsilon.cbp.comparison.CBPDiff;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.eclipse.epsilon.cbp.comparison.CBPMatchFeature;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEObject;

public class CBPDependencyDeterminator {

    public void determineDependencies(List<CBPDiff> diffs) {

	Set<CBPDiff> alreadyComparedDiffs = new HashSet<>();

	for (CBPDiff leftDiff : diffs) {
	    for (CBPDiff rightDiff : diffs) {
		if (leftDiff.equals(rightDiff) || alreadyComparedDiffs.contains(rightDiff)) {
		    continue;
		}

		// ADD
		if (leftDiff.getKind() == CBPDifferenceKind.ADD && rightDiff.getKind() == CBPDifferenceKind.ADD) {
		    handleAddVsAddDiff(leftDiff, rightDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.ADD && rightDiff.getKind() == CBPDifferenceKind.CHANGE) {
		    handleAddVsChangeDiff(leftDiff, rightDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.ADD && rightDiff.getKind() == CBPDifferenceKind.MOVE) {
		    handleAddVsMoveDiff(leftDiff, rightDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.ADD && rightDiff.getKind() == CBPDifferenceKind.DELETE) {
		    handleAddVsDeleteDiff(leftDiff, rightDiff);
		}
		// DELETE
		else if (leftDiff.getKind() == CBPDifferenceKind.DELETE && rightDiff.getKind() == CBPDifferenceKind.DELETE) {
		    handleDeleteVsDeleteDiff(leftDiff, rightDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.DELETE && rightDiff.getKind() == CBPDifferenceKind.ADD) {
		    handleAddVsDeleteDiff(rightDiff, leftDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.DELETE && rightDiff.getKind() == CBPDifferenceKind.CHANGE) {
		    handleDeleteVsChangeDiff(leftDiff, rightDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.DELETE && rightDiff.getKind() == CBPDifferenceKind.MOVE) {
		    handleDeleteVsMoveDiff(leftDiff, rightDiff);
		}
		// CHANGE
		else if (leftDiff.getKind() == CBPDifferenceKind.CHANGE && rightDiff.getKind() == CBPDifferenceKind.CHANGE) {
		    handleChangeVsChangeDiff(leftDiff, rightDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.CHANGE && rightDiff.getKind() == CBPDifferenceKind.ADD) {
		    handleAddVsChangeDiff(rightDiff, leftDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.CHANGE && rightDiff.getKind() == CBPDifferenceKind.DELETE) {
		    handleDeleteVsChangeDiff(rightDiff, leftDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.CHANGE && rightDiff.getKind() == CBPDifferenceKind.MOVE) {
		    handleChangeVsMoveDiff(leftDiff, rightDiff);
		}
		// MOVE
		else if (leftDiff.getKind() == CBPDifferenceKind.MOVE && rightDiff.getKind() == CBPDifferenceKind.CHANGE) {
		    handleChangeVsMoveDiff(rightDiff, leftDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.MOVE && rightDiff.getKind() == CBPDifferenceKind.ADD) {
		    handleAddVsMoveDiff(rightDiff, leftDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.MOVE && rightDiff.getKind() == CBPDifferenceKind.DELETE) {
		    handleDeleteVsMoveDiff(rightDiff, leftDiff);
		} else if (leftDiff.getKind() == CBPDifferenceKind.MOVE && rightDiff.getKind() == CBPDifferenceKind.MOVE) {
		    handleMoveVsMoveDiff(leftDiff, rightDiff);
		}
	    }

	    alreadyComparedDiffs.add(leftDiff);
	}

    }

    private void handleMoveVsMoveDiff(CBPDiff leftDiff, CBPDiff rightDiff) {
	// if (leftDiff.getObject().equals(rightDiff.getOriginObject()) &&
	// leftDiff.getFeature().equals(rightDiff.getOriginFeature()) &&
	// leftDiff.getPosition() == rightDiff.getOrigin()) {
	// leftDiff.getRequiresDiffs().add(rightDiff);
	// rightDiff.getRequiredByDiffs().add(leftDiff);
	// } else if (rightDiff.getObject().equals(leftDiff.getOriginObject())
	// && rightDiff.getFeature().equals(leftDiff.getOriginFeature()) &&
	// rightDiff.getPosition() == leftDiff.getOrigin()) {
	// rightDiff.getRequiresDiffs().add(leftDiff);
	// leftDiff.getRequiredByDiffs().add(rightDiff);
	// }
	//
	// else
	if (leftDiff.getObject().equals(rightDiff.getObject()) && leftDiff.getFeature().equals(rightDiff.getFeature())) {
	    CBPMatchObject leftValue = (CBPMatchObject) leftDiff.getValue();
	    CBPMatchObject rightValue = (CBPMatchObject) rightDiff.getValue();
	    Integer leftLineNum = leftDiff.getFeature().getLeftValueLineNum(leftValue);
	    Integer rightLineNum = rightDiff.getFeature().getLeftValueLineNum(rightValue);
	    if (leftLineNum != null && rightLineNum != null) {
		if (leftLineNum > rightLineNum) {
		    leftDiff.getRequiresDiffs().add(rightDiff);
		    rightDiff.getRequiredByDiffs().add(leftDiff);
		} else {
		    rightDiff.getRequiresDiffs().add(leftDiff);
		    leftDiff.getRequiredByDiffs().add(rightDiff);
		}
	    }
	}

    }

    private void handleChangeVsMoveDiff(CBPDiff leftDiff, CBPDiff rightDiff) {
	// this condition is free of dependency
    }

    private void handleChangeVsChangeDiff(CBPDiff leftDiff, CBPDiff rightDiff) {
	// this condition is free of dependency
    }

    private void handleDeleteVsMoveDiff(CBPDiff deleteDiff, CBPDiff moveDiff) {
	if (deleteDiff.getValue().equals(moveDiff.getObject())) {
	    deleteDiff.getRequiresDiffs().add(moveDiff);
	    moveDiff.getRequiredByDiffs().add(deleteDiff);
	} else if (deleteDiff.getValue().equals(moveDiff.getValue())) {
	    deleteDiff.getRequiresDiffs().add(moveDiff);
	    moveDiff.getRequiredByDiffs().add(deleteDiff);
	} else if (deleteDiff.getValue().equals(moveDiff.getOriginObject())) {
	    deleteDiff.getRequiresDiffs().add(moveDiff);
	    moveDiff.getRequiredByDiffs().add(deleteDiff);
	}
    }

    private void handleDeleteVsChangeDiff(CBPDiff deleteDiff, CBPDiff changeDiff) {
	if (deleteDiff.getValue().equals(changeDiff.getObject())) {
	    deleteDiff.getRequiresDiffs().add(changeDiff);
	    changeDiff.getRequiredByDiffs().add(deleteDiff);
	} else if (deleteDiff.getValue().equals(changeDiff.getValue())) {
	    deleteDiff.getRequiresDiffs().add(changeDiff);
	    changeDiff.getRequiredByDiffs().add(deleteDiff);
	}
    }

    private void handleDeleteVsDeleteDiff(CBPDiff leftDeleteDiff, CBPDiff rightDeleteDiff) {
	if (rightDeleteDiff.getObject().equals(leftDeleteDiff.getValue())) {
	    leftDeleteDiff.getRequiresDiffs().add(rightDeleteDiff);
	    rightDeleteDiff.getRequiredByDiffs().add(leftDeleteDiff);
	} else if (leftDeleteDiff.getObject().equals(rightDeleteDiff.getValue())) {
	    rightDeleteDiff.getRequiresDiffs().add(leftDeleteDiff);
	    leftDeleteDiff.getRequiredByDiffs().add(rightDeleteDiff);
	}

    }

    private void handleAddVsDeleteDiff(CBPDiff addDiff, CBPDiff deleteDiff) {
	if (deleteDiff.getValue().equals(addDiff.getObject())) {
	    deleteDiff.getRequiresDiffs().add(addDiff);
	    addDiff.getRequiredByDiffs().add(deleteDiff);
	} else if (deleteDiff.getValue().equals(addDiff.getValue())) {
	    deleteDiff.getRequiresDiffs().add(addDiff);
	    addDiff.getRequiredByDiffs().add(deleteDiff);
	} 
//	else if (addDiff.getObject().equals(deleteDiff.getObject()) && addDiff.getFeature().equals(deleteDiff.getFeature()) && addDiff.getPosition() == deleteDiff.getPosition()) {
//	    if (deleteDiff.getValue() instanceof CBPMatchObject) {
//		CBPMatchObject x = ((CBPMatchObject) deleteDiff.getValue());
//		if (x.getId().equals("R-520")) {
//		    System.console();
//		}
//	    }

	    // if (addDiff.getValue() instanceof CBPMatchObject &&
	    // deleteDiff.getValue() instanceof CBPMatchObject) {
	    // CBPMatchFeature feature = addDiff.getFeature();
	    // List<CBPDiff> diffs = feature.getDiffs();
	    // for (CBPDiff diff : diffs) {
	    // Integer addPos = ((CBPMatchObject)
	    // addDiff.getValue()).getLeftMergePosition(feature);
	    // Integer diffPos = ((CBPMatchObject)
	    // diff.getValue()).getLeftMergePosition(feature);
	    // if ( addPos != null && diffPos != null && diffPos > addPos) {
	    // addDiff.getRequiresDiffs().add(deleteDiff);
	    // deleteDiff.getRequiredByDiffs().add(addDiff);
	    // break;
	    // }
	    // }
	    //
	    // }

	    // addDiff.getRequiresDiffs().add(deleteDiff);
	    // deleteDiff.getRequiredByDiffs().add(addDiff);
	    // deleteDiff.getRequiresDiffs().add(addDiff);
	    // addDiff.getRequiredByDiffs().add(deleteDiff);

//	}
    }

    private void handleAddVsMoveDiff(CBPDiff addDiff, CBPDiff moveDiff) {
	if (moveDiff.getObject().equals(addDiff.getValue())) {
	    moveDiff.getRequiresDiffs().add(addDiff);
	    addDiff.getRequiredByDiffs().add(moveDiff);
	} else if (moveDiff.getValue().equals(addDiff.getValue())) {
	    moveDiff.getRequiresDiffs().add(addDiff);
	    addDiff.getRequiredByDiffs().add(moveDiff);
	} else if (moveDiff.getObject().equals(addDiff.getObject()) && moveDiff.getFeature().equals(addDiff.getFeature())) {
	    CBPMatchObject leftValue = (CBPMatchObject) moveDiff.getValue();
	    CBPMatchObject rightValue = (CBPMatchObject) addDiff.getValue();
	    Integer leftLineNum = moveDiff.getFeature().getLeftValueLineNum(leftValue);
	    Integer rightLineNum = addDiff.getFeature().getLeftValueLineNum(rightValue);
	    if (leftLineNum != null && rightLineNum != null) {
		if (leftLineNum > rightLineNum) {
		    moveDiff.getRequiresDiffs().add(addDiff);
		    addDiff.getRequiredByDiffs().add(moveDiff);
		} else {
		    addDiff.getRequiresDiffs().add(moveDiff);
		    moveDiff.getRequiredByDiffs().add(addDiff);
		}
	    }
	}

    }

    private void handleAddVsChangeDiff(CBPDiff addDiff, CBPDiff changeDiff) {
	if (changeDiff.getObject().equals(addDiff.getValue())) {
	    changeDiff.getRequiresDiffs().add(addDiff);
	    addDiff.getRequiredByDiffs().add(changeDiff);
	} else if (changeDiff.getValue().equals(addDiff.getValue())) {
	    changeDiff.getRequiresDiffs().add(addDiff);
	    addDiff.getRequiredByDiffs().add(changeDiff);
	}
    }

    private void handleAddVsAddDiff(CBPDiff leftDiff, CBPDiff rightDiff) {
	if (rightDiff.getObject().equals(leftDiff.getValue())) {
	    rightDiff.getRequiresDiffs().add(leftDiff);
	    leftDiff.getRequiredByDiffs().add(rightDiff);
	} else if (leftDiff.getObject().equals(rightDiff.getValue())) {
	    leftDiff.getRequiresDiffs().add(rightDiff);
	    rightDiff.getRequiredByDiffs().add(leftDiff);
	} else if (leftDiff.getObject().equals(rightDiff.getObject()) && leftDiff.getFeature().equals(rightDiff.getFeature())) {
	    CBPMatchObject leftValue = (CBPMatchObject) leftDiff.getValue();
	    CBPMatchObject rightValue = (CBPMatchObject) rightDiff.getValue();
	    Integer leftLineNum = leftDiff.getFeature().getLeftValueLineNum(leftValue);
	    Integer rightLineNum = rightDiff.getFeature().getLeftValueLineNum(rightValue);
	    if (leftLineNum != null && rightLineNum != null) {
		if (leftLineNum > rightLineNum) {
		    leftDiff.getRequiresDiffs().add(rightDiff);
		    rightDiff.getRequiredByDiffs().add(leftDiff);
		} else {
		    rightDiff.getRequiresDiffs().add(leftDiff);
		    leftDiff.getRequiredByDiffs().add(rightDiff);
		}
	    }
	} else if (leftDiff.getValue().equals(rightDiff.getValue()) && leftDiff.getFeature().isContainment()) {
	    rightDiff.getRequiresDiffs().add(leftDiff);
	    leftDiff.getRequiredByDiffs().add(rightDiff);
	} else if (leftDiff.getValue().equals(rightDiff.getValue()) && rightDiff.getFeature().isContainment()) {
	    leftDiff.getRequiresDiffs().add(rightDiff);
	    rightDiff.getRequiredByDiffs().add(leftDiff);
	}

    }

}
