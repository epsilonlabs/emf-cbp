package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackedFeature {
	private String featureName;
	private  Map<Integer, String> featureMap = new HashMap<Integer, String>();
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
		return featureMap.get(pos);
	}
	
	public void addValue(String value) {
		featureMap.put(featureMap.size(), value);
	}
	
	public void addValue(String value, int pos) {
		featureMap.put(pos,  value);
	}
	
	public Map<Integer, String> getValues(){
		return featureMap;
	}

	public String getName() {
		return featureName;
	}

	public void setName(String featureName) {
		this.featureName = featureName;
	}	
}
