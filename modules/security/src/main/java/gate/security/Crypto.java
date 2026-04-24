package gate.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public record Crypto(SecretKey key)
		{

	public String encryptAndSign(String plain)
	{
		try
		{
			byte[] iv = new byte[12];
			new SecureRandom().nextBytes(iv);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

			GCMParameterSpec spec = new GCMParameterSpec(128, iv);

			cipher.init(Cipher.ENCRYPT_MODE, key, spec);

			byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

			ByteBuffer buffer
					= ByteBuffer.allocate(iv.length + encrypted.length)
							.put(iv)
							.put(encrypted);

			return Base64.getUrlEncoder()
					.withoutPadding()
					.encodeToString(buffer.array());
		} catch (InvalidAlgorithmParameterException |
				InvalidKeyException |
				NoSuchAlgorithmException |
				BadPaddingException |
				IllegalBlockSizeException |
				NoSuchPaddingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public String decryptAndVerify(String value)
	{
		try
		{
			byte[] data = Base64.getUrlDecoder().decode(value);

			ByteBuffer buffer = ByteBuffer.wrap(data);

			byte[] iv = new byte[12];
			buffer.get(iv);

			byte[] encrypted = new byte[buffer.remaining()];
			buffer.get(encrypted);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

			cipher.init(
					Cipher.DECRYPT_MODE,
					key,
					new GCMParameterSpec(128, iv)
			);

			byte[] plain = cipher.doFinal(encrypted);

			return new String(plain, StandardCharsets.UTF_8);
		} catch (AEADBadTagException e)
		{
			throw new SecurityException("state adulterado");
		} catch (InvalidAlgorithmParameterException |
				InvalidKeyException |
				NoSuchAlgorithmException |
				BadPaddingException |
				IllegalBlockSizeException |
				NoSuchPaddingException e)
		{
			throw new RuntimeException(e);
		}
	}
}
