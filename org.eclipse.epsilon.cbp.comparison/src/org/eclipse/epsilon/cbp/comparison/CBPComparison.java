package org.eclipse.epsilon.cbp.comparison;

import java.io.File;

public class CBPComparison {

    ICBPComparison comparison;

    public CBPComparison() {
//	comparison = new CBPComparisonApproach01();
	comparison = new CBPComparisonApproach02();
    }

    public void compare(File leftFile, File rightFile) {
	comparison.compare(leftFile, rightFile);
    }

    public void compare(File leftFile, File rightFile, File originFile) {
	comparison.compare(leftFile, rightFile);
    }

}
