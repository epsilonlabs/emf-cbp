package org.eclipse.epsilon.cbp.comparison;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public class CBPDiffComparator implements Comparator<CBPDiff> {

    @Override
    public int compare(CBPDiff left, CBPDiff right) {

	Integer leftLineNum = null;
	Integer rightLineNum = null;

	// System.out.println(left.toString() + " x " + right.toString());

	if (right.getFeature().getName().equals("association")) {
	    System.nanoTime();
	}
	if (left.getValue() instanceof CBPMatchObject && right.getValue() instanceof CBPMatchObject) {
	    // if (left.getObject().getId().equals("O-50917") &&
	    // right.getObject().getId().equals("L-21")) {
	    String leftId = ((CBPMatchObject) left.getValue()).getId();
	    String rightId = ((CBPMatchObject) right.getValue()).getId();
	    // System.out.println(leftId + " vs " + rightId);
	    // // O-39189 vs L-4067
	    if ((leftId.equals("R-211") || rightId.equals("R-211"))) {
		// if ((leftId.equals("L-22") && rightId.equals("L-23")) ||
		// (leftId.equals("L-23") && rightId.equals("L-22"))) {
		System.console();
	    }
	    // }
	}

	if (left.getValue() == null || right.getValue() == null) {
	    System.console();
	}

	// leftLineNum = left.getFeature().getLeftValueLineNum(left.getValue());
	// if (leftLineNum == null || leftLineNum == 0) {
	// int min = Integer.MAX_VALUE;
	// min = getMinLineNum(left, min, new ArrayList<>());
	// if (min == Integer.MAX_VALUE) {
	// leftLineNum = min -
	// left.getFeature().getRightValueLineNum(left.getValue());
	// } else {
	// leftLineNum = min;
	// }
	// }
	//
	// rightLineNum =
	// right.getFeature().getLeftValueLineNum(right.getValue());
	// if (rightLineNum == null || rightLineNum == 0) {
	// int min = Integer.MAX_VALUE;
	//
	// min = getMinLineNum(right, min, new ArrayList<>());
	// if (min == Integer.MAX_VALUE) {
	// rightLineNum = min -
	// right.getFeature().getRightValueLineNum(right.getValue());
	// } else {
	// rightLineNum = min;
	// }
	// }

	leftLineNum = left.getFeature().getLeftValueLineNum(left.getValue());
	if (leftLineNum == null || leftLineNum == 0) {
	    leftLineNum = 1 * - left.getFeature().getRightValueLineNum(left.getValue());
	}

	rightLineNum = right.getFeature().getLeftValueLineNum(right.getValue());
	if (rightLineNum == null || rightLineNum == 0) {
	    rightLineNum = 1 * - right.getFeature().getRightValueLineNum(right.getValue());
	}

	if (leftLineNum > rightLineNum) {
	    return 1;
	} else if (leftLineNum < rightLineNum) {
	    return -1;
	} else {
	    return 0;
	}
    }

    /**
     * @param left
     * @param min
     * @return
     */
    private int getMinLineNum(CBPDiff diff, int min, List<CBPDiff> checkedDiffs) {
	if (checkedDiffs.contains(diff)) {
	    return min;
	}
	for (Object val : diff.getFeature().getLeftValues().values()) {
	    checkedDiffs.add(diff);
	    if (val != null) {
		if (val instanceof CBPMatchObject) {
		    Integer temp = diff.getFeature().getLeftValueLineNum(val);
		    if (temp < min) {
			min = temp;
		    }
		}
	    }
	}
	// if (diff.getValue() instanceof CBPMatchObject) {
	// Object val = diff.getValue();
	// for (CBPMatchFeature feature : ((CBPMatchObject)
	// val).getFeatures().values()) {
	// for (Object value : feature.getRightValues().values()) {
	// if (value instanceof CBPMatchObject) {
	// for (CBPDiff subDiff : ((CBPMatchObject) value).getDiffsAsValue()) {
	// min = getMinLineNum(subDiff, min, checkedDiffs);
	// }
	// }
	// }
	// }
	// }
	return min;
    }

}
