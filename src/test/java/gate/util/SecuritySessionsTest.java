package gate.util;

import gate.security.OneTimeTokenStore;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SecuritySessionsTest
{

	private static OneTimeTokenStore securitySessions;

	@BeforeAll
	public static void setUp()
	{
		securitySessions = OneTimeTokenStore.of(100);
	}

	@Test
	public void testCreateAndCheckSession()
	{
		String session = securitySessions.create();
		assertNotNull(session);
		assertTrue(securitySessions.consume(session));
	}

	@Test
	public void testCreateAndCheckInvalidSession()
	{
		String session = securitySessions.create();
		assertNotNull(session);
		assertFalse(securitySessions.consume("invalid"));
	}

	@Test
	public void testTimeout() throws InterruptedException
	{
		String session = securitySessions.create();
		Thread.sleep(200);
		assertFalse(securitySessions.consume(session));
	}
}
