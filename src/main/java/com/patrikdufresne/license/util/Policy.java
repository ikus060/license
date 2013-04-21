/**
 * Copyright(C) 2013 Patrik Dufresne Service Logiciel <info@patrikdufresne.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
