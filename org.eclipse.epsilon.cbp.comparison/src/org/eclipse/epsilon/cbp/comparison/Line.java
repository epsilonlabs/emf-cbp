package org.eclipse.epsilon.cbp.comparison;

public class Line {

	
	public static final char SIGN_ADD = '+';
	public static final char SIGN_DELETE = '-';
	public static final char SIGN_NEUTRAL = ' ';
	public static final char SIGN_INITIAL = '?';

	
	private char sign = SIGN_NEUTRAL;
	private int lineNumber = -1;
	private int sourceLineNumber = -1;
	private String text;
	private ComparisonLine comparisonLine;

	public Line() {
	}

	public Line(int lineNumber, int sourceLineNumber, char sign, String text) {
		this.lineNumber = lineNumber;
		this.sourceLineNumber = sourceLineNumber;
		this.sign = sign;
		this.text = text;
	}

	public ComparisonLine getComparisonLine() {
		return comparisonLine;
	}

	public void setComparisonLine(ComparisonLine comparisonLine) {
		this.comparisonLine = comparisonLine;
	}

	public char getSign() {
		return sign;
	}

	public void setSign(char sign) {
		this.sign = sign;
	}

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
