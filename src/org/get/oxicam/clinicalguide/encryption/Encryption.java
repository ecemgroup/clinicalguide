package org.get.oxicam.clinicalguide.encryption;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This is the class used for encrypting or decrypting data. If
 * those using this class need data to be encrypted with a different
 * password all that needs to be done is changing the value of the 
 * password String below to something else, and must be at least 
 * 8 bytes in size. The IV can also be changed to something else,
 * and must be exactly 8 bytes in size.
 * 
 * @author Michael Dimchuk
 */
public class Encryption 
{
	/**
	 * The cipher used in decryption of data.
	 */
	private Cipher deCipher;
	/**
	 * The cipher used in encryption of data.
	 */
	private Cipher enCipher;
	/**
	 * The key used in encrypting data, converted from an array of bytes of the password.
	 */
	private SecretKeySpec key;
	/**
	 * The iv used in encrypting data, converted from an array of bytes of the iv.
	 * It is used to strengthen the encryption of data.
	 */
	private IvParameterSpec ivSpec;
	/**
	 * The name of the input file to either decrypt or encrypt.
	 */
	private String inputfile;
	/**
	 * The actual password which is converted to bytes and then a key is created out of it.
	 * It has a minimum size of 8 bytes, in this case 8 characters, and can be very large. 
	 * The password used here was randomly generated.
	 */
	private String password = "M3607Go989VhY3320w647452DIL613D94554T8799U4b39lBWhv2TyfcC699655870487rJn5B" +
			"Wgxk5Tj6V6us6Y015eL3EaI458SG7L931868fR86Gk41A4LD1417BF0hb982gy00SDNV4068592Y55Sd35z19";
	/**
	 * This is the IV. It is used to strengthen the encryption with the password. It must be 
	 * 8 bytes in size, in this case 8 characters. This IV was randomly generated.
	 */
	private String iv = "482UYaIM";
	
	public Encryption(String filename)
	{
	    ivSpec 			= new IvParameterSpec(iv.getBytes());
	    DESKeySpec dkey = null;
		try 
		{
			dkey 		= new  DESKeySpec(password.getBytes());
		} 
		catch (InvalidKeyException e) { e.printStackTrace(); }
		
	    key 			= new SecretKeySpec(dkey.getKey(), "DES");
	    
	    try 
	    {
	    	deCipher 	= Cipher.getInstance("DES/CBC/PKCS5Padding");
			enCipher 	= Cipher.getInstance("DES/CBC/PKCS5Padding");
		} 
	    catch (NoSuchAlgorithmException e) { e.printStackTrace(); } 
	    catch (NoSuchPaddingException e) { e.printStackTrace(); }
	    inputfile 		= filename;
	}
	/**
	 * Decrypts the contents of a file
	 * @param inputfile
	 * @return
	 */
	public byte[] decryptFile(byte[] file)
	{
		byte[] input = null;
		try 
		{
			input = decrypt(file);
		} 
		catch (InvalidKeyException e) { e.printStackTrace(); } 
		catch (InvalidAlgorithmParameterException e) { e.printStackTrace(); } 
		catch (IllegalBlockSizeException e) { e.printStackTrace(); } 
		catch (BadPaddingException e) { e.printStackTrace(); }
		return input;
	}
	/**
	 * Decrypts the contents of a file
	 * @param inputfile
	 * @return 
	 */
	public byte[] decryptFile()
	{
		byte[] input 	= null;
		try 
		{
			input 		= decrypt(getEncryptedInput(inputfile));
		} 
		catch (InvalidKeyException e) { e.printStackTrace(); } 
		catch (InvalidAlgorithmParameterException e) { e.printStackTrace(); } 
		catch (IllegalBlockSizeException e) { return null; } 
		catch (BadPaddingException e) { return null; } 
		catch (IOException e) { return null; }
		return input;
	}
	/**
	 * Gets all of the contents of an encrypted file.
	 * @param filename The name of the file to read.
	 * @throws IOException
	 */
	private byte[] getEncryptedInput(String filename) throws IOException
	{
		FileInputStream read 	= new FileInputStream(filename);
		byte[] input 			= new byte[read.available()];
		read.read(input);
		read.close();
		return input;
	}
	/**
	 * Decrypts the contents of a string.
	 * @param data A string to decrypt.
	 * @return An array of decrypted bytes.
	 */
	public byte[] decryptData(String data)
	{
		byte[] input 	= null;
		try 
		{
			input 		= data.getBytes("ISO-8859-1");
		} 
		catch (UnsupportedEncodingException e1) { e1.printStackTrace(); }
		try 
		{
			input 		= decrypt(input);
		} 
		catch (InvalidKeyException e) { e.printStackTrace(); } 
		catch (InvalidAlgorithmParameterException e) { e.printStackTrace(); } 
		catch (IllegalBlockSizeException e) { e.printStackTrace(); } 
		catch (BadPaddingException e) { e.printStackTrace(); } 
		return input;
	}
	
	/**
	 * Encrypts the contents of a string.
	 * @param data The array of bytes to encrypt.
	 * @return An encrypted array of bytes.
	 */
	public byte[] encryptData(String data)
	{
		byte[] input 	= null;
		try 
		{
			input 		= data.getBytes("ISO-8859-1");
		} 
		catch (UnsupportedEncodingException e1) { e1.printStackTrace(); }
		try 
		{
			input 		= encrypt(input);
		} 
		catch (InvalidKeyException e) { e.printStackTrace(); } 
		catch (InvalidAlgorithmParameterException e) { e.printStackTrace(); } 
		catch (IllegalBlockSizeException e) { e.printStackTrace(); } 
		catch (BadPaddingException e) { e.printStackTrace(); } 
		return input;
	}
	/**
	 * Encrypts an object converted into bytes.
	 * @param objectToEncrypt The object that needs to be encrypted.
	 * @return An array of bytes which have been encrypted.
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] encrypt(byte[] byteInput) throws InvalidKeyException, 
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
	    enCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
	    return enCipher.doFinal(byteInput);
	}
	/**
	 * Decrypts an array of bytes.
	 * @param encrypted An array of bytes to decrypt.
	 * @return A decrypted object.
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private byte[] decrypt(byte[]  encrypted) throws InvalidKeyException, InvalidAlgorithmParameterException, 
						IllegalBlockSizeException, BadPaddingException
	{
		deCipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
	    return deCipher.doFinal(encrypted);
	}
}