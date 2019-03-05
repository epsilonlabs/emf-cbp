package org.eclipse.epsilon.cbp.comparison;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.epsilon.cbp.comparison.CBPMatchObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEStructuralFeatureEvent;

public class UMLObjectTreePostProcessor implements ICBPObjectTreePostProcessor {

    private EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/uml2/5.0.0/UML");

    @Override
    public void process() {
	// TODO Auto-generated method stub

    }

    @Override
    public void process(Map<String, CBPMatchObject> objects) {
	Iterator<Entry<String, CBPMatchObject>> objectIterator = objects.entrySet().iterator();
	while (objectIterator.hasNext()) {
	    Entry<String, CBPMatchObject> objectEntry = objectIterator.next();
	    CBPMatchObject object = objectEntry.getValue();

	    
	    
	    // ------
	    CBPMatchFeature feature = object.getFeatures().get("memberEnd");
	    if (feature != null) {
		Iterator<Entry<Integer, Object>> valueIterator = feature.getLeftValues().entrySet().iterator();
		while (valueIterator.hasNext()) {
		    Entry<Integer, Object> valueEntry = valueIterator.next();
		    int position = valueEntry.getKey();
		    Object leftValue = valueEntry.getValue();
		    Object rightValue = feature.getRightValues().get(position);

		    // handle left value
		    if (leftValue instanceof CBPMatchObject) {
			CBPMatchObject objectValue = (CBPMatchObject) leftValue;
			CBPMatchFeature oppositeFeature = objectValue.getFeatures().get("association");
			if (oppositeFeature == null) {
			    oppositeFeature = createFeature(objectValue, "Property", "association");
			}
			oppositeFeature.setValue(object, CBPSide.LEFT);
		    }

		    // handle right value
		    if (rightValue instanceof CBPMatchObject) {
			CBPMatchObject objectValue = (CBPMatchObject) rightValue;
			CBPMatchFeature oppositeFeature = objectValue.getFeatures().get("association");
			if (oppositeFeature == null) {
			    oppositeFeature = createFeature(objectValue, "Property", "association");
			}
			oppositeFeature.setValue(object, CBPSide.RIGHT);
		    }
		}
	    }

//	    if (object.getClassName().equals("Property")) {
//		feature = object.getFeatures().get("type");
//		if (feature == null) {
//		    createFeature(object, "Property", "type");
//		}
//	    }
	}
    }

    private CBPMatchFeature createFeature(CBPMatchObject targetObject, String eClassName, String featureName) {
	// String featureName = "association";
	// String eClassName = "Property";

	CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
	boolean isContainment = false;
	boolean isMany = false;
	boolean isUnique = false;
	boolean isOrdered = true;

	EClass eClass = (EClass) ePackage.getEClassifier(eClassName);
	EStructuralFeature eFeature = eClass.getEStructuralFeature(featureName);
	if (eFeature.isMany()) {
	    isMany = true;
	}
	isUnique = eFeature.isUnique();
	isOrdered = eFeature.isOrdered();
	if (eFeature instanceof EReference) {
	    featureType = CBPFeatureType.REFERENCE;
	    if (((EReference) eFeature).isContainment()) {
		isContainment = true;
	    }
	}

	CBPMatchFeature feature = targetObject.getFeatures().get(featureName);
	if (feature == null) {
	    feature = new CBPMatchFeature(targetObject, featureName, featureType, isContainment, isMany, isUnique, isOrdered);
	    targetObject.getFeatures().put(featureName, feature);
	}
	return feature;
    }

}
