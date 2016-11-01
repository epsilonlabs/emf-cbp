
package org.eclipse.epsilon.cbp.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.context.PersistenceManager;
import org.eclipse.epsilon.cbp.exceptions.UnknownPackageException;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.eclipse.epsilon.cbp.util.SimpleType;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class CBPTextDeserialiser {
	
	//epackage
	private EPackage ePackage = null;
	
	//change log
	private final Changelog changelog;

	//id to eobject
	private final TIntObjectMap<EObject> IDToEObjectMap = new TIntObjectHashMap<EObject>();
	
	//common simple type map (such a bad name)
	private final TObjectIntMap<String> commonsimpleTypeNameMap;
	
	//text simple type name map (again, bad name)
	private final TObjectIntMap<String> textSimpleTypeNameMap;

	//persistence manager
	private PersistenceManager manager;
	
	//model-element id map
	private final ModelElementIDMap ePackageElementsNamesMap;

	public CBPTextDeserialiser(PersistenceManager manager, Changelog aChangelog,
			ModelElementIDMap ePackageElementsNamesMap) {
		this.manager = manager;
		this.changelog = aChangelog;
		this.ePackageElementsNamesMap = ePackageElementsNamesMap;

		this.commonsimpleTypeNameMap = manager.getCommonSimpleTypesMap();
		this.textSimpleTypeNameMap = manager.getTextSimpleTypesMap();
	}

	
	public void load(Map<?, ?> options) throws Exception {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(manager.getURI().path()), manager.STRING_ENCODING));

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
		manager.setResume(true);
	}
	
	/*
	 * event has the format of:
	 * 0 [(MetaElementTypeID objectID)* (,)*]
	 */
	private void handleCreateAndAddToResource(String line) {
		
		//break line into tokens
		String[] objToCreateAndAddArray = tokeniseString(getValueInSquareBrackets(line));

		for (String str : objToCreateAndAddArray) {
			
			
			String[] stringArray = str.split(" ");
			
			//create eobject with the first token
			EObject obj = createEObject(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[0])));

			//get the object ID with the second token
			int id = Integer.valueOf(stringArray[1]);

			//add object to change log
			changelog.addObjectToMap(obj, id);
			
			//put to IDToEObjectMap
			IDToEObjectMap.put(id, obj);

			// add to resource contents
			manager.addEObjectToContents(obj); 
		}
	}
	
	/*
	 * event format:
	 * 2 [EObjectID*]
	 */

	private void handleRemoveFromResource(String line) {
		//tokenise string
		String[] objValueStringsArray = tokeniseString(getValueInSquareBrackets(line));

		//for each string, get EBoject and remove from contents
		for (String str : objValueStringsArray) {
			manager.removeEObjectFromContents(IDToEObjectMap.get(Integer.valueOf(str)));
		}
	}
	
	/*
	 * event format:
	 * 3/4 objectID EAttributeID [value*]
	 */

	private void handleSetEAttribute(String line)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValuesArray = tokeniseString(getValueInSquareBrackets(line));

		setEAttributeValues(focusObject, eAttribute, featureValuesArray);
	}

	private void setEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray )
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

	
	private void handleAddToEAttribute(String line)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValuesArray = tokeniseString(getValueInSquareBrackets(line));

		addEAttributeValues(focusObject, eAttribute, featureValuesArray);
	}
	
	private void addEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray )
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
	
	private void handleRemoveFromEAttribute(String line)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValuesArray = tokeniseString(getValueInSquareBrackets(line));

		RemoveEAttributeValues(focusObject, eAttribute, featureValuesArray);
	}
	
	private void RemoveEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray )
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

	private void handleSetEReference(String line)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValueStringsArray = tokeniseString(getValueInSquareBrackets(line));
		
		setEReferenceValues(focusObject, eReference,featureValueStringsArray);

	}
	
	private void setEReferenceValues(EObject focusObject, EReference eReference, String[] featureValueStringsArray)
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
	
	private void handleCreateAndSetEReference(String line) 
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
			changelog.addObjectToMap(obj, id);

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
	
	private void handleCreateAndAddToEReference(String line) 
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
			changelog.addObjectToMap(obj, id);

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
	
	private void handleAddToEReference(String line) 
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

	private void handleRemoveFromEReference(String line)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValueStringsArray = tokeniseString(getValueInSquareBrackets(line));
		
		removeEReferenceValues(focusObject, eReference,featureValueStringsArray);
	}

	
	private void removeEReferenceValues(EObject focusObject, EReference eReference, String[] featureValueStringsArray)
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
	
	
	

	private int getTypeID(EDataType type) 
	{
		if(commonsimpleTypeNameMap.containsKey(type.getName()))
    	{
			return commonsimpleTypeNameMap.get(type.getName());
    	}
		else if(textSimpleTypeNameMap.containsKey(type.getName()))
		{
			return textSimpleTypeNameMap.get(type.getName());
		}
    	
    	return SimpleType.COMPLEX_TYPE;
	}

	

	private EPackage loadMetamodel(String metamodelURI) throws UnknownPackageException {
		EPackage ePackage = null;

		if (EPackage.Registry.INSTANCE.containsKey(metamodelURI))
			ePackage = EPackage.Registry.INSTANCE.getEPackage(metamodelURI);

		else
			throw new UnknownPackageException(metamodelURI);

		return ePackage;
	}

	private EObject createEObject(String eClassName)
	{
		return ePackage.getEFactoryInstance().create((EClass) ePackage.getEClassifier(eClassName));
	}

	/*
	 * Tokenises a string seperated by a specified delimiter
	 * http://stackoverflow.com/questions/18677762/handling-delimiter-with-
	 * escape- -in-java-string-split-method
	 */
	private String[] tokeniseString(String input) {
		String regex = "(?<!" + Pattern.quote(PersistenceManager.ESCAPE_CHAR) + ")"
				+ Pattern.quote(PersistenceManager.DELIMITER);

		String[] output = input.split(regex);

		for (int i = 0; i < output.length; i++) {
			output[i] = output[i].replace(PersistenceManager.ESCAPE_CHAR + PersistenceManager.DELIMITER,
					PersistenceManager.DELIMITER);
		}

		return output;
	}

	// returns everything inbetween []
	private String getValueInSquareBrackets(String str) {
		Pattern p = Pattern.compile("\\[(.*?)\\]");
		Matcher m = p.matcher(str);

		String result = "";

		if (m.find())
			result = m.group(1);
		return result;
	}

	private Object convertStringToPrimitive(String str, int primitiveTypeID) {
		switch (primitiveTypeID) {
		case SimpleType.SIMPLE_TYPE_INT:
			return Integer.valueOf(str);
		case SimpleType.SIMPLE_TYPE_SHORT:
			return Short.valueOf(str);
		case SimpleType.SIMPLE_TYPE_LONG:
			return Long.valueOf(str);
		case SimpleType.SIMPLE_TYPE_FLOAT:
			return Float.valueOf(str);
		case SimpleType.SIMPLE_TYPE_DOUBLE:
			return Double.valueOf(str);
		case SimpleType.SIMPLE_TYPE_CHAR:
			return str.charAt(0);
		case SimpleType.SIMPLE_TYPE_BOOLEAN:
			return Boolean.valueOf(str);
		}
		return str;
	}
}
