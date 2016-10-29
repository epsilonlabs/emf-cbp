
package org.eclipse.epsilon.cbp.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
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

import gnu.trove.map.TObjectIntMap;

public class CBPTextDeserializer {
	private final String classname = this.getClass().getSimpleName();
	private EPackage ePackage = null;
	private final Changelog changelog;

	private final HashMap<Integer, EObject> IDToEObjectMap = new HashMap<Integer, EObject>();
	
	private final TObjectIntMap<String> commonsimpleTypeNameMap;
	
	private final TObjectIntMap<String> textSimpleTypeNameMap;

	private PersistenceManager manager;
	private final ModelElementIDMap ePackageElementsNamesMap;

	public CBPTextDeserializer(PersistenceManager manager, Changelog aChangelog,
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
			System.out.println(classname + " Error, file empty");
			System.exit(0);
		}

		while ((line = br.readLine()) != null) {
			// System.out.println(line);
			StringTokenizer st = new StringTokenizer(line);

			int eventType = -1;

			if (st.hasMoreElements())
				eventType = Integer.valueOf(st.nextToken());

			/*
			 * Switches over various event types, calls appropriate handler
			 * method
			 */
			switch (eventType) {
			case SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE:
				createAndAddEObjectsToResource(line);
				break;
			case SerialisationEventType.CREATE_AND_SET_EREFERENCE:
				createEObjectsAndSetEReferenceValues(line);
				break;
			case SerialisationEventType.SET_EATTRIBUTE_COMPLEX:
				handleEAttributeEvent(line,true);
				break;
			case SerialisationEventType.SET_EREFERENCE:
				handleEReferenceEvent(line,true);
				break;
			case SerialisationEventType.UNSET_EATTRIBUTE_COMPLEX:
				handleEAttributeEvent(line,false);
				break;
			case SerialisationEventType.UNSET_EREFERENCE:
				handleEReferenceEvent(line,false);
				break;
			case SerialisationEventType.ADD_TO_RESOURCE:
				createAndAddEObjectsToResource(line);
				break;
			case SerialisationEventType.REMOVE_FROM_RESOURCE:
				removeEObjectsFromResource(line);
				break;
			default:
				break;
			}
		}
		br.close();
		manager.setResume(true);
	}
	private void setEReferenceValues(EObject focusObject, EReference eReference, String[] featureValueStringsArray)
	{
		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);

			for (String str : featureValueStringsArray) {
				featureValuesList.add(IDToEObjectMap.get(Integer.valueOf(str)));
			}
		} else {
			focusObject.eSet(eReference, IDToEObjectMap.get(Integer.valueOf(featureValueStringsArray[0])));
		}
	}
	
	private void unsetEReferenceValues(EObject focusObject, EReference eReference, String[] featureValueStringsArray)
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
	
	private void handleEReferenceEvent(String line, boolean isSetEReference)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValueStringsArray = tokeniseString(getValueInSquareBrackets(line));
		
		if(isSetEReference)
			setEReferenceValues(focusObject, eReference,featureValueStringsArray);
		
		else
			unsetEReferenceValues(focusObject, eReference,featureValueStringsArray);

	}
	private void setEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray )
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
					if (str.equals(manager.NULL_STRING))
						featureValuesList.add(null);
					else
						featureValuesList.add(EcoreUtil.createFromString(eAttribute.getEAttributeType(), str));
				}
			} 
			else // primitiveTypeID != PersistenceManager.COMPLEX_TYPE
			{
				for (String str : featureValuesArray) 
				{
					if (str.equals(manager.NULL_STRING))
						featureValuesList.add(null);
					else
						featureValuesList.add(convertStringToPrimitive(str, primitiveTypeID));
				}
			}
		} 
		else 
		{
			
			if (featureValuesArray[0].equals(manager.NULL_STRING)) 
			{
				focusObject.eSet(eAttribute, null);
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
	}
	
	private void unsetEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray )
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
					if (str.equals(manager.NULL_STRING))
						featureValuesList.remove(null);

					else
						featureValuesList.remove(EcoreUtil.createFromString(eAttribute.getEAttributeType(), str));
				}
			} 
			else // primitiveTypeID != PersistenceManager.COMPLEX_TYPE
			{
				for (String str : featureValuesArray) 
				{
					if (str.equals(manager.NULL_STRING))
						featureValuesList.remove(null);

					else
						featureValuesList.remove(convertStringToPrimitive(str, primitiveTypeID));
				}
			}
		} 
		else 
		{
			focusObject.eUnset(eAttribute);
		}
	}
	
	private void handleEAttributeEvent(String line, boolean isSetEAttribute)
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EAttribute eAttribute = (EAttribute) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] featureValuesArray = tokeniseString(getValueInSquareBrackets(line));

		if(isSetEAttribute)
			setEAttributeValues(focusObject, eAttribute, featureValuesArray);
		
		else
			unsetEAttributeValues(focusObject, eAttribute, featureValuesArray);
	}

	
	private void createEObjectsAndSetEReferenceValues(String line) 
	{
		String[] stringArray = line.split(" ");

		EObject focusObject = IDToEObjectMap.get(Integer.valueOf(stringArray[1]));

		EReference eReference = (EReference) focusObject.eClass()
				.getEStructuralFeature(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[2])));

		String[] refValueStringsArray = tokeniseString(getValueInSquareBrackets(line));

		List<EObject> eObjectToAddList = new ArrayList<EObject>();

		for (String str : refValueStringsArray) {
			String[] temp = str.split(" ");

			EObject obj = createEObject(ePackageElementsNamesMap.getName(Integer.valueOf(temp[0])));

			int id = Integer.valueOf(temp[1]);

			changelog.addObjectToMap(obj, id);

			IDToEObjectMap.put(id, obj);

			eObjectToAddList.add(obj);
		}

		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);

			for (EObject obj : eObjectToAddList) {
				featureValuesList.add(obj);
			}
		} else {
			focusObject.eSet(eReference, eObjectToAddList.get(0));
		}
	}

	private void createAndAddEObjectsToResource(String line) {
		String[] objToCreateAndAddArray = tokeniseString(getValueInSquareBrackets(line));


		for (String str : objToCreateAndAddArray) {
			String[] stringArray = str.split(" ");
			
			EObject obj = createEObject(ePackageElementsNamesMap.getName(Integer.valueOf(stringArray[0])));

			int id = Integer.valueOf(stringArray[1]);

			changelog.addObjectToMap(obj, id);
			IDToEObjectMap.put(id, obj);

			manager.addEObjectToContents(obj); // add to resource contents
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

	private void removeEObjectsFromResource(String line) {
		String[] objValueStringsArray = tokeniseString(getValueInSquareBrackets(line));

		for (String str : objValueStringsArray) {
			manager.removeEObjectFromContents(IDToEObjectMap.get(Integer.valueOf(str)));
		}
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
