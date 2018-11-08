package org.eclipse.epsilon.cbp.comparison.event;

public class CBPEObject {
    
    private String id;
    private String className;
    
    public CBPEObject(String className, String id) {
	this.className = className;
	this.id = id;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
	return this.id;
    }
}
