package org.eclipse.epsilon.cbp.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.cbp.driver.EPackageElementsNamesMap;

public abstract class CBPResource extends ResourceImpl
{


	
	public CBPResource(URI uri)
	{
		super(uri);
	}
	
	public CBPResource()
	{
		
	}
	
	public EPackageElementsNamesMap populateEPackageElementNamesMap(EPackage ePackage)
	{
		EPackageElementsNamesMap ePackageElementsNamesMap = new EPackageElementsNamesMap();
		
	    for(EClassifier eClassifier : ePackage.getEClassifiers())
	    {
	        if(eClassifier instanceof EClass)
	        {
	            EClass eClass = (EClass) eClassifier;
	            
	            ePackageElementsNamesMap.addName(eClass.getName());
	            
	            for(EStructuralFeature feature : eClass.getEAllStructuralFeatures())
	            {
	                ePackageElementsNamesMap.addName(feature.getName());
	            }
	        }
	    }
	    
	    return ePackageElementsNamesMap;
	}
	
}
