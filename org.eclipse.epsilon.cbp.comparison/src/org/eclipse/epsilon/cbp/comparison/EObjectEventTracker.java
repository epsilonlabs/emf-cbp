package org.eclipse.epsilon.cbp.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;

public class EObjectEventTracker {

	Map<EObject, Map<Class<?>, List<ComparisonEvent>>> eObjects2eventTypes = new HashMap<>();

	public void addComparisonEvent(EObject eObject, ComparisonEvent comparisonEvent) {

		if (!eObjects2eventTypes.containsKey(eObject)) {
			Map<Class<?>, List<ComparisonEvent>> eventTypes2events = new HashMap<>();
			List<ComparisonEvent> comparisonEventList = new ArrayList<>();
			eventTypes2events.put(comparisonEvent.getEventType().getSuperclass(), comparisonEventList);
			comparisonEventList.add(comparisonEvent);
			eObjects2eventTypes.put(eObject, eventTypes2events);
		} else {
			Map<Class<?>, List<ComparisonEvent>> eventTypes2events = eObjects2eventTypes.get(eObject);
			if (!eventTypes2events.containsKey(comparisonEvent.getEventType().getSuperclass())) {
				List<ComparisonEvent> comparisonEventList = new ArrayList<>();
				eventTypes2events.put(comparisonEvent.getEventType().getSuperclass(), comparisonEventList);
				comparisonEventList.add(comparisonEvent);
			} else {
				List<ComparisonEvent> comparisonEventList = eventTypes2events
						.get(comparisonEvent.getEventType().getSuperclass());
				
				// temporarily this only allows one
				// member, it will be updated
				// later once more andvance
				// algorithm has been defined to
				// identify the change
				comparisonEventList.clear();
				comparisonEventList.add(comparisonEvent);
			}
		}
	}

	public Set<EObject> getTrackedEObjects() {
		return eObjects2eventTypes.keySet();
	}

	public Map<Class<?>, List<ComparisonEvent>> getEObjectEvents(EObject eObject) {
		Map<Class<?>, List<ComparisonEvent>> result = eObjects2eventTypes.get(eObject);
		return result;
	}
}
