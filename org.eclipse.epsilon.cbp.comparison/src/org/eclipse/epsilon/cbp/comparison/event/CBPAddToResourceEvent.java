package org.eclipse.epsilon.cbp.comparison.event;

public class CBPAddToResourceEvent extends CBPResourceEvent {
	
	
	@Override
	public CBPChangeEvent<?> reverse(){
		CBPRemoveFromResourceEvent event = new CBPRemoveFromResourceEvent();
		event.setValues(this.getValues());
		event.setOldValues(this.getOldValue());
		event.setPosition(this.getPosition());
		event.setComposite(getComposite());
		return event;
	}
	
	@Override
	    public String toString() {
		return String.format("ADD %s TO %s AT %s", this.getValue(), "resource", this.getPosition());
	    }
}
