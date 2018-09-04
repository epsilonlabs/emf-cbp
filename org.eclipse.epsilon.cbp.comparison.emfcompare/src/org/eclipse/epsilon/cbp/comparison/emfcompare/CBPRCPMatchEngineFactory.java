package org.eclipse.epsilon.cbp.comparison.emfcompare;

import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.rcp.EMFCompareRCPPlugin;
import org.eclipse.emf.compare.rcp.internal.match.DefaultRCPMatchEngineFactory;
import org.eclipse.emf.compare.utils.UseIdentifiers;

public class CBPRCPMatchEngineFactory extends DefaultRCPMatchEngineFactory {

    public CBPRCPMatchEngineFactory() {
	super();
    }

    @Override
    public IMatchEngine getMatchEngine() {
	// final UseIdentifiers useUdentifier = getUseIdentifierValue();
	final UseIdentifiers useUdentifier = UseIdentifiers.ONLY;

	// IMatchEngine matchEngine = CBPMatchEngine.create(useUdentifier);
	IMatchEngine matchEngine = CBPMatchEngine.create(useUdentifier, EMFCompareRCPPlugin.getDefault().getWeightProviderRegistry());

	return matchEngine;
    }

}
