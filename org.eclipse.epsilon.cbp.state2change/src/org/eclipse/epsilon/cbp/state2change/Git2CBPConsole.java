package org.eclipse.epsilon.cbp.state2change;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Git2CBPConsole implements IApplication {

	private static final String APP_NAME = "Git2CBPConsole";
	private static final String IS_NOT_DEFINED_YET = " is not defined yet";
	private static final String PREFIX = "PREFIX";
	private static final String GIT_DIR = "GIT_DIR";
	private static final String PRJ_DIR = "PRJ_DIR";
	private static final String XMI_DIR = "XMI_DIR";
	private static final String CBP_DIR = "CBP_DIR";
	private static final String DIFF_DIR = "DIF_DIR";
	private static final String PARAM_VAL_SEP = "=";
	private static final String CBP_FILENAME = "output.cbpxml";
	private static final String DEL_PRJ = "DEL_PRJ";
	private static final String DEL_XMI = "DEL_XMI";

	File gitRepositoryDirectory = new File("D:/TEMP/org.eclipse.bpmn2/");
	File gitProjectsDirectory = new File("D:/TEMP/BPMN2/projects/");
	String prefix = "BPMN2";
	File targetXmiDirectory = new File("D:/TEMP/BPMN2/xmi/");
	File diffDirectory = new File("D:/TEMP/BPMN2/diff/");
	File cbpFile = new File("D:/TEMP/BPMN2/cbp/javamodel.cbpxml");
	boolean deleteProject = false;
	boolean deleteXmi = false;

	@Override
	public Object start(IApplicationContext context) {
		System.out.println();
		System.out.println(Git2CBPConsole.getTimeStamp() + ": Start running " + APP_NAME + " ...");
		System.out.println();

		final Map<?, ?> args = context.getArguments();
		final String[] appArgs = (String[]) args.get("application.args");

		
		try {
			getParameterValues(appArgs);
			System.out.println();
			
			Git2CBPConverter gitProjectsExtractor = new Git2CBPConverter(gitRepositoryDirectory);
			gitProjectsExtractor.convertGit2CBP(gitProjectsDirectory, targetXmiDirectory, cbpFile, diffDirectory, "",
					prefix, deleteProject, deleteXmi);

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println();
		System.out.println(Git2CBPConsole.getTimeStamp() + ": " + APP_NAME + " Finished! ");
		System.out.println();
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// nothing to do
	}

	public static String getTimeStamp() {
		return getTimeStamp(null);
	}

	public static String getTimeStamp(String prefix) {
		prefix = (prefix == null) ? "" : prefix;
		return prefix + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
	}

	private void getParameterValues(String[] appArgs) throws Exception {
		if (appArgs.length == 0) {
			throw new Exception("Required parameters have not defined yet");
		}
		
		for (final String arg : appArgs) {
			if (arg.trim().startsWith(Git2CBPConsole.PREFIX)) {
				prefix = arg.split(Git2CBPConsole.PARAM_VAL_SEP)[1];
				System.out.println(Git2CBPConsole.PREFIX + Git2CBPConsole.PARAM_VAL_SEP + prefix);
				if (prefix == null || prefix.length() == 0) {
					throw new Exception(Git2CBPConsole.PREFIX + IS_NOT_DEFINED_YET);
				}
			} else if (arg.trim().startsWith(Git2CBPConsole.GIT_DIR)) {
				String path = arg.split(Git2CBPConsole.PARAM_VAL_SEP)[1].trim();
				System.out.println(Git2CBPConsole.GIT_DIR + Git2CBPConsole.PARAM_VAL_SEP + path);
				if (path == null || path.length() == 0) {
					throw new Exception(Git2CBPConsole.GIT_DIR + IS_NOT_DEFINED_YET);
				}
				gitRepositoryDirectory = new File(path);
			} else if (arg.trim().startsWith(Git2CBPConsole.PRJ_DIR)) {
				String path = arg.split(Git2CBPConsole.PARAM_VAL_SEP)[1].trim();
				System.out.println(Git2CBPConsole.PRJ_DIR + Git2CBPConsole.PARAM_VAL_SEP + path);
				if (path == null || path.length() == 0) {
					throw new Exception(Git2CBPConsole.PRJ_DIR + IS_NOT_DEFINED_YET);
				}
				gitProjectsDirectory = new File(path);
			} else if (arg.trim().startsWith(Git2CBPConsole.XMI_DIR)) {
				String path = arg.split(Git2CBPConsole.PARAM_VAL_SEP)[1].trim();
				System.out.println(Git2CBPConsole.XMI_DIR + Git2CBPConsole.PARAM_VAL_SEP + path);
				if (path == null || path.length() == 0) {
					throw new Exception(Git2CBPConsole.XMI_DIR + IS_NOT_DEFINED_YET);
				}
				targetXmiDirectory = new File(path);
			} else if (arg.trim().startsWith(Git2CBPConsole.CBP_DIR)) {
				String path = arg.split(Git2CBPConsole.PARAM_VAL_SEP)[1].trim();
				System.out.println(Git2CBPConsole.CBP_DIR + Git2CBPConsole.PARAM_VAL_SEP + path);
				if (path == null || path.length() == 0) {
					throw new Exception(Git2CBPConsole.CBP_DIR + IS_NOT_DEFINED_YET);
				}
				cbpFile = new File(path + File.separator + Git2CBPConsole.CBP_FILENAME);
			} else if (arg.trim().startsWith(Git2CBPConsole.DIFF_DIR)) {
				String path = arg.split(Git2CBPConsole.PARAM_VAL_SEP)[1].trim();
				System.out.println(Git2CBPConsole.DIFF_DIR + Git2CBPConsole.PARAM_VAL_SEP + path);
				if (path == null || path.length() == 0) {
					throw new Exception(Git2CBPConsole.DIFF_DIR + IS_NOT_DEFINED_YET);
				}
				diffDirectory = new File(path);
			} else if (arg.trim().startsWith(Git2CBPConsole.DEL_PRJ)) {
				String value = arg.split(Git2CBPConsole.PARAM_VAL_SEP)[1].trim();
				System.out.println(Git2CBPConsole.DEL_PRJ + Git2CBPConsole.PARAM_VAL_SEP + value);
				if (value == null || value.length() == 0) {
					throw new Exception(Git2CBPConsole.DEL_PRJ + IS_NOT_DEFINED_YET);
				}
				if (value.toUpperCase().equals("Y")) {
					deleteProject = true;
				} else if (value.toUpperCase().equals("N")) {
					deleteProject = false;
				} else {
					throw new Exception(Git2CBPConsole.DEL_PRJ + " should be 'Y' or 'N'");
				}
			} else if (arg.trim().startsWith(Git2CBPConsole.DEL_XMI)) {
				String value = arg.split(Git2CBPConsole.PARAM_VAL_SEP)[1].trim();
				System.out.println(Git2CBPConsole.DEL_XMI + Git2CBPConsole.PARAM_VAL_SEP + value);
				if (value == null || value.length() == 0) {
					throw new Exception(Git2CBPConsole.DEL_XMI + IS_NOT_DEFINED_YET);
				}
				if (value.toUpperCase().equals("Y")) {
					deleteXmi = true;
				} else if (value.toUpperCase().equals("N")) {
					deleteXmi = false;
				} else {
					throw new Exception(Git2CBPConsole.DEL_XMI + " should be 'Y' or 'N'");
				}
			}
		}
	}

}
