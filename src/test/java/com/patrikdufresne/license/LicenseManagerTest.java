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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import org.junit.Before;
import org.junit.Test;

/**
 * This class test all the functionnality provided by the {@link LicenseManager}
 * .
 * 
 * @author ikus060
 * 
 */
public class LicenseManagerTest {

    private LicenseManager manager;

    @Before
    public void initLicenseManager() throws GeneralSecurityException, IOException {
        this.manager = new LicenseManager(getClass().getResourceAsStream("/pubkey.der"), getClass().getResourceAsStream("/privkey.der"));
    }

    /**
     * Check if validate is working.
     * 
     */
    @Test
    public void validate() throws Exception {
        License license = new License();
        File file = new File("unittest2.lic");
        manager.writeLicense(license, file);
        LicenseManager.validate(getClass().getResourceAsStream("/pubkey.der"), file);
    }

    /**
     * Check to create a license file
     * 
     * @throws IOException
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    @Test
    public void writeLicense_WithEmptyLicense_CreateTheFile() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException {
        License license = new License();
        File file = new File("unittest.lic");
        manager.writeLicense(license, file);
        assertTrue(file.exists());
    }

    @Test
    public void writeLicense_WithLicense_CreateTheFile() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException {
        License license = new License();
        license.setProperty(License.NAME, "A test name");
        File file = new File("unittest.lic");
        manager.writeLicense(license, file);
        assertTrue(file.exists());
    }

    /**
     * Read the license file.
     * 
     * @throws IOException
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws ClassNotFoundException
     * @throws LicenseException
     */
    @Test
    public void readLicense_WithValidFile_ReadLicense()
            throws InvalidKeyException,
            NoSuchAlgorithmException,
            SignatureException,
            IOException,
            ClassNotFoundException,
            LicenseException {
        License license = new License();
        File file = new File("unittest2.lic");
        manager.writeLicense(license, file);
        assertTrue(file.exists());

        // Read the file
        License license2 = (License) manager.readLicenseFile(file);
        assertNotNull(license2);

    }

}
