package org.eclipse.epsilon.cbp.conflict;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public class CBPConflict {

    private Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>();
    private Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>();

    public CBPConflict(
	    Set<CBPChangeEvent<?>> leftEvents,  Set<CBPChangeEvent<?>> rightEvents) {
	this.leftEvents.addAll(leftEvents);
	this.rightEvents.addAll(rightEvents);
    }
    public CBPConflict(List<CBPChangeEvent<?>> leftEvents,  List<CBPChangeEvent<?>> rightEvents) {
	this.leftEvents.addAll(leftEvents);
	this.rightEvents.addAll(rightEvents);
    }

    public Set<CBPChangeEvent<?>> getLeftEvents() {
	return this.leftEvents;
    }

    public Set<CBPChangeEvent<?>> getRightEvents() {
	return this.rightEvents;
    }

}
