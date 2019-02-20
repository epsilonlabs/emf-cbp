package org.eclipse.epsilon.cbp.comparison.event;


public class CBPRemoveFromResourceEvent extends CBPResourceEvent {
	
	@Override
	public CBPChangeEvent<?> reverse(){
		CBPAddToResourceEvent event = new CBPAddToResourceEvent();
		event.setValues(this.getValues());
		event.setOldValues(this.getOldValue());
		event.setPosition(this.getPosition());;
		return event;
	}
	
	@Override
	    public String toString() {
		return String.format("REMOVE %s FROM %s AT %s", this.getValue(), "resource", this.getPosition());
	    }
}