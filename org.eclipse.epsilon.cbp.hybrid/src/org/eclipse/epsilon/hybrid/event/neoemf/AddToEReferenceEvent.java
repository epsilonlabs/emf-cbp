package org.eclipse.epsilon.hybrid.event.neoemf;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList.UnmodifiableEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.event.IChangeEventVisitor;

import fr.inria.atlanmod.neoemf.core.PersistentEObject;
import fr.inria.atlanmod.neoemf.core.StringId;

public class AddToEReferenceEvent extends org.eclipse.epsilon.cbp.event.AddToEReferenceEvent {

	@SuppressWarnings("unchecked")
	@Override
	public void replay() {

		List<EObject> list = (List<EObject>) target.eGet(getEStructuralFeature());
		if (list instanceof UnmodifiableEList) {
			return;
		}
		if (position > list.size()) {
			position = list.size();
		}

		try {
//			for (EObject value : this.getValues()) {
//				((PersistentEObject) value).setMapped(false);
//			}
			list.addAll(position, this.getValues());
		} catch (IllegalArgumentException e) {
			for (EObject value : this.getValues()) {
				((PersistentEObject) value).id(new StringId(EcoreUtil.generateUUID()));
			}
			list.addAll(position, this.getValues());
		}

	}

	@Override
	public <U> U accept(IChangeEventVisitor<U> visitor) {
		return visitor.visit(this);
	}

}
