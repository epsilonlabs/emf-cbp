package org.eclipse.epsilon.cbp.comparison;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.epsilon.cbp.comparison.CBPMatchObject.CBPSide;

public class CBPDiff {

    public enum CBPDifferenceKind {
	ADD, CHANGE, DELETE, MOVE, UNDEFINED
    }

    private CBPMatchObject object;
    private CBPMatchFeature feature;
    private CBPMatchObject originObject;
    private CBPMatchFeature originFeature;
    private int position = -1;
    private int origin = -1;
    private Object value;
    private Object otherSideValue;
    private CBPDifferenceKind kind = CBPDifferenceKind.UNDEFINED;
    private CBPSide side;
    private static final String SEP = ".";
    private boolean isResolved = false;
    private boolean underRecursion = false;
    private Set<CBPDiff> requiresDiffs = new HashSet<>();
    private Set<CBPDiff> requiredByDiffs = new HashSet<>();

    public CBPDiff(CBPMatchObject object, CBPMatchFeature feature, Object value, CBPDifferenceKind kind, CBPSide side) {
	this(object, feature, -1, -1, null, null, value, kind, side);
    }

    public CBPDiff(CBPMatchObject object, CBPMatchFeature feature, int position, Object value, CBPDifferenceKind kind, CBPSide side) {
	this(object, feature, position, -1, null, null, value, kind, side);
    }

    public CBPDiff(CBPMatchObject object, CBPMatchFeature feature, int position, Object value, Object otherSideValue, CBPDifferenceKind kind, CBPSide side) {
	this(object, feature, position, -1, null, null, value, otherSideValue, kind, side);
    }

    public CBPDiff(CBPMatchObject object, CBPMatchFeature feature, int position, int origin, CBPMatchFeature originFeature, CBPMatchObject originObject, Object value, CBPDifferenceKind kind,
	    CBPSide side) {
	this(object, feature, position, origin, originFeature, originObject, value, null, kind, side);
    }

    public CBPDiff(CBPMatchObject object, CBPMatchFeature feature, int position, int origin, CBPMatchFeature originFeature, CBPMatchObject originObject, Object value, Object otherSideValue,
	    CBPDifferenceKind kind, CBPSide side) {
	this.object = object;
	this.feature = feature;
	this.feature.getDiffs().add(this);
	this.position = position;
	this.origin = origin;
	this.originFeature = originFeature;
	this.originObject = originObject;
	this.kind = kind;
	this.side = side;
	this.value = value;
	this.otherSideValue = otherSideValue;
	this.object.addDiff(this);
	if (this.value instanceof CBPMatchObject) {
	    ((CBPMatchObject) this.value).addDiffAsValue(this);
	}
    }

    public Set<CBPDiff> getRequiresDiffs() {
	return requiresDiffs;
    }

    public Set<CBPDiff> getRequiredByDiffs() {
	return requiredByDiffs;
    }

    public void setRequiresDiffs(Set<CBPDiff> requiresDiffs) {
	this.requiresDiffs = requiresDiffs;
    }

    public void setRequiredByDiffs(Set<CBPDiff> requiredByDiffs) {
	this.requiredByDiffs = requiredByDiffs;
    }

    public CBPMatchObject getObject() {
	return object;
    }

    public void setObject(CBPMatchObject object) {
	this.object = object;
    }

    public CBPMatchFeature getFeature() {
	return feature;
    }

    public void setFeature(CBPMatchFeature feature) {
	this.feature = feature;
    }

    public int getPosition() {
	return position;
    }

    public void setPosition(int position) {
	this.position = position;
    }

    public Object getValue() {
	return value;
    }

    public void setValue(Object value) {
	this.value = value;
    }

    public CBPDifferenceKind getKind() {
	return kind;
    }

    public void setKind(CBPDifferenceKind kind) {
	this.kind = kind;
    }

    public CBPSide getSide() {
	return side;
    }

    public void setSide(CBPSide side) {
	this.side = side;
    }

    public boolean isResolved() {
	return isResolved;
    }

    public void setResolved(boolean isResolved) {
	this.isResolved = isResolved;
	if (isResolved) {
	    underRecursion = false;
	}
    }

    public int getOrigin() {
	return origin;
    }

    public CBPMatchObject getOriginObject() {
	return originObject;
    }

    public void setOriginObject(CBPMatchObject originObject) {
	this.originObject = originObject;
    }

    public CBPMatchFeature getOriginFeature() {
	return originFeature;
    }

    public void setOriginFeature(CBPMatchFeature originFeature) {
	this.originFeature = originFeature;
    }

    public void setOrigin(int origin) {
	this.origin = origin;
    }

    public Object getOtherSideValue() {
	return otherSideValue;
    }

    public void setOtherSideValue(Object otherSideValue) {
	this.otherSideValue = otherSideValue;
    }

    public boolean isUnderRecursion() {
	return underRecursion;
    }

    public void setUnderRecursion(boolean underRecursion) {
	this.underRecursion = underRecursion;
    }

    @Override
    public String toString() {
	String str = null;
	String valueStr = "";
	String mergePos = "";
	if (value != null) {
	    if (value instanceof CBPMatchObject) {
		valueStr = ((CBPMatchObject) value).getId();
		if (((CBPMatchObject) value).getLeftMergePosition(feature) != null){
		    mergePos = ((CBPMatchObject) value).getLeftMergePosition(feature).toString();    
		}	
	    } else {
		valueStr = String.valueOf(value);
	    }
	}
	str = object.getId() + SEP + feature.getName() + SEP + position + SEP + valueStr + SEP + kind + SEP + side + SEP +
		mergePos;
	return str;
    }

}
