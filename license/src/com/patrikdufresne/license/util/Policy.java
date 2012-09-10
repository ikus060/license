/*
 * Copyright (c) 2012 David Stites, Patrik Dufresne and others.
 * 
 * You may distribute under the terms of either the MIT License, the Apache
 * License 2.0 or the Simplified BSD License, as specified in the README file.
 * 
 * Contributors:
 *     David Stites - initial API and implementation
 *     Patrik Dufresne - refactoring
 */
package com.patrikdufresne.license.util;

/**
 * The Policy class handles settings for behavior, debug flags and logging
 * within the license framework.
 * 
 */
public class Policy {

	private static ILogger log;

	/**
	 * Returns the dummy log to use if none has been set
	 */
	private static ILogger getDummyLog() {
		return new ILogger() {
			@Override
			public void log(String level, String message) {
				System.out.print(level + " " + message);
			}

			@Override
			public void log(String level, Throwable exception) {
				exception.printStackTrace(System.out);
			}
		};
	}

	/**
	 * Returns the logger used by the license framework to log errors.
	 * <p>
	 * The default logger prints the status to <code>System.err</code>.
	 * </p>
	 * 
	 * @return the logger
	 */
	public static ILogger getLog() {
		if (log == null) {
			log = getDummyLog();
		}
		return log;
	}

	/**
	 * Sets the logger used by the license framework to log errors.
	 * 
	 * @param logger
	 *            the logger to use, or <code>null</code> to use the default
	 *            logger
	 */
	public static void setLog(ILogger logger) {
		log = logger;
	}

}
