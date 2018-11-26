package org.eclipse.epsilon.cbp.comparison;

import java.util.Comparator;

import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;

public class CBPDiffComparator implements Comparator<CBPDiff> {

    @Override
    public int compare(CBPDiff left, CBPDiff right) {
	if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0 && left.getPosition() == right.getPosition()
		&& left.getKind() == right.getKind()) {
	    if (left.getValue().equals(right.getValue())) {
		return 0;
	    } else {
		return left.getValue().toString().compareTo(right.getValue().toString());
	    }
	} else if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0
		&& left.getPosition() == right.getPosition()) {
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
	    } else {
		return 0;
	    }
	} else if (left.getObject().getId().compareTo(right.getObject().getId()) == 0 && left.getFeature().getName().compareTo(right.getFeature().getName()) == 0) {
	    if (left.getPosition() < right.getPosition()) {
		return -1;
	    } else if (left.getPosition() > right.getPosition()) {
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

}
