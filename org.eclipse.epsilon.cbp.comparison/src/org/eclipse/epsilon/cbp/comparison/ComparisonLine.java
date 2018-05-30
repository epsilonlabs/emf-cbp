package org.eclipse.epsilon.cbp.comparison;

public class ComparisonLine {

	private int lineNumber = -1;
	
	private boolean isDifferent = false;
	private boolean isConflict = false; 
	private Line leftLine = null;
	private Line rightLine = null;
	
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public Line getLeftLine() {
		return leftLine;
	}
	public void setLeftLine(Line leftLine) {
		this.leftLine = leftLine;
	}
	public Line getRightLine() {
		return rightLine;
	}
	public void setRightLine(Line rightLine) {
		this.rightLine = rightLine;
	}
	public boolean isDifferent() {
		return isDifferent;
	}
	public void setDifferent(boolean isDifferent) {
		this.isDifferent = isDifferent;
	}
	public boolean isConflict() {
		return isConflict;
	}
	public void setConflict(boolean isConflict) {
		this.isConflict = isConflict;
	}

	
}
