package org.eclipse.epsilon.cbp.comparison.emfcompare;

import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.conflict.DefaultConflictDetector;

public class CBPConflictsDetector extends DefaultConflictDetector {

	public CBPConflictsDetector() {
		super();
	}
	
	@Override
	public void detect(Comparison comparison, Monitor monitor) {
		super.detect(comparison, monitor);
	}
}
