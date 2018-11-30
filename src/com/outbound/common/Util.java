package com.outbound.common;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.*;

public class Util {

	private static Util instance = null;

	public static Util getInstance() {
		if (instance == null)
			instance = new Util();
		return instance;
	}

	protected Util() {
		System.out.println("Init Util");
		instance = this;

		PropertyConfigurator.configure("log4j.properties");

	}

	/**
	 * static log function
	 * 
	 * @param className
	 *            the name of class printing log
	 * @param msg
	 */
	public static void info(String className, String msg) {
		Logger logger = Logger.getRootLogger();// .getLogger(className);
		logger.info(msg);
	}

	public static void debug(String className, String msg) {
		Logger logger = Logger.getLogger(className);
		logger.debug(msg);
	}

	public static void warn(String className, String msg) {
		Logger logger = Logger.getLogger(className);
		logger.warn(msg);
	}

	public static void error(String className, String msg) {
		Logger logger = Logger.getLogger(className);
		logger.error(msg);
	}

	public static void fatal(String className, String msg) {
		Logger logger = Logger.getLogger(className);
		logger.fatal(msg);
	}

	public static void trace(Object class1, String msg, Object... params) {
		Logger logger = Logger.getLogger(class1.getClass().getName());
		logger.debug(String.format(msg, params));
	}

	public static void info(Object obj, String msg, Object... params) {

		Logger logger = Logger.getLogger(obj.getClass().getName());
		logger.info(String.format(msg, params));
	}

	public static void error(Object obj, String msg, Exception e) {
		Logger logger = Logger.getLogger(obj.getClass().getName());
		logger.error(msg, e);
	}

	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

}
