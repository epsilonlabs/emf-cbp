package org.eclipse.epsilon.cbp.event;

/**
 * Base class for any visitor: less fragile than using the interface outright.
 */
public class DefaultChangeEventVisitor<T> implements IChangeEventVisitor<T> {

	@Override
	public T visit(AddToEAttributeEvent addToEAttributeEvent) {
		return null;
	}

	@Override
	public T visit(AddToEReferenceEvent addToEReferenceEvent) {
		return null;
	}

	@Override
	public T visit(AddToResourceEvent addToResourceEvent) {
		return null;
	}

	@Override
	public T visit(CreateEObjectEvent createEObjectEvent) {
		return null;
	}

	@Override
	public T visit(MoveWithinEAttributeEvent moveWithinEAttributeEvent) {
		return null;
	}

	@Override
	public T visit(MoveWithinEReferenceEvent moveWithinEReferenceEvent) {
		return null;
	}

	@Override
	public T visit(RegisterEPackageEvent registerEPackageEvent) {
		return null;
	}

	@Override
	public T visit(RemoveFromEAttributeEvent removeFromEAttributeEvent) {
		return null;
	}

	@Override
	public T visit(RemoveFromEReferenceEvent removeFromEReferenceEvent) {
		return null;
	}

	@Override
	public T visit(RemoveFromResourceEvent removeFromResourceEvent) {
		return null;
	}

	@Override
	public T visit(SetEAttributeEvent setEAttributeEvent) {
		return null;
	}

	@Override
	public T visit(SetEReferenceEvent setEReferenceEvent) {
		return null;
	}

	@Override
	public T visit(UnsetEAttributeEvent unsetEAttributeEvent) {
		return null;
	}

	@Override
	public T visit(UnsetEReferenceEvent unsetEReferenceEvent) {
		return null;
	}

	@Override
	public T visit(DeleteEObjectEvent deleteEObjectEvent) {
		return null;
	}


}
