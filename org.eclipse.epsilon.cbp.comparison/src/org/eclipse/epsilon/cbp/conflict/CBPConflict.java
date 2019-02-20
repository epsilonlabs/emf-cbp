package org.eclipse.epsilon.cbp.conflict;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.epsilon.cbp.comparison.CBPMatchFeature;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public class CBPConflict {

    private CBPMatchObject leftContainer;
    private CBPMatchObject rightContainer;
    private CBPMatchObject originalContainer;
    private CBPMatchFeature leftFeature;
    private CBPMatchFeature rightFeature;
    private CBPMatchFeature originalFeature;
    private Integer leftPosition;
    private Integer rightPosition;
    private Integer originalPosition;
    private Object leftValue;
    private Object rightValue;
    private Object originalValue;
    private Set<CBPChangeEvent<?>> leftEvents = new HashSet<>();
    private Set<CBPChangeEvent<?>> rightEvents = new HashSet<>();

    public CBPConflict(CBPMatchObject leftContainer, CBPMatchObject rightContainer, CBPMatchObject originalContainer, CBPMatchFeature leftFeature, CBPMatchFeature rightFeature,
	    CBPMatchFeature originalFeature, Integer leftPosition, Integer rightPosition, Integer originalPosition, Object leftValue, Object rightValue, Object originalValue,
	    List<CBPChangeEvent<?>> leftEvents,  List<CBPChangeEvent<?>> rightEvents) {
	this.leftContainer = leftContainer;
	this.rightContainer = rightContainer;
	this.originalContainer = originalContainer;
	this.leftFeature = leftFeature;
	this.rightFeature = rightFeature;
	this.originalFeature = originalFeature;
	this.leftPosition = leftPosition;
	this.rightPosition = rightPosition;
	this.originalPosition = originalPosition;
	this.leftValue = leftValue;
	this.rightValue = rightValue;
	this.originalValue = originalValue;
	this.leftEvents.addAll(leftEvents);
	this.rightEvents.addAll(rightEvents);
    }

    public CBPMatchObject getLeftContainer() {
	return leftContainer;
    }

    public CBPMatchObject getRightContainer() {
	return rightContainer;
    }

    public CBPMatchObject getOriginalContainer() {
	return originalContainer;
    }

    public CBPMatchFeature getLeftFeature() {
	return leftFeature;
    }

    public CBPMatchFeature getRightFeature() {
	return rightFeature;
    }

    public CBPMatchFeature getOriginalFeature() {
	return originalFeature;
    }

    public Integer getLeftPosition() {
	return leftPosition;
    }

    public Integer getRightPosition() {
	return rightPosition;
    }

    public Integer getOriginalPosition() {
	return originalPosition;
    }

    public Object getLeftValue() {
	return leftValue;
    }

    public Object getRightValue() {
	return rightValue;
    }

    public Object getOriginalValue() {
	return originalValue;
    }

    public Set<CBPChangeEvent<?>> getLeftEvents() {

	return this.leftEvents;
    }

    public Set<CBPChangeEvent<?>> getRightEvents() {

	return this.rightEvents;
    }

}
