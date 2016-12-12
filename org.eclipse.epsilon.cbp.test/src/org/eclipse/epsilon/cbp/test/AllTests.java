package org.eclipse.epsilon.cbp.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ChangeLogTest.class, EventAdapterTest.class, ResourceContentsToEventsConverterTest.class,
		TextSerialiserTest.class, TextDeserialiserTest.class })
public class AllTests {

}
