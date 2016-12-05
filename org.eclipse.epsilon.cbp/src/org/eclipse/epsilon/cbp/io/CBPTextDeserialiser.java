
package org.eclipse.epsilon.cbp.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.context.CBPContext;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.eclipse.epsilon.cbp.util.SimpleType;

public class CBPTextDeserialiser extends AbstractCBPDeserialiser{
	
	public CBPTextDeserialiser(CBPContext context, Resource resource) {
		this.context = context;
		this.ePackageElementsNamesMap = context.getePackageElementsNamesMap();

		this.commonsimpleTypeNameMap = context.getCommonSimpleTypesMap();
		this.textSimpleTypeNameMap = context.getTextSimpleTypesMap();
		
		this.resource = resource;
		contents = resource.getContents();
	}
	
	public void deserialise(Map<?, ?> options) throws Exception {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(resource.getURI().path()), persistenceUtil.STRING_ENCODING));

		String line;

		br.readLine(); // skip file format info

		if ((line = br.readLine()) != null) {
			String[] stringArray = line.split(" ");
			ePackage = loadMetamodel(stringArray[1]);
		} else {
			System.out.println("CBPTextDeserialiser Error, file empty");
			System.exit(0);
		}

		while ((line = br.readLine()) != null) {
			// System.out.println(line);
			StringTokenizer st = new StringTokenizer(line);

			int eventType = -1;

			if (st.hasMoreElements())
				eventType = Integer.valueOf(st.nextToken());

			//Switches over various event types, calls appropriate handler method
			switch (eventType) {
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
			default:
				break;
			}
		}
		br.close();
		context.setResume(true);
	}
	
	/*
	 * event has the format of:
	 * 0 [(MetaElementTypeID objectID)* (,)*]
	 */
	protected void handleCreateAndAddToResource(String line) {
		
		//break line into tokens
		String[] objToCreateAndAddArray = tokeniseString(getValueInSquareBrackets(line));

		for (String str : objToCreateAndAddArray) {
			
			
			String[] stringArray = str.split(" ");
			
			//create eobject with the first token
			EObject obj = createEObject(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[0])));

			//get the object ID with the second token
			int id = Integer.valueOf(stringArray[1]);

			//add object to change log
			context.addObjectToMap(obj, id);
			
			//put to IDToEObjectMap
			IDToEObjectMap.put(id, obj);

			// add to resource contents
			contents.add(obj);
		}
	}
	
	/*
	 * event format:
	 * 2 [EObjectID*]
	 */

	protected void handleRemoveFromResource(String line) {
		//tokenise string
		String[] objValueStringsArray = tokeniseString(getValueInSquareBrackets(line));

		//for each string, get EBoject and remove from contents
		for (String str : objValueStringsArray) {
			contents.remove(IDToEObjectMap.get(Integer.valueOf(str)));
		}
	}
	
	/*
	 * event format:
	 * 3/4 objectID EAttributeID [value*]
	 */

	protected void handleSetEAttribute(String line)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValuesArray = tokeniseString(getValueInSquareBrackets(line));

		setEAttributeValues(focusObject, eAttribute, featureValuesArray);
	}

	protected void setEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray )
	{
		//get typeID;
		int primitiveTypeID = getTypeID(eAttribute.getEAttributeType());

		//if is many
		if (eAttribute.isMany()) 
		{
			@SuppressWarnings("unchecked")
			//feature values
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);

			//clear since this is set
			featureValuesList.clear();
			
			//if typeID is complex
			if (primitiveTypeID == SimpleType.COMPLEX_TYPE) 
			{
				for (String str : featureValuesArray) 
				{
					featureValuesList.add(EcoreUtil.createFromString(eAttribute.getEAttributeType(), str));
				}
			} 
			else // primitiveTypeID != PersistenceManager.COMPLEX_TYPE
			{
				for (String str : featureValuesArray) 
				{
					featureValuesList.add(convertStringToPrimitive(str, primitiveTypeID));
				}
			}
		} 
		else 
		{
			if (primitiveTypeID == SimpleType.COMPLEX_TYPE) 
			{
				focusObject.eSet(eAttribute, EcoreUtil.createFromString(eAttribute.getEAttributeType(), featureValuesArray[0]));
			} 
			else 
			{
				focusObject.eSet(eAttribute, convertStringToPrimitive(featureValuesArray[0], primitiveTypeID));
			}
		}
	}

	
	protected void handleAddToEAttribute(String line)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValuesArray = tokeniseString(getValueInSquareBrackets(line));

		addEAttributeValues(focusObject, eAttribute, featureValuesArray);
	}
	
	protected void addEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray )
	{
		//get typeID;
		int primitiveTypeID = getTypeID(eAttribute.getEAttributeType());

		//if is many
		if (eAttribute.isMany()) 
		{
			@SuppressWarnings("unchecked")
			//feature values
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);

			//if typeID is complex
			if (primitiveTypeID == SimpleType.COMPLEX_TYPE) 
			{
				for (String str : featureValuesArray) 
				{
					featureValuesList.add(EcoreUtil.createFromString(eAttribute.getEAttributeType(), str));
				}
			} 
			else // primitiveTypeID != PersistenceManager.COMPLEX_TYPE
			{
				for (String str : featureValuesArray) 
				{
					featureValuesList.add(convertStringToPrimitive(str, primitiveTypeID));
				}
			}
		} 
		else 
		{
			System.err.println("adding eattribute values to non-collection: use SetEAttributeEvent");
			//should not happen
		}
	}
	
	protected void handleRemoveFromEAttribute(String line)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValuesArray = tokeniseString(getValueInSquareBrackets(line));

		RemoveEAttributeValues(focusObject, eAttribute, featureValuesArray);
	}
	
	protected void RemoveEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray )
	{
		int primitiveTypeID = getTypeID(eAttribute.getEAttributeType());

		if (eAttribute.isMany()) 
		{
			@SuppressWarnings("unchecked")
			EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);
			
			if (primitiveTypeID == SimpleType.COMPLEX_TYPE) 
			{
				for (String str : featureValuesArray) 
				{
					featureValuesList.remove(EcoreUtil.createFromString(eAttribute.getEAttributeType(), str));
				}
			} 
			else // primitiveTypeID != PersistenceManager.COMPLEX_TYPE
			{
				for (String str : featureValuesArray) 
				{
					featureValuesList.remove(convertStringToPrimitive(str, primitiveTypeID));
				}
			}
		} 
		else 
		{
			focusObject.eUnset(eAttribute);
		}
	}

	protected void handleSetEReference(String line)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValueStringsArray = tokeniseString(getValueInSquareBrackets(line));
		
		setEReferenceValues(focusObject, eReference,featureValueStringsArray);

	}
	
	protected void setEReferenceValues(EObject focusObject, EReference eReference, String[] featureValueStringsArray)
	{
		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);

			featureValuesList.clear();
			for (String str : featureValueStringsArray) {
				featureValuesList.add(IDToEObjectMap.get(Integer.valueOf(str)));
			}
		} else {
			focusObject.eSet(eReference, IDToEObjectMap.get(Integer.valueOf(featureValueStringsArray[0])));
		}
	}
	
	protected void handleCreateAndSetEReference(String line) 
	{
		//split line
		String[] stringArray = line.split(" ");

		//create focus object
		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		//get ereference
		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		//get values
		String[] refValueStringsArray = tokeniseString(getValueInSquareBrackets(line));

		//prepare objectToAddList
		List<EObject> eObjectToAddList = new ArrayList<EObject>();

		for (String str : refValueStringsArray) {
			//split string
			String[] temp = str.split(" ");

			//create obj
			EObject obj = createEObject(ePackageElementsNamesMap.getName(Integer.valueOf(temp[0])));

			//get id
			int id = Integer.valueOf(temp[1]);

			//add obj to change log
			context.addObjectToMap(obj, id);

			//put to IdToObjectMap
			IDToEObjectMap.put(id, obj);

			//add to objectToAddList
			eObjectToAddList.add(obj);
		}

		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);

			//clear because this is set
			featureValuesList.clear();
			
			for (EObject obj : eObjectToAddList) {
				featureValuesList.add(obj);
			}
		} else {
			focusObject.eSet(eReference, eObjectToAddList.get(0));
		}
	}
	
	protected void handleCreateAndAddToEReference(String line) 
	{
		//split line
		String[] stringArray = line.split(" ");

		//create focus object
		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		//get ereference
		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		//get values
		String[] refValueStringsArray = tokeniseString(getValueInSquareBrackets(line));

		//prepare objectToAddList
		List<EObject> eObjectToAddList = new ArrayList<EObject>();

		for (String str : refValueStringsArray) {
			//split string
			String[] temp = str.split(" ");

			//create obj
			EObject obj = createEObject(ePackageElementsNamesMap.getName(Integer.valueOf(temp[0])));

			//get id
			int id = Integer.valueOf(temp[1]);

			//add obj to change log
			context.addObjectToMap(obj, id);

			//put to IdToObjectMap
			IDToEObjectMap.put(id, obj);

			//add to objectToAddList
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
			//should not happen
		}
	}
	
	protected void handleAddToEReference(String line) 
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] refValueStringsArray = tokeniseString(getValueInSquareBrackets(line));

		List<EObject> eObjectToAddList = new ArrayList<EObject>();

		for (String str : refValueStringsArray) {
			String[] temp = str.split(" ");

			int id = Integer.valueOf(temp[1]);
			
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
			//this should not happen
			//focusObject.eSet(eReference, eObjectToAddList.get(0));
		}
	}

	protected void handleRemoveFromEReference(String line)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValueStringsArray = tokeniseString(getValueInSquareBrackets(line));
		
		removeEReferenceValues(focusObject, eReference,featureValueStringsArray);
	}

	
	protected void removeEReferenceValues(EObject focusObject, EReference eReference, String[] featureValueStringsArray)
	{
		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);

			for (String str : featureValueStringsArray) {
				featureValuesList.remove(IDToEObjectMap.get(Integer.valueOf(str)));
			}
		} else {
			focusObject.eUnset(eReference);
		}
	}
	
}
