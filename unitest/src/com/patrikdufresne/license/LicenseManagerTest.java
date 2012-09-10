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

	static class MockLicense extends AbstractLicense {

		private static final long serialVersionUID = -4612807836761969030L;

	}

	private LicenseManager manager;

	@Before
	public void initLicenseManager() throws GeneralSecurityException,
			IOException {
		this.manager = new LicenseManager("pubkey.der", "privkey.der");
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
	public void writeLicense_WithLicense_CreateTheFile()
			throws InvalidKeyException, NoSuchAlgorithmException,
			SignatureException, IOException {
		MockLicense license = new MockLicense();
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
	 */
	@Test
	public void readLicense_WithValidFile_ReadLicense()
			throws InvalidKeyException, NoSuchAlgorithmException,
			SignatureException, IOException, ClassNotFoundException {
		MockLicense license = new MockLicense();
		File file = new File("unittest2.lic");
		manager.writeLicense(license, file);
		assertTrue(file.exists());

		// Read the file
		MockLicense license2 = (MockLicense) manager.readLicenseFile(file);
		assertNotNull(license2);

	}

}
