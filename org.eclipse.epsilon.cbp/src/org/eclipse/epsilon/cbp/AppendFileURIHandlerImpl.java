package org.eclipse.epsilon.cbp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.FileURIHandlerImpl;

public class AppendFileURIHandlerImpl extends FileURIHandlerImpl {

	public static String OPTION_APPEND = "APPEND";
	
	public static void main(String[] args) throws Exception {
		AppendFileURIHandlerImpl handler = new AppendFileURIHandlerImpl();
		HashMap<Object, Object> options = new HashMap<Object, Object>();
		options.put(AppendFileURIHandlerImpl.OPTION_APPEND, false);
		handler.createOutputStream(URI.createFileURI("/tmp/foo.model"), options);
	}
	
	@Override
	public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException {
		String filePath = uri.toFileString();
		final File file = new File(filePath);
		final boolean append = options.containsKey(OPTION_APPEND) ? Boolean.parseBoolean(options.get(OPTION_APPEND)+"") : false;
		String parent = file.getParent();
		if (parent != null) {
			new File(parent).mkdirs();
		}
		final Map<Object, Object> response = getResponse(options);
		OutputStream outputStream = new FileOutputStream(file, append) {
			@Override
			public void close() throws IOException {
				try {
					super.close();
				} finally {
					if (response != null) {
						response.put(URIConverter.RESPONSE_TIME_STAMP_PROPERTY, file.lastModified());
					}
				}
			}
		};
		return outputStream;
	}

}
