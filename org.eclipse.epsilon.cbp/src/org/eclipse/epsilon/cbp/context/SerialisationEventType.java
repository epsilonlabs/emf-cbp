package org.eclipse.epsilon.cbp.context;

public class SerialisationEventType {

	public static final int CREATE_AND_ADD_EOBJECTS_TO_RESOURCE = 0;
	public static final int CREATE_EOBJECTS_AND_SET_EREFERENCE_VALUES = 1;
    public static final int ADD_EOBJECTS_TO_RESOURCE = 2;
    public static final int SET_EOBJECT_EREFERENCE_VALUES = 3;
    public static final int SET_EOBJECT_PRIMITIVE_EATTRIBUTE_VALUES = 4;
    public static final int SET_EOBJECT_COMPLEX_EATTRIBUTE_VALUES = 5;
    public static final int REMOVE_EOBJECTS_FROM_RESOURCE = 6;
    public static final int UNSET_EOBJECT_PRIMITIVE_EATTRIBUTE_VALUES = 7;
    public static final int UNSET_EOBJECT_COMPLEX_EATTRIBUTE_VALUES = 8;
    public static final int UNSET_EOBJECT_EREFERENCE_VALUES = 9;
}
