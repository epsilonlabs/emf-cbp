package org.eclipse.epsilon.cbp.comparison.emfcompare;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.compare.EMFCompareMessages;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.rcp.EMFCompareRCPPlugin;
import org.eclipse.emf.compare.rcp.internal.match.DefaultRCPMatchEngineFactory;
import org.eclipse.emf.compare.utils.UseIdentifiers;

public class CBPMatchEngineFactory extends DefaultRCPMatchEngineFactory {
	
	public CBPMatchEngineFactory() {
		super();
	}
	
	@Override
	public IMatchEngine getMatchEngine() {
//		final UseIdentifiers useUdentifier = getUseIdentifierValue();
		final UseIdentifiers useUdentifier = UseIdentifiers.ONLY;
		IMatchEngine matchEngine = CBPMatchEngine.create(useUdentifier,
				EMFCompareRCPPlugin.getDefault().getWeightProviderRegistry());
		
		return matchEngine;
	}
	
	
}

