package org.eclipse.epsilon.cbp.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PersistenceUtil {

	//delimiter
	public final String DELIMITER = ",";
	//escaped char
	public final String ESCAPE_CHAR ="+";
	//UTF-8 string encoding
	public final Charset STRING_ENCODING = StandardCharsets.UTF_8;
	
	//Null string I dont know what this is
	public final String NULL_STRING = "pFgrW";
	
	private static PersistenceUtil instance = null;
	
	private PersistenceUtil()
	{
		
	}
	
	public static PersistenceUtil getInstance()
	{
		if (instance == null) {
			instance = new PersistenceUtil();
		}
		return instance;
	}

}
