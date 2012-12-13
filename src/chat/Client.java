package chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import security.Rsa;

public class Client {

	/**
	 * @param args
	 * @throws NoSuchPaddingException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		Socket socket = null;
		
		try {
			socket = new Socket("127.0.0.1", 4444);
			
		} catch (UnknownHostException e) {
			System.out.println("No such host");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Exception");
			e.printStackTrace();
		}
		
		//SecureRandom random = new SecureRandom();
		//Seeder seed = new Seeder(20);
		//random.setSeed(seed.getSeed());
		//byte[] rand = SecureRandom.getSeed(20);
		
		decrypt(0);
		
		String secret = "dhavanvaidya";
		byte[] rand = secret.getBytes();
		
		Rsa r1 = new Rsa();
		BigInteger tempRand = r1.encryptWithPublic(rand);
		byte[] tempRand2 = tempRand.toByteArray();
		String encRand = tempRand2.toString();
		
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			if(rand != null) {
				System.out.println(encRand);
				out.println(encRand);
			}
						
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String random1 = in.readLine();
			System.out.println("Server: " + random1);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void decrypt(int flag) throws NoSuchAlgorithmException, 
		InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
	
		String passPhrase = null;
		byte fileContent[] = null;
	
		try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
				System.out.println("Please enter a passphrase: ");
				passPhrase = reader.readLine().toString();
	
		} catch (IOException e) {
			System.out.println("Could not read the pass phrase.");
			e.printStackTrace();
			System.exit(0);
		}
	
		MessageDigest md5Digest = MessageDigest.getInstance("MD5");
		byte[] pass = passPhrase.getBytes();
	
		byte[] md5data = md5Digest.digest(pass);
		SecretKey blowKey = new SecretKeySpec(md5data, 0, md5data.length, "Blowfish");
	
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.DECRYPT_MODE, blowKey);
	
		try {
	
			File homeDir = new File(System.getProperty("user.home"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String fileName = null;
	
			System.out.println("Please choose a key ID: ");
			String ID = reader.readLine().toString();
	
			if(flag == 0) fileName = "public";
			else fileName = "private";
	
			File fileToRead = new File(homeDir, "RSA" + fileName + "key" + ID + ".rsa");
			FileInputStream fis = new FileInputStream(fileToRead);
	
			if(!fileToRead.exists()) {
				System.out.println("The key does not exist.");
				System.exit(0);
			}
	
			fileContent = new byte[(int)fileToRead.length()];
			fis.read(fileContent);
			fis.close();
	
			byte[] key = cipher.doFinal(fileContent);
			Rsa rsa = new Rsa();
	
			if(flag == 0) rsa.setPublicKey(key);
			else if(flag == 1) rsa.setPrivateKey(key);
	
			return;
		} catch (FileNotFoundException e) {
			System.out.println("Could not find the file...");
	
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Error while reading the keys...");
	
			System.exit(0);
		}
		return;
	}
}
