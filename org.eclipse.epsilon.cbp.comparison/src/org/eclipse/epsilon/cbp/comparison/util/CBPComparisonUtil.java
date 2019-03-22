package org.eclipse.epsilon.cbp.comparison.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class CBPComparisonUtil {

    public static void sort(Resource resource) {
	EList<EObject> eObjects = resource.getContents();
	doDeepFirstSort(eObjects);
    }

    @SuppressWarnings("unchecked")
    public static void doDeepFirstSort(EObject eObject, EStructuralFeature eFeature) {
	if (eFeature.isMany() && eFeature.isChangeable()) {
	    if (eFeature instanceof EReference) {
		EList<EObject> list = (EList<EObject>) eObject.eGet(eFeature);
		if (((EReference) eFeature).isContainment()) {
		    doDeepFirstSort(list);
		    doSort(list);
		} else {
		    doSort(list);
		}
	    } else if (eFeature instanceof EAttribute) {
		List<Object> list = (List<Object>) eObject.eGet(eFeature);
		doSort(list);
	    }
	} else if (!eFeature.isMany() && eFeature.isChangeable()) {
	    if (eFeature instanceof EReference && ((EReference) eFeature).isContainment()) {
		EObject eVal = (EObject) eObject.eGet(eFeature);
		if (eVal != null) {
		    EList<EObject> list = ECollections.newBasicEList();
		    list.add(eVal);
		    doDeepFirstSort(list);
		}
	    }
	}
    }

    public static void doDeepFirstSort(EList<EObject> list) {
	for (EObject eObject : list) {
	    for (EStructuralFeature eFeature : eObject.eClass().getEAllStructuralFeatures()) {
		doDeepFirstSort(eObject, eFeature);
	    }
	}
    }

    public static void doSort(EList<EObject> list) {
	// remove eObjects that don't have resource
	Iterator<EObject> iterator = list.iterator();
	while (iterator.hasNext()) {
	    EObject eObject = iterator.next();
	    if (eObject.eResource() == null) {
		iterator.remove();
	    }
	}

	// sort
	if (list != null && list.size() >= 2) {
	    for (int i = 0; i < list.size() - 1; i++) {
		for (int pos = 1; pos < list.size(); pos++) {
		    EObject eObject1 = list.get(pos - 1);
		    if (eObject1.eResource() == null) {
			continue;
		    }
		    String left = eObject1.eResource().getURIFragment(eObject1);

		    EObject eObject2 = list.get(pos);
		    if (eObject2.eResource() == null) {
			continue;
		    }
		    String right = eObject2.eResource().getURIFragment(eObject2);
		    if (right.compareTo(left) < 0) {
			list.move(pos - 1, pos);
		    }
		}
	    }
	}
	// // print
	// System.out.println("--------");
	// for (int i = 0; i < list.size(); i++) {
	// EObject eObject = list.get(i);
	// String left = eObject.eResource().getURIFragment(eObject);
	// System.out.println(i + ": " + left);
	// }
    }

    public static void doSort(List<Object> list) {
	// sort
	if (list != null && list.size() >= 2) {
	    for (int i = 0; i < list.size() - 1; i++) {
		for (int pos = 1; pos < list.size(); pos++) {
		    Object eObject1 = list.get(pos - 1);
		    String left = String.valueOf(eObject1);
		    Object eObject2 = list.get(pos);
		    String right = String.valueOf(eObject2);
		    if (right.compareTo(left) < 0) {
			Object temp = list.remove(pos);
			list.add(pos - 1, temp);
		    }
		}
	    }
	}
	// // print
	// // System.out.println("--------");
	// for (int i = 0; i < list.size(); i++) {
	// EObject eObject = list.get(i);
	// String left = eObject.eResource().getURIFragment(eObject);
	// System.out.println(i + ": " + left);
	// }
    }
}
