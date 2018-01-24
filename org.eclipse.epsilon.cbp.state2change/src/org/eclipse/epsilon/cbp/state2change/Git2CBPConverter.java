package org.eclipse.epsilon.cbp.state2change;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;

public class Git2CBPConverter {

	private File gitDirectory;
	private String getHashesCommand = "git rev-list --all";
	private List<String> hashList;

	public Git2CBPConverter(File gitDirectory) throws IOException {
		if (!gitDirectory.exists()) {
			throw new FileNotFoundException("Git Directory does not exists: " + gitDirectory.getAbsolutePath());
		}
		this.gitDirectory = gitDirectory;
		this.getCommitHashes();
	}

	public void convertGit2CBP(File commitsDirectory, File targetXmiDirectory, String directoryName, String code)
			throws IOException, DiscoveryException {
		if (commitsDirectory == null) {
			throw new NullPointerException("Target directory is null");
		}
		if (!commitsDirectory.exists()) {
			commitsDirectory.mkdir();
		}

		String strPath = commitsDirectory.getAbsolutePath();
		String hash;
		String hashPath;
		File hashDirectory;
		String strNum;
		String dirName;
		int num = 0;
		System.out.println(hashList.size() + "version(s)");
		for (int i = hashList.size() - 1; i >= 0; i--) {
			num += 1;
			hash = hashList.get(i);
			strNum = "";
			strNum += num;
			while (strNum.length() <= 6) {
				strNum = "0" + strNum;
			}
			dirName = code + "-" + strNum + "-" + hash;
			hashPath = strPath + File.separator + dirName;
			hashDirectory = new File(hashPath);

			if (hashDirectory.exists()) {
				System.out.println("Deleting " + hashDirectory.getName());
				this.deleteDirectory(hashDirectory);
			}
			System.out.println("Copying to " + dirName);

			hashDirectory.mkdir();
			this.executeCommand("git checkout " + hash, gitDirectory);

			IOFileFilter nameFilter1 = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(),
					FileFilterUtils.nameFileFilter(".git"));
			IOFileFilter nameFilter2 = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
					FileFilterUtils.nameFileFilter(".gitattributes"));
			IOFileFilter nameFilter3 = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
					FileFilterUtils.nameFileFilter(".gitignore"));
			IOFileFilter orFilter = FileFilterUtils.or(nameFilter1, nameFilter2, nameFilter3);
			IOFileFilter filter = FileFilterUtils.notFileFilter(orFilter);

			List<File> sourceSubProjectList = searchProjects(gitDirectory);
			List<File> targetSubProjectList = new ArrayList<>();
			for (File projectPath : sourceSubProjectList) {
				File subProjectDir = new File(hashDirectory.getPath() + File.separator + projectPath.getName());
				subProjectDir.mkdir();
				targetSubProjectList.add(subProjectDir);
				FileUtils.copyDirectory(projectPath, subProjectDir, filter);
			}

			
			UmlXmiGenerator generator = new UmlXmiGenerator();
			generator.generateXmiFile(hashDirectory, targetXmiDirectory); 
			
//			// Convert projects to XMI should be done here
//			UmlXmiGenerator generator = new UmlXmiGenerator();
//			for (File targetSubProject : targetSubProjectList) {
//				generator.generateXmiFile(targetSubProject, targetXmiDirectory);
//			}
//
//			//combine all the generated xmis into one xmi
//			File targetProjectXmiDirectory = new File(targetXmiDirectory.getAbsolutePath() + File.separator + hashDirectory.getName());
//			generator.generateSingleXmiFile(targetProjectXmiDirectory);
			
			
			// delete to save space
			if (hashDirectory.exists()) {
				System.out.println("Deleting " + hashDirectory.getName());
				this.deleteDirectory(hashDirectory);
			}

			// System.out.println("Finished!");
		}
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

	private List<File> searchProjects(File searchInDirectory) {
		List<File> projectList = new ArrayList<>();
		this.recursiveSearchProjects(searchInDirectory, projectList);

		return projectList;
	}

	private void recursiveSearchProjects(File directory, List<File> projectList) {
		File[] fileList = directory.listFiles();
		for (File file : fileList) {
			if (file.isFile()) {
				if (file.getName() == ".project" || file.getName().equals(".project")) {
					projectList.add(directory);
				}
			} else if (file.isDirectory()) {
				recursiveSearchProjects(file, projectList);
			}
		}
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
