
package org.eclipse.epsilon.cbp.io;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.event.AddEObjectsToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EPackageRegistrationEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.util.PrimitiveTypeLength;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.eclipse.epsilon.cbp.util.SimpleType;

public class CBPBinarySerialiser extends AbstractCBPSerialiser {

	public CBPBinarySerialiser(CBPResource resource) {
		this.eventList = resource.getChangelog().getEventsList();

		this.commonsimpleTypeNameMap = persistenceUtil.getCommonSimpleTypesMap();
		this.textSimpleTypeNameMap = persistenceUtil.getTextSimpleTypesMap();

		this.resource = resource;
	}

	public void serialise(Map<?, ?> options) throws IOException {
		if (eventList.isEmpty()) // tbr
		{
			System.out.println("CBPTextSerialiser: no events found, returning!");
			return;
		}

		if (options != null && options.get("ePackage") != null) {
			ePackageElementsNamesMap = persistenceUtil
					.generateEPackageElementNamesMap((EPackage) options.get("ePackage"));
		}
		
		final String filePath = CommonPlugin.resolve(resource.getURI()).toFileString();

		OutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(filePath, resource.isResume()));

		if (!resource.isResume())
			serialiseHeader(outputStream);

		for (Event e : eventList) {
			switch (e.getEventType()) {
			case Event.REGISTER_EPACKAGE:
				handleEPackageRegistrationEvent((EPackageRegistrationEvent) e, outputStream);
				break;
			case Event.ADD_EOBJ_TO_RESOURCE:
				handleAddToResourceEvent((AddEObjectsToResourceEvent) e, outputStream);
				break;
			case Event.SET_EATTRIBUTE:
				handleSetEAttributeEvent((EAttributeEvent) e, outputStream);
				break;
			case Event.ADD_TO_EATTRIBUTE:
				handleAddToEAttributeEvent((EAttributeEvent) e, outputStream);
				break;
			case Event.SET_EREFERENCE:
				handleSetEReferenceEvent((SetEReferenceEvent) e, outputStream);
				break;
			case Event.ADD_TO_EREFERENCE:
				handleAddToEReferenceEvent((AddToEReferenceEvent) e, outputStream);
				break;
			case Event.REMOVE_FROM_EATTRIBUTE:
				handleRemoveFromAttributeEvent((EAttributeEvent) e, outputStream);
				break;
			case Event.REMOVE_FROM_EREFERENCE:
				handleRemoveFromEReferenceEvent((RemoveFromEReferenceEvent) e, outputStream);
				break;
			case Event.REMOVE_EOBJ_FROM_RESOURCE:
				handleRemoveFromResourceEvent((RemoveFromResourceEvent) e, outputStream);
				break;
			}
		}
		outputStream.close();
		resource.setResume(true);
	}

	@Override
	public String getFormatID() {
		return "CBP_BIN";
	}

	@Override
	public double getVersion() {
		return 1.0;
	}

	@Override
	protected void serialiseHeader(Closeable out) {
		OutputStream stream = (OutputStream) out;

		EPackage ePackage = null;

		Event e = eventList.get(0);

		if (e instanceof EPackageRegistrationEvent) {
			ePackage = ((EPackageRegistrationEvent) e).getePackage();
		} else // throw tantrum
		{
			try {
				System.err.println("CBPTextSerialiser: " + e.getEventType());
				throw new Exception("Error! first item in events list is not a EPackageRegistrationEvent.");
			} catch (Exception e1) {
				// FIXME this should be actually thrown!
				e1.printStackTrace();
			}
		}

		if (ePackage == null) {
			System.err.println("CBPTextSerialiser: " + e.getEventType());
		}
		try {
			stream.write(getFormatID().getBytes(persistenceUtil.STRING_ENCODING));
			writePrimitive(stream, getVersion()); // FORMAT VERSION
		} catch (IOException e1) {
			e1.printStackTrace();
			// FIXME this should be rethrown!
		}
	}

	/*
	 * format: 0 size (typeID id)*
	 */
	@Override
	protected void handleAddToResourceEvent(AddEObjectsToResourceEvent e, Closeable out) throws IOException {

		// cast to outputstream
		OutputStream stream = (OutputStream) out;

		// prepare eobject list
		List<EObject> eObjectsList = e.getEObjectList();

		// prepare eobjectToCreateList
		ArrayList<Integer> eObjectsToCreateList = new ArrayList<Integer>();

		// for each object
		for (EObject obj : eObjectsList) {
			// if new
			if (resource.addObjectToMap(obj)) {
				// add add type id
				eObjectsToCreateList.add(getID(obj.eClass()));

				// add object id
				eObjectsToCreateList.add(resource.getObjectId(obj));
			} else {
				// this should not happen
				System.err.println("handleAddToResourceEven: redundant creation");
			}
		}
		if (!eObjectsToCreateList.isEmpty()) // CREATE_AND_ADD_TO_RESOURCE
		{
			writePrimitive(stream, SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE);
			writePrimitive(stream, eObjectsToCreateList.size());

			for (Iterator<Integer> it = eObjectsToCreateList.iterator(); it.hasNext();) {
				writePrimitive(stream, it.next());
			}
		}
	}

	/*
	 * format: 2 size id*
	 */
	@Override
	protected void handleRemoveFromResourceEvent(RemoveFromResourceEvent e, Closeable out) throws IOException {
		// cast to outputstream
		OutputStream stream = (OutputStream) out;

		List<EObject> removedEObjectsList = e.getEObjectList();

		writePrimitive(stream, SerialisationEventType.REMOVE_FROM_RESOURCE);
		writePrimitive(stream, removedEObjectsList.size());

		for (EObject obj : removedEObjectsList) {
			writePrimitive(stream, resource.getObjectId(obj));
		}
	}

	/*
	 * format: 3/4 obj_ID attribute_id size values
	 * 
	 */
	@Override
	protected void handleSetEAttributeEvent(EAttributeEvent e, Closeable out) throws IOException {
		OutputStream stream = (OutputStream) out;

		EObject focusObject = e.getFocusObject();

		// get attr
		EAttribute eAttribute = e.getEAttribute();

		// get data type
		EDataType eDataType = eAttribute.getEAttributeType();
		
		int typeID = getTypeID(eDataType);
		if (typeID != SimpleType.COMPLEX_TYPE) {
			switch (typeID) {
			case SimpleType.SIMPLE_TYPE_INT:
				setPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_INT);
				return;
			case SimpleType.SIMPLE_TYPE_SHORT:
				setPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_SHORT);
				return;
			case SimpleType.SIMPLE_TYPE_LONG:
				setPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_LONG);
				return;
			case SimpleType.SIMPLE_TYPE_FLOAT:
				setPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_FLOAT);
				return;
			case SimpleType.SIMPLE_TYPE_DOUBLE:
				setPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_DOUBLE);
				return;
			case SimpleType.SIMPLE_TYPE_BOOLEAN:
				setPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_BOOLEAN);
				return;
			case SimpleType.SIMPLE_TYPE_CHAR:
				setPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_CHAR);
				return;
			case SimpleType.TEXT_SIMPLE_TYPE_ESTRING:
				setPrimitiveEAttributes(e, stream, SimpleType.TEXT_SIMPLE_TYPE_ESTRING);
				return;
			}
		} else {
			int serializationType = SerialisationEventType.SET_EATTRIBUTE_COMPLEX;
			writeComplexEAttributes(focusObject, eAttribute, e.getEAttributeValuesList(), serializationType, stream);
		}
	}

	@Override
	protected void handleAddToEAttributeEvent(EAttributeEvent e, Closeable out) throws IOException {
		OutputStream stream = (OutputStream) out;

		EObject focusObject = e.getFocusObject();

		// get attr
		EAttribute eAttribute = e.getEAttribute();

		// get data type
		EDataType eDataType = eAttribute.getEAttributeType();

		if (getTypeID(eDataType) != SimpleType.COMPLEX_TYPE) {
			switch (getTypeID(eDataType)) {
			case SimpleType.SIMPLE_TYPE_INT:
				addPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_INT);
				return;
			case SimpleType.SIMPLE_TYPE_SHORT:
				addPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_SHORT);
				return;
			case SimpleType.SIMPLE_TYPE_LONG:
				addPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_LONG);
				return;
			case SimpleType.SIMPLE_TYPE_FLOAT:
				addPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_FLOAT);
				return;
			case SimpleType.SIMPLE_TYPE_DOUBLE:
				addPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_DOUBLE);
				return;
			case SimpleType.SIMPLE_TYPE_BOOLEAN:
				addPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_BOOLEAN);
				return;
			case SimpleType.SIMPLE_TYPE_CHAR:
				addPrimitiveEAttributes(e, stream, SimpleType.SIMPLE_TYPE_CHAR);
				return;
			case SimpleType.TEXT_SIMPLE_TYPE_ESTRING:
				setPrimitiveEAttributes(e, stream, SimpleType.TEXT_SIMPLE_TYPE_ESTRING);
				return;
			}
		} else {
			int serializationType = SerialisationEventType.ADD_TO_EATTRIBUTE_COMPLEX;
			writeComplexEAttributes(focusObject, eAttribute, e.getEAttributeValuesList(), serializationType, stream);
		}
	}

	/*
	 * format:
	 * 
	 */
	@Override
	protected void handleSetEReferenceEvent(SetEReferenceEvent e, Closeable out) throws IOException {

		OutputStream stream = (OutputStream) out;

		boolean created = false;
		EObject focusObject = e.getFocusObject();
		EReference eReference = e.getEReference();

		ArrayList<Integer> eObjectsToAddList = new ArrayList<Integer>();
		ArrayList<Integer> eObjectsToCreateList = new ArrayList<Integer>();

		for (EObject obj : e.getEObjectList()) {
			// if obj is not added already
			if (resource.addObjectToMap(obj)) {
				// add type to object-to-create-list
				eObjectsToCreateList.add(getID(obj.eClass()));

				// add id to object-to-create-list
				eObjectsToCreateList.add(resource.getObjectId(obj));
			} else {
				// add id to object-to-add list
				eObjectsToAddList.add(resource.getObjectId(obj));
			}
		}

		// if create list is not empty
		if (!eObjectsToCreateList.isEmpty()) {
			created = true;
			writePrimitive(stream, SerialisationEventType.CREATE_AND_SET_EREFERENCE);
			writePrimitive(stream, resource.getObjectId(focusObject));
			writePrimitive(stream, getID(focusObject.eClass(), eReference));
			writePrimitive(stream, eObjectsToCreateList.size());
			for (Iterator<Integer> it = eObjectsToCreateList.iterator(); it.hasNext();) {
				writePrimitive(stream, it.next());
			}
		}

		// if add list is not empty
		if (!eObjectsToAddList.isEmpty()) {
			if (created) {
				writePrimitive(stream, SerialisationEventType.ADD_TO_EREFERENCE);
				writePrimitive(stream, resource.getObjectId(focusObject));
				writePrimitive(stream, getID(focusObject.eClass(), eReference));
				writePrimitive(stream, eObjectsToAddList.size());
			} else {
				writePrimitive(stream, SerialisationEventType.SET_EREFERENCE);
				writePrimitive(stream, resource.getObjectId(focusObject));
				writePrimitive(stream, getID(focusObject.eClass(), eReference));
				writePrimitive(stream, eObjectsToAddList.size());
			}
			for (Iterator<Integer> it = eObjectsToAddList.iterator(); it.hasNext();) {
				writePrimitive(stream, it.next());
			}
		}
	}

	@Override
	protected void handleAddToEReferenceEvent(AddToEReferenceEvent e, Closeable out) throws IOException {

		OutputStream stream = (OutputStream) out;

		EObject focusObject = e.getFocusObject();
		EReference eReference = e.getEReference();

		ArrayList<Integer> eObjectsToAddList = new ArrayList<Integer>();
		ArrayList<Integer> eObjectsToCreateList = new ArrayList<Integer>();

		for (EObject obj : e.getEObjectList()) {
			// if obj is not added already
			if (resource.addObjectToMap(obj)) {
				// add type to object-to-create-list
				eObjectsToCreateList.add(getID(obj.eClass()));

				// add id to object-to-create-list
				eObjectsToCreateList.add(resource.getObjectId(obj));
			} else {
				// add id to object-to-add list
				eObjectsToAddList.add(resource.getObjectId(obj));
			}
		}

		// if create list is not empty
		if (!eObjectsToCreateList.isEmpty()) {
			writePrimitive(stream, SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE);
			writePrimitive(stream, resource.getObjectId(focusObject));
			writePrimitive(stream, getID(focusObject.eClass(), eReference));
			writePrimitive(stream, eObjectsToCreateList.size());
			for (Iterator<Integer> it = eObjectsToCreateList.iterator(); it.hasNext();) {
				writePrimitive(stream, it.next());
			}
		}

		// if add list is not empty
		if (!eObjectsToAddList.isEmpty()) {
			writePrimitive(stream, SerialisationEventType.ADD_TO_EREFERENCE);
			writePrimitive(stream, resource.getObjectId(focusObject));
			writePrimitive(stream, getID(focusObject.eClass(), eReference));
			writePrimitive(stream, eObjectsToAddList.size());
			for (Iterator<Integer> it = eObjectsToAddList.iterator(); it.hasNext();) {
				writePrimitive(stream, it.next());
			}
		}
	}

	/*
	 * format: 7/8 objID featureID size value*
	 */
	@Override
	protected void handleRemoveFromAttributeEvent(EAttributeEvent e, Closeable out) throws IOException {
		OutputStream stream = (OutputStream) out;
		// get forcus object
		EObject focusObject = e.getFocusObject();

		// get attr
		EAttribute eAttribute = e.getEAttribute();

		// get data type
		EDataType eDataType = eAttribute.getEAttributeType();

		if (getTypeID(eDataType) != SimpleType.COMPLEX_TYPE) {
			writePrimitive(stream, SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE);
			writePrimitive(stream, resource.getObjectId(focusObject));
			writePrimitive(stream, getID(focusObject.eClass(), eAttribute));
			int size = 0;
			for (Object obj : e.getEAttributeValuesList()) {
				if (obj != null) {
					size++;
				}
			}
			writePrimitive(stream, size);

			for (Object obj : e.getEAttributeValuesList()) {
				if (obj != null) {
					writeString(stream, String.valueOf(obj));
				}
			}
		} else {
			writePrimitive(stream, SerialisationEventType.REMOVE_FROM_EATTRIBUTE_COMPLEX);
			writePrimitive(stream, resource.getObjectId(focusObject));
			writePrimitive(stream, getID(focusObject.eClass(), eAttribute));
			int size = 0;
			for (Object obj : e.getEAttributeValuesList()) {
				if (obj != null) {
					size++;
				}
			}
			writePrimitive(stream, size);

			for (Object obj : e.getEAttributeValuesList()) {
				if (obj != null) {
					String newValue = (EcoreUtil.convertToString(eDataType, obj));
					if (newValue != null) {
						writeString(stream, newValue);
					}
				}
			}
		}
	}

	@Override
	protected void handleRemoveFromEReferenceEvent(RemoveFromEReferenceEvent e, Closeable out) throws IOException {
		OutputStream stream = (OutputStream) out;
		EObject focusObject = e.getFocusObject();
		EReference eReference = e.getEReference();
		List<EObject> removedEObjectsList = e.getEObjectList();

		writePrimitive(stream, SerialisationEventType.REMOVE_FROM_EREFERENCE);
		writePrimitive(stream, resource.getObjectId(focusObject));
		writePrimitive(stream, getID(focusObject.eClass(), eReference));
		writePrimitive(stream, removedEObjectsList.size());

		for (EObject eObject : removedEObjectsList) {
			writePrimitive(stream, resource.getObjectId(eObject));
		}
	}

	/*
	 * format: 14 str
	 */
	@Override
	protected void handleEPackageRegistrationEvent(EPackageRegistrationEvent e, Closeable out) {
		ePackageElementsNamesMap = persistenceUtil.generateEPackageElementNamesMap(e.getePackage());
		OutputStream stream = (OutputStream) out;
		int serialisationType = SerialisationEventType.REGISTER_EPACKAGE;
		try {
			writePrimitive(stream, serialisationType);
			String nsuri = e.getePackage().getNsURI();
			writeString(stream, nsuri);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * format: 3 object_ID EAttribute_ID size
	 */
	private void setPrimitiveEAttributes(EAttributeEvent e, OutputStream out, int primitiveType) throws IOException {
		// get forcus object
		EObject focusObject = e.getFocusObject();

		// get eattribute
		EAttribute eAttribute = e.getEAttribute();

		// get lists
		List<Object> eAttributeValuesList = e.getEAttributeValuesList();

		// serialisation type
		int serializationType = SerialisationEventType.SET_EATTRIBUTE_PRIMITIVE;

		writePrimitive(out, serializationType);
		writePrimitive(out, resource.getObjectId(focusObject));
		writePrimitive(out, getID(focusObject.eClass(), eAttribute));
		int size = 0;
		for (Object obj : eAttributeValuesList) {
			if (obj != null) {
				size++;
			}
		}
		writePrimitive(out, size);

		for (Object obj : eAttributeValuesList) {
			if (obj != null) {
				if (primitiveType == SimpleType.TEXT_SIMPLE_TYPE_ESTRING ) {
					writeString(out, (String) obj);
				}
				else {
					writePrimitive(out, primitiveType, obj);	
				}
			}
		}
	}

	/*
	 * format: 3 object_ID EAttribute_ID size
	 */
	private void addPrimitiveEAttributes(EAttributeEvent e, OutputStream out, int primitiveType) throws IOException {
		// get forcus object
		EObject focusObject = e.getFocusObject();

		// get eattribute
		EAttribute eAttribute = e.getEAttribute();

		// get lists
		List<Object> eAttributeValuesList = e.getEAttributeValuesList();

		// serialisation type
		int serializationType = SerialisationEventType.ADD_TO_EATTRIBUTE_PRIMITIVE;

		writePrimitive(out, serializationType);
		writePrimitive(out, resource.getObjectId(focusObject));
		writePrimitive(out, getID(focusObject.eClass(), eAttribute));
		int size = 0;
		for (Object obj : eAttributeValuesList) {
			if (obj != null) {
				size++;
			}
		}
		writePrimitive(out, size);

		for (Object obj : eAttributeValuesList) {
			if (obj != null) {
				writePrimitive(out, primitiveType, obj);
			}
		}
	}

	/*
	 * format serialisationType Obj_id EAtt_id attr_size
	 */
	private void writeComplexEAttributes(EObject focusObject, EAttribute eAttribute, List<Object> eAttributeValuesList,
			int serializationType, OutputStream out) throws IOException {
		// get EDatatype
		EDataType eDataType = eAttribute.getEAttributeType();

		writePrimitive(out, serializationType);
		writePrimitive(out, resource.getObjectId(focusObject));
		writePrimitive(out, getID(focusObject.eClass(), eAttribute));
		int size = 0;
		for (Object obj : eAttributeValuesList) {
			if (obj != null) {
				size++;
			}
		}
		writePrimitive(out, size);

		for (Object obj : eAttributeValuesList) {
			String valueString = EcoreUtil.convertToString(eDataType, obj);
			if (valueString != null) {
				writeString(out, valueString);
			}
		}
	}

	private void writePrimitive(OutputStream out, int primitiveType, Object obj) throws IOException {
		switch (primitiveType) {
		case SimpleType.SIMPLE_TYPE_INT:
			writePrimitive(out, (int) obj);
			return;
		case SimpleType.SIMPLE_TYPE_BOOLEAN:
			writePrimitive(out, (boolean) obj);
			return;
		case SimpleType.SIMPLE_TYPE_BYTE:
			writePrimitive(out, (byte) obj);
			return;
		case SimpleType.SIMPLE_TYPE_CHAR:
			writePrimitive(out, (char) obj);
			return;
		case SimpleType.SIMPLE_TYPE_DOUBLE:
			writePrimitive(out, (double) obj);
			return;
		case SimpleType.SIMPLE_TYPE_FLOAT:
			writePrimitive(out, (short) obj);
			return;
		case SimpleType.SIMPLE_TYPE_LONG:
			writePrimitive(out, (long) obj);
			return;
		case SimpleType.SIMPLE_TYPE_SHORT:
			writePrimitive(out, (short) obj);
			return;
		}
	}

	private void writeString(OutputStream out, String str) throws IOException {
		// get bytes
		byte[] bytes = str.getBytes(persistenceUtil.STRING_ENCODING);

		// write the length of str
		writePrimitive(out, bytes.length);

		// write the bytes
		out.write(bytes);
	}

	// write int
	private void writePrimitive(OutputStream out, int i) throws IOException {
		byte[] bytes = ByteBuffer.allocate(PrimitiveTypeLength.INTEGER_SIZE).putInt(i).array();
		out.write(bytes);
	}

	// write short
	private void writePrimitive(OutputStream out, short s) throws IOException {
		byte[] bytes = ByteBuffer.allocate(PrimitiveTypeLength.SHORT_SIZE).putShort(s).array();
		out.write(bytes);
	}

	// write byte
	private void writePrimitive(OutputStream out, byte b) throws IOException {
		byte[] bytes = ByteBuffer.allocate(PrimitiveTypeLength.BYTE_SIZE).put(b).array();
		out.write(bytes);
	}

	// write char
	private void writePrimitive(OutputStream out, char c) throws IOException {
		byte[] bytes = ByteBuffer.allocate(PrimitiveTypeLength.CHAR_SIZE).putChar(c).array();
		out.write(bytes);
	}

	// write double
	private void writePrimitive(OutputStream out, double d) throws IOException {
		byte[] bytes = ByteBuffer.allocate(PrimitiveTypeLength.DOUBLE_SIZE).putDouble(d).array();
		out.write(bytes);
	}

	// write long
	private void writePrimitive(OutputStream out, long l) throws IOException {
		byte[] bytes = ByteBuffer.allocate(PrimitiveTypeLength.LONG_SIZE).putLong(l).array();
		out.write(bytes);
	}

	// write boolean
	private void writePrimitive(OutputStream out, boolean b) throws IOException {
		if (b)
			writePrimitive(out, 1);
		else
			writePrimitive(out, 0);
	}

	public int getID(EClass eClass, EStructuralFeature feature) {
		return ePackageElementsNamesMap
				.getID(eClass.getEPackage().getName() + "-" + eClass.getName() + "-" + feature.getName());
	}

	public int getID(EClass eClass) {
		return ePackageElementsNamesMap.getID(eClass.getEPackage().getName() + "-" + eClass.getName());
	}
}
