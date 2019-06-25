package org.eclipse.epsilon.cbp.conflict;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public class CBPConflict {

    private Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>();
    private Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>();
    private boolean isPseudo = false;
    private static int ID = -1;
    private int id = -1;

    public CBPConflict(Set<CBPChangeEvent<?>> leftEvents, Set<CBPChangeEvent<?>> rightEvents) {
	this.leftEvents.addAll(leftEvents);
	this.rightEvents.addAll(rightEvents);
	this.leftEvents.forEach(event -> event.setConflict(this));
	this.rightEvents.forEach(event -> event.setConflict(this));
	ID = ID + 1;
	id = ID;

    }

    public CBPConflict(List<CBPChangeEvent<?>> leftEvents, List<CBPChangeEvent<?>> rightEvents) {
	this.leftEvents.addAll(leftEvents);
	this.rightEvents.addAll(rightEvents);
	this.leftEvents.forEach(event -> event.setConflict(this));
	this.rightEvents.forEach(event -> event.setConflict(this));
	ID = ID + 1;
	id = ID;
    }

    public int getId() {
	return id;
    }

    public Set<CBPChangeEvent<?>> getLeftEvents() {
	return this.leftEvents;
    }

    public Set<CBPChangeEvent<?>> getRightEvents() {
	return this.rightEvents;
    }

    public boolean isPseudo() {
	return isPseudo;
    }

    public void setPseudo(boolean isPseudo) {
	this.isPseudo = isPseudo;
    }

}
