package org.eclipse.epsilon.cbp.comparison;

import org.eclipse.epsilon.cbp.comparison.CBPObject.CBPSide;

public class CBPDiff {

    public enum CBPDifferenceKind {
	ADD, CHANGE, DELETE, MOVE, UNDEFINED
    }

    private CBPObject object;
    private CBPFeature feature;
    private int position = -1;
    private Object value;
    private CBPDifferenceKind kind = CBPDifferenceKind.UNDEFINED;
    private CBPSide side;
    private static final String SEP = ".";

    public CBPDiff(CBPObject object, CBPFeature feature, Object value, CBPDifferenceKind kind, CBPSide side) {
	this(object, feature, -1, value, kind, side);
    }
    public CBPDiff(CBPObject object, CBPFeature feature, int position, Object value, CBPDifferenceKind kind, CBPSide side) {
	this.object = object;
	this.feature = feature;
	this.position = position;
	this.kind = kind;
	this.side = side;
	this.value = value;
	this.object.getDiffs().add(this);
    }

    public CBPObject getObject() {
	return object;
    }

    public void setObject(CBPObject object) {
	this.object = object;
    }

    public CBPFeature getFeature() {
	return feature;
    }

    public void setFeature(CBPFeature feature) {
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

    @Override
    public String toString() {
	String str = null;
	String valueStr = "";
	if (value != null) {
	    if (value instanceof CBPObject) {
		valueStr = ((CBPObject) value).getId();
	    } else {
		valueStr = String.valueOf(value);
	    }
	}
	str = object.getId() + SEP + feature.getName() + SEP + position + SEP + valueStr + SEP + kind + SEP + side;
	return str;
    }

}
