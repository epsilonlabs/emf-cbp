package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.util.HashMap;
import java.util.Map;

public class TrackedFeature {
    private String featureName;
    private Map<Integer, String> values = new HashMap<Integer, String>();
    private Map<Integer, String> oldValues = new HashMap<Integer, String>();
    private boolean isContainment = false;

    public boolean isContainment() {
	return isContainment;
    }

    public void setContainment(boolean isContainment) {
	this.isContainment = isContainment;
    }

    public TrackedFeature(String featureName, boolean isContainment) {
	this.featureName = featureName;
	this.isContainment = isContainment;
    }

    public TrackedFeature(String featureName, String value, boolean isContaiment) {
	this.featureName = featureName;
	this.isContainment = isContaiment;
	this.addValue(value);
    }

    public TrackedFeature(String featureName, String value, int pos, boolean isContaiment) {
	this.featureName = featureName;
	this.isContainment = isContaiment;
	this.addValue(value, pos);
    }

    public String getValue(int pos) {
	return values.get(pos);
    }

    public void addValue(String value) {
	this.addValue(value, values.size());
    }

    public void addValue(String value, int pos) {
	if (oldValues.get(pos) == null) {
	    oldValues.put(pos, value);
	}
	values.put(pos, value);
    }

    public Map<Integer, String> getValues() {
	return values;
    }

    public String getName() {
	return featureName;
    }

    public void setName(String featureName) {
	this.featureName = featureName;
    }

    public String getOldValue(int pos) {
	return oldValues.get(pos);
    }

    public String getFeatureName() {
	return featureName;
    }

    public Map<Integer, String> getOldValues() {
	return oldValues;
    }

}
