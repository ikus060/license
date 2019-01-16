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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
     * Define the encoding to be used to read and write the license file. Since the license may be generate on different
     * platform with different default encoding, we need to hardcode this into a fixed encoding. UTF-8 should be a good
     * choice.
     */
    private static final String ENCODING = "UTF-8";

    private static final int BUF_SIZE = 4096;

    private PublicKey publicKey;

    /**
     * Our private key.
     */
    private PrivateKey privateKey;

    /**
     * This function is used to read a stream.
     * 
     * @param input
     *            the input stream
     * @return the data read from the stream
     * @throws IOException
     */
    private static byte[] readAll(InputStream input) throws IOException {
        if (input == null) {
            return null;
        }
        // Read the content of the file and store it in a byte array.
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUF_SIZE);
        byte[] buf = new byte[BUF_SIZE];
        int size;
        while ((size = input.read(buf)) != -1) {
            out.write(buf, 0, size);
        }
        return out.toByteArray();
    }

    /**
     * This function maybe used to read the public and/or private key from a file.
     * 
     * @param file
     *            the file to read
     * @return the file data
     * 
     * @throws IOException
     *             if the file does not exist, or if the first byte cannot be read for any reason
     */
    private static byte[] readAll(File file) throws IOException {
        if (file == null) {
            return null;
        }
        InputStream input = new FileInputStream(file);
        try {
            return readAll(input);
        } finally {
            input.close();
        }
    }

    /**
     * Use to check if the given data matches the given signature.
     * 
     * @param data
     *            the data
     * @param sig
     *            the signature associated with the data.
     * 
     * @throws NoSuchAlgorithmException
     *             if the algorithm SHA1withRSA is not supported.
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     *             if the key is invalid.
     * @throws SignatureException
     *             if this signature algorithm is unable to process the input data
     */
    protected boolean verify(byte[] data, byte[] sig) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        // Initialize the signing algorithm with our public key
        Signature rsaSignature = Signature.getInstance("SHA1withRSA");
        rsaSignature.initVerify(publicKey);

        // Update the signature algorithm with the data.
        rsaSignature.update(data);

        // Validate the signature
        return rsaSignature.verify(sig);

    }

    /**
     * Sign the given input stream data. The signature is append to the output stream.
     * 
     * @param data
     *            the the data to be signed.
     * @return the signature for the given data.
     * @throws NoSuchAlgorithmException
     *             if no Provider supports a Signature implementation for SHA1withRSA.
     * @throws InvalidKeyException
     *             if the private key is invalid.
     * @throws SignatureException
     *             if this signature algorithm is unable to process the input data provided.
     * @throws UnsupportedOperationException
     *             if the private key was not providedin the constructor.
     */
    protected byte[] sign(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (privateKey == null) {
            throw new UnsupportedOperationException("Can't sign when the private key is not available.");
        }

        // Initialize the signing algorithm with our private key
        Signature rsaSignature = Signature.getInstance("SHA1withRSA");
        rsaSignature.initSign(privateKey);
        rsaSignature.update(data);

        // Generate the signature.
        return rsaSignature.sign();

    }

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
            if (!f.exists()) {
                continue;
            }
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

        if (publicKey == null) {
            throw new NullPointerException("publicKey");
        }

        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.publicKey = kf.generatePublic(spec);

        if (privateKey != null) {
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory privateKeyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = privateKeyFactory.generatePrivate(privateSpec);
        }

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
        this(readAll(publicKey), readAll(privateKey));
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
        this(readAll(publicKey), readAll(privateKey));
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
     * @throws LicenseException
     */
    public License readLicenseFile(File file)
            throws IOException,
            InvalidKeyException,
            NoSuchAlgorithmException,
            SignatureException,
            ClassNotFoundException,
            LicenseException {

        String base64Signature = null;
        // Read the license file as a property file.
        Properties prop = new Properties();
        prop.load(new InputStreamReader(new FileInputStream(file), ENCODING));
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
            throw new LicenseException("No signature was found");
        }
        byte[] sig = Base64.decode(base64Signature.getBytes());

        // Check if the signature matches.
        byte[] data = writeLicenseToByteArray(lic);

        // Validate the signature
        if (!verify(data, sig)) {
            throw new LicenseException("invalid license signature");
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
        byte[] signature = sign(data);
        String base64signature = Base64.encode(signature);

        // Create property file
        Properties prop = new Properties();
        for (Entry<String, String> e : lic.getProperties().entrySet()) {
            prop.setProperty(e.getKey(), e.getValue());
        }
        prop.put(SIGNATURE, base64signature);

        // Write the property file
        prop.store(new OutputStreamWriter(new FileOutputStream(file), ENCODING), "License file");
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
