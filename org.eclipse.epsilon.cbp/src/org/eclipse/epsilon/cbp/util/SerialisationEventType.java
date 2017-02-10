package org.eclipse.epsilon.cbp.util;

public class SerialisationEventType {

	// resource related events
	public static final int CREATE_AND_ADD_TO_RESOURCE = 0;
	public static final int ADD_TO_RESOURCE = 1;
	public static final int REMOVE_FROM_RESOURCE = 2;

	// attribute related events
	public static final int SET_EATTRIBUTE = 3;
	public static final int ADD_TO_EATTRIBUTE = 4;
	public static final int REMOVE_FROM_EATTRIBUTE = 5;

	// reference related events
	public static final int SET_EREFERENCE = 6;
	public static final int CREATE_AND_SET_EREFERENCE = 7;
	public static final int CREATE_AND_ADD_TO_EREFERENCE = 8;
	public static final int ADD_TO_EREFERENCE = 9;
	public static final int REMOVE_FROM_EREFERENCE = 10;

	// EPackage registration event
	public static final int REGISTER_EPACKAGE = 11;

	// debugging events
	public static final int STOP_READING = 12;
}
