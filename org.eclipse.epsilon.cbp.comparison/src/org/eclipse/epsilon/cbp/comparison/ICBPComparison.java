package org.eclipse.epsilon.cbp.comparison;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.comparison.event.CBPChangeEvent;
import org.eclipse.epsilon.cbp.conflict.CBPConflict;

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

    public Map<String, CBPMatchObject> getObjectTree();

    public List<CBPConflict> getConflicts();

    public long getConflictMemory();

    public long getConflictTime();

    public int getConflictCount();
    
    public List<String> getConflictStrings();
}
