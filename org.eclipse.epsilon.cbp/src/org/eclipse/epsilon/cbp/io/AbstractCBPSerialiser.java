package org.eclipse.epsilon.cbp.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.PersistenceUtil;
import org.eclipse.epsilon.cbp.util.SimpleType;

public abstract class AbstractCBPSerialiser {
	
	// event list
	protected List<Event> eventList;

	// epackage element map
	protected ModelElementIDMap ePackageElementsNamesMap;

	// common simple type name
	protected Map<String, Integer> commonsimpleTypeNameMap;

	// tet simple type name
	protected Map<String, Integer> textSimpleTypeNameMap;

	protected CBPResource resource = null;

	protected PersistenceUtil persistenceUtil = PersistenceUtil.getInstance();
	
	public AbstractCBPSerialiser(CBPResource resource) {
		this.eventList = resource.getEvents();

		this.commonsimpleTypeNameMap = persistenceUtil.getCommonSimpleTypesMap();
		this.textSimpleTypeNameMap = persistenceUtil.getTextSimpleTypesMap();

		this.resource = resource;
	}
	
	public void serialise(OutputStream out, Map<?, ?> options) throws IOException {
		for (Event e : eventList) {
			
			if (e instanceof RegisterEPackageEvent) {
				handleEPackageRegistrationEvent((RegisterEPackageEvent) e);
			}
			else if (e instanceof AddToResourceEvent) {
				handleAddToResourceEvent((AddToResourceEvent) e);
			}
			else if (e instanceof SetEAttributeEvent) {
				handleSetEAttributeEvent((SetEAttributeEvent) e);
			}
			else if (e instanceof AddToEAttributeEvent) {
				handleAddToEAttributeEvent((AddToEAttributeEvent) e);
			}
			else if (e instanceof RemoveFromEAttributeEvent) {
				handleRemoveFromAttributeEvent((RemoveFromEAttributeEvent) e);
			}
			else if (e instanceof SetEReferenceEvent) {
				handleSetEReferenceEvent((SetEReferenceEvent) e);
			}
			else if (e instanceof AddToEReferenceEvent) {
				handleAddToEReferenceEvent((AddToEReferenceEvent) e);
			}
			else if (e instanceof RemoveFromEReferenceEvent) {
				handleRemoveFromEReferenceEvent((RemoveFromEReferenceEvent) e);
			}
			else if (e instanceof RemoveFromResourceEvent) {
				handleRemoveFromResourceEvent((RemoveFromResourceEvent) e);
			}
		}		
	}

	public CBPResource getResource() {
		return resource;
	}

	protected abstract void handleEPackageRegistrationEvent(RegisterEPackageEvent e);

	protected abstract void handleAddToResourceEvent(AddToResourceEvent e) throws IOException;

	protected abstract void handleRemoveFromResourceEvent(RemoveFromResourceEvent e) throws IOException;

	protected abstract void handleSetEAttributeEvent(EAttributeEvent e) throws IOException;

	protected abstract void handleAddToEAttributeEvent(EAttributeEvent e) throws IOException;

	protected abstract void handleSetEReferenceEvent(SetEReferenceEvent e) throws IOException;

	protected abstract void handleAddToEReferenceEvent(AddToEReferenceEvent e) throws IOException;

	protected abstract void handleRemoveFromAttributeEvent(EAttributeEvent e) throws IOException;

	protected abstract void handleRemoveFromEReferenceEvent(RemoveFromEReferenceEvent e)
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
