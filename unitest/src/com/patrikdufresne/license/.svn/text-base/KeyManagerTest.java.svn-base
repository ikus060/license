package com.patrikdufresne.license;

import org.junit.Before;
import org.junit.Test;

public class KeyManagerTest {

	private KeyManager manager;

	@Before
	public void initKeyManager() {
		this.manager = new KeyManager();
	}

	@Test
	public void generateKey_WithAuthCode_ReturnKey() {

		String key = this.manager.generateKey(3, "coucou");

	}

	@Test
	public void validateKey_WithValidKey_ReturnTrue()
			throws KeyInvalidException, KeyBlackListedException {

		String key = this.manager.generateKey(3, "coucou");

		this.manager.validateKey(key);

	}

	@Test(expected = KeyInvalidException.class)
	public void validateKey_WithInvalidKey_ReturnTrue()
			throws KeyInvalidException, KeyBlackListedException {

		String key = this.manager.generateKey(3, "coucou");

		key = key.replace("0", "1");

		this.manager.validateKey(key);

	}

}
