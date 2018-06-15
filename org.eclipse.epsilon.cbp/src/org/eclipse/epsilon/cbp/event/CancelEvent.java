package org.eclipse.epsilon.cbp.event;

public class CancelEvent extends ChangeEvent<Object> {

	int lineToCancelOffset = 0;
	
	public CancelEvent() {
	}
	
	public CancelEvent(int lineToCancelOffset){
		this.lineToCancelOffset = lineToCancelOffset;
	}
	
	@Override
	public void replay() {
		
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return null;
	}

	public int getLineToCancelOffset() {
		return lineToCancelOffset;
	}

	public void setLineToCancelOffset(int lineToCancelOffset) {
		this.lineToCancelOffset = lineToCancelOffset;
	}

	

	
}
