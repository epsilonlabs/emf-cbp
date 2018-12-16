package org.eclipse.epsilon.cbp.comparison;

import java.util.Comparator;

import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;

public class CBPDiffComparator implements Comparator<CBPDiff> {

    @Override
    public int compare(CBPDiff left, CBPDiff right) {
	int leftPos = 0;
	int rightPos = 0;

//	if (left.getValue() instanceof CBPMatchObject && right.getValue() instanceof CBPMatchObject) {
//	    if (left.getObject().getId().equals("O-50971") && right.getObject().getId().equals("O-50971")) {
//		String leftId = ((CBPMatchObject) left.getValue()).getId();
//		String rightId = ((CBPMatchObject) right.getValue()).getId();
//		System.out.println(leftId + " vs " + rightId);
//		// O-39189 vs L-4067
//		if (leftId.equals("O-52341") && rightId.equals("L-1437")) {
//		    System.out.println();
//		}
//	    }
//	}

	if (left.getKind() == CBPDifferenceKind.MOVE && left.getObject().equals(left.getOriginObject()) && left.getFeature().equals(left.getOriginFeature())) {
	    leftPos = getLowestPosition(left);
	} else {
	    leftPos = left.getPosition();
	}

	if (right.getKind() == CBPDifferenceKind.MOVE && right.getObject().equals(right.getOriginObject()) && right.getFeature().equals(right.getOriginFeature())) {
	    rightPos = getLowestPosition(right);
	} else {
	    rightPos = right.getPosition();
	}

//	if (left.getKind() == CBPDifferenceKind.MOVE && right.getKind() == CBPDifferenceKind.MOVE && left.getObject().equals(left.getOriginObject())
//		&& left.getFeature().equals(left.getOriginFeature()) && right.getObject().equals(right.getOriginObject()) && right.getFeature().equals(right.getOriginFeature())) {
//	    leftPos = left.getPosition();
//	    rightPos = right.getPosition();
//	}

	if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0 && left.getPosition() == right.getPosition()
		&& left.getKind() == right.getKind()) {
	    if (left.getValue().equals(right.getValue())) {
		return 0;
	    } else {
		return left.getValue().toString().compareTo(right.getValue().toString());
	    }
	} else if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0 && leftPos == rightPos) {
	    if (left.getKind() == CBPDifferenceKind.DELETE) {
		return -1;
	    } else if (right.getKind() == CBPDifferenceKind.DELETE) {
		return 1;
	    } else if (left.getKind() == CBPDifferenceKind.ADD) {
		return -1;
	    } else if (right.getKind() == CBPDifferenceKind.ADD) {
		return 1;
	    } else if (left.getKind() == CBPDifferenceKind.MOVE) {
		return -1;
	    } else if (right.getKind() == CBPDifferenceKind.MOVE) {
		return 1;
	    } else if (left.getKind() == CBPDifferenceKind.CHANGE) {
		return -1;
	    } else if (right.getKind() == CBPDifferenceKind.CHANGE) {
		return 1;
	    } else {
		return 0;
	    }
	} else if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0) {
	    if (leftPos < rightPos) {
		return -1;
	    } else if (leftPos > rightPos) {
		return 1;
	    } else {
		return 0;
	    }
	} else if (left.getObject().getId().compareTo(right.getObject().getId()) == 0) {
	    return left.getFeature().getName().compareTo(right.getFeature().getName());
	} else {
	    return left.getObject().getId().compareTo(right.getObject().getId());
	}

    }

    private int getLowestPosition(CBPDiff diff) {
	if (diff.getOrigin() < diff.getPosition()) {
	    return diff.getOrigin();
	} else {
	    return diff.getPosition();
	}
    }

}
