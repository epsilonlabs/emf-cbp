package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.epsilon.cbp.comparison.emfcompare.CBPEngine.CurrentResourceOrigin;

public class TrackedObject {
    
    private String id;
    private String className;
    private String container;
    private String oldContainer;
    private String containingFeature;
    private String oldContainingFeature;
    private int position = -1;
    private int oldPosition = -1;
    private boolean isNew = false;
    private boolean isDeleted = false;
    private Map<String, TrackedFeature> features = new HashMap<>();

    public TrackedObject(String id) {
	this.id = id;
    }

    public TrackedFeature addFeature(String featureName, boolean isContainment) {
	TrackedFeature feature = new TrackedFeature(featureName, isContainment);
	features.put(featureName, feature);
	return feature;
    }

    public TrackedFeature getFeature(String featureName) {
	return features.get(featureName);
    }

    public TrackedFeature addValue(String featureName, String value, boolean isContainment) {
	TrackedFeature feature = features.get(featureName);
	if (feature == null) {
	    feature = new TrackedFeature(featureName, value, isContainment);
	    features.put(featureName, feature);
	} else {
	    feature.addValue(value);
	}
	return feature;
    }

    public TrackedFeature addValue(String featureName, String value, int pos, boolean isContainment) {
	TrackedFeature feature = features.get(featureName);
	if (feature == null) {
	    feature = new TrackedFeature(featureName, value, pos, isContainment);
	    features.put(featureName, feature);
	} else {
	    feature.addValue(value, pos);
	}
	return feature;
    }

    public Map<String, TrackedFeature> getFeatures() {
	return features;
    }

    public void setFeatures(Map<String, TrackedFeature> features) {
	this.features = features;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getContainer() {
	return container;
    }

    public void setContainer(String container) {
	if (this.oldContainer == null) {
	    this.oldContainer = container;
	}
	this.container = container;
    }

    public int getPosition() {
	return position;
    }

    public void setPosition(int position) {
	if (this.oldPosition == -1) {
	    this.oldPosition = position;
	}
	this.position = position;
    }

    public String getContainingFeature() {
	return containingFeature;
    }

    public void setContainingFeature(String containingFeature) {
	if (this.oldContainingFeature == null) {
	    this.oldContainingFeature = containingFeature;
	}
	this.containingFeature = containingFeature;
    }

    public String getOldContainer() {
	return oldContainer;
    }

    public void setOldContainer(String oldContainer) {
	this.oldContainer = oldContainer;
    }

    public String getOldContainingFeature() {
	return oldContainingFeature;
    }

    public void setOldContainingFeature(String oldContainingFeature) {
	this.oldContainingFeature = oldContainingFeature;
    }

    public String getClassName() {
	return className;
    }

    public void setClassName(String className) {
	this.className = className;
    }

    public int getOldPosition() {
	return oldPosition;
    }

    public void setOldPosition(int oldPosition) {
	this.oldPosition = oldPosition;
    }
    
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setValuesOfFeaturesToOldValues() {
	for (TrackedFeature feature : getFeatures().values()) {
	    feature.getValues().clear();
	    feature.getValues().putAll(feature.getOldValues());
	}
    }

    public void copyTheOldStateOfOtherTrackedObject(TrackedObject otherTrackedObject) {
	// some of the features are set to its old values. Since
	// they are were not changed, their values should be the old
	// values
	this.setClassName(otherTrackedObject.getClassName());
	this.setContainer(otherTrackedObject.getOldContainer());
	this.setContainingFeature(otherTrackedObject.getOldContainingFeature());
	this.setOldContainer(otherTrackedObject.getOldContainer());
	this.setOldContainingFeature(otherTrackedObject.getOldContainingFeature());
	this.setOldPosition(otherTrackedObject.getOldPosition());
	this.setPosition(otherTrackedObject.getOldPosition());
	this.getFeatures().putAll(otherTrackedObject.getFeatures());
	this.setValuesOfFeaturesToOldValues();
    }

}