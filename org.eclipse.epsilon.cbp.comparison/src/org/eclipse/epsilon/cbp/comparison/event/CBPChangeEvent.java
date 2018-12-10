package org.eclipse.epsilon.cbp.comparison.event;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.epsilon.cbp.event.ChangeEvent;

public abstract class CBPChangeEvent<T> {

    protected Collection<Object> values = new ArrayList<Object>();
    protected Collection<Object> oldValues = new ArrayList<Object>();

    protected int position = -1;
    protected String composite = null;

    public String getComposite() {
	return composite;
    }

    public void setComposite(String composite) {
	this.composite = composite;
    }

    public Collection<Object> getValues() {
	return values;
    }

    public Collection<Object> getOldValues() {
	return oldValues;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setValues(Object t) {
	if (t instanceof Collection<?>) {
	    values.addAll((Collection) t);
	} else {
	    values.add((String) t);
	}
    }

    public void setValue(String value) {
	values.add(value);
    }

    public Object getValue() {
	// assert values.size() < 2;
	if (values.isEmpty()) {
	    return null;
	} else {
	    return values.iterator().next();
	}
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setOldValues(Object t) {
	if (t instanceof Collection<?>) {
	    oldValues.addAll((Collection) t);
	} else {
	    oldValues.add((String) t);
	}
    }

    public void setOldValue(String value) {
	oldValues.add(value);
    }

    public Object getOldValue() {
	// assert values.size() < 2;
	if (oldValues.isEmpty()) {
	    return null;
	} else {
	    return oldValues.iterator().next();
	}
    }

    public int getPosition() {
	return position;
    }

    public void setPosition(int position) {
	this.position = position;
    }

    public CBPChangeEvent<?> reverse() {
	this.setComposite(null);
	return this;
    }
    
    @Override
    public String toString() {
	return this.getClass().getSimpleName() + "." + getOldValue() + "." + getValue() + "." + getPosition(); 
    }

}
