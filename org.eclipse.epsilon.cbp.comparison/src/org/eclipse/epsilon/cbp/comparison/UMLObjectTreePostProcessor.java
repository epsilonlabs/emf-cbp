package org.eclipse.epsilon.cbp.comparison;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.epsilon.cbp.comparison.CBPObject.CBPSide;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.comparison.event.CBPEStructuralFeatureEvent;

public class UMLObjectTreePostProcessor implements ICBPObjectTreePostProcessor {

    private EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/uml2/5.0.0/UML");

    @Override
    public void process() {
	// TODO Auto-generated method stub

    }

    @Override
    public void process(Map<String, CBPObject> objects) {
	Iterator<Entry<String, CBPObject>> objectIterator = objects.entrySet().iterator();
	while (objectIterator.hasNext()) {
	    Entry<String, CBPObject> objectEntry = objectIterator.next();
	    CBPObject object = objectEntry.getValue();

	    CBPFeature feature = object.getFeatures().get("memberEnd");
	    if (feature == null) {
		continue;
	    }

	    Iterator<Entry<Integer, Object>> valueIterator = feature.getLeftValues().entrySet().iterator();
	    while (valueIterator.hasNext()) {
		Entry<Integer, Object> valueEntry = valueIterator.next();
		int position = valueEntry.getKey();
		Object leftValue = valueEntry.getValue();
		Object rightValue = feature.getRightValues().get(position);

		// handle left value
		if (leftValue instanceof CBPObject) {
		    CBPObject objectValue = (CBPObject) leftValue;
		    CBPFeature oppositeFeature = objectValue.getFeatures().get("association");
		    if (oppositeFeature == null) {
			oppositeFeature = createAssociationFeature(objectValue);
		    }
		    oppositeFeature.setValue(object, CBPSide.LEFT);
		}

		// handle right value
		if (rightValue instanceof CBPObject) {
		    CBPObject objectValue = (CBPObject) rightValue;
		    CBPFeature oppositeFeature = objectValue.getFeatures().get("association");
		    if (oppositeFeature == null) {
			oppositeFeature = createAssociationFeature(objectValue);
		    }
		    oppositeFeature.setValue(object, CBPSide.RIGHT);
		}
	    }
	}
    }

    private CBPFeature createAssociationFeature(CBPObject targetObject) {
	String featureName = "association";
	String eClassName = "Property";

	CBPFeatureType featureType = CBPFeatureType.ATTRIBUTE;
	boolean isContainment = false;
	boolean isMany = false;

	EClass eClass = (EClass) ePackage.getEClassifier(eClassName);
	EStructuralFeature eFeature = eClass.getEStructuralFeature(featureName);
	if (eFeature.isMany()) {
	    isMany = true;
	}
	if (eFeature instanceof EReference) {
	    featureType = CBPFeatureType.REFERENCE;
	    if (((EReference) eFeature).isContainment()) {
		isContainment = true;
	    }
	}

	CBPFeature feature = targetObject.getFeatures().get(featureName);
	if (feature == null) {
	    feature = new CBPFeature(targetObject, featureName, featureType, isContainment, isMany);
	    targetObject.getFeatures().put(featureName, feature);
	}
	return feature;
    }

}
