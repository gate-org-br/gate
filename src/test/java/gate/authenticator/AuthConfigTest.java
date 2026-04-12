package gate.authenticator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthConfigTest
{
	private static final String[] KEYS = {
			"erp.auth.corp.type",
			"erp.auth.default.type",
			"erp.auth.type",
			"gate.auth.corp.type",
			"gate.auth.default.type",
			"gate.auth.type"
	};

	@AfterEach
	void clearProperties()
	{
		for (String key : KEYS)
			System.clearProperty(key);
	}

	@Test
	void testNamedAuthenticatorPropertyPrecedence()
	{
		System.setProperty("gate.auth.type", "gate");
		System.setProperty("gate.auth.default.type", "gate-default");
		System.setProperty("gate.auth.corp.type", "gate-corp");
		System.setProperty("erp.auth.type", "erp");
		System.setProperty("erp.auth.default.type", "erp-default");
		System.setProperty("erp.auth.corp.type", "erp-corp");

		assertEquals("erp-corp", new AuthConfig("erp", "corp").getProperty("type").orElseThrow());
	}

	@Test
	void testDefaultAuthenticatorPropertyPrecedence()
	{
		System.setProperty("gate.auth.type", "gate");
		System.setProperty("gate.auth.default.type", "gate-default");
		System.setProperty("erp.auth.type", "erp");
		System.setProperty("erp.auth.default.type", "erp-default");

		assertEquals("erp-default", new AuthConfig("erp", "default").getProperty("type").orElseThrow());
	}

	@Test
	void testPropertyMayBeMissing()
	{
		assertTrue(new AuthConfig("erp", "default").getProperty("type").isEmpty());
	}
}
