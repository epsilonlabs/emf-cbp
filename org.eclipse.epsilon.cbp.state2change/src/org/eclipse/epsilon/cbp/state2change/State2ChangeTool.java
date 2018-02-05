package org.eclipse.epsilon.cbp.state2change;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class State2ChangeTool {

	public static String getTimeStamp() {
		return getTimeStamp(null);
	}

	public static String getTimeStamp(String prefix) {
		prefix = (prefix == null) ? "" : prefix;
		return prefix + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
	}
	
	
	public static void generateCbpSessionString(File xmiDirectory) {
		File[] xmiFiles = xmiDirectory.listFiles();
		Arrays.sort(xmiFiles);
		for (File file: xmiFiles) {
			System.out.println("<session id=\"" + file.getName() + "\" time=\"20180129093255161GMT\"/>");
		}
		
	}
}
