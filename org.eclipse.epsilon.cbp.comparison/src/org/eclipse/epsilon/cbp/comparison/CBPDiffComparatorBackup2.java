package org.eclipse.epsilon.cbp.comparison;

import java.util.Comparator;

import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;

public class CBPDiffComparatorBackup2 implements Comparator<CBPDiff> {

    @Override
    public int compare(CBPDiff left, CBPDiff right) {
	int leftLowPos = -1;
	int rightLowPos = -1;
	int leftPos = left.getPosition();
	int rightPos = right.getPosition();

//	if (left.getValue() instanceof CBPMatchObject && right.getValue() instanceof CBPMatchObject) {
//	    if (left.getObject().getId().equals("O-37211") && right.getObject().getId().equals("O-37211")) {
//		String leftId = ((CBPMatchObject) left.getValue()).getId();
//		String rightId = ((CBPMatchObject) right.getValue()).getId();
//		System.out.println(leftId + " vs " + rightId);
//		// O-39189 vs L-4067
//		if ((leftId.equals("L-5265") && rightId.equals("O-11063")) ||
//			(leftId.equals("O-11063") && rightId.equals("L-5265"))) {
//		    System.out.println();
//		}
//	    }
//	}

	if (left.getKind() == CBPDifferenceKind.MOVE && left.getObject().equals(left.getOriginObject()) && left.getFeature().equals(left.getOriginFeature())) {
	    leftLowPos = getLowestPosition(left);
	} else {
	    leftLowPos = left.getPosition();
	}

	if (right.getKind() == CBPDifferenceKind.MOVE && right.getObject().equals(right.getOriginObject()) && right.getFeature().equals(right.getOriginFeature())) {
	    rightLowPos = getLowestPosition(right);
	} else {
	    rightLowPos = right.getPosition();
	}

	// if (left.getKind() == CBPDifferenceKind.MOVE && right.getKind() ==
	// CBPDifferenceKind.MOVE &&
	// left.getObject().equals(left.getOriginObject())
	// && left.getFeature().equals(left.getOriginFeature()) &&
	// right.getObject().equals(right.getOriginObject()) &&
	// right.getFeature().equals(right.getOriginFeature())) {
	// leftPos = left.getPosition();
	// rightPos = right.getPosition();
	// }

	if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0 && left.getPosition() == right.getPosition()
		&& left.getKind() == right.getKind()) {
	    if (left.getValue().equals(right.getValue())) {
		return 0;
	    } else {
		return left.getValue().toString().compareTo(right.getValue().toString());
	    }
	} else if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0
		&& left.getKind() == right.getKind()) {
	    if (leftPos < rightPos) {
		return -1;
	    } else if (leftPos > rightPos) {
		return 1;
	    } else {
		return 0;
	    }
	} else if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0) {
	    if (left.getKind() == CBPDifferenceKind.DELETE) {
		return -1;
	    } else if (right.getKind() == CBPDifferenceKind.DELETE) {
		return 1;
	    } else {
		if (leftPos == rightPos) {
		    if (left.getKind() == CBPDifferenceKind.ADD) {
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
		} else {
		    if ((left.getKind() == CBPDifferenceKind.ADD && right.getKind() == CBPDifferenceKind.MOVE)
			    || (left.getKind() == CBPDifferenceKind.MOVE && right.getKind() == CBPDifferenceKind.ADD)) {
			if (leftLowPos < rightLowPos) {
			    return -1;
			} else if (leftLowPos > rightLowPos) {
			    return 1;
			} else {
			    return 0;
			}
		    } else {
			if (leftPos < rightPos) {
			    return -1;
			} else if (leftPos > rightPos) {
			    return 1;
			} else {
			    return 0;
			}
		    }
		}
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
