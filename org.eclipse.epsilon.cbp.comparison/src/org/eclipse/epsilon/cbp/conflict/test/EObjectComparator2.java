package org.eclipse.epsilon.cbp.conflict.test;

import java.util.Comparator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.epsilon.cbp.comparison.CBPDiff.CBPDifferenceKind;

public class EObjectComparator2 implements Comparator<EObject> {

    @Override
    public int compare(EObject left, EObject right) {
	EStructuralFeature leftNameFeature = left.eClass().getEStructuralFeature("name");
	EStructuralFeature rightNameFeature = right.eClass().getEStructuralFeature("name");
	String leftName = (String) left.eGet(leftNameFeature);
	String rightName = (String) right.eGet(rightNameFeature);
	return leftName.compareTo(rightName);
    }
}
