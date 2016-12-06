package org.eclipse.epsilon.cbp.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

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
	
	private PersistenceUtil()
	{
		
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
		ModelElementIDMap ePackageElementsNamesMap = new ModelElementIDMap();
		
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

}
