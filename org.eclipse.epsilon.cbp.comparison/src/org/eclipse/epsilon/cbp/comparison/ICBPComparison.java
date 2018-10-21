package org.eclipse.epsilon.cbp.comparison;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

public interface ICBPComparison {

    
    

    public void compare(File leftFile, File rightFile) throws IOException, FactoryConfigurationError, XMLStreamException ;

    public void compare(File leftFile, File rightFile, File originFile) throws IOException, FactoryConfigurationError, XMLStreamException ;

    public void addObjectTreePostProcessor(ICBPObjectTreePostProcessor umlObjectTreePostProcessor);

    }
