package org.eclipse.epsilon.cbp.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class PersistenceUtil {

	//delimiter
	public final String DELIMITER = ",";
	//escaped char
	public final String ESCAPE_CHAR ="+";
	//UTF-8 string encoding
	public final Charset STRING_ENCODING = StandardCharsets.UTF_8;
	
	//Null string I dont know what this is
	public final String NULL_STRING = "pFgrW";
	
	private static PersistenceUtil instance = null;
	
	ModelElementIDMap ePackageElementsNamesMap = null;
	
	protected TObjectIntMap<String> textSimpleTypesMap = new TObjectIntHashMap<String>(2);
	
	protected TObjectIntMap<String> commonSimpleTypesMap = new TObjectIntHashMap<String>(13);

	
	private PersistenceUtil()
	{
		populatecommonSimpleTypesMap();
		populateTextSimpleTypesMap();
	}
	
	public static PersistenceUtil getInstance()
	{
		if (instance == null) {
			instance = new PersistenceUtil();
		}
		return instance;
	}
	
	public ModelElementIDMap populateEPackageElementNamesMap(EPackage ePackage)
	{
		ePackageElementsNamesMap = new ModelElementIDMap();
		
		//for each eclassifier
	    for(EClassifier eClassifier : ePackage.getEClassifiers())
	    {
	    	//if is EClass
	        if(eClassifier instanceof EClass)
	        {
	            EClass eClass = (EClass) eClassifier;
	            
	            ePackageElementsNamesMap.addName(eClass.getName());
	            
	            for(EStructuralFeature feature : eClass.getEAllStructuralFeatures())
	            {
	                ePackageElementsNamesMap.addName(eClass.getName() + "-" + feature.getName());
	            }
	        }
	    }
	    return ePackageElementsNamesMap;
	}
	
	public ModelElementIDMap getePackageElementsNamesMap() {
		return ePackageElementsNamesMap;
	}
	
	public TObjectIntMap<String> getCommonSimpleTypesMap() {
		return commonSimpleTypesMap;
	}
	
	public TObjectIntMap<String> getTextSimpleTypesMap() {
		return textSimpleTypesMap;
	}
	
	private void populateTextSimpleTypesMap()
	{
    	textSimpleTypesMap.put("EString", SimpleType.TEXT_SIMPLE_TYPE_ESTRING);
    	textSimpleTypesMap.put("EStringObject", SimpleType.TEXT_SIMPLE_TYPE_ESTRING);
	}
	
	private void populatecommonSimpleTypesMap()
	{
		commonSimpleTypesMap.put("EInt", SimpleType.SIMPLE_TYPE_INT);
		commonSimpleTypesMap.put("EIntegerObject", SimpleType.SIMPLE_TYPE_INT);
		commonSimpleTypesMap.put("EBoolean", SimpleType.SIMPLE_TYPE_BOOLEAN);
		commonSimpleTypesMap.put("EBooleanObject", SimpleType.SIMPLE_TYPE_BOOLEAN);
		commonSimpleTypesMap.put("EFloat", SimpleType.SIMPLE_TYPE_FLOAT);
		commonSimpleTypesMap.put("EFloatObject", SimpleType.SIMPLE_TYPE_FLOAT);
		commonSimpleTypesMap.put("EDouble", SimpleType.SIMPLE_TYPE_DOUBLE);
		commonSimpleTypesMap.put("EDoubleObject", SimpleType.SIMPLE_TYPE_DOUBLE);
		commonSimpleTypesMap.put("EShort", SimpleType.SIMPLE_TYPE_SHORT);
		commonSimpleTypesMap.put("EShortObject", SimpleType.SIMPLE_TYPE_SHORT);
		commonSimpleTypesMap.put("ELong", SimpleType.SIMPLE_TYPE_LONG);
		commonSimpleTypesMap.put("ELongObject", SimpleType.SIMPLE_TYPE_LONG);
		commonSimpleTypesMap.put("EChar", SimpleType.SIMPLE_TYPE_CHAR);
	}


}
