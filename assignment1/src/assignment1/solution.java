package assignment1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class solution {
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		for (int i = 0; i < 20; i++){
		   // Generate a pseudo-random 256-bit nonce.
		    byte[] x = new byte[32]; //256 bit array
		    new Random().nextBytes(x); //pseudo-random
		     
		   //Convert a hex string into a byte array. API requires omitting the leading "0x".
	        String hex = "ED00AF5F774E4135E7746419FEB65DE8AE17D6950C95CEC3891070FBB5B03C77";
			byte[] id = DatatypeConverter.parseHexBinary(hex);

		   // Concatenate two byte arrays
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write(x);
			outputStream.write(id);
			byte concatHex[] = outputStream.toByteArray();
			//String concatHex = DatatypeConverter.printHexBinary(concat);


			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(concatHex); // SHA256 hash
			
			for(byte b:hash){
				if(b==0x1D) 
					System.out.println(DatatypeConverter.printHexBinary(x));
			}	
		}
	}
}
