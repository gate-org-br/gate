package gate.util;

import gate.error.AuthenticatorException;
import gate.lang.json.JsonObject;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * Factory class to create PublicKey instances from JSON Web Key Set (JWKS) representations.
 */
public class JWKSPublicKeyParser
{

	/**
	 * Creates a PublicKey from the provided JSON Web Key Set (JWKS).
	 *
	 * @param jwks The JSON Web Key Set representation.
	 * @return The corresponding PublicKey.
	 * @throws AuthenticatorException If the key type is unsupported or an error occurs during key creation.
	 */
	public static PublicKey parse(JsonObject jwks)
	{
		String keyType = jwks.getString("kty")
			.orElseThrow(() -> new AuthenticatorException("Key type (kty) is missing from the key"));

		try
		{
			return switch (keyType)
			{
				case "RSA" ->
					createRsaPublicKey(jwks);
				case "EC" ->
					createEcPublicKey(jwks);
				default -> throw new AuthenticatorException("Unsupported key type: " + keyType);
			};
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidParameterSpecException e)
		{
			throw new AuthenticatorException("Error creating public key of type: " + keyType, e);
		}
	}

	/**
	 * Creates an RSA PublicKey from the given JSON Web Key Set (JWKS).
	 *
	 * @param jwks The JSON Web Key Set representation.
	 * @return The RSA PublicKey.
	 * @throws NoSuchAlgorithmException If the RSA algorithm is not available.
	 * @throws InvalidKeySpecException If the key specification is invalid.
	 */
	private static PublicKey createRsaPublicKey(JsonObject jwks)
		throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		BigInteger modulus = jwks.getString("n")
			.map(Base64.getUrlDecoder()::decode)
			.map(e -> new BigInteger(1, e))
			.orElseThrow(() -> new AuthenticatorException("Error decoding RSA modulus"));

		BigInteger exponent = jwks.getString("e")
			.map(Base64.getUrlDecoder()::decode)
			.map(e -> new BigInteger(1, e))
			.orElseThrow(() -> new AuthenticatorException("Error decoding RSA exponent"));

		RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(modulus, exponent);
		return KeyFactory.getInstance("RSA").generatePublic(rsaSpec);
	}

	/**
	 * Creates an EC PublicKey from the given JSON Web Key Set (JWKS).
	 *
	 * @param jwks The JSON Web Key Set representation.
	 * @return The EC PublicKey.
	 * @throws NoSuchAlgorithmException If the EC algorithm is not available.
	 * @throws InvalidKeySpecException If the key specification is invalid.
	 * @throws InvalidParameterSpecException If the parameter specification is invalid.
	 */
	private static PublicKey createEcPublicKey(JsonObject jwks)
		throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidParameterSpecException
	{
		String curveName = jwks.getString("crv")
			.orElseThrow(() -> new AuthenticatorException("Curve (crv) is missing from the key"));
		byte[] x = Base64.getUrlDecoder().decode(jwks.getString("x")
			.orElseThrow(() -> new AuthenticatorException("X coordinate (x) is missing from the key")));
		byte[] y = Base64.getUrlDecoder().decode(jwks.getString("y")
			.orElseThrow(() -> new AuthenticatorException("Y coordinate (y) is missing from the key")));

		AlgorithmParameters params = AlgorithmParameters.getInstance("EC");
		params.init(new ECGenParameterSpec(curveName));
		ECParameterSpec ecSpec = params.getParameterSpec(ECParameterSpec.class);

		ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(new ECPoint(new BigInteger(1, x), new BigInteger(1, y)), ecSpec);
		return KeyFactory.getInstance("EC").generatePublic(ecPublicKeySpec);
	}
}
