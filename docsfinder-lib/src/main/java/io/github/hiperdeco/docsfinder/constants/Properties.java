package io.github.hiperdeco.docsfinder.constants;

import java.io.IOException;

import org.apache.log4j.Logger;

public class Properties {
	
	private static java.util.Properties conf = new java.util.Properties();	
	private static Logger log = Logger.getLogger(Properties.class);
	
	static {
		try {
			conf.load(Properties.class.getClassLoader().getResourceAsStream("docsfinder_lib.properties"));
		} catch (IOException e) {
			log.error("Error", e);
		}
	}
	
	
	public static String get(String key) {
		String value = conf.getProperty(key);
		if (value == null || value.isEmpty()) {
			value = getSysProp(key);
		}
		return value;
	}
	
	public static String get(String key, String defaultValue) {
		String value = conf.getProperty(key);
		if (value == null || value.isEmpty()) {
			value = getSysProp(key);
		}
		if (value == null || value.isEmpty()) {
			value = defaultValue;
		}
		return value;
	}
	
	public static String getSysProp(String key) {
		return System.getProperty(key);
	}

}
