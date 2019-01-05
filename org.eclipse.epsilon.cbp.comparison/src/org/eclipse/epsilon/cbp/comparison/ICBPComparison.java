package org.eclipse.epsilon.cbp.comparison;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;

public interface ICBPComparison {

    public List<CBPDiff> compare(File leftFile, File rightFile) throws IOException, FactoryConfigurationError, XMLStreamException;

    public List<CBPDiff> compare(File leftFile, File rightFile, File originFile) throws IOException, FactoryConfigurationError, XMLStreamException;

    public void addObjectTreePostProcessor(ICBPObjectTreePostProcessor umlObjectTreePostProcessor);

    public long getObjectTreeConstructionTime();

    public long getDiffTime();

    public int getDiffCount();

    public long getComparisonTime();

    public long getLoadTime();

    public File getObjectTreeFile();

    public void setObjectTreeFile(File objectTreeFile);

    public File getDiffEMFCompareFile();

    public void setDiffEMFCompareFile(File diffEMFCompareFile);

    public List<CBPChangeEvent<?>> getLeftEvents();

    public List<CBPChangeEvent<?>> getRightEvents();

    public long getObjectTreeConstructionMemory();

    public long getDiffMemory();

    public long getComparisonMemory();

    public long getLoadMemory();
}
