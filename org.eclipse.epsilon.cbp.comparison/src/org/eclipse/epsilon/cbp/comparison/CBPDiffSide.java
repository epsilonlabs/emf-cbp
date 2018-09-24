package org.eclipse.epsilon.cbp.comparison;

import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPLifeStatus;

public class CBPDiffSide {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    private int position;
    private String container;
    private String containingFeature;
    private String value;
    private CBPDifferenceKind kind = CBPDifferenceKind.UNDEFINED;

    public CBPDiffSide(String target, String feature, String value, int position, CBPDifferenceKind kind) {
	this.container = target;
	this.containingFeature = feature;
	this.value = value;
	this.kind = kind;
    }

    public int getPosition() {
	return position;
    }

    public void setPosition(int position) {
	this.position = position;
    }

    public String getContainer() {
	return container;
    }

    public void setContainer(String container) {
	this.container = container;
    }

    public String getContainingFeature() {
	return containingFeature;
    }

    public void setContainingFeature(String containingFeature) {
	this.containingFeature = containingFeature;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public CBPDifferenceKind getKind() {
	return kind;
    }

    public void setKind(CBPDifferenceKind kind) {
	this.kind = kind;
    }

}
