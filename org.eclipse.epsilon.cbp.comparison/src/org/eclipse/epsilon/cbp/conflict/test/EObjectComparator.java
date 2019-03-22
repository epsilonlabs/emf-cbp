package org.eclipse.epsilon.cbp.conflict.test;

import java.util.Comparator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;

public class EObjectComparator implements Comparator<EObject> {

    @Override
    public int compare(EObject left, EObject right) {
	String leftName = (String) left.eResource().getURIFragment(left);
	String rightName = (String) left.eResource().getURIFragment(right);

	// System.out.println(leftName + " <--> " + rightName);
	// if ((leftName.equals("L-0") && rightName.equals("R-0")) ||
	// (leftName.equals("R-0") && rightName.equals("L-0"))) {
	// System.console();
	// }
//	if (leftName.compareTo(rightName) < 0) {
//	    return -1;
//	} else if (leftName.compareTo(rightName) > 0) {
//	    return 1;
//	} else {
//	    return 0;
//	}
	
	return leftName.compareTo(rightName);
    }
}
