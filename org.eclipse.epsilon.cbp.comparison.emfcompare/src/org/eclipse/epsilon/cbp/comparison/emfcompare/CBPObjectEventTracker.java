package org.eclipse.epsilon.cbp.comparison.emfcompare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;

public class CBPObjectEventTracker extends HashMap<String, List<ComparisonEvent>> {

    private static final long serialVersionUID = 1L;

    public List<ComparisonEvent> addEvent(String key, ComparisonEvent comparisonEvent) {
	List<ComparisonEvent> eventList = this.get(key);
	if (eventList == null) {
	    eventList = new ArrayList<>();
	    this.put(key, eventList);
	} 
//	else if (eventList.size() > 1) {
//	    eventList.remove(0);
//	}
	eventList.add(comparisonEvent);
	return eventList;
    }

}
