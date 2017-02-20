package org.eclipse.epsilon.cbp.resource;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.UniqueEList;
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

public abstract class CBPResource extends ResourceImpl {
	
	protected EventAdapter eventAdapter;
	protected UniqueEList<EObject> eObjects;
	
	public CBPResource(URI uri) {
		super(uri);
		eventAdapter = new EventAdapter(this);
		this.eAdapters().add(eventAdapter);
		this.eObjects = new UniqueEList<EObject>();
	}
		
	// Copied from URIHandler.DEFAULT_HANDLERS and ExtensibleURIConverterImpl()
	@Override
	protected URIConverter getURIConverter() {
		return new ExtensibleURIConverterImpl(Arrays.asList(new URIHandler []
          { 
            new PlatformResourceURIHandlerImpl(), 
            new AppendFileURIHandlerImpl(), 
            new EFSURIHandlerImpl(), 
            new ArchiveURIHandlerImpl(), 
            new URIHandlerImpl()
          }), ContentHandler.Registry.INSTANCE.contentHandlers());
	}
	
	public List<Event<?>> getEvents() {
		return eventAdapter.getEvents();
	}
	
	public UniqueEList<EObject> getEObjects() {
		return eObjects;
	}
	
	@Override
	public String getURIFragment(EObject eObject) {
		return eObjects.indexOf(eObject) + "";
	}
	
}
