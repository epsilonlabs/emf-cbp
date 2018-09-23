package org.eclipse.epsilon.cbp.comparison;

import java.io.File;

public interface ICBPComparison {

    
    

    public void compare(File leftFile, File rightFile) ;

    public void compare(File leftFile, File rightFile, File originFile) ;

    }
