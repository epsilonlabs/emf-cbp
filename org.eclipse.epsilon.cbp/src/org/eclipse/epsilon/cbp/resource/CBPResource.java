package org.eclipse.epsilon.cbp.resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ArchiveURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.EFSURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.resource.impl.PlatformResourceURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.ChangeEventAdapter;
import org.eclipse.epsilon.cbp.history.EObjectEventsAdapter;
import org.eclipse.epsilon.cbp.util.AppendFileURIHandlerImpl;
import org.eclipse.epsilon.cbp.util.AppendingURIHandler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public abstract class CBPResource extends ResourceImpl {

	protected ChangeEventAdapter changeEventAdapter;
	protected BiMap<EObject, String> eObjectToIdMap;
	
	protected Set<Integer> ignoreList;
	protected EObjectEventsAdapter eObjectHistoryList;
	
	public CBPResource() {
		super();
		changeEventAdapter = new ChangeEventAdapter(this);
		this.eAdapters().add(changeEventAdapter);
		this.eObjectToIdMap = HashBiMap.create();
		
		this.ignoreList = new TreeSet<>();
		this.eObjectHistoryList = new EObjectEventsAdapter(ignoreList);

	}

	public CBPResource(URI uri) {
		this();
		this.uri = uri;
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

	public List<ChangeEvent<?>> getChangeEvents() {
		return changeEventAdapter.getChangeEvents();
	}

	@Override
	public String getURIFragment(EObject eObject) {
		String uriFragment = eObjectToIdMap.get(eObject);
		if (uriFragment == null) {
			uriFragment = EcoreUtil.getURI(eObject).toString();
		}
		return uriFragment;
	}

	@Override
	public EObject getEObject(String uriFragment) {
		return eObjectToIdMap.inverse().get(uriFragment);
	}

	public String register(EObject eObject) {
		String id = eObjectToIdMap.size() + "";
		adopt(eObject, id);
		return id;
	}
	
	public void adopt(EObject eObject, String id) {
		if (!eObjectToIdMap.containsKey(eObject))
			eObjectToIdMap.put(eObject, id);
	}

	public boolean isRegistered(EObject eObject) {
		return eObjectToIdMap.containsKey(eObject);
	}

	@Override
	protected void doUnload() {
		changeEventAdapter.setEnabled(false);
		super.doUnload();
	}

	protected EObject resolveXRef(final String sEObjectURI) {
		EObject eob = getEObject(sEObjectURI);
		if (eob == null) {
			URI uri = URI.createURI(sEObjectURI);

			String nsURI = uri.trimFragment().toString();
			EPackage pkg = (EPackage) getResourceSet().getPackageRegistry().get(nsURI);
			if (pkg != null) {
				eob = pkg.eResource().getEObject(uri.fragment());
			}
		}
		return eob;
	}
	
	public Set<Integer> getIgnoreList() {
		return this.ignoreList;
	}

	public void setIgnoreList(Set<Integer> ignoreList) {
		this.ignoreList = ignoreList;
	}
	
	public EObjectEventsAdapter getEObjectHistoryList() {
		return eObjectHistoryList;
	}
	
}
