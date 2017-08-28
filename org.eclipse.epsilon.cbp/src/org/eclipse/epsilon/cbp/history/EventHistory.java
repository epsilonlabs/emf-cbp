package org.eclipse.epsilon.cbp.history;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.epsilon.cbp.event.ChangeEvent;

public class EventHistory extends ArrayList<Line> {

	private ChangeEvent<?> event = null;
	
	public EventHistory(Collection<? extends Line> collection) {
		super(collection);
	}
	public EventHistory(Collection<? extends Line> collection, ChangeEvent<?> event) {
		super(collection);
		this.event = event;
	}
	public EventHistory(ChangeEvent<?> event) {
		super();
		this.event = event;
	}

	public ChangeEvent<?> getEvent() {
		return event;
	}

	public void setEvent(ChangeEvent<?> event) {
		this.event = event;
	}
	
	
	@Override
	public EventHistory subList(int fromIndex, int toIndex) {
		EventHistory newEventHistory = new EventHistory(super.subList(fromIndex, toIndex), this.event);
		return newEventHistory;
	}
}
