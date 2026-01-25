package gate.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public record PKCE(String verifier, String challenge)
	{

	public static PKCE create()
	{
		try
		{
			SecureRandom secureRandom = new SecureRandom();
			byte[] randomBytes = new byte[32];
			secureRandom.nextBytes(randomBytes);
			String codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
			String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

			return new PKCE(codeVerifier, codeChallenge);

		} catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException("Error generating PKCE", e);
		}
	}
}
