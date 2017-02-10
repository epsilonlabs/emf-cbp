package org.eclipse.epsilon.cbp.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.epsilon.cbp.AppendFileURIHandlerImpl;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;
import org.eclipse.epsilon.cbp.util.Changelog;

public abstract class CBPResource extends ResourceImpl {
	
	protected EventAdapter eventAdapter;
	protected Changelog changelog = new Changelog();
	
	// eobject to id map
	protected HashMap<EObject, Integer> idMap = new HashMap<EObject, Integer>();
	
	// current id, increases when an object is encountered
	protected int currentId = 0;

	public CBPResource(URI uri) {
		super(uri);
		eventAdapter = new EventAdapter(changelog);
		this.eAdapters().add(eventAdapter);
	}
	
	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		getSerialiser().serialise(outputStream, options);
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		// We do not want changes during loading to be logged
		eventAdapter.setEnabled(false);
		getDeserialiser().deserialise(inputStream, options);
		eventAdapter.setEnabled(true);
	}
	
	public abstract AbstractCBPSerialiser getSerialiser();

	public abstract AbstractCBPDeserialiser getDeserialiser();

	public void setChangelog(Changelog changelog) {
		this.changelog = changelog;
	}

	// add object to map, return false if object exists
	public boolean addObjectToMap(EObject obj) {
		// if obj is not in the map
		if (!idMap.containsKey(obj)) {
			// put current id
			idMap.put(obj, currentId);

			// increase current id
			currentId = currentId + 1;
			return true;
		}
		return false;
	}

	// add object to map with a specific id
	public boolean addObjectToMap(EObject obj, int id) {
		// if obj is not in the map
		if (!idMap.containsKey(obj)) {
			// put id
			idMap.put(obj, id);

			// if current id is less than id, set current id
			if (id >= currentId) {
				currentId = id + 1;
			}
			return true;
		}
		return false;
	}
	
	// get the object id based on obj
	public int getObjectId(EObject obj) {
		if (!idMap.containsKey(obj)) {
			System.err.println("ChangeLog: search returned false");
		}
		return idMap.get(obj);
	}

	public Changelog getChangelog() {
		return changelog;
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
	
}
