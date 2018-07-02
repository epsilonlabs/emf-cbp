package org.eclipse.epsilon.cbp.comparison.event;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class ReconcileEvent extends ComparisonEvent {
	
	private ComparisonEvent cancelledEvent;
	
	public ReconcileEvent(ComparisonEvent cancelingComparisonEvent, ComparisonEvent cancelledComparisonEvent) throws ParserConfigurationException, TransformerException {
		this.cancelledEvent = cancelledComparisonEvent;
		this.setEventType(cancelingComparisonEvent.getEventType());
		this.setTarget(cancelingComparisonEvent.getTarget());
		this.setChangeEvent(cancelingComparisonEvent.getChangeEvent());
		this.setEventString(cancelingComparisonEvent.getEventString());
		this.setFeature(cancelingComparisonEvent.getFeature());
		this.setFeatureName(cancelingComparisonEvent.getFeatureName());
		this.setFrom(cancelingComparisonEvent.getFrom());
		this.setPosition(cancelingComparisonEvent.getPosition());
		this.setTargetId(cancelingComparisonEvent.getTargetId());
		this.setTo(cancelingComparisonEvent.getTo());
		this.setValue(cancelingComparisonEvent.getValue());
		this.setValueId(cancelingComparisonEvent.getValueId());
	}

	public ComparisonEvent getCancelledEvent() {
		return cancelledEvent;
	}	
}
