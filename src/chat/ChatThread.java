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

public class ChatThread extends Thread{
	
	private Socket socket = null;
	String random1;
	BigInteger rand = null;
	
	public ChatThread(Socket socket) {
		super("ClientThread");
		this.socket = socket;
	}
	
	public void run() {
		System.out.println("New client:" + socket);
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			random1 = in.readLine();
			System.out.println("Client: " + random1);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		try {
			rand = operateRandom(random1, 1);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			if(rand != null) {
				byte[] randB = rand.toByteArray();
				String send = randB.toString();				
				
				out.println(send);
			}
						
		} catch (IOException e) {
			
			e.printStackTrace();
		}		
	}
	
	public BigInteger operateRandom(String random, int flag) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
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
			
			byte[]temp = random.getBytes();
			
			BigInteger decRand = rsa.decryptWithPrivate(temp);
	
			return decRand;
		} catch (FileNotFoundException e) {
			System.out.println("Could not find the file...");
	
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Error while reading the keys...");
	
			System.exit(0);
		}
		return null;
	}

}
