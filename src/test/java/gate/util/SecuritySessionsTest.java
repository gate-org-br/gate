package gate.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SecuritySessionsTest
{

	private SecuritySessions securitySessions;

	@Before
	public void setUp()
	{
		securitySessions = SecuritySessions.of(100);
	}

	@Test
	public void testCreateAndCheckSession()
	{
		String session = securitySessions.create();
		Assert.assertNotNull(session);
		Assert.assertTrue(securitySessions.check(session));
	}

	@Test
	public void testCreateAndCheckInvalidSession()
	{
		String session = securitySessions.create();
		Assert.assertNotNull(session);
		Assert.assertFalse(securitySessions.check("invalid"));
	}

	@Test
	public void testTimeout() throws InterruptedException
	{
		String session = securitySessions.create();
		Thread.sleep(200);
		Assert.assertFalse(securitySessions.check(session));
	}
}
