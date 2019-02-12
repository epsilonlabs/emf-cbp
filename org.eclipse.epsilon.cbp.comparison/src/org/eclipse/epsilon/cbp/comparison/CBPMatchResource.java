package org.eclipse.epsilon.cbp.comparison;

import java.util.Map;

public class CBPMatchResource extends CBPMatchObject {
    
    public static final String RESOURCE_STRING = "resource";
    
    public CBPMatchResource(Map<String, CBPMatchObject> objectTree) {
	this(RESOURCE_STRING, objectTree);
    }
    
    public CBPMatchResource(String id, Map<String, CBPMatchObject> objectTree) {
	super("Resource", id, objectTree);
	
	CBPMatchFeature feature = new CBPMatchFeature(this, RESOURCE_STRING, CBPFeatureType.REFERENCE, true, true, true);
	this.getFeatures().put(RESOURCE_STRING, feature);
    }

    public CBPMatchFeature getResourceFeature() {
	return this.getFeatures().get(RESOURCE_STRING);
    }
}
