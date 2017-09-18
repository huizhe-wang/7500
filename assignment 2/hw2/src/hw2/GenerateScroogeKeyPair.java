package hw2;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

import static org.bouncycastle.cms.RecipientId.password;

public class GenerateScroogeKeyPair {

	public static void main(String[] args) throws Exception {

	}
}
