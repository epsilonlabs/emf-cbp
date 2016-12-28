package org.eclipse.epsilon.cbp.exceptions;

public class UnknownPackageException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownPackageException(String nsURI) {
		super("The package with nsURI: " + nsURI + " could not be found within the"
				+ " global EPackage registry. Did you register it?");
	}
}
