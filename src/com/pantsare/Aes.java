/**
 * 
 */
package com.pantsare;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author corey
 *
 */
public class Aes {

	private SecretKeySpec key;
	private IvParameterSpec iv;
	private Cipher enc, dec;
	
	public Aes() {
		this("correct horse battery staple");
	}
	
	public Aes(String key) {
		byte[] salt = {86, 20, 23, 114, 96, 54, 123, 25};
		try {
			// Generate a key and IV using the given salt and data
			generateKey(salt, key.getBytes("UTF-8"), 32, 16, 5);
			
			// get cipher object for password-based encryption
		    enc = Cipher.getInstance("AES/CBC/PKCS5Padding");
		    
		    // initialize cipher for encryption
		    enc.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
		    
		    // get cipher object for password-based encryption
		    dec = Cipher.getInstance("AES/CBC/PKCS5Padding");
		    
		    // initialize cipher for decryption
		    dec.init(Cipher.DECRYPT_MODE, this.key, this.iv);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int blockSize() {
		// Hopefully enc and dec block sizes are the same, otherwise we have bigger problems
		return enc.getBlockSize();
	}
	
	public boolean generateKey(byte[] salt, byte[] keyData, int keyLength, int ivLength, int count) {
		int length = 0;
		boolean keyGenerated = false;
		byte[] dataIn = new byte[0];
		byte[] preData;
		byte[] finalData = new byte[keyLength];
		byte[] dataArray;
		ArrayList<Byte> data = new ArrayList<Byte>();
		
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			
			while (length < keyLength + ivLength) {
				// Assemble data that will be hashed
				preData = new byte[dataIn.length + keyData.length + salt.length];
				System.arraycopy(dataIn, 0, preData, 0, dataIn.length);
				System.arraycopy(keyData, 0, preData, dataIn.length, keyData.length);
				System.arraycopy(salt, 0, preData, dataIn.length + keyData.length, salt.length);
				
				// Get current data by hashing
				for (int i = 0; i < count; ++i) {
					preData = sha.digest(preData);
				}
				
				// Append new data
				for (int i = 0; i < preData.length; ++i) {
					data.add(preData[i]);
				}
				length += preData.length;
				
				// Save current data
				dataIn = preData;
			}

			// Copy data to an array
			dataArray = new byte[data.size()];
			for (int i = 0; i < data.size(); ++i) {
				dataArray[i] = data.get(i);
			}
			// Copy some of it to an array just for the key
			System.arraycopy(dataArray, 0, finalData, 0, keyLength);
			
			// Make the key and IV
			this.key = new SecretKeySpec(finalData, "AES");
			this.iv = new IvParameterSpec(dataArray, keyLength, ivLength);
			keyGenerated = true;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return keyGenerated;
	}
	
	public byte[] encrypt(byte[] value) {
	    try {
		    // encrypt some data
			return enc.doFinal(value);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return null;
	}
	
	public byte[] decrypt(byte[] value) {
	    try {
	    	// decrypt some data
			return dec.doFinal(value);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		return null;
	}
}
