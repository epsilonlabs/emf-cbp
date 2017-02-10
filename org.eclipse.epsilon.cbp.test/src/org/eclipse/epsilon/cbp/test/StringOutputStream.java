package org.eclipse.epsilon.cbp.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream {

	StringBuffer buffer = new StringBuffer();

	@Override
	public void write(int chr) throws IOException {
		buffer.append((char) chr);
	}

	@Override
	public String toString() {
		return buffer.toString();
	}
	
	public String getLine(int number) {
		return toString().split(System.getProperty("line.separator"))[number];
	}
	
	public InputStream getInputStream() {
		return new ByteArrayInputStream(toString().getBytes());
	}
	
}