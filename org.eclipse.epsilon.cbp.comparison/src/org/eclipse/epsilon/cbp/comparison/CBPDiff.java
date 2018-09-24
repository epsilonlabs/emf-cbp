package org.eclipse.epsilon.cbp.comparison;

public class CBPDiff {

    public enum CBPLifeline {
	DEFAULT, CREATED, DELETED
    }

    public enum CBPDifferenceKind {
	ADD, CHANGE, DELETE, MOVE, UNDEFINED
    }
    
    public enum CBPSide {
	LEFT, RIGHT;
    }

    private CBPMatch match;

    private String id;

    private CBPDiffSide[] sides = {null, null};

    public CBPDiff(CBPMatch match, String id, String target, String feature, String value, int position, CBPDifferenceKind kind, int cbpDiffSide) {
	this.id = id;
	this.match = match;
	sides[cbpDiffSide] = new CBPDiffSide(target, feature, value, position, kind);
    }

    public CBPMatch getMatch() {
	return match;
    }

    public void setMatch(CBPMatch match) {
	this.match = match;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public CBPDiffSide[] getSides() {
	return sides;
    }

    public void setSides(CBPDiffSide[] sides) {
	this.sides = sides;
    }

    public CBPDiffSide getLeftSide() {
	return this.sides[CBPDiffSide.LEFT];
    }

    public CBPDiffSide getRightSide() {
	return this.sides[CBPDiffSide.RIGHT];
    }
}
