package org.eclipse.epsilon.cbp.comparison.event;

/**
 * Visitor interface to do different things depending on the type of event.
 */
public interface ICBPChangeEventVisitor<T> {

	T visit(CBPAddToEAttributeEvent addToEAttributeEvent);
	T visit(CBPAddToEReferenceEvent addToEReferenceEvent);
	T visit(CBPAddToResourceEvent addToResourceEvent);
	T visit(CBPStartNewSessionEvent startNewSessionEvent);
	T visit(CBPCreateEObjectEvent createEObjectEvent);
	T visit(CBPDeleteEObjectEvent deleteEObjectEvent);
	T visit(CBPMoveWithinEAttributeEvent moveWithinEAttributeEvent);
	T visit(CBPMoveWithinEReferenceEvent moveWithinEReferenceEvent);
	T visit(CBPRegisterEPackageEvent registerEPackageEvent);
	T visit(CBPRemoveFromEAttributeEvent removeFromEAttributeEvent);
	T visit(CBPRemoveFromEReferenceEvent removeFromEReferenceEvent);
	T visit(CBPRemoveFromResourceEvent removeFromResourceEvent);
	T visit(CBPSetEAttributeEvent setEAttributeEvent);
	T visit(CBPSetEReferenceEvent setEReferenceEvent);
	T visit(CBPUnsetEAttributeEvent unsetEAttributeEvent);
	T visit(CBPUnsetEReferenceEvent unsetEReferenceEvent);

}
