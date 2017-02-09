package org.eclipse.epsilon.cbp.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ArchiveURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.EFSURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.FileURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.PlatformResourceURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.io.AbstractCBPDeserialiser;
import org.eclipse.epsilon.cbp.io.AbstractCBPSerialiser;
import org.eclipse.epsilon.cbp.util.Changelog;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public abstract class CBPResource extends ResourceImpl {
	
	protected EventAdapter eventAdapter;
	protected Changelog changelog = new Changelog();
	protected boolean verbose = false;
	private boolean resume = false;

	// eobject to id map
	protected TObjectIntMap<EObject> eObjToIDMap = new TObjectIntHashMap<EObject>();

	// current id, increases when an object is encountered
	protected int current_id = 0;

	public CBPResource(URI uri) {
		super(uri);
		eventAdapter = new EventAdapter(changelog);
		this.eAdapters().add(eventAdapter);
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		AbstractCBPSerialiser serialiser = getSerialiser();
		try {
			serialiser.serialise(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load(Map<?, ?> options) throws IOException {
		boolean defaultLoading = false;
		if (options.get("DEFAULT_LOADING") != null) {
			defaultLoading = (boolean) options.get("DEFAULT_LOADING");
		}
		if (defaultLoading) {
			super.load(options);
		}
		else {
			// We do not want changes during loading to be logged
			eventAdapter.setEnabled(false);
			AbstractCBPDeserialiser deserialiser = getDeserialiser();
			try {
				deserialiser.deserialise(options, null);
				eventAdapter.setEnabled(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		boolean defaultLoading = false;
		if (options.get("DEFAULT_LOADING") != null) {
			defaultLoading = (boolean) options.get("DEFAULT_LOADING");
		}
		if (defaultLoading) {
			super.load(options);
		}
		else {
			// We do not want changes during loading to be logged
			eventAdapter.setEnabled(false);
			AbstractCBPDeserialiser deserialiser = getDeserialiser();
			try {
				deserialiser.deserialise(options, inputStream);
				eventAdapter.setEnabled(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public abstract AbstractCBPSerialiser getSerialiser();

	public abstract AbstractCBPDeserialiser getDeserialiser();

	public boolean isResume() {
		return resume;
	}

	public void setResume(boolean resume) {
		this.resume = resume;
	}

	public void setChangelog(Changelog changelog) {
		this.changelog = changelog;
	}

	// add object to map, return false if object exists
	public boolean addObjectToMap(EObject obj) {
		// if obj is not in the map
		if (!eObjToIDMap.containsKey(obj)) {
			// put current id
			eObjToIDMap.put(obj, current_id);

			// increase current id
			current_id = current_id + 1;
			return true;
		}
		return false;
	}

	// add object to map with a specific id
	public boolean addObjectToMap(EObject obj, int id) {
		// if obj is not in the map
		if (!eObjToIDMap.containsKey(obj)) {
			// put id
			eObjToIDMap.put(obj, id);

			// if current id is less than id, set current id
			if (id >= current_id) {
				current_id = id + 1;
			}
			return true;
		}
		return false;
	}
	
	// get the object id based on obj
	public int getObjectId(EObject obj) {
		if (!eObjToIDMap.containsKey(obj)) {
			System.err.println("ChangeLog: search returned false");
		}
		return eObjToIDMap.get(obj);
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
            new FileURIHandlerImpl(), 
            new EFSURIHandlerImpl(), 
            new ArchiveURIHandlerImpl(), 
            new URIHandlerImpl()
          }), ContentHandler.Registry.INSTANCE.contentHandlers());
	}
	
}
