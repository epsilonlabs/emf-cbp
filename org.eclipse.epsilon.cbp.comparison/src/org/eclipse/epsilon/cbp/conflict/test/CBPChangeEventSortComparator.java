package org.eclipse.epsilon.cbp.conflict.test;

import java.util.Comparator;

import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public class CBPChangeEventSortComparator implements Comparator<CBPChangeEvent<?>> {

    @Override
    public int compare(CBPChangeEvent<?> left, CBPChangeEvent<?> right) {

	if (left.getLineNumber() < right.getLineNumber()) {
	    return -1;
	} else if (left.getLineNumber() > right.getLineNumber()) {
	    return 1;
	} else {
	    return 0;
	}
    }

}
