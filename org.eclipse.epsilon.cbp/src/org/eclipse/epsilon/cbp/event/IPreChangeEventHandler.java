package org.eclipse.epsilon.cbp.event;

public interface IPreChangeEventHandler {
	public boolean isCancelled(ChangeEvent<?> e);
}
