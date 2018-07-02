package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ChangeEvent<T> {
	
	protected Collection<T> values = new ArrayList<T>();
	protected Collection<T> oldValues = new ArrayList<T>();
	
	protected int position = -1;
	protected String composite = null;
	

	public String getComposite() {
		return composite;
	}

	public void setComposite(String composite) {
		this.composite = composite;
	}

	public Collection<T> getValues() {
		return values;
	}
	
	public Collection<T> getOldValues() {
		return oldValues;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setValues(Object t) {
		if (t instanceof Collection<?>) {
			values.addAll((Collection) t);
		}
		else {
			values.add((T) t);
		}
	}
	
	public void setValue(T value) {
		values.add(value);
	}
	
	public T getValue() {
		//assert values.size() < 2;
		if (values.isEmpty()) {
			return null;
		}
		else {
			return values.iterator().next();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setOldValues(Object t) {
		if (t instanceof Collection<?>) {
			oldValues.addAll((Collection) t);
		}
		else {
			oldValues.add((T) t);
		}
	}
	
	public void setOldValue(T value) {
		oldValues.add(value);
	}
	
	public T getOldValue() {
		//assert values.size() < 2;
		if (oldValues.isEmpty()) {
			return null;
		}
		else {
			return oldValues.iterator().next();
		}
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}

	// TODO move into a visitor?
	public abstract void replay();

	public abstract <U> U accept(IChangeEventVisitor<U> visitor);
	
	public ChangeEvent<?> reverse(){
		this.setComposite(null);
		return this;
	}
}
