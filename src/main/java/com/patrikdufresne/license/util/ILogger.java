/**
 * Copyright(C) 2018 Patrik Dufresne Service Logiciel inc <info@patrikdufresne.com>
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
 * A mechanism to log errors throughout the license framework.
 * <p>
 * Clients may provide their own implementation to change how errors are logged
 * from within the license framework.
 * </p>
 * 
 */
public interface ILogger {
    /**
     * Trace level (value: trace).
     */
    public static final String TRACE = "trace";
    /**
     * Debug level (value: debug).
     */
    public static final String DEBUG = "debug";
    /**
     * Info level (value: info).
     */
    public static final String INFO = "info";
    /**
     * Warn level (value: warn).
     */
    public static final String WARN = "warn";
    /**
     * Error level (value: error).
     */
    public static final String ERROR = "error";

    /**
     * Logs the given status.
     * 
     * @param level
     *            The level
     * @param message
     *            The message to be logged.
     */
    public void log(String level, String message);

    /**
     * Logs the given exception.
     * 
     * @param level
     * @param exception
     */
    public void log(String level, Throwable exception);

}
