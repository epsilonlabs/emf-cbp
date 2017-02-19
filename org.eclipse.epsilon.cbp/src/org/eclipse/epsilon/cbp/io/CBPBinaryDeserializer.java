package org.eclipse.epsilon.cbp.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.util.PrimitiveTypeLength;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.eclipse.epsilon.cbp.util.SimpleType;

public class CBPBinaryDeserializer extends AbstractCBPDeserialiser {
	
	protected DataInputStream in;

	public CBPBinaryDeserializer(CBPResource resource) {
		super(resource);
	}

	@Override
	public void deserialise(InputStream inputStream, Map<?, ?> options) throws IOException {
		
		in = new DataInputStream(inputStream);
		
		/* Read binary records */
		readingLoop:
		while (in.available() > 0) {
			switch (in.readInt()) {
				case SerialisationEventType.REGISTER_EPACKAGE:
					handleRegisterEPackage();
					break;
				case SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE:
					handleCreateAndAddToResource();
					break;
				case SerialisationEventType.REMOVE_FROM_RESOURCE:
					handleRemoveFromResource();
					break;
				case SerialisationEventType.SET_EATTRIBUTE:
					handleSetEAttribute();
					break;
				case SerialisationEventType.ADD_TO_EATTRIBUTE:
					handleAddToEAttribute();
					break;
				case SerialisationEventType.REMOVE_FROM_EATTRIBUTE:
					handleRemoveFromEAttribute();
					break;
				case SerialisationEventType.SET_EREFERENCE:
					handleSetEReference();
					break;
				case SerialisationEventType.CREATE_AND_SET_EREFERENCE:
					handleCreateAndSetEReference();
					break;
				case SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE:
					handleCreateAndAddToEReference();
					break;
				case SerialisationEventType.ADD_TO_EREFERENCE:
					handleAddToEReference();
					break;
				case SerialisationEventType.REMOVE_FROM_EREFERENCE:
					handleRemoveFromEReference();
					break;
				case SerialisationEventType.STOP_READING:
					break readingLoop;
				default:
					break;
				}
		}

		inputStream.close();
	}
	
