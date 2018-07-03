package org.eclipse.epsilon.cbp.comparison.merge;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.eclipse.epsilon.cbp.comparison.event.CompositeEvent;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair;
import org.eclipse.epsilon.cbp.comparison.event.SessionEvent;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair.SolutionOptions;

public class AllLeftResolutionStrategy extends ResolutionStrategy {

	List<ConflictedEventPair> conflictedEventsList;
	Map<String, CompositeEvent> targetCompositeEvents;
	Map<String, CompositeEvent> rightCompositeEvents;

	public AllLeftResolutionStrategy(List<ConflictedEventPair> conflictedEventsList,
			Map<String, CompositeEvent> targetCompositeEvents, Map<String, CompositeEvent> rightCompositeEvents) {
		this.conflictedEventsList = conflictedEventsList;
		this.targetCompositeEvents = targetCompositeEvents;
		this.rightCompositeEvents = rightCompositeEvents;
	}

	@Override
	public void execute(SessionEvent beforeSessionEvent, SessionEvent afterSessionEvent)
			throws ParserConfigurationException, TransformerException, XMLStreamException {
		// resolve conflicts
		Iterator<ConflictedEventPair> iterator1 = conflictedEventsList.iterator();
		while (iterator1.hasNext()) {
			ConflictedEventPair conflictedEventPair = iterator1.next();
			if (conflictedEventPair.isResolved() == false) {
				resolve(conflictedEventPair, SolutionOptions.CHOOSE_LEFT, rightCompositeEvents, beforeSessionEvent,
						afterSessionEvent);
			}
		}
	}

}
