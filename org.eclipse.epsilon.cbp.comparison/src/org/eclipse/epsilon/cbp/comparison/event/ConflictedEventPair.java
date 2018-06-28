package org.eclipse.epsilon.cbp.comparison.event;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.comparison.CBPComparison;
import org.eclipse.epsilon.cbp.comparison.CBPComparisonUtil;
import org.eclipse.epsilon.cbp.event.StartNewSessionEvent;

public class ConflictedEventPair {

	enum SolutionOptions {
		CHOOSE_LEFT,
		CHOOSE_RIGHT
	}
	
	public static final int TYPE_UNDEFINED = 0;
	public static final int TYPE_DIFFERENT_VALUES = 1;
	public static final int TYPE_INAPPLICABLE = 2; 

	int leftLineNumber = -1;
	int rightLineNumber = -1;
	ComparisonEvent leftEvent;
	ComparisonEvent rightEvent;
	int type = ConflictedEventPair.TYPE_UNDEFINED;
	boolean isResolved = false;
	SolutionOptions selectedSolution = null;

	public ConflictedEventPair(int leftLineNumber, ComparisonEvent leftEvent, int rightLineNumber,
			ComparisonEvent rightEvent, int type) {
		this.leftLineNumber = leftLineNumber;
		this.rightLineNumber = rightLineNumber;
		this.leftEvent = leftEvent;
		this.rightEvent = rightEvent;
		this.type = type;
	}

	public int getLeftLineNumber() {
		return leftLineNumber;
	}

	public int getRightLineNumber() {
		return rightLineNumber;
	}

	public ComparisonEvent getLeftEvent() {
		return leftEvent;
	}

	public ComparisonEvent getRightEvent() {
		return rightEvent;
	}

	public int getType() {
		return type;
	}

	public boolean isResolved() {
		return isResolved;
	}

	public void resolve(List<ComparisonEvent> leftComparisonEvents, List<CompositeEvent> leftCompositeEvents,
			List<ComparisonEvent> rightComparisonEvents, List<CompositeEvent> rightCompositeEvents,
			SolutionOptions selectedSolution) throws ParserConfigurationException, TransformerException {
		
		
		//initialise Before SessionEvent
		StartNewSessionEvent beforeStartNewSessionEvent = new StartNewSessionEvent();
		ComparisonEvent beforeComparisonEvent = new ComparisonEvent();
		beforeComparisonEvent.setChangeEvent(beforeStartNewSessionEvent);
		beforeComparisonEvent.setEventString(CBPComparisonUtil.getEventString(beforeStartNewSessionEvent));
		
		SessionEvent beforeSessionEvent = new SessionEvent(beforeComparisonEvent);
		beforeSessionEvent.setSessionId(beforeSessionEvent.getSessionId());
		beforeSessionEvent.setStringTime(beforeSessionEvent.getStringTime());
		beforeSessionEvent.getComparisonEvents().add(beforeComparisonEvent);
		
		
		//initialise  After SessionEvent
		StartNewSessionEvent afterStartNewSessionEvent = new StartNewSessionEvent();
		ComparisonEvent afterComparisonEvent = new ComparisonEvent();
		afterComparisonEvent.setChangeEvent(afterStartNewSessionEvent);
		afterComparisonEvent.setEventString(CBPComparisonUtil.getEventString(afterStartNewSessionEvent));
		
		SessionEvent afterSessionEvent = new SessionEvent(afterComparisonEvent);
		afterSessionEvent.setSessionId(afterSessionEvent.getSessionId());
		afterSessionEvent.setStringTime(afterSessionEvent.getStringTime());
		afterSessionEvent.getComparisonEvents().add(afterComparisonEvent);
		
		
		if (selectedSolution == SolutionOptions.CHOOSE_LEFT) {
			if (this.type == ConflictedEventPair.TYPE_DIFFERENT_VALUES) {
				isResolved = true;
			} else if (this.type == ConflictedEventPair.TYPE_INAPPLICABLE) {
				if (rightEvent.getComposite()!= null) {
					
				}
				isResolved = true;
			}
		}
	}

}
