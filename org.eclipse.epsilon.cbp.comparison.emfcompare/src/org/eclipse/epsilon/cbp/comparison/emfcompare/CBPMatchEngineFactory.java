package org.eclipse.epsilon.cbp.comparison.emfcompare;

import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.gmt.modisco.xml.emf.MoDiscoXMLPackage;

public class CBPMatchEngineFactory extends MatchEngineFactoryImpl {

    public CBPMatchEngineFactory() {
	super();
    }

    @Override
    public IMatchEngine getMatchEngine() {
//	final UseIdentifiers useUdentifier = UseIdentifiers.WHEN_AVAILABLE;
	final UseIdentifiers useUdentifier = UseIdentifiers.ONLY;
	IMatchEngine matchEngine = CBPMatchEngine.create(useUdentifier);
	return matchEngine;
    }

}
