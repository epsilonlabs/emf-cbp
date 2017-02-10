package org.eclipse.epsilon.cbp.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.Event;

public class Changelog {
	// event list
	protected List<Event> eventList;

	// current id, increases when an object is encountered
	protected static int current_id = 0;

	public Changelog() {
		eventList = new ArrayList<Event>();
	}

	public void clear() {
		eventList.clear();
		current_id = 0;
	}

	public void addEvent(Event e) {
		eventList.add(e);
	}

	public void removeEvent(Event e) {
		eventList.remove(e);
	}

	public void clearEvents() {
		eventList.clear();
	}

	public List<Event> getEventsList() {
		return eventList;
	}

	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> allOfType(Class<T> c) {
		ArrayList<T> result = new ArrayList<T>();
		for (Event e : eventList) {
			if (e.getClass().equals(c)) {
				result.add((T) e);
			}
		}
		return result;
	}
	
	/*
	public void printLog() {
		for (Event e : eventList) {
			if (e instanceof EAttributeEvent) {
				System.out.println(e.getEventType() + " " + e.getClass().getSimpleName()
						+ ((EAttributeEvent) e).getEAttributeValuesList().toString());
			} else {
				System.out
						.println(e.getEventType() + " " + e.getClass().getSimpleName() + e.getEObjectList().toString());
			}
		}
	}*/
}
