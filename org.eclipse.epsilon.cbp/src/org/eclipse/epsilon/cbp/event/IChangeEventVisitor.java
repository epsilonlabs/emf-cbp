package org.eclipse.epsilon.cbp.event;

/**
 * Visitor interface to do different things depending on the type of event.
 */
public interface IChangeEventVisitor<T> {

	T visit(AddToEAttributeEvent addToEAttributeEvent);
	T visit(AddToEReferenceEvent addToEReferenceEvent);
	T visit(AddToResourceEvent addToResourceEvent);
	T visit(StartNewSessionEvent startNewSEssionEvent);
	T visit(CreateEObjectEvent createEObjectEvent);
	T visit(DeleteEObjectEvent deleteEObjectEvent);
	T visit(MoveWithinEAttributeEvent moveWithinEAttributeEvent);
	T visit(MoveWithinEReferenceEvent moveWithinEReferenceEvent);
	T visit(RegisterEPackageEvent registerEPackageEvent);
	T visit(RemoveFromEAttributeEvent removeFromEAttributeEvent);
	T visit(RemoveFromEReferenceEvent removeFromEReferenceEvent);
	T visit(RemoveFromResourceEvent removeFromResourceEvent);
	T visit(SetEAttributeEvent setEAttributeEvent);
	T visit(SetEReferenceEvent setEReferenceEvent);
	T visit(UnsetEAttributeEvent unsetEAttributeEvent);
	T visit(UnsetEReferenceEvent unsetEReferenceEvent);

}
