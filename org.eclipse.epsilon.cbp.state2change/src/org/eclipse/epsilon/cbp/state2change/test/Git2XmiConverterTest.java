package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.eclipse.epsilon.cbp.state2change.Git2CBPConverter;
import org.junit.Test;

public class Git2XmiConverterTest {

	@Test
	public void testConvertGitRepositoryToCBP() throws Exception {
		//common
//		File gitRepositoryDirectory = new File("D:/TEMP/eclipse.jdt.core/");
//		File gitProjectsDirectory = new File("D:/TEMP/JDTCoreModel/projects/");
//		String targetProjectName = "";
//		String code = "JDTCoreModel";
//		File targetXmiDirectory = new File("D:/TEMP/JDTCoreModel/xmi/");
//		File diffDirectory = new File("D:/TEMP/JDTCoreModel/diff/");
//		File cbpFile = new File("D:/TEMP/JDTCoreModel/cbp/javamodel.cbpxml");
		File gitRepositoryDirectory = new File("D:/TEMP/org.eclipse.bpmn2/");
		File gitProjectsDirectory = new File("D:/TEMP/BPMN2/projects/");
		String targetProjectName = "";
		String prefix = "BPMN2";
		File targetXmiDirectory = new File("D:/TEMP/BPMN2/xmi/");
		File diffDirectory = new File("D:/TEMP/BPMN2/diff/");
		File cbpFile = new File("D:/TEMP/BPMN2/cbp/javamodel.cbpxml");
		
		System.out.println("Repository to projects--------");
////		Repository to projects--------
		Git2CBPConverter gitProjectsExtractor = new Git2CBPConverter(gitRepositoryDirectory);	
		gitProjectsExtractor.convertGit2CBP(gitProjectsDirectory, targetXmiDirectory, cbpFile, diffDirectory, targetProjectName, prefix);
		
		assertEquals(true, true);
	}
}
