package assignment1;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class FindX {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		String id = "ED00AF5F774E4135E7746419FEB65DE8AE17D6950C95CEC3891070FBB5B03C77";
		String H = "SHA-256";
		String hashHex = "";
		byte[] hash = null;
		String x = "";
		boolean flag = false;
		byte[] nounce = assignment1.CryptoReference1.Convert(id);
		Random rand = new Random();
		while (flag == false) {
			int myRandomNumber = rand.nextInt(0x10) + 0x20;
			x = Integer.toHexString(myRandomNumber);
			byte[] message = assignment1.CryptoReference1.MessageHash(x);
			String contact = assignment1.CryptoReference1.Concatentate(message, nounce);
			byte[] digest = assignment1.CryptoReference1.MessageHash(contact);
			hash = assignment1.CryptoReference1.digest(digest, H);
			flag = CheckHash(hash);
		}
		hashHex = DatatypeConverter.printHexBinary(hash);
		System.out.println(x);
		System.out.println("Hash: " + hashHex);
		System.out.println("# hex digits in hash: " + hashHex.length());
		System.out.println("# bits in hash: " + hash.length * 8);
	}

	private static boolean CheckHash(byte[] hash) {
		boolean flag = false;
		for (byte b: hash) {
			if (b == 0x1D)
				flag = true;
		}
		if(flag == true) return true;
		else return false;
	}

}