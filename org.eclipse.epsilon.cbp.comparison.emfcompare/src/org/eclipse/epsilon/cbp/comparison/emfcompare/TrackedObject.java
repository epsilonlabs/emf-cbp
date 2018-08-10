package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.util.HashMap;
import java.util.Map;

public class TrackedObject {
	private String id;
	private String container;
	private String oldContainer;
	private String containingFeature;
	private String oldContainingFeature;
	private int position;
	private Map<String, TrackedFeature> features = new HashMap<>();
	
	public TrackedObject(String id) {
		this.id = id;
	}
	
	public TrackedFeature addFeature(String featureName, boolean isContainment) {
	    TrackedFeature feature = new TrackedFeature(featureName, isContainment);
	    features.put(featureName, feature);
	    return feature;
	}
	
	public TrackedFeature getFeature (String featureName) {
		return features.get(featureName);
	}
	
	public TrackedFeature addValue(String featureName, String value, boolean isContainment) {
		TrackedFeature feature = features.get(featureName);
		if(feature == null) {
		    	feature = new TrackedFeature(featureName,value, isContainment);
			features.put(featureName, feature);
		}else {
			feature.addValue(value);
		}
		return feature;
	}
	
	public TrackedFeature addValue(String featureName, String value, int pos, boolean isContainment) {
		TrackedFeature feature = features.get(featureName);
		if(feature == null) {
		    	feature = new TrackedFeature(featureName,value, pos, isContainment);
			features.put(featureName, feature);
		}else {
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
	
}