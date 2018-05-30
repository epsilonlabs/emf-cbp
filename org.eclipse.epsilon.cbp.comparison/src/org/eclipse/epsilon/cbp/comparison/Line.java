package org.eclipse.epsilon.cbp.comparison;

public class Line {

	private int lineNumber = -1;
	private int sourceLineNumber = -1;
	private String text;

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getSourceLineNumber() {
		return sourceLineNumber;
	}

	public void setSourceLineNumber(int sourceLineNumber) {
		this.sourceLineNumber = sourceLineNumber;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
