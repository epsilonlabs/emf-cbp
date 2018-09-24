package org.eclipse.epsilon.cbp.comparison;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPLifeline;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPSide;

public class CBPMatch {
    private String id;
    private Map<String, CBPDiff> diffs = new HashMap<>();
    private Map<String, CBPMatch> subMatches = new HashMap<>();

    private CBPObject leftObject = null;
    private CBPObject rightObject = null;

    private CBPLifeline life = CBPLifeline.DEFAULT;

    public CBPMatch(String id) {
	this.id = id;
    }

    public String getId() {
	return id;
    }

    public Map<String, CBPDiff> getDiffs() {
	return diffs;
    }

    public Map<String, CBPMatch> getSubMatches() {
	return subMatches;
    }

    public CBPLifeline getLife() {
	return life;
    }

    public void setLife(CBPLifeline life) {
	this.life = life;
    }

    public void setObject(CBPObject object, CBPSide side) {
	if (side == CBPSide.LEFT) {
	    setLeftObject(object);
	} else {
	    setRightObject(object);
	}
    }

    public CBPObject getObject(CBPSide side) {
	if (side == CBPSide.LEFT) {
	    return leftObject;
	} else {
	    return rightObject;
	}
    }

    public CBPObject getLeftObject() {
	return leftObject;
    }

    public void setLeftObject(CBPObject leftObject) {
	this.leftObject = leftObject;
    }

    public CBPObject getRightObject() {
	return rightObject;
    }

    public void setRightObject(CBPObject rightObject) {
	this.rightObject = rightObject;
    }

}
