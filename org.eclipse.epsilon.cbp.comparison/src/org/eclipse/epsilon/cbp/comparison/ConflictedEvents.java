package org.eclipse.epsilon.cbp.comparison;

public class ConflictedEvents {
	
	public static final int TYPE_UNDEFINED = 0;
	public static final int TYPE_DIFFERENT_VALUES = 1;
	public static final int TYPE_INAPPLICABLE = 2;
	
	int leftLineNumber = -1;
	int rightLineNumber = -1;
	ComparisonEvent leftEvent;
	ComparisonEvent rightEvent;
	int type = ConflictedEvents.TYPE_UNDEFINED; 
	
	public ConflictedEvents(int leftLineNumber, ComparisonEvent leftEvent, int rightLineNumber, ComparisonEvent rightEvent, int type) {
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
	
	
}
