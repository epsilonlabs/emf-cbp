package org.eclipse.epsilon.cbp.test;

import org.eclipse.epsilon.cbp.test.equivalence.EcoreEquivalenceTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ChangeLogTest.class, EventAdapterTest.class, ResourceContentsToEventsConverterTest.class,
		TextSerialiserTest.class, TextDeserialiserTest.class, VerboseTextDeserialiserTest.class, BinaryDeserialiserTest.class,
		EcoreEquivalenceTests.class})
public class AllTests {

}
