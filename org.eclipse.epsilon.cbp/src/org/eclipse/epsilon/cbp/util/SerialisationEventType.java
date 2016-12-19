package org.eclipse.epsilon.cbp.util;

public class SerialisationEventType {


	//resource related events
	public static final int CREATE_AND_ADD_TO_RESOURCE = 0;
	public static final int ADD_TO_RESOURCE = 1;
	public static final int REMOVE_FROM_RESOURCE = 2;
	
	//attribute related events
	public static final int SET_EATTRIBUTE_PRIMITIVE = 3;
    public static final int SET_EATTRIBUTE_COMPLEX = 4;
    public static final int ADD_TO_EATTRIBUTE_PRIMITIVE = 5;
    public static final int ADD_TO_EATTRIBUTE_COMPLEX = 6;
    public static final int REMOVE_FROM_EATTRIBUTE_PRIMITIVE = 7;
    public static final int REMOVE_FROM_EATTRIBUTE_COMPLEX = 8;
    
    //reference related events
	public static final int SET_EREFERENCE = 9;
	public static final int CREATE_AND_SET_EREFERENCE = 10;
	public static final int CREATE_AND_ADD_TO_EREFERENCE = 11;
	public static final int ADD_TO_EREFERENCE = 12;
    public static final int REMOVE_FROM_EREFERENCE = 13;
    
    //EPackage registration event
    public static final int REGISTER_EPACKAGE = 14;
    
    
	//resource related events
	public static final String CREATE_AND_ADD_TO_RESOURCE_VERBOSE = "CREATE_AND_ADD_TO_RESOURCE";
	public static final String ADD_TO_RESOURCE_VERBOSE = "ADD_TO_RESOURCE";
	public static final String REMOVE_FROM_RESOURCE_VERBOSE = "REMOVE_FROM_RESOURCE";
	
	//attribute related events
	public static final String SET_EATTRIBUTE_PRIMITIVE_VERBOSE = "SET_EATTRIBUTE_PRIMITIVE";
    public static final String SET_EATTRIBUTE_COMPLEX_VERBOSE = "SET_EATTRIBUTE_COMPLEX";
    public static final String ADD_TO_EATTRIBUTE_PRIMITIVE_VERBOSE = "ADD_TO_EATTRIBUTE_PRIMITIVE";
    public static final String ADD_TO_EATTRIBUTE_COMPLEX_VERBOSE = "ADD_TO_EATTRIBUTE_COMPLEX";
    public static final String REMOVE_FROM_EATTRIBUTE_PRIMITIVE_VERBOSE = "REMOVE_FROM_EATTRIBUTE_PRIMITIVE";
    public static final String REMOVE_FROM_EATTRIBUTE_COMPLEX_VERBOSE = "REMOVE_FROM_EATTRIBUTE_COMPLEX";
    
    //reference related events
	public static final String SET_EREFERENCE_VERBOSE = "SET_EREFERENCE";
	public static final String CREATE_AND_SET_EREFERENCE_VERBOSE = "CREATE_AND_SET_EREFERENCE";
	public static final String CREATE_AND_ADD_TO_EREFERENCE_VERBOSE = "CREATE_AND_ADD_TO_EREFERENCE";
	public static final String ADD_TO_EREFERENCE_VERBOSE = "ADD_TO_EREFERENCE";
    public static final String REMOVE_FROM_EREFERENCE_VERBOSE = "REMOVE_FROM_EREFERENCE";
    
    //EPackage registration event
    public static final String REGISTER_EPACKAGE_VERBOSE = "REGISTER_EPACKAGE";    
    

}
