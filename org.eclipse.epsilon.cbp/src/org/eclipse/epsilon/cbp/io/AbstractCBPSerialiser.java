package org.eclipse.epsilon.cbp.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.event.AddEObjectsToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EPackageRegistrationEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.PersistenceUtil;
import org.eclipse.epsilon.cbp.util.SimpleType;

import gnu.trove.map.TObjectIntMap;

public abstract class AbstractCBPSerialiser {

	// epacks involved
	protected Set<EPackage> ePackages;

	// event list
	protected List<Event> eventList;

	// epackage element map
	protected ModelElementIDMap ePackageElementsNamesMap;

	// common simple type name
	protected TObjectIntMap<String> commonsimpleTypeNameMap;

	// tet simple type name
	protected TObjectIntMap<String> textSimpleTypeNameMap;

	protected CBPResource resource = null;

	protected PersistenceUtil persistenceUtil = PersistenceUtil.getInstance();

	public abstract void serialise(OutputStream out, Map<?, ?> options) throws IOException;

	public CBPResource getResource() {
		return resource;
	}

	protected abstract void handleEPackageRegistrationEvent(EPackageRegistrationEvent e, Closeable out);

	protected abstract void handleAddToResourceEvent(AddEObjectsToResourceEvent e, Closeable out) throws IOException;

	protected abstract void handleRemoveFromResourceEvent(RemoveFromResourceEvent e, Closeable out) throws IOException;

	protected abstract void handleSetEAttributeEvent(EAttributeEvent e, Closeable out) throws IOException;

	protected abstract void handleAddToEAttributeEvent(EAttributeEvent e, Closeable out) throws IOException;

	protected abstract void handleSetEReferenceEvent(SetEReferenceEvent e, Closeable out) throws IOException;

	protected abstract void handleAddToEReferenceEvent(AddToEReferenceEvent e, Closeable out) throws IOException;

	protected abstract void handleRemoveFromAttributeEvent(EAttributeEvent e, Closeable out) throws IOException;

	protected abstract void handleRemoveFromEReferenceEvent(RemoveFromEReferenceEvent e, Closeable out)
			throws IOException;

	protected int getTypeID(EDataType type) {
		if (commonsimpleTypeNameMap.containsKey(type.getName())) {
			return commonsimpleTypeNameMap.get(type.getName());
		} else if (textSimpleTypeNameMap.containsKey(type.getName())) {
			return textSimpleTypeNameMap.get(type.getName());
		}

		return SimpleType.COMPLEX_TYPE;
	}

}
