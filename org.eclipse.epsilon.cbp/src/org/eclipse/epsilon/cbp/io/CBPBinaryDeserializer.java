package org.eclipse.epsilon.cbp.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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
	private final Charset STRING_ENCODING = StandardCharsets.UTF_8;

	public CBPBinaryDeserializer(CBPResource resource) {

		this.commonsimpleTypeNameMap = persistenceUtil.getCommonSimpleTypesMap();
		this.textSimpleTypeNameMap = persistenceUtil.getTextSimpleTypesMap();

		this.resource = resource;
		contents = resource.getContents();
	}

	@Override
	public void deserialise(InputStream inputStream, Map<?, ?> options) throws IOException {
		
		/* Read binary records */
		readingLoop:
		while (inputStream.available() > 0) {
			switch (readInt(inputStream)) {
			case SerialisationEventType.REGISTER_EPACKAGE:
				handleRegisterEPackage(inputStream);
				break;
			case SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE:
				handleCreateAndAddToResource(inputStream);
				break;
			case SerialisationEventType.REMOVE_FROM_RESOURCE:
				handleRemoveFromResource(inputStream);
				break;
			case SerialisationEventType.SET_EATTRIBUTE_PRIMITIVE:
				handleSetEAttribute(inputStream);
				break;
			case SerialisationEventType.SET_EATTRIBUTE_COMPLEX:
				handleSetEAttribute(inputStream);
				break;
			case SerialisationEventType.ADD_TO_EATTRIBUTE_PRIMITIVE:
				handleAddToEAttribute(inputStream);
				break;
			case SerialisationEventType.ADD_TO_EATTRIBUTE_COMPLEX:
				handleAddToEAttribute(inputStream);
				break;
			case SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE:
				handleRemoveFromEAttribute(inputStream);
				break;
			case SerialisationEventType.REMOVE_FROM_EATTRIBUTE_COMPLEX:
				handleRemoveFromEAttribute(inputStream);
				break;
			case SerialisationEventType.SET_EREFERENCE:
				handleSetEReference(inputStream);
				break;
			case SerialisationEventType.CREATE_AND_SET_EREFERENCE:
				handleCreateAndSetEReference(inputStream);
				break;
			case SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE:
				handleCreateAndAddToEReference(inputStream);
				break;
			case SerialisationEventType.ADD_TO_EREFERENCE:
				handleAddToEReference(inputStream);
				break;
			case SerialisationEventType.REMOVE_FROM_EREFERENCE:
				handleRemoveFromEReference(inputStream);
				break;
			case SerialisationEventType.STOP_READING:
				break readingLoop;
			default:
				break;
			}
		}

		inputStream.close();
	}

	@Override
	protected void handleRegisterEPackage(Object entry) throws IOException {

		InputStream in = (InputStream) entry;

		// read String size
		int numBytes = readInt(in);

		// get nsuri
		String nsuri = readString(in, numBytes);

		EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsuri);
		ePackages.add(ePackage);
		ePackageElementsNamesMap = persistenceUtil.generateEPackageElementNamesMap(ePackage);
	}

	@Override
	protected void handleCreateAndAddToResource(Object entry) throws IOException {
		// cast entry to input stream
		InputStream in = (InputStream) entry;

		// read number of ints
		int numInts = readInt(in);

		// create buffer for number of ints
		byte[] buffer = new byte[numInts * PrimitiveTypeLength.INTEGER_SIZE];

		// read buffer
		in.read(buffer);

		// arr to store num of ints
		int[] intArray = new int[numInts];

		// index
		int index = 0;

		// populate int array
		for (int i = 0; i < numInts; i++) {
			intArray[i] = byteArrayToInt(Arrays.copyOfRange(buffer, index, index + PrimitiveTypeLength.INTEGER_SIZE));
			index = index + PrimitiveTypeLength.INTEGER_SIZE;
		}

		index = 0;

		for (int i = 0; i < (numInts / 2); i++) {
			// create object
			EObject obj = createEObject(ePackageElementsNamesMap.getName(intArray[index]));

			// get id
			int id = intArray[index + 1];

			// increase index
			index = index + 2;

			// add object to map
			resource.addObjectToMap(obj, id);

			// put obj to map
			IDToEObjectMap.put(id, obj);

			// add to content
			contents.add(obj);
		}

	}

	/*
	 * format: 2 size id*
	 */
	@Override
	protected void handleRemoveFromResource(Object entry) throws IOException {
		// cast entry to input stream
		InputStream in = (InputStream) entry;
		int numInts = readInt(in);
		byte[] buffer = new byte[numInts * PrimitiveTypeLength.INTEGER_SIZE];
		in.read(buffer);
		int index = 0;
		for (int i = 0; i < numInts; i++) {
			int id = byteArrayToInt(Arrays.copyOfRange(buffer, index, index + 4));
			contents.remove(IDToEObjectMap.get(id));
			index = index + 4;
		}
	}

	/*
	 * format: 3/4 obj_ID attribute_id size values
	 */
	@Override
	protected void handleSetEAttribute(Object entry) throws IOException {
		// cast entry to input stream
		InputStream in = (InputStream) entry;

		// get Object
		EObject focusObject = IDToEObjectMap.get(readInt(in));

		// get eattribute
		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(readInt(in))));

		// get eDataType
		EDataType eDataType = eAttribute.getEAttributeType();

		int primitiveSize = -1;
		int primitiveType = -1;

		int dataType = getTypeID(eDataType);

		if (dataType == SimpleType.COMPLEX_TYPE || dataType == SimpleType.TEXT_SIMPLE_TYPE_ESTRING) {

			int numStrings = readInt(in);

			String[] featureValuesArray = new String[numStrings];

			for (int i = 0; i < numStrings; i++) {
				int numBytes = readInt(in);

				featureValuesArray[i] = readString(in, numBytes);
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
			int numPrimitives = readInt(in);

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
	@Override
	protected void handleAddToEAttribute(Object entry) throws IOException {
		// cast entry to input stream
		InputStream in = (InputStream) entry;

		// get Object
		EObject focusObject = IDToEObjectMap.get(readInt(in));

		// get eattribute
		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(readInt(in))));

		// get eDataType
		EDataType eDataType = eAttribute.getEAttributeType();

		int primitiveSize = -1;
		int primitiveType = -1;

		int dataType = getTypeID(eDataType);

		if (dataType == SimpleType.COMPLEX_TYPE || dataType == SimpleType.TEXT_SIMPLE_TYPE_ESTRING) {

			int numStrings = readInt(in);

			String[] featureValuesArray = new String[numStrings];

			for (int i = 0; i < numStrings; i++) {
				int numBytes = readInt(in);

				featureValuesArray[i] = readString(in, numBytes);
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
			int numPrimitives = readInt(in);

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
	@Override
	protected void handleRemoveFromEAttribute(Object entry) throws IOException {
		// cast entry to input stream
		InputStream in = (InputStream) entry;

		// get Object
		EObject focusObject = IDToEObjectMap.get(readInt(in));

		// get eattribute
		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(readInt(in))));

		// get eDataType
		EDataType eDataType = eAttribute.getEAttributeType();

		int primitiveSize = -1;
		int primitiveType = -1;

		int dataType = getTypeID(eDataType);

		if (dataType != SimpleType.COMPLEX_TYPE) {

			int numStrings = readInt(in);

			String[] featureValuesArray = new String[numStrings];

			for (int i = 0; i < numStrings; i++) {
				int numBytes = readInt(in);

				featureValuesArray[i] = readString(in, numBytes);
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
			int numPrimitives = readInt(in);

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
	@Override
	protected void handleSetEReference(Object entry) throws IOException {

		// cast entry to input stream
		InputStream in = (InputStream) entry;

		EObject focusObject = IDToEObjectMap.get(readInt(in));

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(readInt(in))));

		int numInts = readInt(in);

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
	@Override
	protected void handleCreateAndSetEReference(Object entry) throws IOException {
		// cast entry to input stream
		InputStream in = (InputStream) entry;

		EObject focusObject = IDToEObjectMap.get(readInt(in));

		EReference ref = (EReference) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(readInt(in))));

		int numInts = readInt(in);

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

			IDToEObjectMap.put(id, obj);
		}

		// set reference
		if (ref.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(ref);
			featureValuesList.clear();
			for (int i = 1; i < numInts; i = i + 2) {
				featureValuesList.add(IDToEObjectMap.get(intArray[i]));
			}
		} else {
			focusObject.eSet(ref, IDToEObjectMap.get(intArray[1]));
		}
	}

	/*
	 * format: 11 objectID featureIDe size values
	 */
	@Override
	protected void handleCreateAndAddToEReference(Object entry) throws IOException {
		// cast entry to input stream
		InputStream in = (InputStream) entry;

		EObject focusObject = IDToEObjectMap.get(readInt(in));

		EReference ref = (EReference) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(readInt(in))));

		int numInts = readInt(in);

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

			IDToEObjectMap.put(id, obj);
		}

		// set reference
		if (ref.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(ref);
			for (int i = 1; i < numInts; i = i + 2) {
				featureValuesList.add(IDToEObjectMap.get(intArray[i]));
			}
		} else {
			focusObject.eSet(ref, IDToEObjectMap.get(intArray[1]));
		}
	}

	/*
	 * format: 12 objectID featureIDe size values
	 */
	@Override
	protected void handleAddToEReference(Object entry) throws IOException {

		// cast entry to input stream
		InputStream in = (InputStream) entry;

		EObject focusObject = IDToEObjectMap.get(readInt(in));

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(readInt(in))));

		int numInts = readInt(in);

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
	@Override
	protected void handleRemoveFromEReference(Object entry) throws IOException {

		// cast entry to input stream
		InputStream in = (InputStream) entry;

		EObject focusObject = IDToEObjectMap.get(readInt(in));

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(getPropertyName(ePackageElementsNamesMap.getName(readInt(in))));

		int numInts = readInt(in);

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

	private int readInt(InputStream in) throws IOException {
		byte[] bytes = new byte[PrimitiveTypeLength.INTEGER_SIZE];
		in.read(bytes);

		return ByteBuffer.wrap(bytes).getInt();
	}

	private String readString(InputStream in, int length) throws IOException {
		byte[] bytes = new byte[length];
		in.read(bytes);

		return new String(bytes, STRING_ENCODING);
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
				featureValuesList.add(IDToEObjectMap.get(i));
			}
		} else {
			focusObject.eSet(eReference, IDToEObjectMap.get(eObjectIDArray[0]));
		}
	}

	private void addEReferenceValues(EObject focusObject, EReference eReference, int[] eObjectIDArray) {
		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);
			for (int i : eObjectIDArray) {
				featureValuesList.add(IDToEObjectMap.get(i));
			}
		} else {
			focusObject.eSet(eReference, IDToEObjectMap.get(eObjectIDArray[0]));
		}
	}

	private void removeFromEReference(EObject focusObject, EReference eReference, int[] eObjectIDArray) {
		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);
			for (int i : eObjectIDArray) {
				featureValuesList.remove(IDToEObjectMap.get(i));
			}
		} else {
			focusObject.eUnset(eReference);
		}
	}
}
