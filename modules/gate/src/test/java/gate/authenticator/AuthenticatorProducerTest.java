package gate.authenticator;

import gate.entity.App;
import gate.error.AuthenticatorException;
import gate.http.TestServletSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticatorProducerTest
{
	private static final String[] KEYS = {
			"gate.auth.default.type",
			"gate.auth.corp.type",
			"gate.auth.corp.ldap.server",
			"gate.auth.corp.oidc.client_id",
			"gate.auth.corp.oidc.provider",
			"gate.auth.corp.oidc.redirect_uri"
	};

	@AfterEach
	void clearProperties()
	{
		for (String key : KEYS)
			System.clearProperty(key);
	}

	@Test
	void testUsesDatabaseAuthenticatorByDefault()
	{
		var producer = producer(TestServletSupport.request().request());

		assertInstanceOf(DatabaseAuthenticator.class, producer.get());
	}

	@Test
	void testResolvesNamedLdapAuthenticator()
	{
		System.setProperty("gate.auth.corp.type", "ldap");
		System.setProperty("gate.auth.corp.ldap.server", "ldaps://ldap.example.com:636");

		var request = TestServletSupport.request(
				java.util.Map.of("authenticator", "corp"),
				java.util.Map.of(),
				null,
				"GET",
				"/Gate");

		assertInstanceOf(LDAPAuthenticator.class, producer(request.request()).get());
	}

	@Test
	void testFailsForUnknownNamedAuthenticator()
	{
		var request = TestServletSupport.request(
				java.util.Map.of("authenticator", "corp"),
				java.util.Map.of(),
				null,
				"GET",
				"/Gate");

		assertThrows(AuthenticatorException.class, () -> producer(request.request()).get());
	}

	private static AuthenticatorProducer producer(jakarta.servlet.http.HttpServletRequest request)
	{
		var producer = new AuthenticatorProducer();
		producer.app = App.of("{\"id\":\"ERP\"}");
		producer.request = request;
		return producer;
	}
}
