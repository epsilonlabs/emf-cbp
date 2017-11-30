package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.epsilon.cbp.state2change.GitProjectsExtractor;
import org.junit.Test;

public class GitCommitExtractorTest {

	private File gitRepositoryDirectory = new File("./test.data/repository".replace("/", File.separator));
	private GitProjectsExtractor gitProjectsExtractor;
	private File gitProjectsDirectory = new File("./test.data/projects/".replace("/", File.separator));
	private String targetProjectName = null;

	public GitCommitExtractorTest() throws IOException {
		this.gitProjectsExtractor = new GitProjectsExtractor(gitRepositoryDirectory);
	}

	@Test
	public void listDirectoryTest() {
		File dir = new File("C:/".replace("/", File.separator));
		String command = "dir";
		String output = this.gitProjectsExtractor.executeCommand(command, dir);
		System.out.println(output);
		assertNotEquals(output.length(), 0);
	}

	@Test
	public void getCommitHashesTest() {
		List<String> hashList = this.gitProjectsExtractor.getCommitHashes();
		for (String hash : hashList) {
			System.out.println(hash);
		}
		assertNotEquals(hashList.size(), 0);
	}
	
	@Test
	public void copyAllCommitsToCommitsDirectoryTest() throws IOException{
		String code = "Hello";
		gitProjectsExtractor.getCommitHashes();
		gitProjectsExtractor.copyTargetProjectToCommitsDirectory
			(gitProjectsDirectory,targetProjectName, code);
		assertNotEquals(gitProjectsDirectory.list().length, 0);
	}
	

}
