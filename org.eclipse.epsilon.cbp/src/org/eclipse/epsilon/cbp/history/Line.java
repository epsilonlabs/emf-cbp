package org.eclipse.epsilon.cbp.history;

import java.util.List;

public class Line {

	protected int lineNumber;
	protected Object value;

	Line(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	Line(int lineNumber, Object value) {
		this.lineNumber = lineNumber;
		this.value = value;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		String val = "";
		if (value instanceof List) {
			val = "[" + lineNumber + ",[" + ((List<Object>) value).get(0).toString() + ","
					+ ((List<Object>) value).get(1).toString() + "]]";
		} else {
			val = (value == null) ? "[" + lineNumber + "]" : "[" + lineNumber + "," + value.toString() + "]";
		}
		return val;
	}

}
