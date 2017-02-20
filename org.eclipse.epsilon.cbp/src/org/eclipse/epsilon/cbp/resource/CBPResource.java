package org.eclipse.epsilon.cbp.resource;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ArchiveURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.EFSURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.PlatformResourceURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.util.AppendFileURIHandlerImpl;
import org.eclipse.epsilon.cbp.util.AppendingURIHandler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public abstract class CBPResource extends ResourceImpl {

	protected EventAdapter eventAdapter;
	protected BiMap<EObject, String> eObjectToIdMap;

	public CBPResource(URI uri) {
		super(uri);
		eventAdapter = new EventAdapter(this);
		this.eAdapters().add(eventAdapter);
		this.eObjectToIdMap = HashBiMap.create();
	}

	// Adapted from URIHandler.DEFAULT_HANDLERS and ExtensibleURIConverterImpl()
	@Override
	protected URIConverter getURIConverter() {
		return new ExtensibleURIConverterImpl(Arrays.asList(new URIHandler[] { 
				new AppendFileURIHandlerImpl(),
				new AppendingURIHandler(new PlatformResourceURIHandlerImpl()),
				new AppendingURIHandler(new EFSURIHandlerImpl()), 
				new AppendingURIHandler(new ArchiveURIHandlerImpl()),
				new AppendingURIHandler(new URIHandlerImpl()) }), 
				ContentHandler.Registry.INSTANCE.contentHandlers());
	}

	public List<Event<?>> getEvents() {
		return eventAdapter.getEvents();
	}

	@Override
	public String getURIFragment(EObject eObject) {
		return eObjectToIdMap.get(eObject);
	}

	@Override
	public EObject getEObject(String uriFragment) {
		return eObjectToIdMap.inverse().get(uriFragment);
	}

	public String adopt(EObject eObject) {
		String id = eObjectToIdMap.size() + ""/*EcoreUtil.generateUUID()*/;
		adopt(eObject, id);
		return id;
	}
	
	public void adopt(EObject eObject, String id) {
		if (!eObjectToIdMap.containsKey(eObject))
			eObjectToIdMap.put(eObject, id);
	}

	public boolean owns(EObject eObject) {
		return eObjectToIdMap.containsKey(eObject);
	}

}
