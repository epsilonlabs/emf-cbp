package org.eclipse.epsilon.cbp.event;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;

public class RemoveFromEReferenceEvent extends MultiValueEReferenceEvent implements EObjectValuesEvent {
	
	@SuppressWarnings("unchecked")
	@Override
	public void replay() {
		
		
		((Collection<EObject>) target.eGet(getEStructuralFeature())).removeAll(getValues());
	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

	@Override
	public ChangeEvent<?> reverse(){
		AddToEReferenceEvent event = new AddToEReferenceEvent();
		event.setEStructuralFeature(this.getEStructuralFeature());
		event.setValues(this.getValues());
		event.setOldValues(this.getOldValue());
		event.setPosition(this.getPosition());;
		event.setTarget(this.getTarget());
		event.setComposite(this.getComposite());
		return event;
	}
}
