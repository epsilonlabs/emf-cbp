package org.eclipse.epsilon.cbp.comparison;

import org.eclipse.epsilon.cbp.comparison.CBPMatchObject.CBPSide;

public class CBPDiff {

    public enum CBPDifferenceKind {
	ADD, CHANGE, DELETE, MOVE, UNDEFINED
    }

    private CBPMatchObject object;
    private CBPMatchFeature feature;
    private int position = -1;
    private Object value;
    private CBPDifferenceKind kind = CBPDifferenceKind.UNDEFINED;
    private CBPSide side;
    private static final String SEP = ".";
    private boolean isResolved = false;

    public CBPDiff(CBPMatchObject object, CBPMatchFeature feature, Object value, CBPDifferenceKind kind, CBPSide side) {
	this(object, feature, -1, value, kind, side);
    }

    public CBPDiff(CBPMatchObject object, CBPMatchFeature feature, int position, Object value, CBPDifferenceKind kind, CBPSide side) {
	this.object = object;
	this.feature = feature;
	this.position = position;
	this.kind = kind;
	this.side = side;
	this.value = value;
	this.object.addDiff(this);
	if (this.value instanceof CBPMatchObject) {
	    ((CBPMatchObject) this.value).addDiffAsValue(this);
	}
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
    }

    @Override
    public String toString() {
	String str = null;
	String valueStr = "";
	if (value != null) {
	    if (value instanceof CBPMatchObject) {
		valueStr = ((CBPMatchObject) value).getId();
	    } else {
		valueStr = String.valueOf(value);
	    }
	}
	str = object.getId() + SEP + feature.getName() + SEP + position + SEP + valueStr + SEP + kind + SEP + side;
	return str;
    }

}
