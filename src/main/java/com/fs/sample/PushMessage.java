package com.fs.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.JsonObject;

public class PushMessage {

	private final SecureRandom SECURE_RANDOM = new SecureRandom();
	private KeyPairGenerator keyPairGenerator;
	private KeyFactory keyFactory;
	private ECPublicKey publicKey;
	private ECPrivateKey privateKey;
	
	public PushMessage() throws URISyntaxException{
		
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("EC");
			keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
			keyFactory = KeyFactory.getInstance("EC");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		long publicKeyLength = 0;
		long privateKeylength = 0;
		try {
			publicKeyLength = ResourceUtils.getFile("classpath:public.key").length();
			privateKeylength = ResourceUtils.getFile("classpath:private.key").length();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		if( publicKeyLength > 0 && privateKeylength > 0){
			try {
				byte[] pubKeyArray = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:public.key").toURI())); 
				byte[] priKeyArray = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:private.key").toURI()));
				
				X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyArray);
				publicKey = (ECPublicKey)keyFactory.generatePublic(pubKeySpec);
				
				PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(priKeyArray);
				privateKey = (ECPrivateKey)keyFactory.generatePrivate(priKeySpec);
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}else {
			if(genKeyPair()) {
				System.out.println("Pair of Keys are generated successfuly!");
			}else {
				System.out.println("There is issue in generating Pair of Keys");
			}
		}
	}
	
	public boolean genKeyPair() {

		boolean isCreated = false;
		try {
					
			KeyPair pair = keyPairGenerator.generateKeyPair();
	        publicKey = (ECPublicKey) pair.getPublic();
	        privateKey = (ECPrivateKey) pair.getPrivate();
			
	        System.out.println("PublicKey: "+publicKey);
	        System.out.println("PrivateKey: "+privateKey);
	        
			Files.write(Paths.get(ResourceUtils.getFile("classpath:public.key").toURI()), publicKey.getEncoded());
			Files.write(Paths.get(ResourceUtils.getFile("classpath:private.key").toURI()), privateKey.getEncoded());
			 
			isCreated = true;
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return isCreated;
	}
	
	public String getPublickKey() {
		
		byte[] publicKeyUncompressed = toUncompressedECPublicKey(publicKey);
		  
		String publicKeyBase64 = Base64.getUrlEncoder().withoutPadding()
		  .encodeToString(publicKeyUncompressed);
		
		return publicKeyBase64;
	}
	
	
	private static byte[] P256_HEAD = Base64.getDecoder()
		      							.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgA");
	
	
	public static byte[] toUncompressedECPublicKey(ECPublicKey publicKey) {
		
		byte[] result = new byte[65];
	    byte[] encoded = publicKey.getEncoded();
	    System.arraycopy(encoded, P256_HEAD.length, result, 0,
	        encoded.length - P256_HEAD.length);
	    return result;
	}
	
	public void sendNotification(JsonObject payload) {
		
		String origin=null;
		try {
			URL url=new URL(payload.get("endpoint").getAsString());
			origin = url.getProtocol()+"://"+url.getHost();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		Algorithm jwtAlgorithm = Algorithm.ECDSA256(publicKey,privateKey);
		
		String token = JWT.create()
						.withAudience(origin)
						.withExpiresAt(new Date(new Date().getTime()+12*60*60*1000))
						.withSubject("mailto:lyogesh@findabilitysciences.com")
						.sign(jwtAlgorithm);
						
		System.out.println("Token: "+token);

		String endpointURI = payload.get("endpoint").getAsString();
		
		System.out.println("Endpoint: "+endpointURI+"\n\nPublic Key: "+getPublickKey()); 
		
		String auth="vapid t="+token+", k="+getPublickKey();
		
		
		
		URL u;
		HttpURLConnection conn=null;
		
		try {
			u = new URL(endpointURI);
			conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization",auth);
			conn.setRequestProperty("TTL","180");
			conn.setRequestProperty("Content-Length","0");
			int statusCode=conn.getResponseCode();
			System.out.println(statusCode);
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public ECPublicKey fromUncompressedECPublicKey(String encodedPublicKey)
		      throws InvalidKeySpecException {

		byte[] w = Base64.getUrlDecoder().decode(encodedPublicKey);
		byte[] encodedKey = new byte[P256_HEAD.length + w.length];
		System.arraycopy(P256_HEAD, 0, encodedKey, 0, P256_HEAD.length);
		System.arraycopy(w, 0, encodedKey, P256_HEAD.length, w.length);

		X509EncodedKeySpec ecpks = new X509EncodedKeySpec(encodedKey);
		return (ECPublicKey) this.keyFactory.generatePublic(ecpks);
	}
	
	byte[] concat(byte[]... arrays) {
	    // Determine the length of the result array
	    int totalLength = 0;
	    for (byte[] array : arrays) {
	      totalLength += array.length;
	    }

	    // create the result array
	    byte[] result = new byte[totalLength];

	    // copy the source arrays into the result array
	    int currentIndex = 0;
	    for (byte[] array : arrays) {
	      System.arraycopy(array, 0, result, currentIndex, array.length);
	      currentIndex += array.length;
	    }

	    return result;
	  }
	
		/*
		 * public byte[] encrypt(String plainTextString, String uaPublicKeyString,
		 * String authSecret, int paddingSize) throws InvalidKeyException,
		 * NoSuchAlgorithmException, InvalidKeySpecException,
		 * InvalidAlgorithmParameterException, NoSuchPaddingException,
		 * IllegalBlockSizeException, BadPaddingException {
		 * 
		 * // ecdh_secret = ECDH(as_private, ua_public) // auth_secret = <from user
		 * agent> // salt = random(16) KeyPair asKeyPair =
		 * this.keyPairGenerator.genKeyPair(); ECPublicKey asPublicKey = (ECPublicKey)
		 * asKeyPair.getPublic(); byte[] uncompressedASPublicKey =
		 * toUncompressedECPublicKey(asPublicKey);
		 * 
		 * ECPublicKey uaPublicKey = fromUncompressedECPublicKey(uaPublicKeyString);
		 * 
		 * KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
		 * keyAgreement.init(asKeyPair.getPrivate()); keyAgreement.doPhase(uaPublicKey,
		 * true);
		 * 
		 * byte[] ecdhSecret = keyAgreement.generateSecret();
		 * 
		 * byte[] salt = new byte[16]; this.SECURE_RANDOM.nextBytes(salt);
		 * 
		 * // ## Use HKDF to combine the ECDH and authentication secrets // #
		 * HKDF-Extract(salt=auth_secret, IKM=ecdh_secret) // PRK_key =
		 * HMAC-SHA-256(auth_secret, ecdh_secret) Mac hmacSHA256 =
		 * Mac.getInstance("HmacSHA256"); hmacSHA256 .init(new
		 * SecretKeySpec(Base64.getUrlDecoder().decode(authSecret), "HmacSHA256"));
		 * byte[] prkKey = hmacSHA256.doFinal(ecdhSecret);
		 * 
		 * // # HKDF-Expand(PRK_key, key_info, L_key=32) // key_info = "WebPush: info"
		 * || 0x00 || ua_public || as_public
		 * 
		 * byte[] keyInfo = concat("WebPush: info\0".getBytes(StandardCharsets.UTF_8),
		 * toUncompressedECPublicKey(uaPublicKey), uncompressedASPublicKey); // IKM =
		 * HMAC-SHA-256(PRK_key, key_info || 0x01) hmacSHA256.init(new
		 * SecretKeySpec(prkKey, "HmacSHA256")); hmacSHA256.update(keyInfo);
		 * hmacSHA256.update((byte) 1); byte[] ikm = hmacSHA256.doFinal();
		 * 
		 * // ## HKDF calculations from RFC 8188 // # HKDF-Extract(salt, IKM) // PRK =
		 * HMAC-SHA-256(salt, IKM) hmacSHA256.init(new SecretKeySpec(salt,
		 * "HmacSHA256")); byte[] prk = hmacSHA256.doFinal(ikm);
		 * 
		 * // # HKDF-Expand(PRK, cek_info, L_cek=16) // cek_info =
		 * "Content-Encoding: aes128gcm" || 0x00 byte[] cekInfo =
		 * "Content-Encoding: aes128gcm\0".getBytes(StandardCharsets.UTF_8); // CEK =
		 * HMAC-SHA-256(PRK, cek_info || 0x01)[0..15] hmacSHA256.init(new
		 * SecretKeySpec(prk, "HmacSHA256")); hmacSHA256.update(cekInfo);
		 * hmacSHA256.update((byte) 1); byte[] cek = hmacSHA256.doFinal(); cek =
		 * Arrays.copyOfRange(cek, 0, 16);
		 * 
		 * // # HKDF-Expand(PRK, nonce_info, L_nonce=12) // nonce_info =
		 * "Content-Encoding: nonce" || 0x00 byte[] nonceInfo =
		 * "Content-Encoding: nonce\0".getBytes(StandardCharsets.UTF_8); // NONCE =
		 * HMAC-SHA-256(PRK, nonce_info || 0x01)[0..11] hmacSHA256.init(new
		 * SecretKeySpec(prk, "HmacSHA256")); hmacSHA256.update(nonceInfo);
		 * hmacSHA256.update((byte) 1); byte[] nonce = hmacSHA256.doFinal(); nonce =
		 * Arrays.copyOfRange(nonce, 0, 12);
		 * 
		 * Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		 * cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(cek, "AES"), new
		 * GCMParameterSpec(128, nonce)); List<byte[]> inputs = new ArrayList<>();
		 * byte[] plainTextBytes = plainTextString.getBytes(StandardCharsets.UTF_8);
		 * inputs.add(plainTextBytes); inputs.add(new byte[] { 2 }); // padding
		 * delimiter
		 * 
		 * int padSize = Math.max(0, paddingSize - plainTextBytes.length); if (padSize >
		 * 0) { inputs.add(new byte[padSize]); }
		 * 
		 * byte[] encrypted = cipher.doFinal(concat(inputs.toArray(new byte[0][])));
		 * 
		 * ByteBuffer encryptedArrayLength = ByteBuffer.allocate(4);
		 * encryptedArrayLength.putInt(encrypted.length);
		 * 
		 * byte[] header = concat(salt, encryptedArrayLength.array(), new byte[] {
		 * (byte) uncompressedASPublicKey.length }, uncompressedASPublicKey);
		 * 
		 * return concat(header, encrypted); }
		 */
	
}
