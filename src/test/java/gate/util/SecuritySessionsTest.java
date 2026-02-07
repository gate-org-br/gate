package gate.util;

import gate.security.SecuritySessions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SecuritySessionsTest
{

	private static SecuritySessions securitySessions;

	@BeforeAll
	public static void setUp()
	{
		securitySessions = SecuritySessions.of(100);
	}

	@Test
	public void testCreateAndCheckSession()
	{
		String session = securitySessions.create();
		assertNotNull(session);
		assertTrue(securitySessions.check(session));
	}

	@Test
	public void testCreateAndCheckInvalidSession()
	{
		String session = securitySessions.create();
		assertNotNull(session);
		assertFalse(securitySessions.check("invalid"));
	}

	@Test
	public void testTimeout() throws InterruptedException
	{
		String session = securitySessions.create();
		Thread.sleep(200);
		assertFalse(securitySessions.check(session));
	}
}
