package org.eclipse.epsilon.cbp.state2change.test;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.epsilon.cbp.state2change.GitCommitExtractor;
import org.junit.Test;

public class GitCommitExtractorTest {

	private File gitDirectory = new File("./test.data/repository".replace("/", File.separator));
	private GitCommitExtractor gitCommitsExtractor;
	private File gitCommitsDirectory = new File("./test.data/projects/".replace("/", File.separator));
	private String targetProjectName = null;

	public GitCommitExtractorTest() throws IOException {
		this.gitCommitsExtractor = new GitCommitExtractor(gitDirectory);
	}

	@Test
	public void listDirectoryTest() {
		File dir = new File("C:/".replace("/", File.separator));
		String command = "dir";
		String output = this.gitCommitsExtractor.executeCommand(command, dir);
		System.out.println(output);
		assertNotEquals(output.length(), 0);
	}

	@Test
	public void getCommitHashesTest() {
		List<String> hashList = this.gitCommitsExtractor.getCommitHashes();
		for (String hash : hashList) {
			System.out.println(hash);
		}
		assertNotEquals(hashList.size(), 0);
	}
	
	@Test
	public void copyAllCommitsToCommitsDirectoryTest() throws IOException{
		String code = "Hello";
		gitCommitsExtractor.getCommitHashes();
		gitCommitsExtractor.copyTargetProjectToCommitsDirectory
			(gitCommitsDirectory,targetProjectName, code);
		assertNotEquals(gitCommitsDirectory.list().length, 0);
	}
	

}
