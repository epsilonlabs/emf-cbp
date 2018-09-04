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

    public TrackedFeature(String featureName, String oldValue, String value, boolean isContaiment) {
	this.featureName = featureName;
	this.isContainment = isContaiment;
	this.addOldValue(oldValue);
	this.addValue(value);
    }

    public TrackedFeature(String featureName, String oldValue, String value, int pos, boolean isContaiment) {
	this.featureName = featureName;
	this.isContainment = isContaiment;
	this.addOldValue(oldValue, pos);
	this.addValue(value, pos);
    }

    public String getValue() {
	if (values.size() > 0) {
	    return values.get(values.size() - 1);
	}
	return null;
    }

    public String getOldValue() {
	if (oldValues.size() > 0) {
	    return oldValues.get(oldValues.size() - 1);
	}
	return null;
    }

    public String getValue(int pos) {
	return values.get(pos);
    }
    
    public String getOldValue(int pos) {
	return oldValues.get(pos);
    }

    public void addValue(String value) {
	this.addValue(value, values.size());
    }

    public void addOldValue(String oldValue) {
	this.addOldValue(oldValue, oldValues.size());
    }

    public void addValue(String value, int pos) {
	if (oldValues.get(pos) == null) {
	    oldValues.put(pos, value);
	}
	values.put(pos, value);
    }

    public void addOldValue(String oldValue, int pos) {
	if (oldValues.get(pos) == null) {
	    oldValues.put(pos, oldValue);
	}
    }

    public Map<Integer, String> getValues() {
	return values;
    }

    

    public String getFeatureName() {
	return featureName;
    }

    public Map<Integer, String> getOldValues() {
	return oldValues;
    }

}
