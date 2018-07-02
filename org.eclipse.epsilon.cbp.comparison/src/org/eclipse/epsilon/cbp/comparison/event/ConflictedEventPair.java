package org.eclipse.epsilon.cbp.comparison.event;

public class ConflictedEventPair {

	public enum SolutionOptions {
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

	public void setResolved(boolean isResolved) {
		this.isResolved = isResolved;
	}

	
	

}
