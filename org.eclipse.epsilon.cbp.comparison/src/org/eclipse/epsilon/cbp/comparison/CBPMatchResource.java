package org.eclipse.epsilon.cbp.comparison;

public class CBPMatchResource extends CBPMatchObject {
    
    public static final String RESOURCE_STRING = "resource";
    
    public CBPMatchResource() {
	this(RESOURCE_STRING);
    }
    
    public CBPMatchResource(String id) {
	super("Resource", id);
	
	CBPMatchFeature feature = new CBPMatchFeature(this, RESOURCE_STRING, CBPFeatureType.REFERENCE, true, true);
	this.getFeatures().put(RESOURCE_STRING, feature);
    }

    public CBPMatchFeature getResourceFeature() {
	return this.getFeatures().get(RESOURCE_STRING);
    }
}
