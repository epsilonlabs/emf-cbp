package org.eclipse.epsilon.cbp.event;

import java.util.ArrayList;
import java.util.Collection;

public abstract class Event<T> {
	
	protected Collection<T> values = new ArrayList<T>();

	public Collection<T> getValues() {
		return values;
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
		assert values.size() < 2;
		if (values.isEmpty()) {
			return null;
		}
		else {
			return values.iterator().next();
		}
	}
	
	public void replay() {};
	
}
