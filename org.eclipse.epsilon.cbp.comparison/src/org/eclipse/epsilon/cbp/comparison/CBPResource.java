package org.eclipse.epsilon.cbp.comparison;

public class CBPResource extends CBPObject {
    
    public static final String RESOURCE_STRING = "resource";
    
    public CBPResource() {
	this(RESOURCE_STRING);
    }
    
    public CBPResource(String id) {
	super(id);
	
	CBPFeature feature = new CBPFeature(this, RESOURCE_STRING, CBPFeatureType.REFERENCE, true, true);
	this.getFeatures().put(RESOURCE_STRING, feature);
    }

    public CBPFeature getResourceFeature() {
	return this.getFeatures().get(RESOURCE_STRING);
    }
}
