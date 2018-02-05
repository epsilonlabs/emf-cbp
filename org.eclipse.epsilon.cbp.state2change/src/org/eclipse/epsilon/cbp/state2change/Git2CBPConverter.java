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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceFactory;
import org.eclipse.epsilon.cbp.resource.CBPXMLResourceImpl;
import org.eclipse.modisco.infra.discovery.core.exception.DiscoveryException;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;

public class Git2CBPConverter {

	private File gitDirectory;
	private String getHashesCommand = "git rev-list --all";
	private List<String> hashList;
	private static final int START_FROM_COMMIT = 1;

	public Git2CBPConverter(File gitDirectory) throws IOException {
		if (!gitDirectory.exists()) {
			throw new FileNotFoundException("Git Directory does not exists: " + gitDirectory.getAbsolutePath());
		}
		this.gitDirectory = gitDirectory;
		this.getCommitHashes();
	}

	public void convertGit2CBP(File commitsDirectory, File targetXmiDirectory, File cbpFile, File diffDirectory,
			String directoryName, String code) throws Exception {
		this.convertGit2CBP(commitsDirectory, targetXmiDirectory, cbpFile, diffDirectory, directoryName, code, false,
				false);
	}

	public void convertGit2CBP(File commitsDirectory, File targetXmiDirectory, File cbpFile, File diffDirectory,
			String directoryName, String code, boolean deleteProject, boolean deleteXmi) throws Exception {
		if (commitsDirectory == null) {
			throw new NullPointerException("Target directory is null");
		} else if (!commitsDirectory.exists()) {
			commitsDirectory.mkdir();
		} else if (!targetXmiDirectory.exists()) {
			targetXmiDirectory.mkdir();
		} else if (!cbpFile.getParentFile().exists()) {
			cbpFile.getParentFile().mkdir();
		} else if (!diffDirectory.exists()) {
			diffDirectory.mkdir();
		}

		String strPath = commitsDirectory.getAbsolutePath();
		String hash;
		String hashPath;
		File hashDirectory;
		String strNum;
		String dirName;
		int num = START_FROM_COMMIT - 1;

		URI cbpUri = URI.createFileURI(cbpFile.getAbsolutePath());
		File file = new File(cbpUri.toFileString());

		if (file.getParentFile().exists() == false) {
			file.getParentFile().mkdir();
		}
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("cbpxml",
				new CBPXMLResourceFactory());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uml", new UMLResourceFactoryImpl());

		Resource cbpResource = resourceSet.createResource(cbpUri);

		System.out.println(hashList.size() + " version(s)");
		for (int i = hashList.size() - 1 - START_FROM_COMMIT + 1; i >= 0; i--) {
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
				System.out.println("Deleting existing " + hashDirectory.getName());
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
			Resource xmiResource = generator.generateXmiFile(hashDirectory, targetXmiDirectory);
			File xmiFile = new File(xmiResource.getURI().toFileString());

			State2ChangeConverter xmi2CbpConverter = new State2ChangeConverter(null);
			xmi2CbpConverter.generateFromSingleFile(cbpResource, xmiResource, diffDirectory);

			// delete to save space
			if (deleteProject == true && hashDirectory.exists()) {
				System.out.println("Deleting " + hashDirectory.getName() + " project to save space");
				this.deleteDirectory(hashDirectory);
			}

			if (deleteXmi == true && xmiFile.exists()) {
				System.out.println("Deleting " + xmiFile.getName() + " file to save space");
				xmiFile.delete();
			}

			System.out.println("");
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
			String os = System.getProperty("os.name").toLowerCase();
			if (os.indexOf("nux") >= 0) {
				p = Runtime.getRuntime().exec(command, null, dir);
			} else {
				p = Runtime.getRuntime().exec("cmd /c " + command, null, dir);
			}

			//while (p.isAlive()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line = "";
				while ((line = reader.readLine()) != null) {
					output.append(line + "\n");
				}
			//}

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
