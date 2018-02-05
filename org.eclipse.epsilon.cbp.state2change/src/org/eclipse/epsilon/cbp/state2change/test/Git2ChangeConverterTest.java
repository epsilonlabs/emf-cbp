package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.*;

import java.io.File;

import org.eclipse.epsilon.cbp.state2change.GitProjectsExtractor;
import org.eclipse.epsilon.cbp.state2change.State2ChangeConverter;
import org.eclipse.epsilon.cbp.state2change.UmlXmiGenerator;
import org.junit.Test;

public class Git2ChangeConverterTest {

	@Test
	public void convertGitRepositoryToCBP() throws Exception {
		//common
		File gitRepositoryDirectory = new File("D:/TEMP/org.eclipse.bpmn2/");
		File gitProjectsDirectory = new File("D:/TEMP/BigModel/projects/");
		String targetProjectName = "org.eclipse.bpmn2.edit";
		String code = "BigModel";
		File targetXmiDirectory = new File("D:/TEMP/BigModel/xmi/");
		File diffDirectory = new File("D:/TEMP/BigModel/diff/");
		File cbpFile = new File("D:/TEMP/BigModel/cbp/" + "javamodel.cbpxml");
		
//		System.out.println("Repository to projects--------");
//		Repository to projects--------
//		GitProjectsExtractor gitProjectsExtractor = new GitProjectsExtractor(gitRepositoryDirectory);	
//		gitProjectsExtractor.getCommitHashes();
//		gitProjectsExtractor.copyTargetProjectToCommitsDirectory(gitProjectsDirectory, targetProjectName, code);
		
		System.out.println("projects to xmis-------");
//		//projects to xmis-------
//		UmlXmiGenerator generator = new UmlXmiGenerator();
//		generator.generateXmiFiles(gitProjectsDirectory, targetXmiDirectory);
		
		System.out.println("xmis to cbp-----");
		//xmis to cbp-----
		State2ChangeConverter state2ChangeConverter = new State2ChangeConverter(targetXmiDirectory);
		boolean result = state2ChangeConverter.generateFromMultipleFiles(cbpFile, diffDirectory);
		assertEquals(true, result);		
	}
}
