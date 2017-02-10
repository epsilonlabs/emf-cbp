
package org.eclipse.epsilon.cbp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.eclipse.epsilon.cbp.util.SimpleType;

public class CBPTextDeserialiser extends AbstractCBPDeserialiser {

	public CBPTextDeserialiser(CBPResource resource) {
		super(resource);
	}
	
	public void deserialise(InputStream inputStream, Map<?, ?> options) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, persistenceUtil.STRING_ENCODING));

		String line;

		readingLoop:
		while ((line = br.readLine()) != null) {
			// System.out.println(line);
			StringTokenizer st = new StringTokenizer(line);

			int eventType = -1;

			if (st.hasMoreElements())
				eventType = Integer.valueOf(st.nextToken());

			// Switches over various event types, calls appropriate handler
			// method
			switch (eventType) {
			case SerialisationEventType.REGISTER_EPACKAGE:
				handleRegisterEPackage(line);
				break;
			case SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE:
				handleCreateAndAddToResource(line);
				break;
			case SerialisationEventType.REMOVE_FROM_RESOURCE:
				handleRemoveFromResource(line);
				break;
			case SerialisationEventType.SET_EATTRIBUTE_PRIMITIVE:
				handleSetEAttribute(line);
				break;
			case SerialisationEventType.SET_EATTRIBUTE_COMPLEX:
				handleSetEAttribute(line);
				break;
			case SerialisationEventType.ADD_TO_EATTRIBUTE_PRIMITIVE:
				handleAddToEAttribute(line);
				break;
			case SerialisationEventType.ADD_TO_EATTRIBUTE_COMPLEX:
				handleAddToEAttribute(line);
				break;
			case SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE:
				handleRemoveFromEAttribute(line);
				break;
			case SerialisationEventType.REMOVE_FROM_EATTRIBUTE_COMPLEX:
				handleRemoveFromEAttribute(line);
				break;
			case SerialisationEventType.SET_EREFERENCE:
				handleSetEReference(line);
				break;
			case SerialisationEventType.CREATE_AND_SET_EREFERENCE:
				handleCreateAndSetEReference(line);
				break;
			case SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE:
				handleCreateAndAddToEReference(line);
				break;
			case SerialisationEventType.ADD_TO_EREFERENCE:
				handleAddToEReference(line);
				break;
			case SerialisationEventType.REMOVE_FROM_EREFERENCE:
				handleRemoveFromEReference(line);
				break;
			case SerialisationEventType.STOP_READING:
				break readingLoop;
			default:
				break;
			}
		}
		
		br.close();
	}

	/*
	 * event has the format of: 0 (MetaElementTypeID objectID(,)?)*
	 */
	protected void handleCreateAndAddToResource(Object entry) {

		String line = (String) entry;

		// break line into tokens
		String[] objToCreateAndAddArray = tokeniseString(getSubString(line, 1));

		for (String str : objToCreateAndAddArray) {

			String[] stringArray = str.split(" ");

			EObject obj = null;
			if (verbose) {
				// create eobject with the first token
				obj = createEObject(String.valueOf(stringArray[0]));
			} else {
				// create eobject with the first token
				obj = createEObject(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[0])));
			}

			// get the object ID with the second token
			int id = Integer.valueOf(stringArray[1]);

			// add object to change log
			resource.addObjectToMap(obj, id);

			// put to IDToEObjectMap
			IDToEObjectMap.put(id, obj);

			// add to resource contents
			resource.getContents().add(obj);
		}
	}

	/*
	 * event format: 2 EObjectID*
	 */

	protected void handleRemoveFromResource(Object entry) {

		String line = (String) entry;

		// tokenise string
		String[] objValueStringsArray = tokeniseString(getSubString(line, 1));

		// for each string, get EBoject and remove from contents
		for (String str : objValueStringsArray) {
			resource.getContents().remove(IDToEObjectMap.get(Integer.valueOf(str)));
		}
	}

	/*
	 * event format: 3/4 objectID EAttributeID [value*]
	 */

	protected void handleSetEAttribute(Object entry) {
		String line = (String) entry;

		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EAttribute eAttribute = null;
		if (verbose) {
			eAttribute = (EAttribute) focusObject.eClass()
					.getEStructuralFeature(getPropertyName(String.valueOf(stringArray[2])));
		} else {
			eAttribute = (EAttribute) focusObject.eClass().getEStructuralFeature(
					getPropertyName(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2]))));
		}

		String[] featureValuesArray = tokeniseString(getSubString(line, 3));

		setEAttributeValues(focusObject, eAttribute, featureValuesArray);
	}

	/*
	 * event format: 3/4 objectID EAttributeID [value*]
	 */
	protected void handleAddToEAttribute(Object entry) {
		String line = (String) entry;
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EAttribute eAttribute = null;

		if (verbose) {
			eAttribute = (EAttribute) focusObject.eClass()
					.getEStructuralFeature(getPropertyName(String.valueOf(stringArray[2])));
		} else {
			eAttribute = (EAttribute) focusObject.eClass().getEStructuralFeature(
					getPropertyName(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2]))));
		}

		String[] featureValuesArray = tokeniseString(getSubString(line, 3));

		addEAttributeValues(focusObject, eAttribute, featureValuesArray);
	}

	/*
	 * event type: 7/8 objectID EAttributeID value*
	 */
	protected void handleRemoveFromEAttribute(Object entry) {
		String line = (String) entry;
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EAttribute eAttribute = null;

		if (verbose) {
			eAttribute = (EAttribute) focusObject.eClass()
					.getEStructuralFeature(getPropertyName(String.valueOf(stringArray[2])));
		} else {
			eAttribute = (EAttribute) focusObject.eClass().getEStructuralFeature(
					getPropertyName(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2]))));
		}

		String[] featureValuesArray = tokeniseString(getSubString(line, 3));

		RemoveEAttributeValues(focusObject, eAttribute, featureValuesArray);
	}

	/*
	 * event format: 10 objectID EReferenceID (ECLass ID, EObject (,)?)* 12/9
	 * objectID EReferenceID EObjectID
	 */
	protected void handleSetEReference(Object entry) {
		String line = (String) entry;
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));
		EReference eReference = null;
		if (verbose) {
			eReference = (EReference) focusObject.eClass()
					.getEStructuralFeature(getPropertyName(String.valueOf(stringArray[2])));
		} else {
			eReference = (EReference) focusObject.eClass().getEStructuralFeature(
					getPropertyName(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2]))));
		}

		String[] featureValueStringsArray = tokeniseString(getSubString(line, 3));

		setEReferenceValues(focusObject, eReference, featureValueStringsArray);

	}

	protected void handleCreateAndSetEReference(Object entry) {

		String line = (String) entry;

		// split line
		String[] stringArray = line.split(" ");

		// create focus object
		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = null;

		if (verbose) {
			// get ereference
			eReference = (EReference) focusObject.eClass()
					.getEStructuralFeature(getPropertyName(String.valueOf(stringArray[2])));
		} else {
			// get ereference
			eReference = (EReference) focusObject.eClass().getEStructuralFeature(
					getPropertyName(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2]))));
		}

		// get values
		String[] refValueStringsArray = tokeniseString(getSubString(line, 3));

		// prepare objectToAddList
		List<EObject> eObjectToAddList = new ArrayList<EObject>();

		for (String str : refValueStringsArray) {
			// split string
			String[] temp = str.split(" ");

			EObject obj = null;

			if (verbose) {
				// create obj
				obj = createEObject(String.valueOf(temp[0]));
			} else {
				// create obj
				obj = createEObject(ePackageElementsNamesMap.getName(Integer.valueOf(temp[0])));
			}

			// get id
			int id = Integer.valueOf(temp[1]);

			// add obj to change log
			resource.addObjectToMap(obj, id);

			// put to IdToObjectMap
			IDToEObjectMap.put(id, obj);

			// add to objectToAddList
			eObjectToAddList.add(obj);
		}

		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);

			// clear because this is set
			featureValuesList.clear();

			for (EObject obj : eObjectToAddList) {
				featureValuesList.add(obj);
			}
		} else {
			focusObject.eSet(eReference, eObjectToAddList.get(0));
		}
	}

	protected void handleCreateAndAddToEReference(Object entry) {
		String line = (String) entry;

		// split line
		String[] stringArray = line.split(" ");

		// create focus object
		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = null;

		if (verbose) {
			// get ereference
			eReference = (EReference) focusObject.eClass()
					.getEStructuralFeature(getPropertyName(String.valueOf(stringArray[2])));
		} else {
			// get ereference
			eReference = (EReference) focusObject.eClass().getEStructuralFeature(
					getPropertyName(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2]))));
		}

		// get values
		String[] refValueStringsArray = tokeniseString(getSubString(line, 3));

		// prepare objectToAddList
		List<EObject> eObjectToAddList = new ArrayList<EObject>();

		for (String str : refValueStringsArray) {
			// split string
			String[] temp = str.split(" ");

			EObject obj = null;

			if (verbose) {
				// create obj
				obj = createEObject(String.valueOf(temp[0]));
			} else {
				// create obj
				obj = createEObject(ePackageElementsNamesMap.getName(Integer.valueOf(temp[0])));
			}

			// get id
			int id = Integer.valueOf(temp[1]);

			// add obj to change log
			resource.addObjectToMap(obj, id);

			// put to IdToObjectMap
			IDToEObjectMap.put(id, obj);

			// add to objectToAddList
			eObjectToAddList.add(obj);
		}

		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);

			for (EObject obj : eObjectToAddList) {
				featureValuesList.add(obj);
			}
		} else {
			System.err.println("create and add to non-many reference, warning");
			// should not happen
		}
	}

	protected void handleAddToEReference(Object entry) {

		String line = (String) entry;

		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = null;

		if (verbose) {
			eReference = (EReference) focusObject.eClass()
					.getEStructuralFeature(getPropertyName(String.valueOf(stringArray[2])));
		} else {
			eReference = (EReference) focusObject.eClass().getEStructuralFeature(
					getPropertyName(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2]))));
		}

		String[] refValueStringsArray = tokeniseString(getSubString(line, 3));

		List<EObject> eObjectToAddList = new ArrayList<EObject>();

		for (String str : refValueStringsArray) {

			int id = Integer.valueOf(str);

			EObject obj = IDToEObjectMap.get(id);

			if (obj == null) {
				System.err.println("error when handling addToEReference: obj " + id + " not found");
			}

			eObjectToAddList.add(obj);
		}

		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);

			for (EObject obj : eObjectToAddList) {
				featureValuesList.add(obj);
			}
		} else {
			System.err.println("add to non-many reference, error, operation not applicable");
			// this should not happen
			// focusObject.eSet(eReference, eObjectToAddList.get(0));
		}
	}

	protected void handleRemoveFromEReference(Object entry) {

		String line = (String) entry;

		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = null;

		if (verbose) {
			eReference = (EReference) focusObject.eClass()
					.getEStructuralFeature(getPropertyName(String.valueOf(stringArray[2])));
		} else {
			eReference = (EReference) focusObject.eClass().getEStructuralFeature(
					getPropertyName(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2]))));
		}
		String[] featureValueStringsArray = tokeniseString(getSubString(line, 3));

		removeEReferenceValues(focusObject, eReference, featureValueStringsArray);
	}

	@Override
	protected void handleRegisterEPackage(Object entry) {
		String line = (String) entry;
		int index = line.indexOf(" ");
		String nsuri = line.substring(index + 1, line.length());
		EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(nsuri);
		ePackages.add(ePackage);
		ePackageElementsNamesMap = persistenceUtil.generateEPackageElementNamesMap(ePackage);
	}

	public String getSubString(String str, int offset) {
		String[] arr = str.split(" ");
		int count = offset;
		for (int i = 0; i < offset; i++) {
			count += arr[i].length();
		}
		return str.substring(count, str.length());
	}

	protected void setEAttributeValues(EObject focusObject, EAttribute eAttribute, Object[] featureValuesArray) {
		String[] valuesArray = (String[]) featureValuesArray;
		// get typeID;
		int primitiveTypeID = getTypeID(eAttribute.getEAttributeType());

		// if is many
		if (eAttribute.isMany()) {
			@SuppressWarnings("unchecked")
			// feature values
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);

			// clear since this is set
			featureValuesList.clear();

			// if typeID is complex
			if (primitiveTypeID == SimpleType.COMPLEX_TYPE) {
				for (String str : valuesArray) {
					featureValuesList.add(EcoreUtil.createFromString(eAttribute.getEAttributeType(), str));
				}
			} else // primitiveTypeID != PersistenceManager.COMPLEX_TYPE
			{
				for (String str : valuesArray) {
					featureValuesList.add(convertStringToPrimitive(str, primitiveTypeID));
				}
			}
		} else {
			if (primitiveTypeID == SimpleType.COMPLEX_TYPE) {
				focusObject.eSet(eAttribute,
						EcoreUtil.createFromString(eAttribute.getEAttributeType(), valuesArray[0]));
			} else {
				focusObject.eSet(eAttribute, convertStringToPrimitive(valuesArray[0], primitiveTypeID));
			}
		}
	}

	protected void addEAttributeValues(EObject focusObject, EAttribute eAttribute, Object[] featureValuesArray) {
		String[] valuesArray = (String[]) featureValuesArray;

		// get typeID;
		int primitiveTypeID = getTypeID(eAttribute.getEAttributeType());

		// if is many
		if (eAttribute.isMany()) {
			@SuppressWarnings("unchecked")
			// feature values
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);

			// if typeID is complex
			if (primitiveTypeID == SimpleType.COMPLEX_TYPE) {
				for (String str : valuesArray) {
					featureValuesList.add(EcoreUtil.createFromString(eAttribute.getEAttributeType(), str));
				}
			} else // primitiveTypeID != PersistenceManager.COMPLEX_TYPE
			{
				for (String str : valuesArray) {
					featureValuesList.add(convertStringToPrimitive(str, primitiveTypeID));
				}
			}
		} else {
			System.err.println("adding eattribute values to non-collection: use SetEAttributeEvent");
			// should not happen
		}
	}

	protected void RemoveEAttributeValues(EObject focusObject, EAttribute eAttribute, Object[] featureValuesArray) {
		String[] valuesArray = (String[]) featureValuesArray;

		int primitiveTypeID = getTypeID(eAttribute.getEAttributeType());

		if (eAttribute.isMany()) {
			@SuppressWarnings("unchecked")
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);

			if (primitiveTypeID == SimpleType.COMPLEX_TYPE) {
				for (String str : valuesArray) {
					featureValuesList.remove(EcoreUtil.createFromString(eAttribute.getEAttributeType(), str));
				}
			} else // primitiveTypeID != PersistenceManager.COMPLEX_TYPE
			{
				for (String str : valuesArray) {
					featureValuesList.remove(convertStringToPrimitive(str, primitiveTypeID));
				}
			}
		} else {
			focusObject.eUnset(eAttribute);
		}
	}

	protected void setEReferenceValues(EObject focusObject, EReference eReference, Object[] featureValuesArray) {
		String[] valuesArray = (String[]) featureValuesArray;

		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);

			featureValuesList.clear();
			for (String str : valuesArray) {
				featureValuesList.add(IDToEObjectMap.get(Integer.valueOf(str)));
			}
		} else {
			focusObject.eSet(eReference, IDToEObjectMap.get(Integer.valueOf(valuesArray[0])));
		}
	}

	protected void removeEReferenceValues(EObject focusObject, EReference eReference, Object[] featureValuesArray) {
		String[] valuesArray = (String[]) featureValuesArray;

		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);

			for (String str : valuesArray) {
				featureValuesList.remove(IDToEObjectMap.get(Integer.valueOf(str)));
			}
		} else {
			focusObject.eUnset(eReference);
		}
	}
}
