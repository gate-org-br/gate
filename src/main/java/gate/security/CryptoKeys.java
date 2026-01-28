package gate.security;

import javax.crypto.SecretKey;

public record CryptoKeys(
	SecretKey hmac,
	SecretKey aes)
	{

}
