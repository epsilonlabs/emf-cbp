package org.eclipse.epsilon.cbp.util;

public class SerialisationEventType {

	// resource related events
	public static final int CREATE_AND_ADD_TO_RESOURCE = 0;
	public static final int ADD_TO_RESOURCE = 1;
	public static final int REMOVE_FROM_RESOURCE = 2;

	// attribute related events
	public static final int SET_EATTRIBUTE_PRIMITIVE = 3;
	public static final int SET_EATTRIBUTE_COMPLEX = 4;
	public static final int ADD_TO_EATTRIBUTE_PRIMITIVE = 5;
	public static final int ADD_TO_EATTRIBUTE_COMPLEX = 6;
	public static final int REMOVE_FROM_EATTRIBUTE_PRIMITIVE = 7;
	public static final int REMOVE_FROM_EATTRIBUTE_COMPLEX = 8;

	// reference related events
	public static final int SET_EREFERENCE = 9;
	public static final int CREATE_AND_SET_EREFERENCE = 10;
	public static final int CREATE_AND_ADD_TO_EREFERENCE = 11;
	public static final int ADD_TO_EREFERENCE = 12;
	public static final int REMOVE_FROM_EREFERENCE = 13;

	// EPackage registration event
	public static final int REGISTER_EPACKAGE = 14;

	// debugging events
	public static final int STOP_READING = 15;

	// debugging events
	public static final String STOP_VERBOSE = "STOP_READING";
}
