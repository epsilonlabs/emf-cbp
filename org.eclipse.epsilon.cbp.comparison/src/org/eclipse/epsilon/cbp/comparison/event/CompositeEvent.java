package org.eclipse.epsilon.cbp.comparison.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;

public class CompositeEvent extends ComparisonEvent {

	private String  compositeId = null;
	private List<ComparisonEvent> comparisonEvents = new ArrayList<>();

	public CompositeEvent(String compositeId) {
		this.compositeId = compositeId;
	}

	public String getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(String compositeId) {
		this.compositeId = compositeId;
	}

	public List<ComparisonEvent> getComparisonEvents() {
		return comparisonEvents;
	}

	public void setComparisonEvents(List<ComparisonEvent> comparisonEvents) {
		this.comparisonEvents = comparisonEvents;
	}

	
	
	
	

}
