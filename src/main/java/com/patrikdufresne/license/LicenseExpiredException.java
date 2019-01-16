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
 * Thrown when the license validation determine the license to be expired. The
 * expiration date should then by retrieve using the
 * {@link AbstractLicense#getExpiration()}.
 * 
 * @author Patrik Dufresne
 * 
 */
public class LicenseExpiredException extends LicenseException {

    private static final long serialVersionUID = -9069804052012922999L;

    /**
     * Constructs a new exception with null as its detail message. The cause is
     * not initialized, and may subsequently be initialized by a call to
     * Throwable.initCause(java.lang.Throwable).
     */
    public LicenseExpiredException() {
        super("license expired");
    }

}