	protected void handleRegisterEPackage() throws IOException {
		// get nsuri
		String nsuri = readString();

		EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsuri);
		ePackages.add(ePackage);
		ePackageElementsNamesMap = persistenceUtil.generateEPackageElementNamesMap(ePackage);
	}

	protected void handleCreateAndAddToResource() throws IOException {

		// read number of ints
		int numInts = in.readInt();
		
		for (int i=0;i<numInts;i++) {
			// create object
			EObject obj = createEObject(ePackageElementsNamesMap.getName(in.readInt()));

			// get id
			int id = in.readInt();
			
			// add object to map
			resource.addObjectToMap(obj, id);

			// put obj to map
			idToEObjectMap.put(id, obj);

			// add to content
			resource.getContents().add(obj);
		}
	}

	/*
	 * format: 2 size id*
	 */
	protected void handleRemoveFromResource() throws IOException {

		int numInts = in.readInt();
		
		for (int i = 0; i < numInts; i++) {
			int id = in.readInt();
			resource.getContents().remove(idToEObjectMap.get(id));
		}
	}

	/*
	 * format: 3/4 obj_ID attribute_id size values
	 */
	protected void handleSetEAttribute() throws IOException {

		// get Object
		EObject focusObject = idToEObjectMap.get(in.readInt());

		// get eattribute
		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(in.readInt())));

		// get eDataType
		EDataType eDataType = eAttribute.getEAttributeType();

		int primitiveSize = -1;
		int primitiveType = -1;

		int dataType = getTypeID(eDataType);

		if (dataType == SimpleType.COMPLEX_TYPE || dataType == SimpleType.TEXT_SIMPLE_TYPE_ESTRING) {

			int numStrings = in.readInt();

			String[] featureValuesArray = new String[numStrings];

			for (int i = 0; i < numStrings; i++) {
				featureValuesArray[i] = readString();
			}
			handleComplexEAttributeValues(focusObject, eAttribute, featureValuesArray, true);
		} else {

			switch (dataType) {
			case SimpleType.SIMPLE_TYPE_INT:
				primitiveType = SimpleType.SIMPLE_TYPE_INT;
				primitiveSize = PrimitiveTypeLength.INTEGER_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_SHORT:
				primitiveType = SimpleType.SIMPLE_TYPE_SHORT;
				primitiveSize = PrimitiveTypeLength.SHORT_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_LONG:
				primitiveType = SimpleType.SIMPLE_TYPE_LONG;
				primitiveSize = PrimitiveTypeLength.LONG_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_FLOAT:
				primitiveType = SimpleType.SIMPLE_TYPE_FLOAT;
				primitiveSize = PrimitiveTypeLength.FLOAT_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_DOUBLE:
				primitiveType = SimpleType.SIMPLE_TYPE_DOUBLE;
				primitiveSize = PrimitiveTypeLength.DOUBLE_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_BOOLEAN:
				primitiveType = SimpleType.SIMPLE_TYPE_BOOLEAN;
				primitiveSize = PrimitiveTypeLength.INTEGER_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_CHAR:
				primitiveType = SimpleType.SIMPLE_TYPE_CHAR;
				primitiveSize = PrimitiveTypeLength.CHAR_SIZE;
				return;
			}

			// read number of primitives
			int numPrimitives = in.readInt();

			Object[] featureValuesArray = new Object[numPrimitives];

			byte[] buffer = new byte[numPrimitives * primitiveSize];

			in.read(buffer);

			int index = 0;

			for (int i = 0; i < numPrimitives; i++) {
				featureValuesArray[i] = byteArrayToPrimitive(Arrays.copyOfRange(buffer, index, index + primitiveSize),
						primitiveType);

				index = index + primitiveSize;
			}

			if (primitiveType == SimpleType.SIMPLE_TYPE_BOOLEAN)
				handlePrimitiveBooleanEAttributeValues(focusObject, eAttribute, featureValuesArray, true);

			else
				handlePrimitiveEAttributeValues(focusObject, eAttribute, featureValuesArray, true);
		}
	}

	/*
	 * format: 3/4 obj_ID attribute_id size values
	 */
	protected void handleAddToEAttribute() throws IOException {

		// get Object
		EObject focusObject = idToEObjectMap.get(in.readInt());

		// get eattribute
		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(in.readInt())));

		// get eDataType
		EDataType eDataType = eAttribute.getEAttributeType();

		int primitiveSize = -1;
		int primitiveType = -1;

		int dataType = getTypeID(eDataType);

		if (dataType == SimpleType.COMPLEX_TYPE || dataType == SimpleType.TEXT_SIMPLE_TYPE_ESTRING) {

			int numStrings = in.readInt();

			String[] featureValuesArray = new String[numStrings];

			for (int i = 0; i < numStrings; i++) {
				featureValuesArray[i] = readString();
			}
			handleComplexEAttributeValues(focusObject, eAttribute, featureValuesArray, false);
		} else {

			switch (dataType) {
			case SimpleType.SIMPLE_TYPE_INT:
				primitiveType = SimpleType.SIMPLE_TYPE_INT;
				primitiveSize = PrimitiveTypeLength.INTEGER_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_SHORT:
				primitiveType = SimpleType.SIMPLE_TYPE_SHORT;
				primitiveSize = PrimitiveTypeLength.SHORT_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_LONG:
				primitiveType = SimpleType.SIMPLE_TYPE_LONG;
				primitiveSize = PrimitiveTypeLength.LONG_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_FLOAT:
				primitiveType = SimpleType.SIMPLE_TYPE_FLOAT;
				primitiveSize = PrimitiveTypeLength.FLOAT_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_DOUBLE:
				primitiveType = SimpleType.SIMPLE_TYPE_DOUBLE;
				primitiveSize = PrimitiveTypeLength.DOUBLE_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_BOOLEAN:
				primitiveType = SimpleType.SIMPLE_TYPE_BOOLEAN;
				primitiveSize = PrimitiveTypeLength.INTEGER_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_CHAR:
				primitiveType = SimpleType.SIMPLE_TYPE_CHAR;
				primitiveSize = PrimitiveTypeLength.CHAR_SIZE;
				return;
			}

			// read number of primitives
			int numPrimitives = in.readInt();

			Object[] featureValuesArray = new Object[numPrimitives];

			byte[] buffer = new byte[numPrimitives * primitiveSize];

			in.read(buffer);

			int index = 0;

			for (int i = 0; i < numPrimitives; i++) {
				featureValuesArray[i] = byteArrayToPrimitive(Arrays.copyOfRange(buffer, index, index + primitiveSize),
						primitiveType);

				index = index + primitiveSize;
			}

			if (primitiveType == SimpleType.SIMPLE_TYPE_BOOLEAN)
				handlePrimitiveBooleanEAttributeValues(focusObject, eAttribute, featureValuesArray, false);

			else
				handlePrimitiveEAttributeValues(focusObject, eAttribute, featureValuesArray, false);
		}
	}

	/*
	 * format: 7/8 objID featureID size value*
	 */
	protected void handleRemoveFromEAttribute() throws IOException {
		// get Object
		EObject focusObject = idToEObjectMap.get(in.readInt());

		// get eattribute
		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(in.readInt())));

		// get eDataType
		EDataType eDataType = eAttribute.getEAttributeType();

		int primitiveSize = -1;
		int primitiveType = -1;

		int dataType = getTypeID(eDataType);

		if (dataType != SimpleType.COMPLEX_TYPE) {

			int numStrings = in.readInt();

			String[] featureValuesArray = new String[numStrings];

			for (int i = 0; i < numStrings; i++) {
				featureValuesArray[i] = readString();
			}
			removeComplexEAttributeValues(focusObject, eAttribute, featureValuesArray);
		} else {

			switch (dataType) {
			case SimpleType.SIMPLE_TYPE_INT:
				primitiveType = SimpleType.SIMPLE_TYPE_INT;
				primitiveSize = PrimitiveTypeLength.INTEGER_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_SHORT:
				primitiveType = SimpleType.SIMPLE_TYPE_SHORT;
				primitiveSize = PrimitiveTypeLength.SHORT_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_LONG:
				primitiveType = SimpleType.SIMPLE_TYPE_LONG;
				primitiveSize = PrimitiveTypeLength.LONG_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_FLOAT:
				primitiveType = SimpleType.SIMPLE_TYPE_FLOAT;
				primitiveSize = PrimitiveTypeLength.FLOAT_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_DOUBLE:
				primitiveType = SimpleType.SIMPLE_TYPE_DOUBLE;
				primitiveSize = PrimitiveTypeLength.DOUBLE_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_BOOLEAN:
				primitiveType = SimpleType.SIMPLE_TYPE_BOOLEAN;
				primitiveSize = PrimitiveTypeLength.INTEGER_SIZE;
				break;
			case SimpleType.SIMPLE_TYPE_CHAR:
				primitiveType = SimpleType.SIMPLE_TYPE_CHAR;
				primitiveSize = PrimitiveTypeLength.CHAR_SIZE;
				return;
			}

			// read number of primitives
			int numPrimitives = in.readInt();

			Object[] featureValuesArray = new Object[numPrimitives];

			byte[] buffer = new byte[numPrimitives * primitiveSize];

			in.read(buffer);

			int index = 0;

			for (int i = 0; i < numPrimitives; i++) {
				featureValuesArray[i] = byteArrayToPrimitive(Arrays.copyOfRange(buffer, index, index + primitiveSize),
						primitiveType);

				index = index + primitiveSize;
			}

			if (primitiveType == SimpleType.SIMPLE_TYPE_BOOLEAN)
				removePrimitiveBooleanEAttributeValues(focusObject, eAttribute, featureValuesArray);

			else
				removePrimitiveEAttributeValues(focusObject, eAttribute, featureValuesArray);
		}
	}

	/*
	 * format: 9 objectID referenceID size objectIDs*
	 */
	protected void handleSetEReference() throws IOException {

		EObject focusObject = idToEObjectMap.get(in.readInt());

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(in.readInt())));

		int numInts = in.readInt();

		byte[] buffer = new byte[numInts * 4];

		in.read(buffer);

		int[] intArray = new int[numInts]; // stores 'n' numbers

		int index = 0;

		for (int i = 0; i < numInts; i++) {
			intArray[i] = byteArrayToInt(Arrays.copyOfRange(buffer, index, index + 4));

			index = index + 4;
		}
		setEReferenceValues(focusObject, eReference, intArray);
	}

	/*
	 * format: 10 objectID featureIDe size values
	 */
	protected void handleCreateAndSetEReference() throws IOException {

		EObject focusObject = idToEObjectMap.get(in.readInt());

		EReference ref = (EReference) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(in.readInt())));

		int numInts = in.readInt();

		// create a buffer to hold numInts integers
		byte[] buffer = new byte[numInts * PrimitiveTypeLength.INTEGER_SIZE];

		// read in integers
		in.read(buffer);

		// intArr to store numbers
		int[] intArray = new int[numInts];

		int index = 0;

		// populate int
		for (int i = 0; i < numInts; i++) {
			intArray[i] = byteArrayToInt(Arrays.copyOfRange(buffer, index, index + 4));

			index = index + 4;
		}

		index = 0;

		// create objects
		for (int i = 0; i < (numInts / 2); i++) {
			EObject obj = createEObject(ePackageElementsNamesMap.getName(intArray[index]));

			int id = intArray[index + 1];

			index = index + 2;

			resource.addObjectToMap(obj, id);

			idToEObjectMap.put(id, obj);
		}

		// set reference
		if (ref.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(ref);
			featureValuesList.clear();
			for (int i = 1; i < numInts; i = i + 2) {
				featureValuesList.add(idToEObjectMap.get(intArray[i]));
			}
		} else {
			focusObject.eSet(ref, idToEObjectMap.get(intArray[1]));
		}
	}

	/*
	 * format: 11 objectID featureIDe size values
	 */
	protected void handleCreateAndAddToEReference() throws IOException {

		EObject focusObject = idToEObjectMap.get(in.readInt());

		EReference ref = (EReference) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(in.readInt())));

		int numInts = in.readInt();

		// create a buffer to hold numInts integers
		byte[] buffer = new byte[numInts * PrimitiveTypeLength.INTEGER_SIZE];

		// read in integers
		in.read(buffer);

		// intArr to store numbers
		int[] intArray = new int[numInts];

		int index = 0;

		// populate int
		for (int i = 0; i < numInts; i++) {
			intArray[i] = byteArrayToInt(Arrays.copyOfRange(buffer, index, index + 4));

			index = index + 4;
		}

		index = 0;

		// create objects
		for (int i = 0; i < (numInts / 2); i++) {
			EObject obj = createEObject(ePackageElementsNamesMap.getName(intArray[index]));

			int id = intArray[index + 1];

			index = index + 2;

			resource.addObjectToMap(obj, id);

			idToEObjectMap.put(id, obj);
		}

		// set reference
		if (ref.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(ref);
			for (int i = 1; i < numInts; i = i + 2) {
				featureValuesList.add(idToEObjectMap.get(intArray[i]));
			}
		} else {
			focusObject.eSet(ref, idToEObjectMap.get(intArray[1]));
		}
	}

	/*
	 * format: 12 objectID featureIDe size values
	 */
	protected void handleAddToEReference() throws IOException {

		EObject focusObject = idToEObjectMap.get(in.readInt());

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(in.readInt())));

		int numInts = in.readInt();

		byte[] buffer = new byte[numInts * 4];

		in.read(buffer);

		int[] intArray = new int[numInts]; // stores 'n' numbers

		int index = 0;

		for (int i = 0; i < numInts; i++) {
			intArray[i] = byteArrayToInt(Arrays.copyOfRange(buffer, index, index + 4));

			index = index + 4;
		}
		addEReferenceValues(focusObject, eReference, intArray);
	}

	/*
	 * format: 12 objectID featureIDe size values
	 */
	protected void handleRemoveFromEReference() throws IOException {

		EObject focusObject = idToEObjectMap.get(in.readInt());

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(in.readInt())));

		int numInts = in.readInt();

		byte[] buffer = new byte[numInts * 4];

		in.read(buffer);

		int[] intArray = new int[numInts];

		int index = 0;

		for (int i = 0; i < numInts; i++) {
			intArray[i] = byteArrayToInt(Arrays.copyOfRange(buffer, index, index + 4));

			index = index + 4;
		}
		removeFromEReference(focusObject, eReference, intArray);
	}

	protected String readString() throws IOException {
		int length = in.readInt();
		byte[] bytes = new byte[length];
		in.read(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	private Object byteArrayToPrimitive(byte[] bytes, int primitiveType) {
		switch (primitiveType) {
		case SimpleType.SIMPLE_TYPE_BOOLEAN:
			return byteArrayToInt(bytes);
		case SimpleType.SIMPLE_TYPE_BYTE:
			return byteArrayToByte(bytes);
		case SimpleType.SIMPLE_TYPE_CHAR:
			return byteArrayToChar(bytes);
		case SimpleType.SIMPLE_TYPE_DOUBLE:
			return byteArrayToDouble(bytes);
		case SimpleType.SIMPLE_TYPE_FLOAT:
			return byteArrayToFloat(bytes);
		case SimpleType.SIMPLE_TYPE_LONG:
			return byteArrayToLong(bytes);
		case SimpleType.SIMPLE_TYPE_INT:
			return byteArrayToInt(bytes);
		case SimpleType.SIMPLE_TYPE_SHORT:
			return byteArrayToShort(bytes);
		}
		return null;
	}

	private int byteArrayToInt(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getInt();
	}

	private byte byteArrayToByte(byte[] bytes) {
		return ByteBuffer.wrap(bytes).get();
	}

	private char byteArrayToChar(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getChar();
	}

	private double byteArrayToDouble(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getDouble();
	}

	private float byteArrayToFloat(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getFloat();
	}

	private long byteArrayToLong(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getLong();
	}

	private short byteArrayToShort(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getShort();
	}

	private void handlePrimitiveBooleanEAttributeValues(EObject focusObject, EAttribute eAttribute,
			Object[] featureValuesArray, boolean set) {
		boolean b = true;

		if (eAttribute.isMany()) {
			@SuppressWarnings("unchecked")
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);
			if (set) {
				featureValuesList.clear();
			}
			for (Object obj : featureValuesArray) {
				b = true;
				if ((int) obj == 0) {
					b = false;
				}
				featureValuesList.add(b);
			}
		} else {
			focusObject.eSet(eAttribute, b);
		}
	}

	private void handlePrimitiveEAttributeValues(EObject focusObject, EAttribute eAttribute,
			Object[] featureValuesArray, boolean set) {
		if (eAttribute.isMany()) {
			@SuppressWarnings("unchecked")
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);
			if (set) {
				featureValuesList.clear();
			}
			for (Object obj : featureValuesArray) {
				featureValuesList.add(obj);
			}
		} else {
			focusObject.eSet(eAttribute, featureValuesArray[0]);
		}
	}

	private void handleComplexEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray,
			boolean set) {
		EDataType eDataType = eAttribute.getEAttributeType();

		if (eAttribute.isMany()) {
			@SuppressWarnings("unchecked")
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);
			if (set) {
				featureValuesList.clear();
			}
			for (String str : featureValuesArray) {
				if (eDataType.getName().equals("EString"))
					featureValuesList.add(str);
				else
					featureValuesList.add(EcoreUtil.createFromString(eDataType, str));
			}
		} else {
			String str = featureValuesArray[0];
			if (eDataType.getName().equals("EString"))
				focusObject.eSet(eAttribute, str);
			else
				focusObject.eSet(eAttribute, EcoreUtil.createFromString(eDataType, str));
		}
	}

	private void removePrimitiveEAttributeValues(EObject focusObject, EAttribute eAttribute,
			Object[] featureValuesArray) {
		if (eAttribute.isMany()) {
			@SuppressWarnings("unchecked")
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);
			for (Object obj : featureValuesArray) {
				featureValuesList.remove(obj);
			}
		} else {
			focusObject.eUnset(eAttribute);
		}
	}

	private void removeComplexEAttributeValues(EObject focusObject, EAttribute eAttribute,
			String[] featureValuesArray) {
		EDataType eDataType = eAttribute.getEAttributeType();

		if (eAttribute.isMany()) {
			@SuppressWarnings("unchecked")
			EList<Object> featureValueList = (EList<Object>) focusObject.eGet(eAttribute);
			for (String str : featureValuesArray) {
				if (eDataType.getName().equals("EString"))
					featureValueList.remove(str);
				else
					featureValueList.remove(EcoreUtil.createFromString(eDataType, str));
			}
		} else {
			focusObject.eUnset(eAttribute);
		}
	}

	private void removePrimitiveBooleanEAttributeValues(EObject focusObject, EAttribute eAttribute,
			Object[] featureValuesArray) {
		boolean b = true;

		if (eAttribute.isMany()) {
			@SuppressWarnings("unchecked")
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);
			for (Object obj : featureValuesArray) {
				b = true;
				if ((int) obj == 0)
					b = false;
				featureValuesList.remove(b);
			}
		} else {
			focusObject.eUnset(eAttribute);
		}
	}

	private void setEReferenceValues(EObject focusObject, EReference eReference, int[] eObjectIDArray) {
		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);
			featureValuesList.clear();
			for (int i : eObjectIDArray) {
				featureValuesList.add(idToEObjectMap.get(i));
			}
		} else {
			focusObject.eSet(eReference, idToEObjectMap.get(eObjectIDArray[0]));
		}
	}

	private void addEReferenceValues(EObject focusObject, EReference eReference, int[] eObjectIDArray) {
		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);
			for (int i : eObjectIDArray) {
				featureValuesList.add(idToEObjectMap.get(i));
			}
		} else {
			focusObject.eSet(eReference, idToEObjectMap.get(eObjectIDArray[0]));
		}
	}

	private void removeFromEReference(EObject focusObject, EReference eReference, int[] eObjectIDArray) {
		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);
			for (int i : eObjectIDArray) {
				featureValuesList.remove(idToEObjectMap.get(i));
			}
		} else {
			focusObject.eUnset(eReference);
		}
	}
}
