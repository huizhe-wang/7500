
package assignment1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class CryptoReference1 {

	public static byte[] MessageHash(String messageStr) {
		byte[] message = messageStr.getBytes(StandardCharsets.UTF_8);
		return message;
	}

	public static byte[] digest(byte[] s, String type) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(type);
		byte[] hash = digest.digest(s);
		return hash;
	}

	public static byte[] Nonce() {
		byte[] nonce = new byte[32]; // 256 bit array
		new Random().nextBytes(nonce); // pseudo-random
		return nonce;
	}

	public static String Concatentate(byte[] message, byte[] nonce) throws IOException {
		// Concatentate two byte arrays
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(message);
		outputStream.write(nonce);
		byte concat[] = outputStream.toByteArray();
		String concatHex = DatatypeConverter.printHexBinary(concat);
		return concatHex;
	}

	public static byte[] Convert(String hex) {
		byte[] b = DatatypeConverter.parseHexBinary(hex);
		return b;
	}

}