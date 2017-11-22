package org.eclipse.epsilon.cbp.state2change;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class GitCommitExtractor {

	private File gitDirectory;
	private String getHashesCommand = "git rev-list --all";
	private List<String> hashList;

	public GitCommitExtractor(File gitDirectory) throws IOException {
		if (!gitDirectory.exists()) {
			throw new FileNotFoundException("Git Directory does not exists: " + gitDirectory.getAbsolutePath());
		}
		this.gitDirectory = gitDirectory;
	}

	public void copyTargetProjectToCommitsDirectory(File commitsDirectory, String code) throws IOException {
		this.copyTargetProjectToCommitsDirectory(commitsDirectory, null, code);
	}
	
	public void copyTargetProjectToCommitsDirectory(File commitsDirectory, String directoryName, String code) throws IOException {
		if (commitsDirectory == null) {
			throw new NullPointerException("Target directory is null");
		}
		if (!commitsDirectory.exists()) {
			commitsDirectory.mkdir();
		}
		for (File file : commitsDirectory.listFiles()) {
			System.out.println("Deleting " + file.getName());
			this.deleteDirectory(file);
		}

		String strPath = commitsDirectory.getAbsolutePath();
		String hash;
		String hashPath;
		File hashDirectory;
		String strNum;
		String dirName;
		int num = 0;
		for (int i = hashList.size() - 1; i >= 0; i--) {
			num += 1;
			hash = hashList.get(i);
			strNum = "";
			strNum += num;
			while (strNum.length() <= 6) {
				strNum = "0" + strNum;
			}
			dirName = code + "-" + strNum + "-" + hash;
			System.out.println("Copying to " + dirName);
			hashPath = strPath + File.separator + dirName;
			hashDirectory = new File(hashPath);

			hashDirectory.mkdir();
			this.executeCommand("git checkout " + hash, gitDirectory);
			if (directoryName == null || directoryName.trim() == "") {
				FileUtils.copyDirectory(gitDirectory, hashDirectory);
			} else {
				File searchDir = searchDirectory(gitDirectory, directoryName);
				if (searchDir != null) {
					FileUtils.copyDirectory(searchDir, hashDirectory);
				}
			}
		}
		System.out.println("Finished!");
	}

	public List<String> getCommitHashes() {
		this.hashList = new ArrayList<String>();
		String output = this.executeCommand(this.getHashesCommand);
		hashList = Arrays.asList(output.split("\n"));
		return hashList;
	}

	public String executeCommand(String command) {
		return this.executeCommand(command, gitDirectory);
	}

	public String executeCommand(String command, File dir) {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec("cmd /c " + command, null, dir);
			while (p.isAlive()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line = "";
				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}

	private File searchDirectory(File searchInDirectory, String fileNameToSearch) {
		File[] fileList = searchInDirectory.listFiles();
		for (File file : fileList) {
			if (file.isDirectory()) {
				if (file.getName() == fileNameToSearch || file.getName().equals(fileNameToSearch)) {
					// System.out.println(fileNameToSearch);
					return file;
				}
				searchDirectory(file, fileNameToSearch);
			}
		}
		return null;
	}

	private void deleteDirectory(File directory) {
		File[] files = {};
		if (directory.isDirectory()) {
			files = directory.listFiles();
		}
		for (File file : files) {
			deleteDirectory(file);
		}
		directory.delete();
	}

}
