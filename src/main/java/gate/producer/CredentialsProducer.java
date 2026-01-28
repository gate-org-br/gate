package gate.producer;

import gate.security.Credentials;
import gate.security.CryptoKeys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class CredentialsProducer
{

	@Inject
	CryptoKeys cruCryptoKeys;

	@Produces
	@Dependent
	public Credentials produce()
	{
		return new Credentials(cruCryptoKeys.hmac());
	}
}
