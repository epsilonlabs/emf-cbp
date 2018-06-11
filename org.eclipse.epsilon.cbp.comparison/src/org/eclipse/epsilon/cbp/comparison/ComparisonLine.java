package org.eclipse.epsilon.cbp.comparison;

public class ComparisonLine {

	private int lineNumber = -1;
	
	private boolean isDifferent = false;
	private boolean isConflict = false; 
	private Line leftLine = null;
	private Line rightLine = null;
	
	public ComparisonLine() {
	}
	
	public ComparisonLine(int lineNumber, boolean isDifferent, boolean isConflict, Line leftLine, Line rightLine) {
		this.lineNumber = lineNumber;
		this.isDifferent = isDifferent;
		this.isConflict = isConflict;
		this.leftLine = leftLine;
		this.rightLine = rightLine;
		this.leftLine.setComparisonLine(this);
		this.rightLine.setComparisonLine(this);
	}
	
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
