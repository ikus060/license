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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * This the main entry point of the licensing module. This class should be used
 * to create and check license files.
 * <p>
 * Generally, an application will not required more then one instance of license
 * manager.
 * 
 * @author Patrik Dufresne
 * 
 */
public class LicenseManager {
    /**
     * Property to store the signature.
     */
    private static final String SIGNATURE = "signature";
    /**
     * The encryption manager used by this class.F
     */
    private EncryptionManager encryptionManager;

    /**
     * Utility function to easily validate a license file.
     */
    public static License validate(InputStream publicKey, File... files) throws LicenseException {
        if (publicKey == null || files == null || files.length == 0) {
            throw new IllegalArgumentException();
        }
        // Create a new license manager.
        LicenseManager licenseManager;
        try {
            licenseManager = new LicenseManager(publicKey, null);
        } catch (Exception e) {
            throw new LicenseException("invalid public key", e);
        }
        // Validate each license file.
        LicenseException lastException = null;
        for (File f : files) {
            License license;
            try {
                license = licenseManager.readLicenseFile(f);
                license.validate(new Date(), null);
                return license;
            } catch (LicenseException e) {
                lastException = e;
            } catch (Exception e) {
                lastException = new LicenseException("invalid licence file", e);
            }
        }
        throw lastException;
    }

    /**
     * Create a new license manager. Generally, an application will not required
     * more then one instance of license manager.
     * 
     * @param publicKey
     *            the public key (can't be null).
     * @param privateKey
     *            the private key (null if not available).
     * @throws GeneralSecurityException
     *             if the provided key are invalid.
     */
    public LicenseManager(byte[] publicKey, byte[] privateKey) throws GeneralSecurityException {
        this.encryptionManager = new EncryptionManager(publicKey, privateKey);
    }

    /**
     * Create a new license manager.
     * 
     * @param publicKey
     *            the public key file.
     * @param privateKey
     *            the private key file (null if not available).
     * @throws GeneralSecurityException
     *             if the provided key are invalid.
     * @throws IOException
     *             if the file doesn't exists
     */
    public LicenseManager(File publicKey, File privateKey) throws GeneralSecurityException, IOException {
        byte[] pubdata = EncryptionManager.readAll(publicKey);
        byte[] privdata = null;
        if (privateKey != null) {
            privdata = EncryptionManager.readAll(privateKey);
        }
        this.encryptionManager = new EncryptionManager(pubdata, privdata);
    }

    /**
     * Create a new license manager.
     * 
     * @param publicKey
     *            an input stream containing the public key
     * @param privateKey
     *            an input stream containing the private key
     */
    public LicenseManager(InputStream publicKey, InputStream privateKey) throws GeneralSecurityException, IOException {
        byte[] pubdata = EncryptionManager.readAll(publicKey);
        byte[] privdata = null;
        if (privateKey != null) {
            privdata = EncryptionManager.readAll(privateKey);
        }
        this.encryptionManager = new EncryptionManager(pubdata, privdata);
    }

    /**
     * Create a new license manager.
     * 
     * @param publicKey
     *            the public key filename.
     * @param privateKey
     *            the private key filename (null if not available).
     * @throws GeneralSecurityException
     *             if the provided key are invalid.
     * @throws IOException
     *             if the file doesn't exists
     */
    public LicenseManager(String publicKey, String privateKey) throws GeneralSecurityException, IOException {
        this(new File(publicKey), new File(privateKey));
    }

    /**
     * Read the content of an encrypted license file.
     * 
     * @param file
     *            the location to the license file.
     * @return the license object if the license file is valid, null otherwise.
     * @throws IOException
     *             if file not found or read error.
     * @throws SignatureException
     *             if this signature algorithm is unable to process the content
     *             of the file
     * @throws NoSuchAlgorithmException
     *             if the SHA algorithm doesn't exists
     * @throws InvalidKeyException
     *             if the public key is invalid
     * @throws ClassNotFoundException
     *             if the implementation of {@link License} stored in the file
     *             can't be found
     */
    public License readLicenseFile(File file) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, ClassNotFoundException {

        String base64Signature = null;
        // Read the license file as a property file.
        Properties prop = new Properties();
        prop.load(new FileReader(file));
        License lic = new License();
        for (Object key : prop.keySet()) {
            String value = (String) prop.get(key);
            if (SIGNATURE.equals(key)) {
                base64Signature = value;
            } else {
                lic.setProperty((String) key, value);
            }
        }
        // Check if the signature is available.
        if (base64Signature == null) {
            throw new SignatureException("No signature was found");
        }
        byte[] sig = Base64.decode(base64Signature.getBytes());

        // Check if the signature matches.
        byte[] data = writeLicenseToByteArray(lic);

        // Validate the signature
        if (!encryptionManager.verify(data, sig)) {
            return null;
        }

        return lic;

    }

    /**
     * Used to serialize a license object.
     * 
     * @param license
     *            the license object.
     * @param file
     *            the location where to save the new license file. If file
     *            exists, it's overwrite.
     * @throws IOException
     *             if the file doesn't exists or can't be written to
     * @throws SignatureException
     *             if this signature algorithm is unable to process the license
     *             data
     * @throws NoSuchAlgorithmException
     *             if the algorithm SHA is not supported
     * @throws InvalidKeyException
     *             if the private key is invalid.
     */
    public void writeLicense(License lic, File file) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {

        byte[] data = writeLicenseToByteArray(lic);

        // Then sign the byte array
        byte[] signature = this.encryptionManager.sign(data);
        String base64signature = Base64.encode(signature);

        // Create property file
        Properties prop = new Properties();
        for (Entry<String, String> e : lic.getProperties().entrySet()) {
            prop.setProperty(e.getKey(), e.getValue());
        }
        prop.put(SIGNATURE, base64signature);

        // Write the property file
        prop.store(new FileWriter(file), "License file");
    }

    /**
     * Write the license information into a byte array ready to be signed.
     * 
     * @param lic
     *            the license
     * @return the byte array
     * @throws IOException
     */
    protected byte[] writeLicenseToByteArray(License lic) throws IOException {
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(dataStream);
        // Sort the key to have a predictable results.
        List<String> keys = new ArrayList<String>(lic.getProperties().keySet());
        Collections.sort(keys);
        for (String key : keys) {
            String value = lic.getProperty(key);
            out.writeChars(key);
            out.writeChars(value);
        }
        out.flush();
        byte[] data = dataStream.toByteArray();
        out.close();
        return data;
    }
}
