package org.eclipse.epsilon.cbp.comparison.event;

/**
 * Base class for any visitor: less fragile than using the interface outright.
 */
public class CBPDefaultChangeEventVisitor<T> implements ICBPChangeEventVisitor<T> {

	@Override
	public T visit(CBPAddToEAttributeEvent addToEAttributeEvent) {
		return null;
	}

	@Override
	public T visit(CBPAddToEReferenceEvent addToEReferenceEvent) {
		return null;
	}

	@Override
	public T visit(CBPAddToResourceEvent addToResourceEvent) {
		return null;
	}

	@Override
	public T visit(CBPCreateEObjectEvent createEObjectEvent) {
		return null;
	}

	@Override
	public T visit(CBPMoveWithinEAttributeEvent moveWithinEAttributeEvent) {
		return null;
	}

	@Override
	public T visit(CBPMoveWithinEReferenceEvent moveWithinEReferenceEvent) {
		return null;
	}

	@Override
	public T visit(CBPRegisterEPackageEvent registerEPackageEvent) {
		return null;
	}

	@Override
	public T visit(CBPRemoveFromEAttributeEvent removeFromEAttributeEvent) {
		return null;
	}

	@Override
	public T visit(CBPRemoveFromEReferenceEvent removeFromEReferenceEvent) {
		return null;
	}

	@Override
	public T visit(CBPRemoveFromResourceEvent removeFromResourceEvent) {
		return null;
	}

	@Override
	public T visit(CBPSetEAttributeEvent setEAttributeEvent) {
		return null;
	}

	@Override
	public T visit(CBPSetEReferenceEvent setEReferenceEvent) {
		return null;
	}

	@Override
	public T visit(CBPUnsetEAttributeEvent unsetEAttributeEvent) {
		return null;
	}

	@Override
	public T visit(CBPUnsetEReferenceEvent unsetEReferenceEvent) {
		return null;
	}

	@Override
	public T visit(CBPDeleteEObjectEvent deleteEObjectEvent) {
		return null;
	}

	@Override
	public T visit(CBPStartNewSessionEvent startNewSessionEvent) {
		return null;
	}

}
