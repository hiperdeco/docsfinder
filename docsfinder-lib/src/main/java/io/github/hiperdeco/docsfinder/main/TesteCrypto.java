package io.github.hiperdeco.docsfinder.main;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class TesteCrypto {

	public static void main(String[] args) {
		
		String clearText = "teste";
		try {
			//para gerar senha para authenticacao de usuario
			String cripto = new String(
			        Base64.getEncoder().encode(MessageDigest.getInstance("SHA-256")
			        		.digest(clearText.getBytes(StandardCharsets.UTF_8))));
			System.out.println(cripto); 
			//para gerar senha q sera gravada no banco
			cripto = new String(Base64.getEncoder().encode(encode(clearText).getBytes()));
			System.out.println(cripto);
			System.out.println(decode(new String(Base64.getDecoder().decode(cripto.getBytes()))));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);

	}
	
	private static String encode(String secret)
		      throws NoSuchPaddingException, NoSuchAlgorithmException,
		      InvalidKeyException, BadPaddingException, IllegalBlockSizeException
		   {
		      byte[] kbytes = getSecret().getBytes();
		      SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

		      Cipher cipher = Cipher.getInstance("Blowfish");
		      cipher.init(Cipher.ENCRYPT_MODE, key);
		      byte[] encoding = cipher.doFinal(secret.getBytes());
		      BigInteger n = new BigInteger(encoding);
		      return n.toString(16);
		   }

	private static String decode(String secret)
		      throws NoSuchPaddingException, NoSuchAlgorithmException,
		      InvalidKeyException, BadPaddingException, IllegalBlockSizeException
		   {
		      byte[] kbytes = getSecret().getBytes();
		      SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

		      BigInteger n = new BigInteger(secret, 16);
		      byte[] encoding = n.toByteArray();
		      
		      //SECURITY-344: fix leading zeros
		      if (encoding.length % 8 != 0)
		      {
		         int length = encoding.length;
		         int newLength = ((length / 8) + 1) * 8;
		         int pad = newLength - length; //number of leading zeros
		         byte[] old = encoding;
		         encoding = new byte[newLength];
		         for (int i = old.length - 1; i >= 0; i--)
		         {
		            encoding[i + pad] = old[i];
		         }
		         //SECURITY-563: handle negative numbers
		         if (n.signum() == -1)
		         {
		            for (int i = 0; i < newLength - length; i++)
		            {
		               encoding[i] = (byte) -1;
		            }
		         }
		      }
		      
		      Cipher cipher = Cipher.getInstance("Blowfish");
		      cipher.init(Cipher.DECRYPT_MODE, key);
		      byte[] decode = cipher.doFinal(encoding);
		      return new String(decode);
		   }

	public static String getSecret() {
		Properties conf = new Properties();
		try {
			conf.load(TesteCrypto.class.getClassLoader().getResourceAsStream("docsfinder_lib.properties"));
			System.out.println(conf.getProperty("secret.key"));
			return conf.getProperty("secret.key");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "bla";
	}
}
