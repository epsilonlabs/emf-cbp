package org.eclipse.epsilon.cbp.conflict;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public class CBPConflict {

    private Set<CBPChangeEvent<?>> leftEvents = new LinkedHashSet<>();
    private Set<CBPChangeEvent<?>> rightEvents = new LinkedHashSet<>();
    private boolean isPseudo = false;
    private static int ID = 0;
    private int id = 0;

    public CBPConflict() {
	ID = ID + 1;
	id = ID;
    }

    public CBPConflict(Set<CBPChangeEvent<?>> leftEvents, Set<CBPChangeEvent<?>> rightEvents) {
	this.leftEvents.addAll(leftEvents);
	this.rightEvents.addAll(rightEvents);
	this.leftEvents.forEach(event -> event.addConflict(this));
	this.rightEvents.forEach(event -> event.addConflict(this));
	ID = ID + 1;
	id = ID;

    }

    public CBPConflict(List<CBPChangeEvent<?>> leftEvents, List<CBPChangeEvent<?>> rightEvents) {
	this.leftEvents.addAll(leftEvents);
	this.rightEvents.addAll(rightEvents);
	this.leftEvents.forEach(event -> event.addConflict(this));
	this.rightEvents.forEach(event -> event.addConflict(this));
	ID = ID + 1;
	id = ID;
    }

    @Override
    public String toString() {
	return id + ":" + this.leftEvents.size() + ":" + this.rightEvents.size();
    }

    public int getId() {
	return id;
    }

    public void setLeftEvents(Set<CBPChangeEvent<?>> events) {
	this.leftEvents = events;
    }

    public void setRightEvents(Set<CBPChangeEvent<?>> events) {
	this.rightEvents = events;
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
