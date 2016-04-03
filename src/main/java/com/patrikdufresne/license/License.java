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
package com.patrikdufresne.license;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represent the license information. The {@link License} class should be used to store related information
 * about the license. This information may then be saved into an encrypted file.
 * 
 * @author Patrik Dufresne
 * 
 */
public final class License {

    public static final String EMAIL = "email";
    public static final String EXPIRATION = "expiration";
    public static final String ID = "id";

    public static final String LICENSE_NUMBER = "licenseNumber";
    public static final String LICENSE_TYPE = "licenseType";
    public static final String NAME = "name";
    /**
     * License type for lifetime version. Always valid.
     */
    public static final String TYPE_LIFETIME = "lifetime";
    /**
     * License type for single version. This type is valid for the given version.
     */
    public static final String TYPE_SINGLE_VERSION = "single-version";
    /**
     * License type for trial version. This type is valid until the expiration date.
     */
    public static final String TYPE_TRIAL = "trial";
    public static final String VERSION = "version";

    /**
     * Map to store properties.
     */
    private Map<String, String> properties;

    /**
     * Create a new license with default property value.
     */
    public License() {
        this.properties = new HashMap<String, String>();
        // id = "";
        // name = "";
        // email = "";
        // licenseNumber = "";
        // expiration = new Date();
        // version = "";
        // licenseType = TYPE_TRIAL;
    }

    /**
     * Return an unmodifiable map of properties.
     * 
     * @return
     */
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    /**
     * Return the expiration date.
     * 
     * @return the expiration
     */
    public Date getExpiration() {
        String value = getProperty(EXPIRATION);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Get the property value.
     * 
     * @param key
     *            the property key
     */
    public String getProperty(String key) {
        return this.properties.get(key);
    }

    /**
     * Set the license expiration date. Required with TYPE_TRIAL
     * 
     * @param expiration
     *            the expiration date.
     */
    public void setExpiration(Date expiration) {
        if (expiration == null) {
            setProperty(EXPIRATION, null);
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            setProperty(EXPIRATION, df.format(expiration));
        }
    }

    /**
     * Sets the property value.
     * 
     * @param key
     *            the property key
     * @param value
     *            the property value.
     */
    public void setProperty(String key, String value) {
        if (value == null) {
            this.properties.remove(key);
        } else {
            this.properties.put(key, value);
        }
    }

    /**
     * Check if the given license object is valid.
     * 
     * @param license
     *            the license object
     */
    public void validate(Date currentDate, String currentVersion) throws LicenseException {

        validateExpiration(new Date());

        validateVersion(currentVersion);

    }

    /**
     * Used to validate the expiration date according to the license type.
     * 
     * @param currentDate
     *            the current date.
     * @throws LicenseExpiredException
     */
    protected void validateExpiration(Date currentDate) throws LicenseExpiredException {
        if (TYPE_TRIAL.equals(getProperty(LICENSE_TYPE))) {
            if (getExpiration() == null || currentDate.after(getExpiration())) {
                throw new LicenseExpiredException();
            }
        }
        // The expiration date doesn't matter for a single version license or a
        // lifetime version.
    }

    /**
     * Used to validate the version according to the license type.
     * 
     * @param currentVersion
     * @throws LicenseVersionExpiredException
     */
    protected void validateVersion(String currentVersion) throws LicenseVersionExpiredException {

        if (TYPE_SINGLE_VERSION.equals(getProperty(LICENSE_TYPE))) {
            if (getProperty(VERSION) == null) {
                throw new LicenseVersionExpiredException();
            }
            Pattern pattern = Pattern.compile(getProperty(VERSION));
            Matcher matcher = pattern.matcher(currentVersion);
            if (!matcher.matches()) {
                throw new LicenseVersionExpiredException();
            }
        }

    }

}
