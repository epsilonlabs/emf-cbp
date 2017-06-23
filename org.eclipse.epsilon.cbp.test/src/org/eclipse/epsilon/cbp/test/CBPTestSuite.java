package org.eclipse.epsilon.cbp.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({
	BlogEquivalenceTests.class,
	EcoreEquivalenceTests.class,
	EcoreAppendTests.class,
	CBPResourceConverterTests.class,
	EcoreCrossReferenceTests.class
})

public class CBPTestSuite {

}
