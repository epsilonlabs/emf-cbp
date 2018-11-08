package org.eclipse.epsilon.cbp.comparison;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

public interface ICBPObjectTreePostProcessor {

    public void process();

    public void process(Map<String, CBPMatchObject> objects);

}
