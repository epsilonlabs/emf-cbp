package org.eclipse.epsilon.cbp.comparison;

import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.ecore.resource.Resource;

public interface ICBPEObjectMatcher extends IEObjectMatcher {

	public void createMatches(Comparison comparison, Resource left, Resource right, Monitor monitor);
}
