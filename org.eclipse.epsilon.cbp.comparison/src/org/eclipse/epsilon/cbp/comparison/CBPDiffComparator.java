package org.eclipse.epsilon.cbp.comparison;

import java.util.Comparator;

import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;

public class CBPDiffComparator implements Comparator<CBPDiff> {

    @Override
    public int compare(CBPDiff left, CBPDiff right) {
	int leftPos = left.getPosition();
	int rightPos = right.getPosition();

	if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0 && left.getPosition() == right.getPosition()
		&& left.getKind() == right.getKind() && left.getValue().equals(right.getValue())) {
	    return 0;
	} else if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0
		&& left.getPosition() == right.getPosition() && left.getKind() == right.getKind()) {
	    return left.getValue().toString().compareTo(right.getValue().toString());
	} else if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0
		&& left.getPosition() == right.getPosition()) {
	    return left.getKind().compareTo(right.getKind());
	} else if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0) {
	    if (leftPos > rightPos) {
		return 1;
	    } else if (leftPos < rightPos) {
		return -1;
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
