package org.eclipse.epsilon.cbp.comparison.emfstore;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class DeleteEObjectEvent extends org.eclipse.epsilon.cbp.event.DeleteEObjectEvent {

	private final CBP2EMFStoreAdapter resource;

	public DeleteEObjectEvent(EClass eClass, CBP2EMFStoreAdapter resource, String id) {
		super(eClass, id);
		this.eClass = eClass;
		this.id = id;
		this.resource = resource;
	}

	@Override
	public void replay() {
		try {
			eObject = resource.getEObject(id);
			if (eObject != null) {
				setValue(eObject);
				try {
					// ESModelElementId elementId = resource.getLocalProject().getModelElementId(eObject);
//					if (eObject.eContainer() != null) {
						EcoreUtil.delete(eObject);
//					}
				} catch (Exception e1) {
					System.console();
					try {
						EcoreUtil.remove(eObject);
					} catch (Exception e2) {
						System.console();
					}
				}
				// resource.unregister(eObject);
			}
		} catch (Exception e) {
			System.console();
		}
	}

}