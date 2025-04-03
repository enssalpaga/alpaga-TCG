package bzh.enssalpaga.tcg.socket;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

public class CryptoManager {
	private KeyPair keyPair;
	private SecretKey key;
	private InputStream keyFile;


	public CryptoManager(InputStream keyFile) throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		/* @see https://www.keylength.com/ */
		keyPairGenerator.initialize(4096);
		this.keyPair = keyPairGenerator.generateKeyPair();

		this.keyFile = keyFile;
	}

	public byte[] generatePublic() {
		byte[] publicKey = keyPair.getPublic().getEncoded();
		// 0x00 is code for old "k"
		return Utility.concatenate((byte)0x00, publicKey);
	}

	public void saveKey(byte[] pong) throws Exception {
		AsymmetricCryptography ac = new AsymmetricCryptography();
		PublicKey serverKey = ac.getPublic(keyFile);
		byte[] pong0 = ac.decryptText(Utility.get_after(pong, 1), serverKey);

		// android put unnecessary bytes from 0 to 513 and the utils are from 513 to 1025
		// this modification is not necessary on computer
		byte[] pong4android = Utility.get_after(pong0, pong0.length-512);

		// parameter is needed only on android :
		// https://stackoverflow.com/questions/46042127/android-8-0-illegalblocksizeexception-when-using-rsa-ecb-oaepwithsha-512andmgf1
		OAEPParameterSpec sp = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
		cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(), sp);

		byte[] pong1 = cipher.doFinal(pong4android, 0, pong4android.length);
		// 0x07 is code for old "It's me, the server"
		if (pong1 == null || pong1.length < 20 || pong1[0] != (byte)0x07){
			throw new Exception("Error, the server is strange or I do not know to code corectly !");
		}
		byte[] pong2 = Utility.get_after(pong1, 1);

		// rebuild key using SecretKeySpec
		key = new SecretKeySpec(pong2, 0, pong2.length, "AES");
	}

	public String encrypt(String txt, byte[] nonce) throws Exception {
		// ENCRYPTION
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
	    GCMParameterSpec spec = new GCMParameterSpec(16 * 8, nonce);
	    cipher.init(Cipher.ENCRYPT_MODE, key, spec);

	    byte[] byteCipher = cipher.doFinal(txt.getBytes(StandardCharsets.UTF_8));
	    // CONVERSION of raw bytes to BASE64 representation
	    String cipherText = Base64.getEncoder().encodeToString(byteCipher);
	    return cipherText;
	}

	public String decrypt(String txt, byte[] nonce) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
	    GCMParameterSpec spec = new GCMParameterSpec(16 * 8, nonce);
	    cipher.init(Cipher.ENCRYPT_MODE, key, spec);

	    cipher.init(Cipher.DECRYPT_MODE, key, spec);
	    byte[] decryptedCipher = cipher.doFinal(Base64.getDecoder().decode(txt));
	    String decryptedCipherText = new String(decryptedCipher, StandardCharsets.UTF_8);
	    return decryptedCipherText;
	}
}
