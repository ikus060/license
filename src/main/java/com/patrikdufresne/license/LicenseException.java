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
package com.patrikdufresne.license;

/**
 * 
 * This exception regroup all the license exceptions.
 * 
 * @author Patrik Dufresne
 * 
 */
public class LicenseException extends Exception {

    private static final long serialVersionUID = 7895696254570225320L;

    /**
     * Constructs a new exception with null as its detail message. The cause is
     * not initialized, and may subsequently be initialized by a call to
     * Throwable.initCause(java.lang.Throwable).
     */
    public LicenseException() {
        this(null, null);
    }

    /**
     * Constructs a new exception with the specified message.
     * 
     * @param message
     *            the detail message (which is saved for later retrieval by the
     *            Throwable.getMessage() method).
     */
    public LicenseException(String message) {
        this(message, null);
    }

    /**
     * Constructs a new exception with the specified cause.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            Throwable.getCause() method). (A null value is permitted, and
     *            indicates that the cause is nonexistent or unknown.)
     */
    public LicenseException(Throwable cause) {
        this(null, cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message
     *            the detail message (which is saved for later retrieval by the
     *            Throwable.getMessage() method).
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            Throwable.getCause() method). (A null value is permitted, and
     *            indicates that the cause is nonexistent or unknown.)
     */
    public LicenseException(String message, Throwable cause) {
        super(message, cause);
    }

}
