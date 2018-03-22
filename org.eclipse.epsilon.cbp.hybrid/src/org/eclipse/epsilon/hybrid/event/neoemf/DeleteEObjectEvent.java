package org.eclipse.epsilon.hybrid.event.neoemf;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.hybrid.HybridResource;

public class DeleteEObjectEvent extends org.eclipse.epsilon.cbp.event.DeleteEObjectEvent {

	protected HybridResource resource;

	public DeleteEObjectEvent(EObject eObject, String id) {
		super(eObject, id);
		this.eObject = eObject;
		this.eClass = eObject.eClass();
		this.id = id;
		this.setValue(eObject);
	}

	public DeleteEObjectEvent(EClass eClass, HybridResource resource, String id) {
		super();
		this.eClass = eClass;
		this.id = id;
		this.resource = resource;
		this.eObject = resource.getEObject(id);
	}

	@Override
	public void replay() {
		this.eObject = resource.getEObject(this.id);
		this.setValue(eObject);
		try {
			EcoreUtil.delete(eObject);
		} catch (Exception e) {
			for (EContentsEList.FeatureIterator featureIterator = (EContentsEList.FeatureIterator) eObject
					.eCrossReferences().iterator(); featureIterator.hasNext();) {
				EObject eObject = (EObject) featureIterator.next();
				EReference eReference = (EReference) featureIterator.feature();
//				eReference.eContents().remove(this.eObject);
			}
			EcoreUtil.remove(eObject);
		}
		resource.unregister(eObject);
	}

}