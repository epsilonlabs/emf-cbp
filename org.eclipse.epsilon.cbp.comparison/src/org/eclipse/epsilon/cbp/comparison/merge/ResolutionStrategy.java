package org.eclipse.epsilon.cbp.comparison.merge;

import java.util.Collections;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.comparison.event.ComparisonEvent;
import org.eclipse.epsilon.cbp.comparison.event.CompositeEvent;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair;
import org.eclipse.epsilon.cbp.comparison.event.ConflictedEventPair.SolutionOptions;
import org.eclipse.epsilon.cbp.comparison.event.SessionEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;

public abstract class ResolutionStrategy {

	public void execute(SessionEvent beforeSessionEvent, SessionEvent afterSessionEvent)
			throws ParserConfigurationException, TransformerException, XMLStreamException {
	}

	protected void resolve(ConflictedEventPair conflictedEventPair, SolutionOptions selectedSolution,
			Map<String, CompositeEvent> inputCompositeEvents, SessionEvent beforeSessionEvent,
			SessionEvent afterSessionEvent)
			throws ParserConfigurationException, TransformerException, XMLStreamException {
		Map<String, CompositeEvent> rightCompositeEvents = inputCompositeEvents;

		if (selectedSolution == SolutionOptions.CHOOSE_LEFT) {
			if (conflictedEventPair.getType() == ConflictedEventPair.TYPE_DIFFERENT_STATES) {
				if (conflictedEventPair.getLeftEvent().getEventType() == RemoveFromEReferenceEvent.class
						&& conflictedEventPair.getRightEvent().getEventType() == MoveWithinEReferenceEvent.class) {
					beforeSessionEvent.getComparisonEvents().add(conflictedEventPair.getRightEvent().reverse());
				} else {
				}
				conflictedEventPair.setResolved(true);
			} else if (conflictedEventPair.getType() == ConflictedEventPair.TYPE_INAPPLICABLE) {
				if (conflictedEventPair.getRightEvent().getCompositeId() != null) {
					if (conflictedEventPair.getRightEvent().getEventType() == DeleteEObjectEvent.class) {
						String compositeId = conflictedEventPair.getRightEvent().getCompositeId();
						CompositeEvent compositeEvent = rightCompositeEvents.get(compositeId);
						CompositeEvent reversedCompositeEvent = reverseCompositeEvent(compositeEvent);
						beforeSessionEvent.getComparisonEvents().addAll(reversedCompositeEvent.getComparisonEvents());
						resolveRelatedConflicts(conflictedEventPair, SolutionOptions.CHOOSE_LEFT);
					}
				} else {
					ComparisonEvent reversedComparisonEvent = conflictedEventPair.getRightEvent().reverse();
					beforeSessionEvent.getComparisonEvents().add(reversedComparisonEvent);
				}
				conflictedEventPair.setResolved(true);
			}

		}

		else if (selectedSolution == SolutionOptions.CHOOSE_RIGHT) {
			if (conflictedEventPair.getType() == ConflictedEventPair.TYPE_DIFFERENT_STATES) {
				if (conflictedEventPair.getLeftEvent().getEventType() == RemoveFromEReferenceEvent.class
						&& conflictedEventPair.getRightEvent().getEventType() == MoveWithinEReferenceEvent.class) {
					beforeSessionEvent.getComparisonEvents().add(conflictedEventPair.getRightEvent().reverse());
					afterSessionEvent.getComparisonEvents().add(conflictedEventPair.getLeftEvent().reverse());
					afterSessionEvent.getComparisonEvents().add(conflictedEventPair.getRightEvent());
				} else {
					afterSessionEvent.getComparisonEvents().add(conflictedEventPair.getRightEvent());
				}
				conflictedEventPair.setResolved(true);
			} else if (conflictedEventPair.getType() == ConflictedEventPair.TYPE_INAPPLICABLE) {

			}

		}
	}

	private void resolveRelatedConflicts(ConflictedEventPair conflictedEventPair, SolutionOptions selectedSolution) {
		// if the right event is selected as a solution then every another
		// conflicts that is caused by the left event should be
		// marked also as resolved. This is to prevent double solutions for the
		// same conflicts.
		if (selectedSolution == SolutionOptions.CHOOSE_RIGHT) {
			for (ConflictedEventPair conflictPair : conflictedEventPair.getLeftEvent().getConflictedEventPairList()) {
				conflictPair.setResolved(true);
			}
			// if the left event is selected as a solution then every another
			// conflicts that is caused by the right event should be
			// marked also as resolved. This is to prevent double solutions for
			// the same conflicts.
		} else if (selectedSolution == SolutionOptions.CHOOSE_LEFT) {
			for (ConflictedEventPair conflictPair : conflictedEventPair.getRightEvent().getConflictedEventPairList()) {
				conflictPair.setResolved(true);
			}
		}
	}

	/***
	 * Reverse the order of events that constitute a composite event as well
	 * transform them into their inverse events
	 * 
	 * @param compositeEvent
	 * @return
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws XMLStreamException
	 */
	public CompositeEvent reverseCompositeEvent(CompositeEvent compositeEvent)
			throws ParserConfigurationException, TransformerException, XMLStreamException {

		CompositeEvent newCompositeEvent = new CompositeEvent(EcoreUtil.generateUUID());
		for (ComparisonEvent comparisonEvent : compositeEvent.getComparisonEvents()) {
			// String eventString = comparisonEvent.getEventString();
			ComparisonEvent newComparisonEvent = comparisonEvent.reverse(newCompositeEvent.getCompositeId());
			// String reverseEventString = newComparisonEvent.getEventString();
			newCompositeEvent.getComparisonEvents().add(newComparisonEvent);
		}
		Collections.reverse(newCompositeEvent.getComparisonEvents());

		return newCompositeEvent;
	}
}
