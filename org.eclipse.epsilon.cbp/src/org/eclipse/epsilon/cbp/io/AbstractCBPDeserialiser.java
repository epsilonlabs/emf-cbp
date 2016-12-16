package org.eclipse.epsilon.cbp.io;

import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.impl.CBPResource;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.PersistenceUtil;
import org.eclipse.epsilon.cbp.util.SimpleType;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public abstract class AbstractCBPDeserialiser {
	
	//epackage

	protected HashSet<EPackage> ePackages = new HashSet<EPackage>();
	
	protected EList<EObject> contents;

	//id to eobject
	protected TIntObjectMap<EObject> IDToEObjectMap = new TIntObjectHashMap<EObject>();
	
	//common simple type map (such a bad name)
	protected TObjectIntMap<String> commonsimpleTypeNameMap;
	
	//text simple type name map (again, bad name)
	protected TObjectIntMap<String> textSimpleTypeNameMap;

	//model-element id map
	protected ModelElementIDMap ePackageElementsNamesMap;
	
	protected CBPResource resource = null;
	
	protected PersistenceUtil persistenceUtil = PersistenceUtil.getInstance();

	
	public abstract void deserialise(Map<?, ?> options) throws Exception;
	
	public Resource getResource() {
		return resource;
	}
	
	protected abstract void handleRegisterEPackage(String line);

	protected abstract void handleCreateAndAddToResource(String line);
	protected abstract void handleRemoveFromResource(String line);
	
	protected abstract void handleSetEAttribute(String line);
	protected abstract void setEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray);
	protected abstract void handleAddToEAttribute(String line);
	protected abstract void addEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray);
	
	protected abstract void handleRemoveFromEAttribute(String line);
	protected abstract void RemoveEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray );
	
	
	protected abstract void handleSetEReference(String line);
	protected abstract void setEReferenceValues(EObject focusObject, EReference eReference, String[] featureValueStringsArray);
	protected abstract void handleCreateAndSetEReference(String line); 
	protected abstract void handleCreateAndAddToEReference(String line) ;
	protected abstract void handleAddToEReference(String line) ;
	protected abstract void handleRemoveFromEReference(String line);
	protected abstract void removeEReferenceValues(EObject focusObject, EReference eReference, String[] featureValueStringsArray);
	
	protected int getTypeID(EDataType type) 
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

	protected EObject createEObject(String name)
	{
		String[] tokens = name.split("-");
		
		EPackage ePackage = getEPackage(tokens[0]);
		
		return ePackage.getEFactoryInstance().create((EClass) ePackage.getEClassifier(tokens[1]));
	}
	
	protected EPackage getEPackage(String name)
	{
		for(EPackage ep: ePackages)
		{
			if (ep.getName().equals(name)) {
				return ep;
			}
		}
		return null;
	}

	/*
	 * Tokenises a string seperated by a specified delimiter
	 * http://stackoverflow.com/questions/18677762/handling-delimiter-with-
	 * escape- -in-java-string-split-method
	 */
	protected String[] tokeniseString(String input) {
		String regex = "(?<!" + Pattern.quote(persistenceUtil.ESCAPE_CHAR) + ")"
				+ Pattern.quote(persistenceUtil.DELIMITER);

		String[] output = input.split(regex);

		for (int i = 0; i < output.length; i++) {
			output[i] = output[i].replace(persistenceUtil.ESCAPE_CHAR + persistenceUtil.DELIMITER,
					persistenceUtil.DELIMITER);
		}

		return output;
	}

	// returns everything inbetween []
	protected String getValueInSquareBrackets(String str) {
		Pattern p = Pattern.compile("\\[(.*?)\\]");
		Matcher m = p.matcher(str);

		String result = "";

		if (m.find())
			result = m.group(1);
		return result;
	}

	protected Object convertStringToPrimitive(String str, int primitiveTypeID) {
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
