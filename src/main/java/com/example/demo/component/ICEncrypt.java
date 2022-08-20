package com.example.demo.component;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class ICEncrypt {
	
	private static final String ALLOWED_STRINGS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	/**
	 * createa a random string with allowed characters and length len
	 *
	 * @param len lenght of the random string
	 * @return random string
	 */
	public String randomString(final int len) {
		if(len < 0) {
			throw new IllegalArgumentException("Length is negative!");
		}
		final Random rnd = new SecureRandom();
		final StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(ALLOWED_STRINGS.charAt(rnd.nextInt(ALLOWED_STRINGS.length())));
		}
		return sb.toString();
	}
}
