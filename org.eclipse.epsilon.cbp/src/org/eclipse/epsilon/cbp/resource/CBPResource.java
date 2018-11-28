package org.eclipse.epsilon.cbp.resource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
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
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.ChangeEventAdapter;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.history.ModelHistory;
import org.eclipse.epsilon.cbp.util.AppendFileURIHandlerImpl;
import org.eclipse.epsilon.cbp.util.AppendingURIHandler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public abstract class CBPResource extends ResourceImpl {

    protected ChangeEventAdapter changeEventAdapter;
    protected BiMap<EObject, String> eObjectToIdMap;
    protected Set<Integer> ignoreSet;
    protected List<Integer> ignoreList;
    protected ModelHistory modelHistory;
    protected int persistedIgnoredEvents = 0;
    protected int idCounter = 0;
    protected IdType idType = IdType.NUMERIC;
    protected String idPrefix = "";

    public enum IdType {
	NUMERIC, UUID
    }

    public CBPResource() {
	this.ignoreSet = new HashSet<>();
	this.ignoreList = new ArrayList<Integer>();
	this.modelHistory = new ModelHistory(ignoreSet, this, ignoreList);
	this.changeEventAdapter = new ChangeEventAdapter(this, modelHistory);
	this.eAdapters().add(changeEventAdapter);
	this.eObjectToIdMap = HashBiMap.create();
    }

    public BiMap<EObject, String> getEObjectToIdMap() {
	return eObjectToIdMap;
    }

    public ChangeEventAdapter getChangeEventAdapter() {
	return changeEventAdapter;
    }

    public ModelHistory getModelHistory() {
	return modelHistory;
    }

    public double getAvgTimeDelete() {
	return modelHistory.getAvgTimeDelete();
    }

    public double getAvgTimeReferenceSetUnset() {
	return modelHistory.getAvgTimeReferenceSetUnset();
    }

    public double getAvgTimeReferenceAddRemoveMove() {
	return modelHistory.getAvgTimeReferenceAddRemoveMove();
    }

    public double getAvgTimeAttributeSetUnset() {
	return modelHistory.getAvgTimeAttributeSetUnset();
    }

    public double getAvgTimeAttributeAddRemoveMove() {
	return modelHistory.getAvgTimeAttributeAddRemoveMove();
    }

    public CBPResource(URI uri) {
	this();
	this.uri = uri;
    }

    // Adapted from URIHandler.DEFAULT_HANDLERS and ExtensibleURIConverterImpl()
    @Override
    protected URIConverter getURIConverter() {
	return new ExtensibleURIConverterImpl(Arrays.asList(new URIHandler[] { new AppendFileURIHandlerImpl(), new AppendingURIHandler(new PlatformResourceURIHandlerImpl()),
		new AppendingURIHandler(new EFSURIHandlerImpl()), new AppendingURIHandler(new ArchiveURIHandlerImpl()), new AppendingURIHandler(new URIHandlerImpl()) }),
		ContentHandler.Registry.INSTANCE.contentHandlers());
    }

    public List<ChangeEvent<?>> getChangeEvents() {
	return changeEventAdapter.getChangeEvents();
    }

    @Override
    public String getURIFragment(EObject eObject) {
	String uriFragment = null;
	if (eObjectToIdMap == null) {
	    uriFragment = null;
	} else {
	    uriFragment = eObjectToIdMap.get(eObject);
	}
	if (uriFragment == null) {
	    uriFragment = super.getURIFragment(eObject);
	}
	return uriFragment;
    }

    @Override
    public EObject getEObject(String uriFragment) {
	return eObjectToIdMap.inverse().get(uriFragment);
    }

    public String getEObjectId(EObject eObject) {
	if (eObjectToIdMap.containsKey(eObject)) {
	    return eObjectToIdMap.get(eObject);
	}
	return null;
    }

    public void unregister(EObject eObject) {
	eObjectToIdMap.remove(eObject);
    }

    public String register(EObject eObject, String id) {
	adopt(eObject, id);
	return id;
    }

    public String register(EObject eObject) {
	String id = null;
	if (idType == IdType.NUMERIC) {
	    while (eObjectToIdMap.containsValue(String.valueOf(idCounter))) {
		idCounter += 1;
	    }
	    id = idPrefix + String.valueOf(idCounter);
	    idCounter = idCounter + 1;
	} else if (idType == IdType.UUID) {
	    id = EcoreUtil.generateUUID();
	}
	adopt(eObject, id);
	return id;
    }

    public void adopt(EObject eObject, String id) {
	if (!eObjectToIdMap.containsKey(eObject)) {
	    eObjectToIdMap.put(eObject, id);
	}
    }

    public boolean isRegistered(EObject eObject) {
	return eObjectToIdMap.containsKey(eObject);
    }

    @Override
    protected void doUnload() {
	changeEventAdapter.setEnabled(false);
	getChangeEvents().clear();
	getModelHistory().clear();
	clearIgnoreSet();
	eObjectToIdMap.clear();
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

    public Set<Integer> getIgnoreSet() {
	return this.ignoreSet;
    }

    public void setIgnoreSet(Set<Integer> ignoreSet) {
	this.ignoreSet = ignoreSet;
    }

    public void loadIgnoreSet(BufferedInputStream inputStream) throws IOException {
	DataInputStream dis = new DataInputStream(inputStream);
	ignoreSet.clear();
	ignoreList.clear();
	while (dis.available() > 0) {
	    int value = dis.readInt();
	    if (ignoreSet.add(value)) {
		ignoreList.add(value);
	    }
	}
	persistedIgnoredEvents = ignoreList.size();
    }

    public void loadIgnoreSet(ByteArrayInputStream inputStream) throws IOException {
	DataInputStream dis = new DataInputStream(inputStream);
	ignoreSet.clear();
	ignoreList.clear();
	while (dis.available() > 0) {
	    int value = dis.readInt();
	    if (ignoreSet.add(value)) {
		ignoreList.add(value);
	    }
	}
	persistedIgnoredEvents = ignoreList.size();
    }

    public void loadIgnoreSet(FileInputStream inputStream) throws IOException {
	// new way to read ignore list -> much faster
	if (inputStream.getChannel().size() <= Integer.MAX_VALUE) {
	    FileChannel inChannel = inputStream.getChannel();
	    ByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
	    int[] result = new int[(int) (inChannel.size() / 4)];
	    buffer.order(ByteOrder.BIG_ENDIAN);
	    IntBuffer intBuffer = buffer.asIntBuffer();
	    intBuffer.get(result);
	    ignoreSet.clear();
	    ignoreList.clear();
	    for (int i : result)
		ignoreSet.add(i);
	    ignoreList = new ArrayList<>(ignoreSet);
	} else {
	    DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
	    int count = (int) (inputStream.getChannel().size() / 4);
	    Integer[] values = new Integer[count];
	    for (int n = 0; n < count; n++) {
		values[n] = dis.readInt();
	    }
	    ignoreSet.clear();
	    ignoreList.clear();
	    ignoreSet = new HashSet<Integer>(Arrays.asList(values));
	    ignoreList = new ArrayList<>(ignoreSet);
	}

	persistedIgnoredEvents = ignoreList.size();
    }

    public void saveIgnoreSet(ByteArrayOutputStream outputStream) throws IOException {
	DataOutputStream dos = new DataOutputStream(outputStream);
	for (int item : ignoreList.subList(persistedIgnoredEvents, ignoreList.size())) {
	    dos.writeInt(item);
	}
	dos.flush();
	dos.close();
	clearIgnoreSet();
	persistedIgnoredEvents = ignoreList.size();
    }

    public void saveIgnoreSet(FileOutputStream outputStream) throws IOException {
	DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(outputStream));
	for (int item : ignoreList.subList(persistedIgnoredEvents, ignoreList.size())) {
	    dos.writeInt(item);
	}

	dos.flush();
	dos.close();
	clearIgnoreSet();
	persistedIgnoredEvents = ignoreList.size();
    }

    public void clearIgnoreSet() {
	this.ignoreSet.clear();
	this.ignoreList.clear();
	persistedIgnoredEvents = 0;
    }

    public void startNewSession(String id) {
	changeEventAdapter.handleStartNewSession(id);
    }

    public void startNewSession() {
	changeEventAdapter.handleStartNewSession();
    }

    public IdType getIdType() {
	return idType;
    }

    public void setIdType(IdType idType) {
	this.setIdType(idType, "");
    }

    public void setIdType(IdType idType, String idPrefix) {
	this.idType = idType;
	this.idPrefix = idPrefix;
    }

    public void deleteElement(EObject targetEObject) {
	this.startCompositeEvent();

	recursiveDeleteEvent(targetEObject);
	removeFromExternalRefferences(targetEObject);
	unsetAllReferences(targetEObject);
	unsetAllAttributes(targetEObject);
	EcoreUtil.remove(targetEObject);

	this.endCompositeEvent();
    }

    private void recursiveDeleteEvent(EObject targetEObject) {
	for (EReference eRef : targetEObject.eClass().getEAllReferences()) {
	    if (eRef.isChangeable() && targetEObject.eIsSet(eRef) && !eRef.isDerived() && eRef.isContainment()) {
		if (eRef.isMany()) {
		    List<EObject> values = (List<EObject>) targetEObject.eGet(eRef);
		    while (values.size() > 0) {
			EObject value = values.get(values.size() - 1);
			recursiveDeleteEvent(value);
			removeFromExternalRefferences(value);
			unsetAllReferences(value);
			unsetAllAttributes(value);
			values.remove(value);
		    }
		} else {
		    EObject value = (EObject) targetEObject.eGet(eRef);
		    if (value != null) {
			recursiveDeleteEvent(value);
			removeFromExternalRefferences(value);
			unsetAllReferences(value);
			unsetAllAttributes(value);
			targetEObject.eUnset(eRef);
		    }
		}
	    }
	}
    }

    private void unsetAllReferences(EObject targetEObject) {
	for (EReference eRef : targetEObject.eClass().getEAllReferences()) {
	    if (eRef.isChangeable() && targetEObject.eIsSet(eRef) && !eRef.isDerived() && eRef.isContainment() == false) {
		if (eRef.isMany()) {
		    List<EObject> values = (List<EObject>) targetEObject.eGet(eRef);
		    while (values.size() > 0) {
			EObject value = values.get(values.size() - 1);
			values.remove(value);
		    }
		} else {
		    EObject value = (EObject) targetEObject.eGet(eRef);
		    if (value != null) {
			targetEObject.eUnset(eRef);
		    }
		}
	    }
	}
    }

    private void unsetAllAttributes(EObject targetEObject) {
	for (EAttribute eAttr : targetEObject.eClass().getEAllAttributes()) {
	    if (eAttr.isChangeable() && targetEObject.eIsSet(eAttr) && !eAttr.isDerived()) {
		if (eAttr.isMany()) {
		    EList<?> valueList = (EList<?>) targetEObject.eGet(eAttr);
		    while (valueList.size() > 0) {
			valueList.remove(valueList.size() - 1);
		    }
		} else {
		    Object value = targetEObject.eGet(eAttr);
		    targetEObject.eUnset(eAttr);
		}
	    }
	}
    }

    private void removeFromExternalRefferences(EObject refferedEObject) {
	Iterator<EObject> iterator = this.getAllContents();
	while (iterator.hasNext()) {
	    EObject refferingEObject = iterator.next();
	    for (EReference eRef : refferingEObject.eClass().getEAllReferences()) {
		if (eRef.isContainment() == false) {
		    if (eRef.isMany()) {
			List<EObject> valueList = (List<EObject>) refferingEObject.eGet(eRef);
			valueList.remove(refferedEObject);
		    } else {
			EObject value = (EObject) refferingEObject.eGet(eRef);
			if (value != null && value.equals(refferedEObject)) {
			    refferingEObject.eUnset(eRef);
			}
		    }
		}
	    }
	}
    }

    public void startCompositeEvent() {
	getChangeEventAdapter().startCompositeOperation();
    }

    public void endCompositeEvent() {
	getChangeEventAdapter().endCompositeOperation();
    }

}
