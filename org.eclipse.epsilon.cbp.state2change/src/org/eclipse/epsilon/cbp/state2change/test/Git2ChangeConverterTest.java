package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.epsilon.cbp.state2change.GitProjectsExtractor;
import org.eclipse.epsilon.cbp.state2change.State2ChangeConverter;
import org.eclipse.epsilon.cbp.state2change.UmlXmiGenerator;
import org.junit.Test;

public class Git2ChangeConverterTest {

	@Test
	public void convertGitRepositoryToCBP() throws Exception {
		//common
		File gitRepositoryDirectory = new File("D:/TEMP/org.eclipse.bpmn2/".replace("/", File.separator));
		File gitProjectsDirectory = new File("D:/TEMP/BigModel/projects/".replace("/", File.separator));
		String targetProjectName = "org.eclipse.bpmn2";
		String code = "BigModel";
		File targetXmiDirectory = new File("D:/TEMP/BigModel/xmi/".replace("/", File.separator));
		File diffDirectory = new File("D:/TEMP/BigModel/diff/".replace("/", File.separator));
		File cbpFile = new File("D:/TEMP/BigModel/cbp/".replace("/", File.separator) + "javamodel.cbpxml");
		
		System.out.println("Repository to projects--------");
		//Repository to projects--------
//		GitProjectsExtractor gitProjectsExtractor = new GitProjectsExtractor(gitRepositoryDirectory);	
//		gitProjectsExtractor.getCommitHashes();
//		gitProjectsExtractor.copyTargetProjectToCommitsDirectory(gitProjectsDirectory, targetProjectName, code);
		
		System.out.println("projects to xmis-------");
		//projects to xmis-------
//		UmlXmiGenerator generator = new UmlXmiGenerator();
//		generator.generateXmi(gitProjectsDirectory, targetXmiDirectory);
		
		System.out.println("xmis to cbp-----");
		//xmis to cbp-----
		FileOutputStream fop = new FileOutputStream(cbpFile);	
		State2ChangeConverter state2ChangeConverter = new State2ChangeConverter(targetXmiDirectory);
		String cbpText = state2ChangeConverter.generate(fop, diffDirectory);
		fop.flush();
		fop.close();
		assertNotEquals(cbpText.length(), 0);		
	}
}
