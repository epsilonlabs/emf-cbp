
package org.eclipse.epsilon.cbp.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.eclipse.epsilon.cbp.util.SimpleType;

public class CBPBinarySerialiser extends AbstractCBPSerialiser {

	protected DataOutputStream out;
	
	public CBPBinarySerialiser(CBPResource resource) {
		super(resource);
	}

	public void serialise(OutputStream outputStream, Map<?, ?> options) throws IOException {
		out = new DataOutputStream(outputStream);
		super.serialise(outputStream, options);
		out.close();
	}

	/*
	 * format: 0 size (typeID id)*
	 */
	protected void handleAddToResourceEvent(AddToResourceEvent e) throws IOException {

		// prepare eobject list
		List<EObject> eObjectsList = e.getEObjects();

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
			out.writeInt(SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE);
			out.writeInt(eObjectsToCreateList.size());

			for (Iterator<Integer> it = eObjectsToCreateList.iterator(); it.hasNext();) {
				out.writeInt(it.next());
			}
		}
	}

	/*
	 * format: 2 size id*
	 */
	protected void handleRemoveFromResourceEvent(RemoveFromResourceEvent e) throws IOException {

		List<EObject> removedEObjectsList = e.getEObjects();

		out.writeInt(SerialisationEventType.REMOVE_FROM_RESOURCE);
		out.writeInt(removedEObjectsList.size());

		for (EObject obj : removedEObjectsList) {
			out.writeInt(resource.getObjectId(obj));
		}
	}

	/*
	 * format: 3/4 obj_ID attribute_id size values
	 * 
	 */
	protected void handleSetEAttributeEvent(EAttributeEvent e) throws IOException {

		EObject focusObject = e.getEObject();

		// get attr
		EAttribute eAttribute = e.getEAttribute();

		// get data type
		EDataType eDataType = eAttribute.getEAttributeType();
		
		int typeID = getTypeID(eDataType);
		if (typeID != SimpleType.COMPLEX_TYPE) {
			switch (typeID) {
			case SimpleType.SIMPLE_TYPE_INT:
				setPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_INT);
				return;
			case SimpleType.SIMPLE_TYPE_SHORT:
				setPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_SHORT);
				return;
			case SimpleType.SIMPLE_TYPE_LONG:
				setPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_LONG);
				return;
			case SimpleType.SIMPLE_TYPE_FLOAT:
				setPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_FLOAT);
				return;
			case SimpleType.SIMPLE_TYPE_DOUBLE:
				setPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_DOUBLE);
				return;
			case SimpleType.SIMPLE_TYPE_BOOLEAN:
				setPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_BOOLEAN);
				return;
			case SimpleType.SIMPLE_TYPE_CHAR:
				setPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_CHAR);
				return;
			case SimpleType.TEXT_SIMPLE_TYPE_ESTRING:
				setPrimitiveEAttributes(e, SimpleType.TEXT_SIMPLE_TYPE_ESTRING);
				return;
			}
		} else {
			writeComplexEAttributes(focusObject, eAttribute, e.getValues());
		}
	}

	protected void handleAddToEAttributeEvent(EAttributeEvent e) throws IOException {

		EObject focusObject = e.getEObject();

		// get attr
		EAttribute eAttribute = e.getEAttribute();

		// get data type
		EDataType eDataType = eAttribute.getEAttributeType();

		if (getTypeID(eDataType) != SimpleType.COMPLEX_TYPE) {
			switch (getTypeID(eDataType)) {
			case SimpleType.SIMPLE_TYPE_INT:
				addPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_INT);
				return;
			case SimpleType.SIMPLE_TYPE_SHORT:
				addPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_SHORT);
				return;
			case SimpleType.SIMPLE_TYPE_LONG:
				addPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_LONG);
				return;
			case SimpleType.SIMPLE_TYPE_FLOAT:
				addPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_FLOAT);
				return;
			case SimpleType.SIMPLE_TYPE_DOUBLE:
				addPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_DOUBLE);
				return;
			case SimpleType.SIMPLE_TYPE_BOOLEAN:
				addPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_BOOLEAN);
				return;
			case SimpleType.SIMPLE_TYPE_CHAR:
				addPrimitiveEAttributes(e, SimpleType.SIMPLE_TYPE_CHAR);
				return;
			case SimpleType.TEXT_SIMPLE_TYPE_ESTRING:
				setPrimitiveEAttributes(e, SimpleType.TEXT_SIMPLE_TYPE_ESTRING);
				return;
			}
		} else {
			writeComplexEAttributes(focusObject, eAttribute, e.getValues());
		}
	}

	/*
	 * format:
	 * 
	 */
	protected void handleSetEReferenceEvent(SetEReferenceEvent e) throws IOException {

		boolean created = false;
		EObject focusObject = e.getEObject();
		EReference eReference = e.getEReference();

		ArrayList<Integer> eObjectsToAddList = new ArrayList<Integer>();
		ArrayList<Integer> eObjectsToCreateList = new ArrayList<Integer>();

		for (EObject obj : e.getEObjects()) {
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
			out.writeInt(SerialisationEventType.CREATE_AND_SET_EREFERENCE);
			out.writeInt(resource.getObjectId(focusObject));
			out.writeInt(getID(focusObject.eClass(), eReference));
			out.writeInt(eObjectsToCreateList.size());
			for (Iterator<Integer> it = eObjectsToCreateList.iterator(); it.hasNext();) {
				out.writeInt(it.next());
			}
		}

		// if add list is not empty
		if (!eObjectsToAddList.isEmpty()) {
			if (created) {
				out.writeInt(SerialisationEventType.ADD_TO_EREFERENCE);
				out.writeInt(resource.getObjectId(focusObject));
				out.writeInt(getID(focusObject.eClass(), eReference));
				out.writeInt(eObjectsToAddList.size());
			} else {
				out.writeInt(SerialisationEventType.SET_EREFERENCE);
				out.writeInt(resource.getObjectId(focusObject));
				out.writeInt(getID(focusObject.eClass(), eReference));
				out.writeInt(eObjectsToAddList.size());
			}
			for (Iterator<Integer> it = eObjectsToAddList.iterator(); it.hasNext();) {
				out.writeInt(it.next());
			}
		}
	}

	protected void handleAddToEReferenceEvent(AddToEReferenceEvent e) throws IOException {

		EObject focusObject = e.getEObject();
		EReference eReference = e.getEReference();

		ArrayList<Integer> eObjectsToAddList = new ArrayList<Integer>();
		ArrayList<Integer> eObjectsToCreateList = new ArrayList<Integer>();

		for (EObject obj : e.getEObjects()) {
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
			
			out.writeInt(SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE);
			out.writeInt(resource.getObjectId(focusObject));
			out.writeInt(getID(focusObject.eClass(), eReference));
			out.writeInt(eObjectsToCreateList.size());
			for (Iterator<Integer> it = eObjectsToCreateList.iterator(); it.hasNext();) {
				out.writeInt(it.next());
			}
		}

		// if add list is not empty
		if (!eObjectsToAddList.isEmpty()) {
			out.writeInt(SerialisationEventType.ADD_TO_EREFERENCE);
			out.writeInt(resource.getObjectId(focusObject));
			out.writeInt(getID(focusObject.eClass(), eReference));
			out.writeInt(eObjectsToAddList.size());
			for (Iterator<Integer> it = eObjectsToAddList.iterator(); it.hasNext();) {
				out.writeInt(it.next());
			}
		}
	}

	/*
	 * format: 7/8 objID featureID size value*
	 */
	protected void handleRemoveFromAttributeEvent(EAttributeEvent e) throws IOException {

		// get forcus object
		EObject focusObject = e.getEObject();

		// get attr
		EAttribute eAttribute = e.getEAttribute();

		// get data type
		EDataType eDataType = eAttribute.getEAttributeType();

		if (getTypeID(eDataType) != SimpleType.COMPLEX_TYPE) {
			out.writeInt(SerialisationEventType.REMOVE_FROM_EATTRIBUTE);
			out.writeInt(resource.getObjectId(focusObject));
			out.writeInt(getID(focusObject.eClass(), eAttribute));
			int size = 0;
			for (Object obj : e.getValues()) {
				if (obj != null) {
					size++;
				}
			}
			out.writeInt(size);

			for (Object obj : e.getValues()) {
				if (obj != null) {
					writeString(String.valueOf(obj));
				}
			}
		} else {
			out.writeInt(SerialisationEventType.REMOVE_FROM_EATTRIBUTE);
			out.writeInt(resource.getObjectId(focusObject));
			out.writeInt(getID(focusObject.eClass(), eAttribute));
			int size = 0;
			for (Object obj : e.getValues()) {
				if (obj != null) {
					size++;
				}
			}
			out.writeInt(size);

			for (Object obj : e.getValues()) {
				if (obj != null) {
					String newValue = (EcoreUtil.convertToString(eDataType, obj));
					if (newValue != null) {
						writeString(newValue);
					}
				}
			}
		}
	}

	protected void handleRemoveFromEReferenceEvent(RemoveFromEReferenceEvent e) throws IOException {

		EObject focusObject = e.getEObject();
		EReference eReference = e.getEReference();
		List<EObject> removedEObjectsList = e.getEObjects();

		out.writeInt(SerialisationEventType.REMOVE_FROM_EREFERENCE);
		out.writeInt(resource.getObjectId(focusObject));
		out.writeInt(getID(focusObject.eClass(), eReference));
		out.writeInt(removedEObjectsList.size());

		for (EObject eObject : removedEObjectsList) {
			out.writeInt(resource.getObjectId(eObject));
		}
	}

	/*
	 * format: 14 str
	 */
	protected void handleEPackageRegistrationEvent(RegisterEPackageEvent e) {
		ePackageElementsNamesMap = persistenceUtil.generateEPackageElementNamesMap(e.getePackage());

		int serialisationType = SerialisationEventType.REGISTER_EPACKAGE;
		try {
			out.writeInt(serialisationType);
			String nsuri = e.getePackage().getNsURI();
			writeString(nsuri);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * format: 3 object_ID EAttribute_ID size
	 */
	private void setPrimitiveEAttributes(EAttributeEvent e, int primitiveType) throws IOException {
		// get forcus object
		EObject focusObject = e.getEObject();

		// get eattribute
		EAttribute eAttribute = e.getEAttribute();

		// get lists
		List<Object> eAttributeValuesList = e.getValues();

		// serialisation type
		int serializationType = SerialisationEventType.SET_EATTRIBUTE;

		out.writeInt(serializationType);
		out.writeInt(resource.getObjectId(focusObject));
		out.writeInt(getID(focusObject.eClass(), eAttribute));
		int size = 0;
		for (Object obj : eAttributeValuesList) {
			if (obj != null) {
				size++;
			}
		}
		out.writeInt(size);

		for (Object obj : eAttributeValuesList) {
			if (obj != null) {
				if (primitiveType == SimpleType.TEXT_SIMPLE_TYPE_ESTRING ) {
					writeString((String) obj);
				}
				else {
					writePrimitive(primitiveType, obj);	
				}
			}
		}
	}

	/*
	 * format: 3 object_ID EAttribute_ID size
	 */
	private void addPrimitiveEAttributes(EAttributeEvent e, int primitiveType) throws IOException {
		// get forcus object
		EObject focusObject = e.getEObject();

		// get eattribute
		EAttribute eAttribute = e.getEAttribute();

		// get lists
		List<Object> eAttributeValuesList = e.getValues();

		// serialisation type
		int serializationType = SerialisationEventType.ADD_TO_EATTRIBUTE;
		
		out.writeInt(serializationType);
		out.writeInt(resource.getObjectId(focusObject));
		out.writeInt(getID(focusObject.eClass(), eAttribute));
		int size = 0;
		for (Object obj : eAttributeValuesList) {
			if (obj != null) {
				size++;
			}
		}
		out.writeInt(size);

		for (Object obj : eAttributeValuesList) {
			if (obj != null) {
				writePrimitive(primitiveType, obj);
			}
		}
	}

	/*
	 * format serialisationType Obj_id EAtt_id attr_size
	 */
	private void writeComplexEAttributes(EObject focusObject, EAttribute eAttribute, List<Object> eAttributeValuesList) throws IOException {
		// get EDatatype
		EDataType eDataType = eAttribute.getEAttributeType();

		out.writeInt(SerialisationEventType.ADD_TO_EATTRIBUTE);
		out.writeInt(resource.getObjectId(focusObject));
		out.writeInt(getID(focusObject.eClass(), eAttribute));
		int size = 0;
		for (Object obj : eAttributeValuesList) {
			if (obj != null) {
				size++;
			}
		}
		out.writeInt(size);

		for (Object obj : eAttributeValuesList) {
			String valueString = EcoreUtil.convertToString(eDataType, obj);
			if (valueString != null) {
				writeString(valueString);
			}
		}
	}

	private void writePrimitive(int primitiveType, Object obj) throws IOException {
		switch (primitiveType) {
			case SimpleType.SIMPLE_TYPE_INT:
				out.writeInt((int) obj);
				return;
			case SimpleType.SIMPLE_TYPE_BOOLEAN:
				out.writeBoolean((boolean) obj);
				return;
			case SimpleType.SIMPLE_TYPE_BYTE:
				out.writeByte((byte) obj);
				return;
			case SimpleType.SIMPLE_TYPE_CHAR:
				out.writeChar((char) obj);
				return;
			case SimpleType.SIMPLE_TYPE_DOUBLE:
				out.writeDouble((double) obj);
				return;
			case SimpleType.SIMPLE_TYPE_FLOAT:
				out.writeFloat((float) obj);
				return;
			case SimpleType.SIMPLE_TYPE_LONG:
				out.writeLong((long) obj);
				return;
			case SimpleType.SIMPLE_TYPE_SHORT:
				out.writeFloat((float) obj);
				return;
			}
	}

	private void writeString(String str) throws IOException {
		// get bytes
		byte[] bytes = str.getBytes(persistenceUtil.STRING_ENCODING);

		// write the length of str
		out.writeInt(bytes.length);

		// write the bytes
		out.write(bytes);
	}

	public int getID(EClass eClass, EStructuralFeature feature) {
		return ePackageElementsNamesMap
				.getID(eClass.getEPackage().getName() + "-" + eClass.getName() + "-" + feature.getName());
	}

	public int getID(EClass eClass) {
		return ePackageElementsNamesMap.getID(eClass.getEPackage().getName() + "-" + eClass.getName());
	}
}
