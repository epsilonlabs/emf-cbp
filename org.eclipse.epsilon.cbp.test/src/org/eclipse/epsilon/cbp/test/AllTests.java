package org.eclipse.epsilon.cbp.test;

import org.eclipse.epsilon.cbp.test.equivalence.EcoreEquivalenceTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ResourceContentsToEventsConverterTests.class, EventAdapterTests.class, ResourceContentsToEventsConverterTests.class,
		CBPTextResourceTests.class, CBPBinaryResourceTests.class,
		EcoreEquivalenceTests.class})
public class AllTests {

}
