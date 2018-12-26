package io.github.hiperdeco.docsfinder.controller;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import io.github.hiperdeco.docsfinder.constants.Properties;

//TODO: documentar classe
public final class CryptoUtil {

	
	public final static String encodeBase64(String value) throws NoSuchAlgorithmException {
		return encodeBase64(value, null);
	}
	public final static String encodeBase64(String value, String algorithm) throws NoSuchAlgorithmException {
		if (algorithm == null) algorithm = "SHA-256";
		return new String(
		        Base64.getEncoder().encode(MessageDigest.getInstance(algorithm)
		        		.digest(value.getBytes(StandardCharsets.UTF_8))));
	}
	
	public final static String encode(String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException {
		String secret = getSecret();
		byte[] kbytes = secret.getBytes();
		SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encoding = cipher.doFinal(value.getBytes());
		BigInteger n = new BigInteger(encoding);
		return new String(Base64.getEncoder().encode(n.toString(16).getBytes()));
	}

	public final static String decode(String value) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException {
		value = new String(Base64.getDecoder().decode(value.getBytes()));
		String secret = getSecret();
		byte[] kbytes = secret.getBytes();
		SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

		BigInteger n = new BigInteger(value, 16);
		byte[] encoding = n.toByteArray();

		// SECURITY-344: fix leading zeros
		if (encoding.length % 8 != 0) {
			int length = encoding.length;
			int newLength = ((length / 8) + 1) * 8;
			int pad = newLength - length; // number of leading zeros
			byte[] old = encoding;
			encoding = new byte[newLength];
			for (int i = old.length - 1; i >= 0; i--) {
				encoding[i + pad] = old[i];
			}
			// SECURITY-563: handle negative numbers
			if (n.signum() == -1) {
				for (int i = 0; i < newLength - length; i++) {
					encoding[i] = (byte) -1;
				}
			}
		}

		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decode = cipher.doFinal(encoding);
		return new String(decode);
	}

	private static String getSecret() {
	
		return Properties.get("secret.key","bla");
	
	}

}
