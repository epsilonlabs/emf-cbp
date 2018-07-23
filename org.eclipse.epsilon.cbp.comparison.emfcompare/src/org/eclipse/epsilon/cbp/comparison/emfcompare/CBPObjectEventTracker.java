package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.util.List;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;

public class CBPObjectEventTracker extends MultiKeyMap<Object, List<ComparisonEvent>> {

	private static final long serialVersionUID = 1L;

	private final static String DUMMY = "A";

	public List<ComparisonEvent> put(String target, List<ComparisonEvent> events) {
		return super.put(DUMMY, target, events);
	}

	public List<ComparisonEvent> put(String target, String feature, List<ComparisonEvent> events) {
		return super.put(DUMMY, target, feature, events);
	}

	public List<ComparisonEvent> put(String target, String feature, Object value,
			List<ComparisonEvent> events) {
		return super.put(DUMMY, target, feature, value, events);
	}

	public List<ComparisonEvent> get(String target) {
		return super.get(DUMMY, target);
	}

	public List<ComparisonEvent> get(String target, String feature) {
		return super.get(DUMMY, target, feature);
	}

	public List<ComparisonEvent> get(String target, String feature, Object value) {
		return super.get(DUMMY, target, feature, value);
	}

	public boolean containsKey(String target) {
		return super.containsKey(DUMMY, target);
	}

	public boolean containsKey(String target, String feature) {
		return super.containsKey(DUMMY, target, feature);
	}

	public boolean containsKey(String target, String feature, Object value) {
		return super.containsKey(DUMMY, target, feature, value);
	}

}
