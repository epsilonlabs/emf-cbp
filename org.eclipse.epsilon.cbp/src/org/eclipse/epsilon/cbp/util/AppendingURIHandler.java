package org.eclipse.epsilon.cbp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;

public class AppendingURIHandler implements URIHandler {
	
	protected URIHandler wrapped = null;
	
	public AppendingURIHandler(URIHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public boolean canHandle(URI uri) {
		return wrapped.canHandle(uri);
	}

	@Override
	public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
		return wrapped.createInputStream(uri, options);
	}

	@Override
	public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException {
		OutputStream outputStream = wrapped.createOutputStream(uri, options);
		try {
    		InputStream inputStream = createInputStream(uri, options);
    		int i = -1;
    		while ((i = inputStream.read()) != -1) {
    			outputStream.write(i);
    		}
		}
		catch (Exception ex) {}
		return outputStream;
	}

	@Override
	public void delete(URI uri, Map<?, ?> options) throws IOException {
		wrapped.delete(uri, options);
	}

	@Override
	public Map<String, ?> contentDescription(URI uri, Map<?, ?> options) throws IOException {
		return wrapped.contentDescription(uri, options);
	}

	@Override
	public boolean exists(URI uri, Map<?, ?> options) {
		return wrapped.exists(uri, options);
	}

	@Override
	public Map<String, ?> getAttributes(URI uri, Map<?, ?> options) {
		return wrapped.getAttributes(uri, options);
	}

	@Override
	public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException {
		wrapped.setAttributes(uri, attributes, options);
	}
	
	
}
