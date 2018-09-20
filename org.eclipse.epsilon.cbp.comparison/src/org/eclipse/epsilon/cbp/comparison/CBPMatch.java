package org.eclipse.epsilon.cbp.comparison;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPLifeline;

public class CBPMatch {
    private String id;
    private Map<String, CBPDiff> diffs = new HashMap<>();
    private Map<String, CBPMatch> subMatches = new HashMap<>();

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

}
