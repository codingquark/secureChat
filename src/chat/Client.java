package chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;


public class Client {
	
	public static final String ALGORITHM = "RSA";
	public static final String PRIVATE_KEY_FILE = "/home/RSAprivatekey1.rsa";
	public static final String PUBLIC_KEY_FILE = "/home/RSApublickey1.rsa";
	public static byte[] input = null;
	public static Socket socket = null;
	
	
	public static void main(String args[]) {
		
		try {
			socket = new Socket("localhost", 4444);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {

		      final String originalText = "Text to be encrypted ";
		      ObjectInputStream inputStream = null;

		      // Encrypt the string using the public key
		      inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
		      final PublicKey publicKey = (PublicKey) inputStream.readObject();
		      final byte[] cipherText = encrypt(originalText, publicKey);
		      
		      sendBytes(cipherText);
		      
		      //inputStream.close();

//		      // Decrypt the cipher text using the private key.
//		      inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
//		      final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
//		      final String plainText = decrypt(cipherText, privateKey);
//
//		      // Printing the Original, Encrypted and Decrypted Text
//		      System.out.println("Original Text: " + originalText);
//		      System.out.println("Encrypted Text: " + new String(cipherText));
//		      System.out.println("Decrypted Text: " + plainText);
		      
		      inputStream.close();
		      
		      //InputStream in = socket.getInputStream();
		      
		     // System.out.println("Server: " + new String(input));

		    } catch (Exception e) {
		      e.printStackTrace();
		    }
	}
	
	public static void sendBytes(byte[] bytes) {
		try {
			OutputStream out = socket.getOutputStream(); 
	    
			out.write(bytes);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void generateKey() {
	    try {
	      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
	      keyGen.initialize(1024);
	      final KeyPair key = keyGen.generateKeyPair();

	      File privateKeyFile = new File(PRIVATE_KEY_FILE);
	      File publicKeyFile = new File(PUBLIC_KEY_FILE);

	      // Create files to store public and private key
	      if (privateKeyFile.getParentFile() != null) {
	        privateKeyFile.getParentFile().mkdirs();
	      }
	      privateKeyFile.createNewFile();

	      if (publicKeyFile.getParentFile() != null) {
	        publicKeyFile.getParentFile().mkdirs();
	      }
	      publicKeyFile.createNewFile();

	      // Saving the Public key in a file
	      ObjectOutputStream publicKeyOS = new ObjectOutputStream(
	          new FileOutputStream(publicKeyFile));
	      publicKeyOS.writeObject(key.getPublic());
	      publicKeyOS.close();

	      // Saving the Private key in a file
	      ObjectOutputStream privateKeyOS = new ObjectOutputStream(
	          new FileOutputStream(privateKeyFile));
	      privateKeyOS.writeObject(key.getPrivate());
	      privateKeyOS.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }

	  }
	
	public static boolean areKeysPresent() {

	    File privateKey = new File(PRIVATE_KEY_FILE);
	    File publicKey = new File(PUBLIC_KEY_FILE);

	    if (privateKey.exists() && publicKey.exists()) {
	      return true;
	    }
	    return false;
	  }
	
	public static byte[] encrypt(String text, PublicKey key) {
	    byte[] cipherText = null;
	    try {
	      // get an RSA cipher object and print the provider
	      final Cipher cipher = Cipher.getInstance(ALGORITHM);
	      // encrypt the plain text using the public key
	      cipher.init(Cipher.ENCRYPT_MODE, key);
	      cipherText = cipher.doFinal(text.getBytes());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return cipherText;
	  }
	
	public static String decrypt(byte[] text, PrivateKey key) {
	    byte[] dectyptedText = null;
	    try {
	      // get an RSA cipher object and print the provider
	      final Cipher cipher = Cipher.getInstance(ALGORITHM);

	      // decrypt the text using the private key
	      cipher.init(Cipher.DECRYPT_MODE, key);
	      dectyptedText = cipher.doFinal(text);

	    } catch (Exception ex) {
	      ex.printStackTrace();
	    }

	    return new String(dectyptedText);
	  }
}
