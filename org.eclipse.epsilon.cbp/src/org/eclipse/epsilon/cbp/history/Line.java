package org.eclipse.epsilon.cbp.history;

import java.util.List;

public class Line {

	protected int lineNumber;
	protected Object value;

	Line(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	Line(int lineNumber2, Object value)  {
		this.lineNumber = lineNumber2;
		this.value = value;
		if (lineNumber2 < 0) {
			int x = 2/ 0;
		}
	}

	public int getEventNumber() {
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
