package org.eclipse.epsilon.cbp.comparison;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

public class CBPComparison {

    ICBPComparison comparison;

    public CBPComparison() {
//	comparison = new CBPComparisonApproach01();
//	comparison = new CBPComparisonApproach02();
	comparison = new CBPComparisonImpl();
    }

    public void compare(File leftFile, File rightFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	comparison.compare(leftFile, rightFile);
    }

    public void compare(File leftFile, File rightFile, File originFile) throws IOException, FactoryConfigurationError, XMLStreamException {
	comparison.compare(leftFile, rightFile, originFile);
    }

}
