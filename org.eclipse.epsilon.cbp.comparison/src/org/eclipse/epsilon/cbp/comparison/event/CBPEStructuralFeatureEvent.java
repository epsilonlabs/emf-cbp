package org.eclipse.epsilon.cbp.comparison.event;

public abstract class CBPEStructuralFeatureEvent<T> extends CBPChangeEvent<T> {

    protected String eStructuralFeature;
    protected String target;

    protected String eClass;

    public String getEClass() {
	return eClass;
    }

    public void setEClass(String eClass) {
	this.eClass = eClass;
    }

    public String getEStructuralFeature() {
	return eStructuralFeature;
    }

    public String getTarget() {
	return target;
    }

    public void setEStructuralFeature(String eStructuralFeature) {
	this.eStructuralFeature = eStructuralFeature;
    }

    public void setTarget(String target) {
	this.target = target;
    }

}
