package org.eclipse.epsilon.cbp.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.cbp.util.EPackageElementsNamesMap;

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
